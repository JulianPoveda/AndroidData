package class_amdata;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import miscelanea.SQLite;

public class Class_Solicitudes {
	private Context _contextSolicitudes;
	private String  _folderSolicitudes;  
	private SQLite 	SolicitudesSQL; 
	
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
		if(Integer.parseInt(_orden)>0){
			_retorno =false;
		}else{
			_retorno =true;
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
