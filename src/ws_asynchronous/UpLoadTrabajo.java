/************************************************************************************************************
 * Fecha: 	07-07-2014
 * Version:	1.1
 * Se crea el servicio con el fin de poder subir un acta en linea y de esta forma la analista pueda evaluarla
************************************************************************************************************/

package ws_asynchronous;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import miscelanea.Archivos;
import miscelanea.SQLite;

//import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class UpLoadTrabajo extends AsyncTask<ArrayList<String>, Integer, Integer>{ //doInBakGround, Progress, onPostExecute
	
	/**Instancias a clases**/
	private Archivos 	ArchConnectServer;
	private SQLite 		UploadSQL;
	private Archivos    ArchUpLoadWS;
	
	/**Variables Locales**/
	private Context 			ConnectServerContext;
	private String 				DirectorioConexionServer;
	private String 				StringSQL;
	private String       		FolderAplicacion="Descarga";
	private ArrayList<String> 	InformacionCarga = new ArrayList<String>(); //arrary donde se esta colocando toda la informacion	
	//private String LineasSQL[];
	private ContentValues				_tempRegistro 	= new ContentValues();
	private ArrayList<ContentValues>	_tempTabla		= new ArrayList<ContentValues>();
	private ArrayList<ContentValues>	_tempTabla1		= new ArrayList<ContentValues>();
	private ContentValues				_tempRegistro1	= new ContentValues();
	private ContentValues				_tempRegistro2	= new ContentValues();
	private ArrayList<String>           _Ordenes        = new ArrayList<String>();
	ArrayList<String> _OrdenesS = new ArrayList<String>();
	
	/** Variables para manejar los archivos**/
	private ContentValues			InformacionArchivos = new ContentValues();
	//private ArrayList<ContentValues>RegistroArchivos 	= new ArrayList<ContentValues>();
	
	//private File[]				ListaDirectorios;
	private File[]				ListaArchivos;
	//private ArrayList<String>	ListaFolderAplicacionActas = new ArrayList<String>();
	private String 				Respuesta;
	
	/**Variables para el consumo del web service a traves de nusoap**/
	private String 	_ip_servidor	= "";
	private String  _puerto			= "";
	private String  _modulo 		= "";
	private String 	_web_service 	= "";
	private String _NPDA;
	
	private String URL="";				//= "http://190.93.133.87:8080/ControlEnergia/WS/AndroidWS.php?wsdl";
	private String NAMESPACE="";		//= "http://190.93.133.87:8080/ControlEnergia/WS";
	
	//Variables con la informacion del web service
	private static final String METHOD_NAME	= "UpLoadTrabajo";
	private static final String SOAP_ACTION	= "UpLoadTrabajo";
	SoapPrimitive 	response = null;
	ProgressDialog 	_pDialog;
	
		
	//Contructor de la clase
	public UpLoadTrabajo(Context context, String Directorio){
		this.ConnectServerContext 		= context;
		this.DirectorioConexionServer 	= Directorio;		
		this.UploadSQL		= new SQLite(this.ConnectServerContext, this.DirectorioConexionServer);
		this.ArchConnectServer	= new Archivos(this.ConnectServerContext,this.DirectorioConexionServer,10);
		ArchUpLoadWS = new Archivos(this.ConnectServerContext,this.DirectorioConexionServer,10);
	}

   
	//Operaciones antes de realizar la conexion con el servidor
	public void onPreExecute(){
		
		/***Codigo para el cargue desde la base de datos de la ip y puerto de conexion para el web service***/
		this._ip_servidor 	= this.UploadSQL.StrSelectShieldWhere("db_parametros","valor", "item='servidor'");
		this._puerto 		= this.UploadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='puerto'"); 
		this._modulo		= this.UploadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='modulo'");
		this._web_service	= this.UploadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='web_service'");		
		this.URL 			= _ip_servidor+":"+_puerto+"/"+_modulo+"/"+_web_service;
		this.NAMESPACE 		= _ip_servidor+":"+_puerto+"/"+_modulo;
		
		Toast.makeText(this.ConnectServerContext,"Preparando informacion...", Toast.LENGTH_SHORT).show();	
		
		Toast.makeText(this.ConnectServerContext,"Iniciando conexion con el servidor, por favor espere...", Toast.LENGTH_SHORT).show();	
		_pDialog = new ProgressDialog(this.ConnectServerContext);
        _pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _pDialog.setMessage("Ejecutando operaciones...");
        _pDialog.setCancelable(false);
        _pDialog.setProgress(0);
        _pDialog.setMax(100);
        _pDialog.show(); 
	}
	
	//Conexion con el servidor en busca de actualizaciones de trabajo programado
	@Override
	protected Integer doInBackground(ArrayList<String>... params) {
		int _retorno = 0;
		_Ordenes=params[0];
		
//obtener numero de PDA
		
		this._tempTabla1	= this.UploadSQL.SelectData("amd_param_sistema", "valor","codigo='NPDA'");
		this._tempRegistro1 = this._tempTabla1.get(0);
		_NPDA = this._tempRegistro1.getAsString("valor");
		
/*DESCARGAR SGD_ORDENES_TRABAJO_EXP*/
		
	//actualizar la tabla amd_ordenes_trabajo con hora ini y hora fin.	
		
	//Se consulta la fecha min y max de las ordenes seleccionadas	y se actualiza en amd_ordenes_trabajo
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			this._tempTabla	= this.UploadSQL.SelectData("upload_trabajos_exp", "min(fecha_ins), max(fecha_ins)","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
				this._tempRegistro2.put("hora_ini", this._tempRegistro.getAsString("min(fecha_ins)"));
				this._tempRegistro2.put("hora_fin", this._tempRegistro.getAsString("max(fecha_ins)"));
				this.UploadSQL.UpdateRegistro("amd_ordenes_trabajo", this._tempRegistro2, "id_orden='"+orden+"'");
			 }		    
		}
		

		
//genera el archivo para actualizar sgd_ordenes_trabajos_exp

		this.InformacionCarga.clear();		
		String estado="C";
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			this._tempTabla	= this.UploadSQL.SelectData("upload_sgd_ordenes_exp", "id_orden,fecha_revision,hora_ini,hora_fin,observacion_pad,usuario","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
					  this.InformacionCarga.add(this._tempRegistro.getAsString("id_orden")+","+this._tempRegistro.getAsString("fecha_revision")+","+this._tempRegistro.getAsString("hora_ini")+","+this._tempRegistro.getAsString("hora_fin")+","+this._tempRegistro.getAsString("observacion_pad")+","+this._tempRegistro.getAsString("usuario")+","+estado+"\r\n");
			}
		}
		
		String listSgd = "";
		for (String s : InformacionCarga){
			listSgd += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_ORDENES_TRABAJO_EXP",listSgd);
		
		//genera el archivo insertar en sgd_ordenes_trabajo_pda
		
		this.InformacionCarga.clear();		
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			this._tempTabla	= this.UploadSQL.SelectData("upload_sgd_ordenes_pda", "id_orden,cuenta,fecha_atencion,hora_ini,hora_fin,usuario,observacion_pad,bodega,solicitud,clase_solicitud,tipo_solicitud,dependencia,tipo_accion,dependencia_asignada,consecutivo_accion,propietario,municipio,ubicacion,clase_servicio,estrato,id_nodo,fecha_ven,direccion,observacion_trabajo","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
					  this.InformacionCarga.add(this._tempRegistro.getAsString("id_orden")+","+this._tempRegistro.getAsString("cuenta")+","+this._tempRegistro.getAsString("fecha_atencion")+","+this._tempRegistro.getAsString("hora_ini")+","+this._tempRegistro.getAsString("hora_fin")+","+this._tempRegistro.getAsString("usuario")+","+this._tempRegistro.getAsString("observacion_pad")+","+this._tempRegistro.getAsString("bodega")+","+this._tempRegistro.getAsString("solicitud")+","
					  		+ ""+this._tempRegistro.getAsString("clase_solicitud")+","+this._tempRegistro.getAsString("tipo_solicitud")+","+this._tempRegistro.getAsString("dependencia")+","+this._tempRegistro.getAsString("tipo_accion")+","+this._tempRegistro.getAsString("dependencia_asignada")+","+this._tempRegistro.getAsString("consecutivo_accion")+","+this._tempRegistro.getAsString("propietario")+","+this._tempRegistro.getAsString("municipio")+","+this._tempRegistro.getAsString("ubicacion")+","
					  				+ ""+this._tempRegistro.getAsString("clase_servicio")+","+this._tempRegistro.getAsString("estrato")+","+this._tempRegistro.getAsString("id_nodo")+","+this._tempRegistro.getAsString("fecha_ven")+","+this._tempRegistro.getAsString("direccion")+","+this._tempRegistro.getAsString("observacion_trabajo")+"\r\n");
			}
		}
		String listSgd1 = "";
		for (String s : InformacionCarga){
			listSgd1 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_ORDENES_TRABAJO_PDA",listSgd1);
		
		/*GENERAR SGD_aCTAS_PDA*/
		this.InformacionCarga.clear();
		StringSQL="INICIO_ARCHIVO" + "&ENT&";
		this.InformacionCarga.add(StringSQL);
		
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			this._tempTabla	= this.UploadSQL.SelectData("upload_nodos_exp", "id_acta, id_orden,id_revision, codigo_trabajo, nombre_enterado, cedula_enterado, evento, tipo_enterado, fecha_revision, login, fecha_ins, cedula_testigo, nombre_testigo","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
				this.InformacionCarga.add("INSERT INTO SGD_ACTAS_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_acta")+"','"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("id_revision")+"','"
				+this._tempRegistro.getAsString("codigo_trabajo")+"','"+this._tempRegistro.getAsString("cedula_enterado")+"','"+this._tempRegistro.getAsString("nombre_enterado")+"','"
				+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("tipo_enterado")+"','"+this._tempRegistro.getAsString("fecha_revision")+"','"
				+this._tempRegistro.getAsString("login")+"','"+this._tempRegistro.getAsString("fecha_ins")+"','"+this._tempRegistro.getAsString("cedula_testigo")+"','"
				+this._tempRegistro.getAsString("nombre_testigo")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");  
			}
	    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString = "";
		for (String s : InformacionCarga){
		    listString += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_ACTAS_PDA",listString);
		
/*GENERAR SGD_ACOMETIDAS_PDA*/
		
		this.InformacionCarga.clear();
		StringSQL="INICIO_ARCHIVO" + "&ENT&";
		this.InformacionCarga.add(StringSQL);
		
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
		String orden=	this._Ordenes.get(j).toString();
		this._tempTabla	= this.UploadSQL.SelectData("upload_acometidas_pda", "id_orden, tipo_ingreso,id_acometida,longitud,login,fecha_ins,fase,clase","id_orden='"+orden+"'");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
				  this.InformacionCarga.add("INSERT INTO SGD_ACOMETIDAS_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("tipo_ingreso")+"','"+this._tempRegistro.getAsString("id_acometida")+"','"
							+this._tempRegistro.getAsString("longitud")+"','"+this._tempRegistro.getAsString("login")+"','"+this._tempRegistro.getAsString("fecha_ins")+"','"
							+this._tempRegistro.getAsString("fase")+"','"+this._tempRegistro.getAsString("clase")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
			  
		 }
	    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString1 = "";
		for (String s : InformacionCarga)
		{
		    listString1 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_ACOMETIDAS_PDA",listString1);
       
/*GENERAR SGD_DETALLE_CENSO_CARGA_PDA*/
		
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
		String orden=	this._Ordenes.get(j).toString();
		this._tempTabla	= this.UploadSQL.SelectData("upload_detalle_censo_carga_pda", "id_elemento, capacidad, cantidad, login, fecha_ins, tipo_carga, id_orden","id_orden='"+orden+"'");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
				  this.InformacionCarga.add("INSERT INTO SGD_DETALLE_CENSO_CARGA_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_elemento")+"','"+this._tempRegistro.getAsString("capacidad")+"','"+this._tempRegistro.getAsString("cantidad")+"','"
							+this._tempRegistro.getAsString("login")+"','"+this._tempRegistro.getAsString("fecha_ins")+"','"+this._tempRegistro.getAsString("tipo_carga")+"','"+this._tempRegistro.getAsString("id_orden")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
			  
		 }
	    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString2 = "";
		for (String s : InformacionCarga){
		    listString2 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_DETALLE_CENSO_CARGA_PDA",listString2);

/*GENERAR SGD_PCTERROR_PDA*/
		
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
		String orden=	this._Ordenes.get(j).toString();
		this._tempTabla	= this.UploadSQL.SelectData("upload_pct_error_pda", "id_orden, tipo_carga, voltaje, corriente, tiempo, vueltas, total, login, fecha_ins, rev, fase","id_orden='"+orden+"'");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
				  this.InformacionCarga.add("INSERT INTO SGD_PCTERROR_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("tipo_carga")+"','"+this._tempRegistro.getAsString("voltaje")+"','"
							+this._tempRegistro.getAsString("corriente")+"','"+this._tempRegistro.getAsString("tiempo")+"','"+this._tempRegistro.getAsString("vueltas")+"','"+this._tempRegistro.getAsString("total")+"','"+this._tempRegistro.getAsString("login")+"','"
						    +this._tempRegistro.getAsString("fecha_ins")+"','"+this._tempRegistro.getAsString("rev")+"','"+_NPDA+"','1','@','"+this._tempRegistro.getAsString("fase")+"')"+";"+"&ENT&");
			  
		 }
	    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString3 = "";
		for (String s : InformacionCarga)
		{
		    listString3 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_PCTERROR_PDA",listString3);

/*GENERAR SGD_SELLOS_PDA*/
		
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
		String orden=	this._Ordenes.get(j).toString();
		this._tempTabla	= this.UploadSQL.SelectData("upload_sellos_pda", "id_orden, estado, tipo, numero, color, irregularidad, ubicacion,login, fecha_ins","id_orden='"+orden+"'");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
				  this.InformacionCarga.add("INSERT INTO SGD_SELLOS_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("estado")+"','"+this._tempRegistro.getAsString("tipo")+"','"
							+this._tempRegistro.getAsString("numero")+"','"+this._tempRegistro.getAsString("color")+"','"+this._tempRegistro.getAsString("irregularidad")+"','"+this._tempRegistro.getAsString("ubicacion")+"','"+this._tempRegistro.getAsString("login")+"','"
						    +this._tempRegistro.getAsString("fecha_ins")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
			  
		 }
	    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString4 = "";
		for (String s : InformacionCarga)
		{
		    listString4 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_SELLOS_PDA",listString4);

/*GENERAR SGD_IRREGULARIDADES_PDA*/		
		
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
		String orden=	this._Ordenes.get(j).toString();
		this._tempTabla	= this.UploadSQL.SelectData("upload_irregularidades_pda", " id_anomalia,id_orden, id_irregularidad,login, fecha_ins","id_orden='"+orden+"'");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
				  this.InformacionCarga.add("INSERT INTO SGD_IRREGULARIDADES_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_anomalia")+"','"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("id_irregularidad")+"','"
							+this._tempRegistro.getAsString("login")+"','"+this._tempRegistro.getAsString("fecha_ins")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
			  
		 }
	    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString5 = "";
		for (String s : InformacionCarga)
		{
		    listString5 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_IRREGULARIDADES_PDA",listString5);
		
		/*GENERAR SGD_DIAGRAMAS_PDA*/
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//No Se realiza ninguna consulta ya que la tabla SGD_DIAGRAMAS_PDA , amd_diagramas no se utiliza 
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString6 = "";
		for (String s : InformacionCarga)
		{
		    listString6 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_DIAGRAMAS_PDA",listString6);
		
		
		/*GENERAR SGD_MVTO_CONTADORES_PDA*/
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
		String orden=	this._Ordenes.get(j).toString();
		this._tempTabla	= this.UploadSQL.SelectData("upload_mvto_contadores_pda", "id_orden,tipo, marca, serie, lectura, cuenta, login, fecha_ins, cobro","id_orden='"+orden+"'");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
				  this.InformacionCarga.add("INSERT INTO SGD_MVTO_CONTADORES_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("tipo")+"','"+this._tempRegistro.getAsString("marca")+"','"
							+this._tempRegistro.getAsString("serie")+"','"+this._tempRegistro.getAsString("lectura")+"','"+this._tempRegistro.getAsString("cuenta")+"','"+this._tempRegistro.getAsString("login")+"','"
						   +this._tempRegistro.getAsString("fecha_ins")+"','"+this._tempRegistro.getAsString("cobro")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
			  
		 }
	    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString7 = "";
		for (String s : InformacionCarga){
		    listString7 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_MVTO_CONTADORES_PDA",listString7);

/*GENERAR SGD_MATERIALES_TRABAJO_PDA*/
		
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
		String orden=	this._Ordenes.get(j).toString();
		this._tempTabla	= this.UploadSQL.SelectData("upload_materiales_trabajo_pda", "id_orden, id_trabajo, id_material, cantidad, cuotas, automatico, usuario_ins, fecha_ins","id_orden='"+orden+"'");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
				  this.InformacionCarga.add("INSERT INTO SGD_MATERIALES_TRABAJO_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("id_trabajo")+"','"+this._tempRegistro.getAsString("id_material")+"','"
							+this._tempRegistro.getAsString("cantidad")+"','"+this._tempRegistro.getAsString("cuotas")+"','"+this._tempRegistro.getAsString("automatico")+"','"+this._tempRegistro.getAsString("usuario_ins")+"','"
						   +this._tempRegistro.getAsString("fecha_ins")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
			  
		 }
	    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString8 = "";
		for (String s : InformacionCarga)
		{
		    listString8 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_MATERIALES_TRABAJO_PDA",listString8);
		
/*GENERAR SGD_VISITAS_PDA*/
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//No Se realiza ninguna consulta ya que la tabla SGD_VISITAS_PDA , amd_visitas no se utiliza 
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString9 = "";
		for (String s : InformacionCarga)
		{
		    listString9 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_VISITAS_PDA",listString9);
		
/*GENERAR SGD_SERVICIO_NUEVO_PDA*/
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//se consulta la vista con las ordenes seleccionadas 
				for(int j=0; j<this._Ordenes.size();j++){
				String orden=	this._Ordenes.get(j).toString();
				this._tempTabla	= this.UploadSQL.SelectData("upload_servicio_nuevo_pda", "id_orden, cuenta, cuenta_vecina1, cuenta_vecina2, nodo_transformador,nodo_secundario,doc1,doc2,doc3,doc4,doc5,doc6,doc7","id_orden='"+orden+"'");
				for(int i=0; i<this._tempTabla.size();i++){
					this._tempRegistro = this._tempTabla.get(i);
						  this.InformacionCarga.add("INSERT INTO SGD_SERVICIO_NUEVO_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("cuenta")+"','"+this._tempRegistro.getAsString("cuenta_vecina1")+"','"
									+this._tempRegistro.getAsString("cuenta_vecina2")+"','"+this._tempRegistro.getAsString("nodo_transformador")+"','"+this._tempRegistro.getAsString("nodo_secundario")+"','"+this._tempRegistro.getAsString("doc1")+"','"
								    +this._tempRegistro.getAsString("doc2")+"','"+this._tempRegistro.getAsString("doc3")+"','"+this._tempRegistro.getAsString("doc4")+"','"+this._tempRegistro.getAsString("doc5")+"','"+this._tempRegistro.getAsString("doc6")+"','"
									+this._tempRegistro.getAsString("doc7")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
					  
				 }
			    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString10 = "";
		for (String s : InformacionCarga)
		{
		    listString10 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_SERVICIO_NUEVO_PDA",listString10);
		
/*GENERAR SGD_MEDIDOR_ENCONTRADO_PDA*/
		
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//se consulta la vista con las ordenes seleccionadas 
				for(int j=0; j<this._Ordenes.size();j++){
				String orden=	this._Ordenes.get(j).toString();
				this._tempTabla	= this.UploadSQL.SelectData("upload_medidor_encontrado_pda", "id_orden, marca, serie, lectura, lectura_2, lectura_3, tipo","id_orden='"+orden+"'");
				for(int i=0; i<this._tempTabla.size();i++){
					this._tempRegistro = this._tempTabla.get(i);
						  this.InformacionCarga.add("INSERT INTO SGD_MEDIDOR_ENCONTRADO_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("marca")+"','"+this._tempRegistro.getAsString("serie")+"','"
									+this._tempRegistro.getAsString("lectura")+"','"+this._tempRegistro.getAsString("lectura_2")+"','"+this._tempRegistro.getAsString("lectura_3")+"','"+this._tempRegistro.getAsString("tipo")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
					  
				 }
			    }
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString11 = "";
		for (String s : InformacionCarga){
		    listString11 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_MEDIDOR_ENCONTRADO_PDA",listString11);

		/*GENERAR ITEM_PAGO*/
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			this._tempTabla	= this.UploadSQL.SelectData("upload_item_pago", "id_orden, items","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
				this.InformacionCarga.add("INSERT INTO ITEM_PAGO VALUES "+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("items")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
			}
		}
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString12 = "";
		for (String s : InformacionCarga){
		    listString12 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"ITEM_PAGO",listString12);
		
		/*GENERAR SGD_INCONSISTENCIA_PDA*/		
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			this._tempTabla	= this.UploadSQL.SelectData("upload_inconsistencia_pda", "id_inconsistencia,id_orden, id_nodo,valor,fecha_ins,login, cod_inconsistencia, cuenta, trabajo","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
				this.InformacionCarga.add("INSERT INTO SGD_INCONSISTENCIA_PDA (ID_INCONSISTENCIA,ID_ORDEN,NODO,VALOR,FECHA_INS,USUARIO_INS,COD_INCONSISTENCIA,CUENTA,TRABAJO,ID_PDA,ID_CONTRATO,ID_CARGA ) VALUES "
				+"('"+this._tempRegistro.getAsString("id_inconsistencia")+"','"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("id_nodo")+"','"
				+this._tempRegistro.getAsString("valor").replaceAll("\n","")+"','"+this._tempRegistro.getAsString("fecha_ins")+"','"+this._tempRegistro.getAsString("login")+"','"+this._tempRegistro.getAsString("cod_inconsistencia")+"','"
				+this._tempRegistro.getAsString("cuenta")+"','"+this._tempRegistro.getAsString("trabajo")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
			}
		}
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString13 = "";
		for (String s : InformacionCarga){
		    listString13 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_INCONSISTENCIA_PDA",listString13);
		
		/*GENERAR SGD_TRABAJOS_ORDEN_PDA*/
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		//se consulta la vista con las ordenes seleccionadas 
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			this._tempTabla	= this.UploadSQL.SelectData("upload_trabajos_orden_pda", "id_revision,id_orden, id_trabajo,cuenta,nodo,estado,login,fecha_ins,cantidad","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
				this.InformacionCarga.add("INSERT INTO SGD_TRABAJOS_ORDEN_PDA VALUES "
				+"('"+this._tempRegistro.getAsString("id_revision")+"','"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("id_trabajo")+"','"
				+this._tempRegistro.getAsString("cuenta")+"','"+this._tempRegistro.getAsString("nodo")+"','"+this._tempRegistro.getAsString("estado")+"','"+this._tempRegistro.getAsString("login")+"','"
				+this._tempRegistro.getAsString("fecha_ins")+"','"+this._tempRegistro.getAsString("cantidad")+"','"+_NPDA+"','1','@')"+";"+"&ENT&");
			}
		}
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString14 = "";
		for (String s : InformacionCarga){
		    listString14 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_TRABAJOS_ORDEN_PDA",listString14);
		
		
		/*GENERAR SGD_CENSO_CARGA_PDA*/
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");		
		double registrada;
		double directa;
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			registrada=this.UploadSQL.DoubleSelectShieldWhere("amd_censo_carga", "sum(cantidad*capacidad) as subtotal", "id_orden='"+orden+"' and tipo_carga='R'");
			directa=this.UploadSQL.DoubleSelectShieldWhere("amd_censo_carga", "sum(cantidad*capacidad) as subtotal", "id_orden='"+orden+"' and tipo_carga='D'");
			this._tempTabla	= this.UploadSQL.SelectData("upload_censo_carga_pda", "id_orden, total_censo, login, fecha_ins","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
				this.InformacionCarga.add("INSERT INTO SGD_CENSO_CARGA_PDA VALUES "
				+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("total_censo")+"','"+registrada+"','"
				+directa+"','"+this._tempRegistro.getAsString("login")+"','"+this._tempRegistro.getAsString("fecha_ins")+"','"+_NPDA+"','1','@','0')"+";"+"&ENT&");		  
			}
		}
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString15 = "";
		for (String s : InformacionCarga){
			listString15 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_CENSO_CARGA_PDA",listString15);

		/*GENERAR SGD_ELEMENTOS_PROV_PDA*/
		this.InformacionCarga.clear();
		this.InformacionCarga.add("INICIO_ARCHIVO" + "&ENT&");
		
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			this._tempTabla	= this.UploadSQL.SelectData("upload_elementos_prov_pda", "id_orden,  elemento, marca, serie, valor, id_agrupador, cuenta,proceso,estado,usuario_ins, fecha_ins, cantidad","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
				this.InformacionCarga.add("INSERT INTO SGD_ELEMENTOS_PROV_PDA (id_orden,elemento,marca,serie,valor,id_agrupador,cuenta,proceso,estado,cantidad,usuario_ins,fecha_ins,id_pda,id_contrato,id_carga,id_descarga) VALUES "
				+"('"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("elemento")+"','"+this._tempRegistro.getAsString("marca")+"','"+this._tempRegistro.getAsString("serie")+"',"
				+ "'"+this._tempRegistro.getAsString("valor")+"','"+this._tempRegistro.getAsString("id_agrupador")+"','"+this._tempRegistro.getAsString("cuenta")+"','"+this._tempRegistro.getAsString("proceso")+"','"+this._tempRegistro.getAsString("estado")+"'"
				+ ",'"+this._tempRegistro.getAsString("cantidad")+"','"+this._tempRegistro.getAsString("usuario_ins")+"','"+this._tempRegistro.getAsString("fecha_ins")+"','"+_NPDA+"','1','@','0')"+";"+"&ENT&");  
			}
		}
		
		this.InformacionCarga.add("FIN_ARCHIVO");
		String listString16 = "";
		for (String s : InformacionCarga){
			listString16 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_ELEMENTOS_PROV_PDA",listString16);

		/*GENERAR SGD_NODOS_EXP*/
		this.InformacionCarga.clear();		
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			this._tempTabla	= this.UploadSQL.SelectData("upload_nodos", "id_nodo, observacion","id_orden='"+orden+"'");
			for(int i=0; i<this._tempTabla.size();i++){
				this._tempRegistro = this._tempTabla.get(i);
				this.InformacionCarga.add(this._tempRegistro.getAsString("id_nodo")+","+this._tempRegistro.getAsString("observacion")+"\r\n");
			}
		}
		String listString17 = "";
		for (String s : InformacionCarga){
			listString17 += s;
		}
		ArchConnectServer.DoFile(this.FolderAplicacion,"SGD_NODOS_EXP",listString17);

		
		//para descargar archivos
		File carpeta=new File(this.DirectorioConexionServer+File.separator+this.FolderAplicacion);
		this.ListaArchivos = new File(carpeta.toString()).listFiles();  
		if(this.ListaArchivos.length == 0){
			ArchUpLoadWS.DeleteFile(carpeta.toString());	
		}else{
			this.InformacionArchivos.clear();
			this.InformacionArchivos.put("idPda", this._NPDA);
			for(int j=0;j<this.ListaArchivos.length;j++){
				//this.InformacionArchivos.put("Archivo"+j,this.ListaArchivos[j].getName());
				this.InformacionArchivos.put(this.ListaArchivos[j].getName(),this.ListaArchivos[j].toString());
   			}	    				
   		}	    			
   		
		//  this.InformacionArchivos = this.RegistroArchivos.get(i);
		try{
			SoapObject so=new SoapObject(NAMESPACE, METHOD_NAME);
			/*so.addProperty("idPda",this.InformacionArchivos.getAsString("idPda"));
			so.addProperty("ITEM_PAGO",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("ITEM_PAGO")));
			so.addProperty("SGD_ACOMETIDAS_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ACOMETIDAS_PDA")));
			so.addProperty("SGD_ACTAS_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ACTAS_PDA")));
			so.addProperty("SGD_CENSO_CARGA_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_CENSO_CARGA_PDA")));
			so.addProperty("SGD_DETALLE_CENSO_CARGA_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_DETALLE_CENSO_CARGA_PDA")));
			so.addProperty("SGD_DIAGRAMAS_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_DIAGRAMAS_PDA")));
			so.addProperty("SGD_ELEMENTOS_PROV_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ELEMENTOS_PROV_PDA")));
			so.addProperty("SGD_INCONSISTENCIA_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_INCONSISTENCIA_PDA")));
			so.addProperty("SGD_IRREGULARIDADES_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_IRREGULARIDADES_PDA")));
			so.addProperty("SGD_MATERIALES_TRABAJO_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_MATERIALES_TRABAJO_PDA")));
			so.addProperty("SGD_MEDIDOR_ENCONTRADO_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_MEDIDOR_ENCONTRADO_PDA")));
			so.addProperty("SGD_MVTO_CONTADORES_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_MVTO_CONTADORES_PDA")));
			so.addProperty("SGD_PCTERROR_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_PCTERROR_PDA")));
			so.addProperty("SGD_SELLOS_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_SELLOS_PDA")));
			so.addProperty("SGD_SERVICIO_NUEVO_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_SERVICIO_NUEVO_PDA")));
			so.addProperty("SGD_TRABAJOS_ORDEN_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_TRABAJOS_ORDEN_PDA")));
			so.addProperty("SGD_VISITAS_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_VISITAS_PDA")));
			so.addProperty("SGD_NODOS_EXP",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_NODOS_EXP")));
			so.addProperty("SGD_ORDENES_TRABAJO_EXP",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ORDENES_TRABAJO_EXP")));
			so.addProperty("SGD_ORDENES_TRABAJO_PDA",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ORDENES_TRABAJO_PDA")));*/
			
			so.addProperty("pda",this.InformacionArchivos.getAsString("idPda"));
			so.addProperty("archivo1",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("ITEM_PAGO")));
			so.addProperty("archivo2",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ACOMETIDAS_PDA")));
			so.addProperty("archivo3",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ACTAS_PDA")));
			so.addProperty("archivo4",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_CENSO_CARGA_PDA")));
			so.addProperty("archivo5",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_DETALLE_CENSO_CARGA_PDA")));
			so.addProperty("archivo6",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_DIAGRAMAS_PDA")));
			so.addProperty("archivo7",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ELEMENTOS_PROV_PDA")));
			so.addProperty("archivo8",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_INCONSISTENCIA_PDA")));
			so.addProperty("archivo9",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_IRREGULARIDADES_PDA")));
			so.addProperty("archivo10",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_MATERIALES_TRABAJO_PDA")));
			so.addProperty("archivo11",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_MEDIDOR_ENCONTRADO_PDA")));
			so.addProperty("archivo12",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_MVTO_CONTADORES_PDA")));
			so.addProperty("archivo13",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_PCTERROR_PDA")));
			so.addProperty("archivo14",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_SELLOS_PDA")));
			so.addProperty("archivo15",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_SERVICIO_NUEVO_PDA")));
			so.addProperty("archivo16",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_TRABAJOS_ORDEN_PDA")));
			so.addProperty("archivo17",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_VISITAS_PDA")));
			so.addProperty("archivo18",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_NODOS_EXP")));
			so.addProperty("archivo19",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ORDENES_TRABAJO_EXP")));
			so.addProperty("archivo20",this.ArchUpLoadWS.FileToArrayBytes(this.InformacionArchivos.getAsString("SGD_ORDENES_TRABAJO_PDA")));
			
					
			SoapSerializationEnvelope sse=new SoapSerializationEnvelope(SoapEnvelope.VER11);
			new MarshalBase64().register(sse);
			sse.dotNet=true;
			sse.setOutputSoapObject(so);
			HttpTransportSE htse=new HttpTransportSE(URL);
			htse.call(SOAP_ACTION, sse);
			response=(SoapPrimitive) sse.getResponse();
			
			if(response.toString()==null) {
				this.Respuesta = "-1";
			}else if(response.toString().isEmpty()){
				this.Respuesta = "-2";
			}else if(response.toString().equals("1")){
				this.Respuesta = "1";
				_retorno = 1;
				this.ArchConnectServer.DeleteFile(this.DirectorioConexionServer+File.separator+this.FolderAplicacion);
				BorrarOrdenes();
			}
		} catch (Exception e) {
			this.Respuesta = e.toString();
		}
		return _retorno;
	}
	
	
	public void BorrarOrdenes(){
		for(int j=0; j<this._Ordenes.size();j++){
			String orden=	this._Ordenes.get(j).toString();
			String cuenta = UploadSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "cuenta", "id_orden='"+orden+"'");
			String nodo = UploadSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "id_nodo", "id_orden='"+orden+"'");
			
			this.UploadSQL.DeleteRegistro("amd_actas", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_acometida", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_borrar_orden", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_cambios_contadores", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_censo_carga", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_consumos_orden", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_datos_tranfor", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_impresiones_inf", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_inconsistencias", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_irregularidades", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_material_usuario", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_contador_cliente_orden", "cuenta='"+cuenta+"'");
			this.UploadSQL.DeleteRegistro("amd_materiales_provisionales", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_materiales_trabajo_orden", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_medidor_encontrado", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_observacion_materiales", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_ordenes_trabajo", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("param_trabajos_orden", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_nodo", "id_nodo='"+nodo+"'");
			this.UploadSQL.DeleteRegistro("amd_pct_error", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_prueba_integracion", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_pruebas", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_sellos", "id_orden='"+orden+"'");
			this.UploadSQL.DeleteRegistro("amd_servicio_nuevo", "id_orden='"+orden+"'");
		}
	}
	
	
	//Operaciones despues de finalizar la conexion con el servidor
	@Override
	protected void onPostExecute(Integer rta) {
		if(rta==1){
			Toast.makeText(this.ConnectServerContext,"Carga de trabajo finalizada.", Toast.LENGTH_LONG).show();
		}else if(rta==-1){
			Toast.makeText(this.ConnectServerContext,"Intento fallido, el servidor no ha respondido.", Toast.LENGTH_SHORT).show();
		}else if(rta==-2){
			Toast.makeText(this.ConnectServerContext,"No hay nuevas ordenes pendientes para cargar.", Toast.LENGTH_SHORT).show();	
		}else{
			Toast.makeText(this.ConnectServerContext,"Error desconocido.", Toast.LENGTH_SHORT).show();
		}
		_pDialog.dismiss();
	}	
	
	
	@Override
    protected void onProgressUpdate(Integer... values) {
        int progreso = values[0].intValue();
        _pDialog.setProgress(progreso);
    }
}


