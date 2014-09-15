/************************************************************************************************************
 * Fecha: 	07-07-2014
 * Version:	1.1
 * Se crea el servicio con el fin de poder subir un acta en linea y de esta forma la analista pueda evaluarla
************************************************************************************************************/

package ws_asynchronous;

import java.io.IOException;
import java.util.ArrayList;

import miscelanea.Archivos;
import miscelanea.SQLite;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class UpLoadTrabajo extends AsyncTask<String, Integer, Integer>{ //doInBakGround, Progress, onPostExecute
	
	/**Instancias a clases**/
	private Archivos 	ArchConnectServer;
	private SQLite 		UploadSQL;
	
	/**Variables Locales**/
	private Context 			ConnectServerContext;
	private String 				DirectorioConexionServer;
	private ArrayList<String> 	InformacionCarga = new ArrayList<String>();	
	private String LineasSQL[];
	private ContentValues				_tempRegistro 	= new ContentValues();
	private ArrayList<ContentValues>	_tempTabla		= new ArrayList<ContentValues>();
	
	/**Variables para el consumo del web service a traves de nusoap**/
	private String 	_ip_servidor	= "";
	private String  _puerto			= "";
	private String  _modulo 		= "";
	private String 	_web_service 	= "";
	
	private String URL;				//= "http://190.93.133.87:8080/ControlEnergia/WS/AndroidWS.php?wsdl";
	private String NAMESPACE;		//= "http://190.93.133.87:8080/ControlEnergia/WS";
	
	//Variables con la informacion del web service
	private static final String METHOD_NAME	= "ActaImpresa";
	private static final String SOAP_ACTION	= "ActaImpresa";
	SoapPrimitive 	response = null;
	ProgressDialog 	_pDialog;
	
		
	//Contructor de la clase
	public UpLoadTrabajo(Context context, String Directorio){
		this.ConnectServerContext 		= context;
		this.DirectorioConexionServer 	= Directorio;		
		this.UploadSQL		= new SQLite(this.ConnectServerContext, this.DirectorioConexionServer);
		this.ArchConnectServer	= new Archivos(this.ConnectServerContext,this.DirectorioConexionServer,10);
	}


	//Operaciones antes de realizar la conexion con el servidor
	protected void onPreExecute(){
		/***Codigo para el cargue desde la base de datos de la ip y puerto de conexion para el web service***/
		this._ip_servidor 	= this.UploadSQL.StrSelectShieldWhere("db_parametros","valor", "item='servidor'");
		this._puerto 		= this.UploadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='puerto'"); 
		this._modulo		= this.UploadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='modulo'");
		this._web_service	= this.UploadSQL.StrSelectShieldWhere("db_parametros", "valor", "item='web_service'");		
		this.URL 			= _ip_servidor+":"+_puerto+"/"+_modulo+"/"+_web_service;
		this.NAMESPACE 		= _ip_servidor+":"+_puerto+"/"+_modulo;
		
		Toast.makeText(this.ConnectServerContext,"Preparando informacion...", Toast.LENGTH_SHORT).show();	
		this.InformacionCarga.clear();
		
		/**************************************************************************************************************************************************************************************
		 ************************************INICIO DE CONSULTA Y CREACION DE ARCHIVO DE CARGA DE INFORMACION SEGUN LAS TABLAS SGD DEL SERVIDOR************************************************
		 *************************************************************************************************************************************************************************************/
		
		/******************************************************************************Tabla SGD_ORDENES_TRABAJO_EXP**************************************************************************/
		//Primero se crea un registro por orden en la tabla borrar_orden cuando la orden es negativa(autogestion,servicio nuevo) y estado = 'PC' o cuando la orden esta en traslado
		
		//Segundo se consulta la informacion basica de la orden para actualizar el el SIGEDATA
		this.InformacionCarga.add("CMD|SGD_ORDENES_TRABAJO_EXP");
		this._tempTabla = this.UploadSQL.SelectData("amd_ordenes_trabajo","id_orden, fecha_atencion, hora_ini, hora_fin, observacion_pad, usuario", "estado='T'");		
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("fecha_atencion")+"|"+this._tempRegistro.getAsString("hora_ini")+"|"+this._tempRegistro.getAsString("hora_fin")+"|"+this._tempRegistro.getAsString("observacion_pda")+"|"+this._tempRegistro.getAsString("usuario"));
		}
		
		//Tercero se consulta toda la informacion de las autogestiones y servicios nuevos cuyo estado sea 'PC'
		this.InformacionCarga.add("CMD|SGD_ORDENES_TRABAJO_PDA");
		this._tempTabla = this.UploadSQL.SelectData("amd_ordenes_trabajo", "id_orden,cuenta,fecha_atencion,hora_ini,hora_fin,usuario,observacion_pad,bodega,solicitud,clase_solicitud,tipo_solicitud,dependencia,tipo_accion,dependencia_asignada,consecutivo_accion,propietario,municipio,ubicacion,clase_servicio,estrato,id_nodo,fecha_ven,direccion,observacion_trabajo", "cast(id_orden AS INTEGER) < 0 AND estado ='PC'");
		
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("cuenta")+"|"+this._tempRegistro.getAsString("fecha_atencion")+"|"
											+this._tempRegistro.getAsString("hora_ini")+"|"+this._tempRegistro.getAsString("hora_fin")+"|"+this._tempRegistro.getAsString("usuario")+"|"
											+this._tempRegistro.getAsString("observacion_pad")+"|"+this._tempRegistro.getAsString("bodega")+"|"+this._tempRegistro.getAsString("solicitud")+"|"
											+this._tempRegistro.getAsString("clase_solicitud")+"|"+this._tempRegistro.getAsString("tipo_solicitud")+"|"+this._tempRegistro.getAsString("dependencia")+"|"
											+this._tempRegistro.getAsString("tipo_accion")+"|"+this._tempRegistro.getAsString("dependencia_asignada")+"|"+this._tempRegistro.getAsString("consecutivo_accion")+"|"
											+this._tempRegistro.getAsString("propietario")+"|"+this._tempRegistro.getAsString("municipio")+"|"+this._tempRegistro.getAsString("ubicacion")+"|"
											+this._tempRegistro.getAsString("clase_servicio")+"|"+this._tempRegistro.getAsString("estrato")+"|"+this._tempRegistro.getAsString("id_nodo")+"|"
											+this._tempRegistro.getAsString("fecha_ven")+"|"+this._tempRegistro.getAsString("direccion")+"|"+this._tempRegistro.getAsString("observacion_trabajo"));
		}	
		
		
		/*********************************************************************************Tabla SGD_TRABAJOS_ORDEN_PDA************************************************************************/
		//Primero se consulta el id_orden de la tabla amd_ordenes_trabajo que tengan el estado 'PC'
		//Con el resultado de la consulta anterior se realiza una consulta en la tabla amd_param_trabajos_orden, condicion id_orden = al id_orden de la consulta anterior
		
		this.InformacionCarga.add("CMD|SGD_TRABAJOS_ORDEN_PDA");
		this._tempTabla = this.UploadSQL.SelectData("upload_trabajos_orden_pda","id_orden, id_revision, id_trabajo, cuenta, nodo, estado, fecha_ins, usuario_ins, cantidad" , "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_revision")+"|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("id_trabajo")+"|"	
											+this._tempRegistro.getAsString("cuenta")+"|"+this._tempRegistro.getAsString("nodo")+"|"+this._tempRegistro.getAsString("estado")+"|"
											+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins")+"|"+this._tempRegistro.getAsString("cantidad").replace(",","."));
		}
		
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/****************************************************************************************Tabla SGD_NODOS_EXP**************************************************************************/
		//Primero se consulta el id_nodo y la observacion de la tabla amd_nodo cuyo estado sea 'PC'
		this.InformacionCarga.add("CMD|SGD_NODOS_EXP");	
		this._tempTabla = this.UploadSQL.SelectData("amd_nodo", "id_nodo,observacion", "estado = 'PC'");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_nodo")+"|"+this._tempRegistro.getAsString("observacion"));
		}
		
		
		/****************************************************************************************Tabla SGD_ACTAS_PDA**************************************************************************/
		//Primero se borra de SGD_ACTAS_PDA los registros que coincidan en idCarga, idContrato, e idPDA
		//Segundo se consultan las ordenes que estan en estado 'PC' y que coincida el id_orden en amd_acta
		this.InformacionCarga.add("CMD|SGD_ACTAS_PDA");
		this._tempTabla	= this.UploadSQL.SelectData("upload_nodos_exp", "id_orden,id_acta,id_revision,codigo_trabajo,nombre_enterado,cedula_enterado,evento,tipo_enterado,fecha_revision,usuario_ins,fecha_ins,cedula_testigo,nombre_testigo,estado","id_orden IS NOT NULL");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_acta")+"|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("id_revision")+"|"
											+this._tempRegistro.getAsString("codigo_trabajo")+"|"+this._tempRegistro.getAsString("nombre_enterado")+"|"+this._tempRegistro.getAsString("cedula_enterado")+"|"
											+this._tempRegistro.getAsString("evento")+"|"+this._tempRegistro.getAsString("tipo_enterado")+"|"+this._tempRegistro.getAsString("fecha_revision")+"|"
											+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins")+"|"+this._tempRegistro.getAsString("cedula_testigo")+"|"
											+this._tempRegistro.getAsString("nombre_testigo")+"|"+this._tempRegistro.getAsString("estado"));
		}
		
		
		/***********************************************************************************Tabla SGD_IRREGULARIDADES_PDA********************************************************************/
		//Primero se consultan las ordenes que estan en estado 'PC' y que coincida el id_orden en amd_irregularidades
		this.InformacionCarga.add("CMD|SGD_IRREGULARIDADES_PDA");	
		this._tempTabla = this.UploadSQL.SelectData("upload_irregularidades_pda", "id_orden,id_anomalia,id_irregularidad,usuario_ins,fecha_ins", "id_orden IS NOT NULL");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("id_anomalia")+"|"+this._tempRegistro.getAsString("id_irregularidad")+"|"
											+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/*********************************************************************************Tabla SGD_MATERIALES_TRABAJO_PDA******************************************************************/
		//Primero se borra de SGD_MATERIALES_TRABAJO_PDA los registros que coincidan en idCarga, idContrato, e idPDA		
		this.InformacionCarga.add("CMD|SGD_MATERIALES_TRABAJO_PDA");	
		this._tempTabla = this.UploadSQL.SelectData("upload_materiales_trabajo_pda", "id_orden,id_trabajo,id_material,cantidad,cuotas,automatico,usuario_ins,fecha_ins", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("id_trabajo")+"|"+this._tempRegistro.getAsString("id_material")+"|"
											+this._tempRegistro.getAsString("cantidad")+"|"+this._tempRegistro.getAsString("cuotas")+"|"+this._tempRegistro.getAsString("automatico")+"|"
											+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/************************************************************************************Tabla SGD_CENSO_CARGA_PDA**********************************************************************/
		//Primero se borra de SGD_CENSO_CARGA_PDA los registros que coincidan en idCarga, idContrato, e idPDA
		this.InformacionCarga.add("CMD|SGD_CENSO_CARGA_PDA");	
		this._tempTabla = this.UploadSQL.SelectData("upload_censo_carga_pda","id_orden,id_elemento,capacidad,cantidad,usuario_ins,fecha_ins,tipo_carga","id_orden IS NOT NULL");
		for(int i=0; i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("total_censo")+"|"+this._tempRegistro.getAsString("carga_registrada")+"|"
											+this._tempRegistro.getAsString("carga_directa")+"|"+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins")+"|"
											+this._tempRegistro.getAsString("carga_ndefinida"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/************************************************************************************Tabla SGD_SELLOS_PDA**********************************************************************/
		//Primero se borra de SGD_SELLOS_PDA los registros que coincidan en idCarga, idContrato, e idPDA		
		this.InformacionCarga.add("CMD|SGD_SELLOS_PDA");	
		this._tempTabla = this.UploadSQL.SelectData("upload_sellos_pda", "id_orden,estado,tipo,numero,color,irregularidad,ubicacion,usuario_ins,fecha_ins", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("estado")+"|"+this._tempRegistro.getAsString("tipo")+"|"
											+this._tempRegistro.getAsString("numero")+"|"+this._tempRegistro.getAsString("color")+"|"+this._tempRegistro.getAsString("irregularidad")+"|"
											+this._tempRegistro.getAsString("ubicacion")+"|"+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/************************************************************************************Tabla SGD_PCTERROR_PDA**********************************************************************/
		//Primero se borra de SGD_PCTERROR_PDA los registros que coincidan en idCarga, idContrato, e idPDA	
		this.InformacionCarga.add("CMD|SGD_PCTERROR_PDA");	
		this._tempTabla = this.UploadSQL.SelectData("upload_pct_error_pda", "id_orden,tipo_carga,voltaje,corriente,tiempo,vueltas,total,usuario_ins,fecha_ins,rev,fp,fase", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("tipo_carga")+"|"+this._tempRegistro.getAsString("voltaje")+"|"
											+this._tempRegistro.getAsString("corriente")+"|"+this._tempRegistro.getAsString("tiempo")+"|"+this._tempRegistro.getAsString("vueltas")+"|"
											+this._tempRegistro.getAsString("total")+"|"+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins")+"|"
											+this._tempRegistro.getAsString("rev")+"|"+this._tempRegistro.getAsString("fp")+"|"+this._tempRegistro.getAsString("fase"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/************************************************************************************Tabla SGD_INCONSISTENCIA_PDA**********************************************************************/
		this.InformacionCarga.add("CMD|SGD_INCONSISTENCIA_PDA");	
		this._tempTabla = this.UploadSQL.SelectData("upload_inconsistencia_pda", "id_orden,id_inconsistencia,id_nodo,valor,fecha_ins,usuario_ins,cod_inconsistencia,cuenta,tipo,trabajo", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("id_inconsistencia")+"|"+this._tempRegistro.getAsString("id_nodo")+"|"
											+this._tempRegistro.getAsString("valor")+"|"+this._tempRegistro.getAsString("fecha_ins")+"|"+this._tempRegistro.getAsString("usuario_ins")+"|"
											+this._tempRegistro.getAsString("cod_inconsistencia")+"|"+this._tempRegistro.getAsString("cuenta")+"|"+this._tempRegistro.getAsString("tipo")+"|"
											+this._tempRegistro.getAsString("trabajo"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/************************************************************************************Tabla SGD_MVTO_CONTADORES_PDA**********************************************************************/
		//Primero se borra de SGD_MVTO_CONTADORES_PDA los registros que coincidan en idCarga, idContrato, e idPDA	
		this.InformacionCarga.add("CMD|SGD_MVTO_CONTADORES_PDA");	
		this._tempTabla = this.UploadSQL.SelectData("upload_mvto_contadores_pda", "id_orden,tipo,marca,serie,lectura,cuenta,usuario_ins,fecha_ins,cobro", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("tipo")+"|"+this._tempRegistro.getAsString("marca")+"|"
											+this._tempRegistro.getAsString("serie")+"|"+this._tempRegistro.getAsString("lectura")+"|"+this._tempRegistro.getAsString("cuenta")+"|"
											+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins")+"|"+this._tempRegistro.getAsString("cobro"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/************************************************************************************Tabla SGD_ACOMETIDAS_PDA**********************************************************************/
		//Primero se borra de SGD_ACOMETIDAS_PDA los registros que coincidan en idCarga, idContrato, e idPDA
		this.InformacionCarga.add("CMD|SGD_ACOMETIDAS_PDA");	
		this._tempTabla = this.UploadSQL.SelectData("upload_acometidas_pda", "id_orden,tipo_ingreso,id_acometida,longitud,usuario_ins,fecha_ins,fase,clase", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("tipo_ingreso")+"|"+this._tempRegistro.getAsString("id_acometida")+"|"
											+this._tempRegistro.getAsString("longitud")+"|"+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins")+"|"
											+this._tempRegistro.getAsString("fase")+"|"+this._tempRegistro.getAsString("clase"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/************************************************************************************Tabla SGD_ELEMENTOS_PROV_PDA**********************************************************************/
		this.InformacionCarga.add("CMD|SGD_ELEMENTOS_PROV_PDA");
		this._tempTabla = this.UploadSQL.SelectData("upload_elementos_prov_pda", "id_orden,elemento,marca,serie,id_agrupador,cuenta,proceso,estado,usuario_ins,fecha_ins,valor,cantidad", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("elemento")+"|"+this._tempRegistro.getAsString("marca")+"|"
											+this._tempRegistro.getAsString("serie")+"|"+this._tempRegistro.getAsString("id_agrupador")+"|"+this._tempRegistro.getAsString("cuenta")+"|"
											+this._tempRegistro.getAsString("proceso")+"|"+this._tempRegistro.getAsString("estado")+"|"+this._tempRegistro.getAsString("usuario_ins")+"|"
											+this._tempRegistro.getAsString("fecha_ins")+"|"+this._tempRegistro.getAsString("valor")+"|"+this._tempRegistro.getAsString("cantidad"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		
		/************************************************************************************Tabla SGD_MEDIDOR_ENCONTRADO_PDA**********************************************************************/
		//Primero se borra de SGD_MEDIDOR_ENCONTRADO_PDA los registros que coincidan en idCarga, idContrato, e idPDA
		this.InformacionCarga.add("CMD|SGD_MEDIDOR_ENCONTRADO_PDA");
		this._tempTabla = this.UploadSQL.SelectData("upload_medidor_encontrado_pda", "id_orden,marca,serie,lectura,lectura_2,lectura_3,tipo", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("marca")+"|"+this._tempRegistro.getAsString("serie")+"|"
											+this._tempRegistro.getAsString("lectura")+"|"+this._tempRegistro.getAsString("lectura_2")+"|"+this._tempRegistro.getAsString("lectura_3")+"|"
											+this._tempRegistro.getAsString("tipo"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		
		/************************************************************************************Tabla SGD_MEDIDOR_ENCONTRADO_PDA**********************************************************************/
		//this.InformacionCarga.add("CMD|SGD_DIAGRAMAS_PDA");	-->nunca se ha utilizado esta tabla
		
		
		/******************************************************************************************Tabla SGD_VISITAS_PDA***************************************************************************/
		//this.InformacionCarga.add("CMD|SGD_VISITAS_PDA");		-->nunca se ha utilizado esta tabla	
		
		
		/**********************************************************************************Tabla SGD_DETALLE_CENSO_CARGA_PDA***********************************************************************/
		//Primero se borra de SGD_DETALLE_CENSO_CARGA_PDA los registros que coincidan en idCarga, idContrato, e idPDA
		this.InformacionCarga.add("CMD|SGD_DETALLE_CENSO_CARGA_PDA");
		this._tempTabla = this.UploadSQL.SelectData("upload_detalle_censo_carga_pda", "id_orden,id_elemento,capacidad,cantidad,usuario_ins,fecha_ins,tipo_carga", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("id_elemento")+"|"+this._tempRegistro.getAsString("capacidad")+"|"
											+this._tempRegistro.getAsString("cantidad")+"|"+this._tempRegistro.getAsString("usuario_ins")+"|"+this._tempRegistro.getAsString("fecha_ins")+"|"
											+this._tempRegistro.getAsString("tipo_carga"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/**********************************************************************************Tabla SGD_SERVICIO_NUEVO_PDA***********************************************************************/
		this.InformacionCarga.add("CMD|SGD_SERVICIO_NUEVO_PDA");
		this._tempTabla = this.UploadSQL.SelectData("upload_servicio_nuevo_pda", "id_orden,cuenta,cuenta_vecina1,cuenta_vecina2,nodo_transformador,nodo_secundario,doc1,doc2,doc3,doc4,doc5,doc6,doc7", "id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("cuenta")+"|"+this._tempRegistro.getAsString("cuenta_vecina1")+"|"
											+this._tempRegistro.getAsString("cuenta_vecina2")+"|"+this._tempRegistro.getAsString("nodo_transformador")+"|"+this._tempRegistro.getAsString("nodo_secundario")+"|"
											+this._tempRegistro.getAsString("doc1")+"|"+this._tempRegistro.getAsString("doc2")+"|"+this._tempRegistro.getAsString("doc3")+"|"
											+this._tempRegistro.getAsString("doc4")+"|"+this._tempRegistro.getAsString("doc5")+"|"+this._tempRegistro.getAsString("doc6")+"|"
											+this._tempRegistro.getAsString("doc7"));
		}
		//Por ultimo se invoca el WS validar_recep_datos
		
		
		/**********************************************************************************Tabla ITEM_PAGO***********************************************************************/
		this.InformacionCarga.add("CMD|ITEM_PAGO");
		this._tempTabla = this.UploadSQL.SelectData("upload_item_pago", "id_orden,items","id_orden IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.InformacionCarga.add("INF|"+this._tempRegistro.getAsString("id_orden")+"|"+this._tempRegistro.getAsString("cuenta").trim().replace(" ", "|"));
		}
			
		
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
	protected Integer doInBackground(String... params) {
		int _retorno = 0;
		/*try{
			SoapObject so=new SoapObject(NAMESPACE, METHOD_NAME);
			so.addProperty("Contrato", params[0]);	
			so.addProperty("PDA", params[1]);	
			SoapSerializationEnvelope sse=new SoapSerializationEnvelope(SoapEnvelope.VER11);
			sse.dotNet=true;
			sse.setOutputSoapObject(so);
			HttpTransportSE htse=new HttpTransportSE(URL);
			htse.call(SOAP_ACTION, sse);
			response=(SoapPrimitive) sse.getResponse();
			
		
			if(response.toString()==null) {
				_retorno = -1;
			}else if(response.toString().isEmpty()){
				_retorno = -2;
			}else{
				byte[] resultado = Base64.decode(response.toString());
				try {
					ArchConnectServer.ByteArrayToFile(resultado, "Trabajo.txt");
					this.InformacionDescarga = ArchConnectServer.FileToArrayString("Trabajo.txt",false);
					
					for(int i=0;i<this.InformacionDescarga.size();i++){
						LineasSQL = this.InformacionDescarga.get(i).toString().split("\\|");
						this.UploadSQL.EjecutarSQL(this.LineasSQL[1]);
						publishProgress((int)((i+1)*100/this.InformacionDescarga.size()));
					}
					//ArchConnectServer.DeleteFile("Trabajo.txt");
					_retorno = 1;
				} catch (IOException e) {
					e.printStackTrace();
					_retorno = -3;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		*/
		return _retorno;
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


