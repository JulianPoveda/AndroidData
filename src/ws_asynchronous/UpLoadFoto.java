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



public class  UpLoadFoto extends AsyncTask<String, Integer, String>{
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
	private static final String METHOD_NAME	= "FotoTomada";	
	private static final String SOAP_ACTION	= "FotoTomada";
	SoapPrimitive response = null;
	
	ProgressDialog 	_pDialog;

	
	public  UpLoadFoto(Context context, String Directorio){
		this.WS_UpLoadContext = context;
		this.FolderWS = Directorio;
		
		ArchUpLoadWS= new Archivos(this.WS_UpLoadContext, this.FolderWS, 10);
		UpLoadSQL 	= new SQLite(this.WS_UpLoadContext, this.FolderWS);
		
		this._ip_servidor 	= this.UpLoadSQL.StrSelectShieldWhere("db_parametros","valor", "item='servidor'");
		this._puerto 		= this.UpLoadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='puerto'"); 
		this._modulo		= this.UpLoadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='modulo'");
		this._web_service	= "ServiciosV100.php?wsdl";		
		
		this.URL 			= _ip_servidor+":"+_puerto+"/"+_modulo+"/"+_web_service;
		this.NAMESPACE 		= _ip_servidor+":"+_puerto+"/"+_modulo;
	}

	
   	 
	protected void onPreExecute(){

	}

	@Override
	protected String doInBackground(String... params) {
				
			try{
				SoapObject so=new SoapObject(NAMESPACE, METHOD_NAME);
				so.addProperty("orden", params[0]);			
				so.addProperty("informacion",this.ArchUpLoadWS.FileToArrayBytes(params[1]));
				
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
					this.ArchUpLoadWS.DeleteFile(params[1]);
				}	
			} catch (Exception e) {
				this.Respuesta = e.toString();
			}			
		
		return this.Respuesta;
	}


	protected void onPostExecute(int rta) {
		//Toast.makeText(this.WS_UpLoadContext,"Recibido "+this.Respuesta, Toast.LENGTH_LONG).show();
    }
}
