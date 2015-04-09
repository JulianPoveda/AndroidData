package ws_asynchronous;

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


public class  UpLoadUbicacion extends AsyncTask<String, Integer, String>{
	private Context 			UpLoadContext;
	private SQLite				UpLoadSQL;
	private Archivos 			UpLoadArch;
	private String 				UpLoadFolder;
	
	private ContentValues			_tempRegistro	= new ContentValues();
	private ArrayList<ContentValues>_tempTabla 		= new ArrayList<ContentValues>();
	
	private String				UpLoadInformacion="";
	private String 				Respuesta = "";
	
	private String 	_ip_servidor	= "";
	private String  _puerto			= "";
	private String  _modulo 		= "";
	private String 	_web_service 	= "";
	
	private String URL; 			//= "http://cav.com.co/Sigesp/WebService/WS_CAV.php?wsdl";
	private String NAMESPACE; 		//= "http://cav.com.co/Sigesp/WebService";
	private static final String METHOD_NAME	= "UbicacionGPS";	
	private static final String SOAP_ACTION	= "UbicacionGPS";
	SoapPrimitive response = null;
	
	ProgressDialog 	_pDialog;

	
	public  UpLoadUbicacion(Context context, String Directorio){
		this.UpLoadContext 	= context;
		this.UpLoadFolder	= Directorio;
		
		this.UpLoadArch	= new Archivos(this.UpLoadContext, this.UpLoadFolder, 10);
		this.UpLoadSQL 	= new SQLite(this.UpLoadContext, this.UpLoadFolder);
		
		this._ip_servidor 	= this.UpLoadSQL.StrSelectShieldWhere("db_parametros","valor", "item='servidor'");
		this._puerto 		= this.UpLoadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='puerto'"); 
		this._modulo		= this.UpLoadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='modulo'");
		this._web_service	= this.UpLoadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='web_service'");		
		
		this.URL 			= _ip_servidor+":"+_puerto+"/"+_modulo+"/"+_web_service;
		this.NAMESPACE 		= _ip_servidor+":"+_puerto+"/"+_modulo;
	}
	
   	 
	protected void onPreExecute(){
		this._tempTabla = this.UpLoadSQL.SelectData("db_gps", "id_serial,id_tablet,fecha_ins,longitud,latitud", "id_serial IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.UpLoadInformacion +=this._tempRegistro.getAsString("fecha_ins")+"|"
									+this._tempRegistro.getAsString("id_serial")+"|"
									+this._tempRegistro.getAsString("id_tablet")+"|"
									+this._tempRegistro.getAsString("longitud")+"|"
									+this._tempRegistro.getAsString("latitud")+"\r\n";		
		}
		this.UpLoadArch.DoFile("", "registros_gps", this.UpLoadInformacion);
	}

	
	@Override
	protected String doInBackground(String... params) {
		try{
			SoapObject so=new SoapObject(NAMESPACE, METHOD_NAME);
			so.addProperty("pda",this.UpLoadSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'"));
			so.addProperty("informacion",this.UpLoadArch.FileToArrayBytes(this.UpLoadFolder+"/registros_gps"));
				
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
			}else{
				this.Respuesta = response.toString();
			}	
		} catch (Exception e) {
			this.Respuesta = e.toString();
		}			
		return this.Respuesta;
	}


	protected void onPostExecute(String rta) {
		this.UpLoadArch.DeleteFile(this.UpLoadFolder+"/registros_gps");
		String seriales[] = rta.split("\\|");
		for(int i=0;i<seriales.length;i++){
			this.UpLoadSQL.DeleteRegistro("db_gps", "id_serial = "+seriales[i]);
		}
	}
}
