package class_amdata;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import miscelanea.DateTime;
import miscelanea.SQLite;

public class Class_Solicitudes {
	private Context 	_contextSolicitudes;
	private String  	_folderSolicitudes;  
	private SQLite 		SolicitudesSQL; 
	private DateTime 	FcnDate = new DateTime();
	
	private ContentValues			_tempRegistro	= new ContentValues();
	private ArrayList<ContentValues>_tempTabla		= new ArrayList<ContentValues>();
	//private ContentValues			_tempRegistro	= new ContentValues();
	
	
	public Class_Solicitudes(Context _ctx, String _folderAplicacion){
		this._contextSolicitudes= _ctx;
		this._folderSolicitudes	= _folderAplicacion;
		SolicitudesSQL	= new SQLite(this._contextSolicitudes,this._folderSolicitudes);
	}
	
	
	/*************************Funcion que retorna el estado que tiene la orden que se recibe por parametro*************************/
	public boolean isAutogestion(String _orden){
		boolean _retorno;
		char _number;
	    _number=_orden.charAt(0);
		if(_number == '-'){
			_retorno =true;
		}else{
			_retorno =false;
		}
		return _retorno;
	}

	
	/*************************Funcion que retorna el estado que tiene la orden que se recibe por parametro*************************/
	public String getEstadoOrden(String _orden){
		return this.SolicitudesSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "estado", "id_orden='"+_orden+"'");
	}
	
	/***************Funcion para validar si se permite el ingreso de informacion a la orden recibida por parametro****************/
	public boolean IniciarOrden(String _orden){
		boolean _retorno = false;
		if(this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "estado='E'")){
			if(this.SolicitudesSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "id_orden", "estado='E'").equals(_orden)){
				_retorno = true;
			}else{
				_retorno = false;
			}
		}else if(this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "id_orden='"+_orden+"' AND estado='P'")){
			_retorno = true;
		}else{
			_retorno = false;
		}	
		return _retorno;
	}
	
	
	/**************Insert manual de datos minimos cuando el usuario que la cierra es el administrador*************/
	public boolean InsertDatosMinimos(String _ordenTrabajo, String _cedulaUsuario){
		this._tempRegistro.clear();
		this._tempRegistro.put("fecha_atencion", this.FcnDate.GetFecha());
		this._tempRegistro.put("hora_ini", this.FcnDate.GetDateTimeHora());
		this._tempRegistro.put("hora_fin", this.FcnDate.GetDateTimeHora());
		this.SolicitudesSQL.UpdateRegistro("amd_ordenes_trabajo", this._tempRegistro,"id_orden='"+_ordenTrabajo+"'");	
		
		this._tempRegistro.clear();
		this._tempRegistro.put("id_orden",_ordenTrabajo);
		this._tempRegistro.put("id_acta",this.SolicitudesSQL.IntSelectShieldWhere("amd_actas", "max(cast(id_acta AS INTEGER)) as id_acta", "id_acta IS NOT NULL")+1);
		this._tempRegistro.put("id_revision","0");
		this._tempRegistro.put("codigo_trabajo",this.SolicitudesSQL.StrSelectShieldWhere("amd_param_trabajos_orden", "id_trabajo", "id_orden='"+_ordenTrabajo+"'"));
		this._tempRegistro.put("cedula_enterado","00000");
		this._tempRegistro.put("nombre_enterado","Administrador");
		this._tempRegistro.put("evento","");
		this._tempRegistro.put("tipo_enterado","Propietario");
		this._tempRegistro.put("cedula_testigo","");
		this._tempRegistro.put("nombre_testigo","");
		this._tempRegistro.put("estado","E");
		this._tempRegistro.put("usuario_ins",_cedulaUsuario);
		return this.SolicitudesSQL.InsertRegistro("amd_actas", this._tempRegistro);	
	}
	
	
	/********************Validacion de datos minimos necesarios antes de cerrar la orden**************************/
	public boolean ExisteDatos(String _orden, String _cuenta, String _nodo){
		return 	this.SolicitudesSQL.ExistRegistros("amd_actas", "id_orden='"+_orden+"'") &
				(this.SolicitudesSQL.ExistRegistros("amd_medidor_encontrado", "id_orden='"+_orden+"'") |
				this.SolicitudesSQL.ExistRegistros("amd_cambios_contadores", "id_orden='"+_orden+"'"));
	}
	
	public void CerrarOrdenes(String _orden){
		this._tempRegistro.clear();
		this._tempRegistro.put("id_orden",_orden);
		this._tempRegistro.put("id_acta","1234");
		this._tempRegistro.put("id_revision","0");
		this._tempRegistro.put("codigo_trabajo","2");
		this._tempRegistro.put("cedula_enterado","00000");
		this._tempRegistro.put("nombre_enterado","Administrador");
		this._tempRegistro.put("evento","");
		this._tempRegistro.put("tipo_enterado","Propietario");
		this._tempRegistro.put("cedula_testigo","");
		this._tempRegistro.put("nombre_testigo","");
		this._tempRegistro.put("estado","E");
		this._tempRegistro.put("usuario_ins","123456");
		this.SolicitudesSQL.InsertRegistro("amd_actas", this._tempRegistro);
		
		this._tempRegistro.clear();
		this._tempRegistro.put("id_orden",_orden);
		this._tempRegistro.put("marca","A B (Marca A B) ");
		this._tempRegistro.put("serie","00000");
		this._tempRegistro.put("lectura","2");
		this._tempRegistro.put("lectura2","");
		this._tempRegistro.put("lectura3","");
		this._tempRegistro.put("tipo","MONOFASICO");
		this.SolicitudesSQL.InsertRegistro("amd_medidor_encontrado", this._tempRegistro);
		
	}
	
	/**********************Funcion para eliminar la informacion de la orden y volver a estado P********************/
	public void EliminarDatosOrden(String _orden, String _cuenta, String _nodo){
		this.SolicitudesSQL.DeleteRegistro("amd_actas", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_acometida", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_borrar_orden", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_cambios_contadores", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_censo_carga", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_consumos_orden", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_datos_tranfor", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_impresiones_inf", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_inconsistencias", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_irregularidades", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_material_usuario", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_contador_cliente_orden", "cuenta='"+_cuenta+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_materiales_provisionales", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_materiales_trabajo_orden", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_medidor_encontrado", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_observacion_materiales", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("param_trabajos_orden", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_nodo", "id_nodo='"+_nodo+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_pct_error", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_prueba_integracion", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_pruebas", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_sellos", "id_orden='"+_orden+"'");
		this.SolicitudesSQL.DeleteRegistro("amd_servicio_nuevo", "id_orden='"+_orden+"'");		
	}
	
	
	/***************************Funcion para el cambio de estado de la solicitud*************************************/
	public boolean setEstadoOrden(String _orden, String _estado){
		this._tempRegistro.clear();
		this._tempRegistro.put("estado", _estado);
		return this.SolicitudesSQL.UpdateRegistro(	"amd_ordenes_trabajo", 
													this._tempRegistro, 
													"id_orden='"+_orden+"'");	
	}
	
	
	
	/**Funcion que valida si es posible abrir una con el codigo de apertura, para esto no debe haber ninguno otra 
	 * orden abierta y la seleccionada debe estar ya cerrada**/
	public boolean IniciarAperturaOrden(String _orden){
		boolean _retorno = false;
		if(this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "estado='E'")){
			_retorno = false;
		}else if(this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "id_orden='"+_orden+"' AND estado='T'")){
			_retorno = true;
		}	
		return _retorno;
	}
	
	/**Funcion para verificar el codigo de apertura de la orden seleccionada y el codigo ingresado por el usuario**/
	public boolean verificarCodigoApertura(String _orden, String _codigoApertura){
		return this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "id_orden='"+_orden+"' AND codigo_apertura='"+_codigoApertura+"'");
	}
	
	/**Funcion que retorna el nodo de la orden que se reciba como parametro**/
	public String getNodo(String _orden){
		return this.SolicitudesSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "id_nodo", "id_orden='"+_orden+"'");
	}
	
	
	/**Funcion que retorna el id_trabajo_contrato segun la ordenq ue se reciba como parametro**/
	public int getIdTrabajoContrato(String _orden){
		return this.SolicitudesSQL.IntSelectShieldWhere("vista_ordenes_trabajo", "id_trabajo", "id_orden='"+_orden+"'");
	}
	
	
	public ArrayList<String> getNodosSolicitudes(){
		ArrayList<String> _listaNodos = new ArrayList<String>();
		_listaNodos.add("Todos");
		this._tempTabla = this.SolicitudesSQL.SelectData("amd_ordenes_trabajo","id_nodo","estado in ('P','E','T','TA')");
		for(int i=0;i<this._tempTabla.size();i++){
			_listaNodos.add(this._tempTabla.get(i).getAsString("id_nodo"));
		}	
		return _listaNodos;
	}
	
	
}
