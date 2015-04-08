package ws_asynchronous;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import miscelanea.SQLite;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;


public class  UpLoadInformacionOrdenActual extends AsyncTask<String, Integer, String>{
	private Context 			ActaTrabajoContext;
	private SQLite				ActaTrabajoSQL;
	private String 				ActaTrabajoFolder;
	
	private ContentValues		_tempRegistro	= new ContentValues();
	private String 				Respuesta = "";
	
	private String 	_ip_servidor	= "";
	private String  _puerto			= "";
	private String  _modulo 		= "";
	private String 	_web_service 	= "";
	
	private String URL; 			
	private String NAMESPACE; 		
	private static final String METHOD_NAME	= "RegOrdenTrabajo";	
	private static final String SOAP_ACTION	= "RegOrdenTrabajo";
	SoapPrimitive response = null;
	
	ProgressDialog 	_pDialog;

	
	public  UpLoadInformacionOrdenActual(Context context, String Directorio){
		this.ActaTrabajoContext 	= context;
		this.ActaTrabajoFolder	= Directorio;
		
		this.ActaTrabajoSQL 	= new SQLite(this.ActaTrabajoContext, this.ActaTrabajoFolder);
		
		this._ip_servidor 	= this.ActaTrabajoSQL.StrSelectShieldWhere("db_parametros","valor", "item='servidor'");
		this._puerto 		= this.ActaTrabajoSQL.StrSelectShieldWhere("db_parametros", "valor", "item='puerto'"); 
		this._modulo		= this.ActaTrabajoSQL.StrSelectShieldWhere("db_parametros", "valor", "item='modulo'");
		this._web_service	= this.ActaTrabajoSQL.StrSelectShieldWhere("db_parametros", "valor", "item='web_service'");		
		
		this.URL 			= _ip_servidor+":"+_puerto+"/"+_modulo+"/"+_web_service;
		this.NAMESPACE 		= _ip_servidor+":"+_puerto+"/"+_modulo;
	}
	
   	 
	protected void onPreExecute(){
		this._tempRegistro.clear();
		this._tempRegistro.put("orden", this.ActaTrabajoSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "id_orden", "estado='E'"));
		this._tempRegistro.put("cuenta", this.ActaTrabajoSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "cuenta", "estado='E'"));
		this._tempRegistro.put("pda", this.ActaTrabajoSQL.IntSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'"));
		this._tempRegistro.put("tecnico", this.ActaTrabajoSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NOM_TECNICO'"));
		this._tempRegistro.put("municipio", this.ActaTrabajoSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "municipio", "estado='E'"));
		this._tempRegistro.put("ubicacion", this.ActaTrabajoSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "ubicacion", "estado='E'"));
	}

	
	@Override
	protected String doInBackground(String... params) {
		try{
			SoapObject so=new SoapObject(NAMESPACE, METHOD_NAME);
			so.addProperty("orden",this._tempRegistro.getAsString("orden"));
			so.addProperty("cuenta",this._tempRegistro.getAsString("cuenta"));
			so.addProperty("pda",this._tempRegistro.getAsInteger("pda"));
			so.addProperty("tecnico",this._tempRegistro.getAsString("tecnico"));
			so.addProperty("municipio",this._tempRegistro.getAsString("municipio"));
			so.addProperty("ubicacion",this._tempRegistro.getAsString("ubicacion"));
				
			SoapSerializationEnvelope sse=new SoapSerializationEnvelope(SoapEnvelope.VER11);
			//new MarshalBase64().register(sse);
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
		this._tempRegistro.clear();
	}
}
