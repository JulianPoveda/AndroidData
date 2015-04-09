package ws_asynchronous;

import java.io.File;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import miscelanea.Archivos;
import miscelanea.SQLite;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;



public class  UpLoadActa extends AsyncTask<String, Integer, String>{
	private Context 			WS_UpLoadContext;
	private SQLite				UpLoadSQL;
	private Archivos 			ArchUpLoadWS;
	private String 				FolderWS;
	
	private ContentValues			InformacionArchivos = new ContentValues();
	private ArrayList<ContentValues>RegistroArchivos 	= new ArrayList<ContentValues>();
	
	private File[]				ListaDirectorios;
	private File[]				ListaArchivos;
	private ArrayList<String>	ListaFolderActas = new ArrayList<String>();
	private String 				Respuesta = "";
	
	private String 	_ip_servidor	= "";
	private String  _puerto			= "";
	private String  _modulo 		= "";
	private String 	_web_service 	= "";
	
	private String URL; 			
	private String NAMESPACE; 	
	private static final String METHOD_NAME	= "StrActaImpresa";	
	private static final String SOAP_ACTION	= "StrActaImpresa";
	SoapPrimitive response = null;
	
	ProgressDialog 	_pDialog;

	
	public  UpLoadActa(Context context, String Directorio){
		this.WS_UpLoadContext = context;
		this.FolderWS = Directorio;
		
		ArchUpLoadWS= new Archivos(this.WS_UpLoadContext, this.FolderWS, 10);
		UpLoadSQL 	= new SQLite(this.WS_UpLoadContext, this.FolderWS);
		
		this._ip_servidor 	= this.UpLoadSQL.StrSelectShieldWhere("db_parametros","valor", "item='servidor'");
		this._puerto 		= this.UpLoadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='puerto'"); 
		this._modulo		= this.UpLoadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='modulo'");
		this._web_service	= this.UpLoadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='web_service'");		
		
		this.URL 			= _ip_servidor+":"+_puerto+"/"+_modulo+"/"+_web_service;
		this.NAMESPACE 		= _ip_servidor+":"+_puerto+"/"+_modulo;
	}

	
   	 
	protected void onPreExecute(){
		this.RegistroArchivos.clear();
		this.ListaDirectorios = new File(this.FolderWS).listFiles();
	    this.ListaFolderActas.clear();
	    for(int i=0;i<this.ListaDirectorios.length;i++){
	    	if(this.ListaDirectorios[i].isDirectory() && !this.ListaDirectorios[i].getName().equals("Descarga")){
	    		this.ListaArchivos = new File(this.ListaDirectorios[i].toString()).listFiles();
	    		if(this.ListaArchivos.length == 0){
	    			ArchUpLoadWS.DeleteFile(this.ListaDirectorios[i].toString());	
	    		}else if(this.ListaArchivos.length>0){
	    			for(int j=0;j<this.ListaArchivos.length;j++){
	    				if(this.ListaArchivos[j].isDirectory()){
	    					ArchUpLoadWS.DeleteFile(this.ListaArchivos[j].toString());	
	    				}else{
	    					this.InformacionArchivos.clear();
		    				String temp1[] = this.ListaArchivos[j].toString().split("\\/");
		    				int cantCarpetas = temp1.length;
		    				String temp2[] = temp1[cantCarpetas-1].split("\\_");
		    				this.InformacionArchivos.put("orden", temp1[cantCarpetas-2]);	 
		    				this.InformacionArchivos.put("cuenta", this.UpLoadSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "cuenta", "id_orden='"+temp1[cantCarpetas-2]+"'"));
		    				this.InformacionArchivos.put("tipo_archivo", temp2[0]);
		    				this.InformacionArchivos.put("tipo_copia", temp2[1]);
		    				this.InformacionArchivos.put("consecutivo", temp2[2]);
		    				this.InformacionArchivos.put("items",this.UpLoadSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "num_acta", "id_orden='"+temp1[cantCarpetas-2]+"'"));
		    				this.InformacionArchivos.put("ruta_archivo",this.ListaArchivos[j].toString());
		    				this.RegistroArchivos.add(this.InformacionArchivos);
	    				}	    				
	    			}	    			
	    		}
	    	}
	    }
	}

	@Override
	protected String doInBackground(String... params) {
		for(int i=0; i<this.RegistroArchivos.size();i++){
			this.InformacionArchivos = this.RegistroArchivos.get(i);
			try{
				SoapObject so=new SoapObject(NAMESPACE, METHOD_NAME);
				so.addProperty("orden", this.InformacionArchivos.getAsString("orden"));
				so.addProperty("cuenta",this.InformacionArchivos.getAsString("cuenta"));
				so.addProperty("tipo_archivo",this.InformacionArchivos.getAsString("tipo_archivo"));
				so.addProperty("tipo_copia",this.InformacionArchivos.getAsString("tipo_copia"));
				so.addProperty("consecutivo",this.InformacionArchivos.getAsString("consecutivo"));
				so.addProperty("items",this.InformacionArchivos.getAsString("items"));
				so.addProperty("informacion",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("ruta_archivo")));
				
				SoapSerializationEnvelope sse=new SoapSerializationEnvelope(SoapEnvelope.VER11);
				new MarshalBase64().register(sse);
				sse.dotNet=true;
				sse.setOutputSoapObject(so);
				HttpTransportSE htse=new HttpTransportSE(URL);
				htse.call(SOAP_ACTION, sse);
				response=(SoapPrimitive) sse.getResponse();
				
				if(response==null) {
					this.Respuesta = "-1";
				}else if(response.toString().isEmpty()){
					this.Respuesta = "-2";
				}else if(response.toString().equals("1")){
					this.Respuesta = "1";
					this.ArchUpLoadWS.DeleteFile(this.InformacionArchivos.getAsString("ruta_archivo"));
				}	
			} catch (Exception e) {
				this.Respuesta = e.toString();
			}			
		}
		return this.Respuesta;
	}


	protected void onPostExecute(int rta) {
		//Toast.makeText(this.WS_UpLoadContext,"Recibido "+this.Respuesta, Toast.LENGTH_LONG).show();
    }
}
