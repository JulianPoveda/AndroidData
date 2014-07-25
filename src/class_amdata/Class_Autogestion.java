package class_amdata;

import java.util.ArrayList;

import miscelanea.DateTime;
import miscelanea.SQLite;
import miscelanea.Util;
import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

/** Clase que contiene los metodos necesarias para la validacion y creacion de autogestiones
 * @author 	Julian Eduardo Poveda Daza
 * @version	1.0
 * @since	25-07-2014
 */
public class Class_Autogestion{
	private Util	 	AutogestionUtil;
	private DateTime 	AutogestionDT;
	private SQLite 		AutogestionSQL;
	private Context 	_ctxAutogestion;
	private String 		_NPDA, _folderAutogestion;
	
	private String 	_tipoOrden,_consecutivo, _dependencia, _claseSolicitud, _tipoSolicitud, _tipoAccion, _propietario, _cuenta, _ubicacion, _direccion, _municipio, _nodo, _carga, _estrato, _ciclo, _claseServicio;
	private String  _tipoConexion, _marcaContador, _serieContador, _lecturaContador, _observacionTrabajo, _id_orden, _solicitud, _estado, _conContador;
	
	private String 					_strTemp[];
	private ContentValues 			_tempRegistro	= new ContentValues();
	private ArrayList<ContentValues> _tempTabla 	= new ArrayList<ContentValues>();
	
	
	/**
	 * @param _ctx
	 * @param _folderAplicacion
	 */
	public Class_Autogestion(Context _ctx, String _folderAplicacion){
		this._ctxAutogestion	= _ctx;
		this._folderAutogestion	= _folderAplicacion;
				
		this.AutogestionUtil	= new Util();
		this.AutogestionDT		= new DateTime();
		this.AutogestionSQL		= new SQLite(this._ctxAutogestion,this._folderAutogestion);
		
		this._NPDA 	= this.AutogestionSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'");
	}
	
	
	
	/**Funcion para listar todas las dependencias que hay registradas en la base de datos**/
	public ArrayList<String> getDependencias(){
		ArrayList<String> _dependencias = new ArrayList<String>();
		_dependencias.clear();
		_dependencias.add("");
		
		this._tempTabla = AutogestionSQL.SelectData("vista_aut_dependencia", "resumen", "id IS NOT NULL");
		for(int i=0;i<this._tempTabla.size();i++){
			_dependencias.add(this._tempTabla.get(i).getAsString("resumen"));
		}
		return _dependencias;
	}
	
	
	/**Funcion para listar las clases de solicitudes segun la dependencia recibida com parametro
	 * @param Dependencia -> Parametro de entrada con el cual realiza la busqueda de las clases de solicitudes relacionadas con la dependencia
	 * @return -> Retorna un ArrayList<String> con las Clases de Solicitudes
	**/
	public ArrayList<String> getClaseSolicitud(String Dependencia){
		ArrayList<String> _claseSolicitud = new ArrayList<String>();
		_claseSolicitud.clear();
		String _codDependencia[] = Dependencia.split(" ");
		this._tempTabla = AutogestionSQL.SelectData("vista_aut_combinacion", "clase_solicitud||' ('||txt_clase_solicitud||')' as clase_solicitud", "dependencia='"+_codDependencia[0]+"'");
		for(int i=0;i<this._tempTabla.size();i++){
			_claseSolicitud.add(this._tempTabla.get(i).getAsString("clase_solicitud"));
		}		
		return _claseSolicitud;
	}
	
	
	
	/**Funcion para listar los tipos de solicitudes dependiendo de la dependencia y clase de solicitud recibidas como parametro**/
	public ArrayList<String> getTipoSolicitud(String Dependencia, String claseSolicitud){
		ArrayList<String> _tipoSolicitud = new ArrayList<String>();
		_tipoSolicitud.clear();
		String _codDependencia[] 	= Dependencia.split(" ");
		String _codClaseSolicitud[]	= claseSolicitud.split(" ");
		this._tempTabla = AutogestionSQL.SelectData("vista_aut_combinacion", "tipo_solicitud||' ('||txt_tipo_solicitud||')' as tipo_solicitud", "dependencia='"+_codDependencia[0]+"' AND clase_solicitud='"+_codClaseSolicitud[0]+"'");
		for(int i=0;i<this._tempTabla.size();i++){
			_tipoSolicitud.add(this._tempTabla.get(i).getAsString("tipo_solicitud"));
		}		
		return _tipoSolicitud;
	}
	
	
	/**Funcion para listar los tipos de solicitudes dependiendo de la dependencia,clase de solicitud y tipo de solicitud recibidas como parametro**/
	public ArrayList<String> getTipoAccion(String Dependencia, String claseSolicitud, String tipoSolicitud){
		ArrayList<String> _tipoSolicitud = new ArrayList<String>();
		_tipoSolicitud.clear();
		String _codDependencia[] 	= Dependencia.split(" ");
		String _codClaseSolicitud[]	= claseSolicitud.split(" ");
		String _codTipoSolicitud[]	= tipoSolicitud.split(" ");
		this._tempTabla = AutogestionSQL.SelectData("vista_aut_combinacion", "tipo_accion||' ('||txt_tipo_accion||')' as tipo_accion", "dependencia='"+_codDependencia[0]+"' AND clase_solicitud='"+_codClaseSolicitud[0]+"' AND tipo_solicitud='"+_codTipoSolicitud[0]+"'");
		for(int i=0;i<this._tempTabla.size();i++){
			_tipoSolicitud.add(this._tempTabla.get(i).getAsString("tipo_accion"));
		}		
		return _tipoSolicitud;
	}
	
	
	
	/**Funcion para determiar si las cuentas vecinas esta bien digitadas**/
	public boolean validarCuentas(String _cuenta1, String _cuenta2){
		boolean _retorno1 = false;
		boolean _retorno2 = false;
		
		if(_cuenta1.isEmpty()){
			_retorno1 = false;
		}else{
			_retorno1 = this.AutogestionUtil.validacionCuenta(_cuenta1);
		}
		
		if(_cuenta2.isEmpty()){
			_retorno2 = true;
		}else{
			_retorno2 = this.AutogestionUtil.validacionCuenta(_cuenta2);;
		}
		return _retorno1 && _retorno2;
	}
	
	
	/**Funcion para validar la informacion basica de la autogestion**/
	public boolean validarDatosAutogestion(String _tipoOrden, String _consecutivo, String _dependencia, String _claseSolicitud, String _tipoSolicitud, String _tipoAccion, String _propietario, String _cuenta, String _ubicacion, String _direccion, String _municipio, String _nodo, String _carga, String _estrato, String _ciclo, String _claseServicio, String _tipoConexion, String _marcaContador, String _serieContador, String _lecturaContador){
		boolean _retorno = true;
		this._tipoOrden 		= _tipoOrden;
		this._consecutivo 		= _consecutivo;
		
		this._strTemp			= _dependencia.split(" ");
		this._dependencia		= this._strTemp[0];
		
		this._strTemp			= _claseSolicitud.split(" ");
		this._claseSolicitud 	= this._strTemp[0];
		
		this._strTemp			= _tipoSolicitud.split(" ");
		this._tipoSolicitud		= this._strTemp[0];
		
		this._strTemp			= _tipoAccion.split(" ");
		this._tipoAccion		= this._strTemp[0];
		
		this._propietario		= _propietario;
		this._cuenta			= _cuenta;
		this._ubicacion		 	= _ubicacion.substring(0,1);
		this._direccion			= _direccion;
		this._municipio			= _municipio;
		this._nodo				=  _nodo;
		this._carga 			= _carga;
		this._estrato 			= _estrato;
		this._ciclo				= _ciclo;
		this._claseServicio		= _claseServicio;
		
		if(_tipoConexion.equals("Trifasico")){
			this._tipoConexion = "T";
		}else if(_tipoConexion.equals("Bifasico")){
			this._tipoConexion = "B";
		}else if(_tipoConexion.equals("Monofasico")){
			this._tipoConexion = "MB";
		}else if(_tipoConexion.equals("Trifilar Neutro Directo")){
			this._tipoConexion = "MT";
		}else{
			this._tipoConexion = "";
		}
		
		this._strTemp			= _marcaContador.split(" ");
		this._marcaContador		= this._strTemp[0];
			
		this._serieContador		= _serieContador;
		this._lecturaContador	= _lecturaContador;
		this._solicitud = this.AutogestionUtil.generarAleatorio(6);
		
		
		if(this._propietario.isEmpty()){
			Toast.makeText(this._ctxAutogestion,"No ha ingresado el nombre del propietario.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(this._ubicacion.isEmpty()){
			Toast.makeText(this._ctxAutogestion,"No ha seleccionado la ubicacion de la orden.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(this._direccion.isEmpty()){
			Toast.makeText(this._ctxAutogestion,"No ha ingresado la direccion del inmueble.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(this._municipio.isEmpty()){
			Toast.makeText(this._ctxAutogestion,"No ha seleccionado el municipio en el cual se realiza la orden.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(this._estrato.isEmpty()){
			Toast.makeText(this._ctxAutogestion,"No ha seleccionado el estrato del inmueble.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(this._claseServicio.isEmpty()){
			Toast.makeText(this._ctxAutogestion,"No ha seleccionado la clase de servicio.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(this._marcaContador.isEmpty()){
			Toast.makeText(this._ctxAutogestion,"No ha seleccionado la marca del contador.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(!this._marcaContador.equals("SD (SERVICIO DIRECTO)") && !this._marcaContador.equals("SS (SIN SERVICIO)")){
			if(this._tipoConexion.isEmpty()){
				Toast.makeText(this._ctxAutogestion,"No ha seleccionado el tipo de conexion que tiene el predio.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(this._serieContador.isEmpty()){
				Toast.makeText(this._ctxAutogestion,"No ha ingresado la serie del contador.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(this._lecturaContador.isEmpty()){
				Toast.makeText(this._ctxAutogestion,"No ha seleccionado la lectura del contador.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(this._nodo.isEmpty()){
				Toast.makeText(this._ctxAutogestion,"No ha ingresado el nodo.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(this._carga.isEmpty()){
				Toast.makeText(this._ctxAutogestion,"No ha ingresado la carga contratada.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(this._ciclo.isEmpty()){
				Toast.makeText(this._ctxAutogestion,"No ha seleccionado el ciclo.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else{
				this._estado 		= "I";
				this._conContador 	= "S";
			}
		}else if(this._tipoOrden.isEmpty()){
			Toast.makeText(this._ctxAutogestion,"No ha seleccionado el tipo de orden a crear.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(this._tipoOrden.equals("SOLICITUD")){
			this._observacionTrabajo = "AUTOGESTIONS";
			if(this._consecutivo.isEmpty()){
				Toast.makeText(this._ctxAutogestion,"No ha seleccionado un consecutivo.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(this._dependencia.isEmpty() || this._consecutivo.isEmpty() || this._claseSolicitud.isEmpty() || this._tipoSolicitud.isEmpty() || this._tipoAccion.isEmpty()){
				Toast.makeText(this._ctxAutogestion,"No ha seleccionado la dependencia, la clase, el tipo de solicitud y/o tipo accion.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else{
				this._id_orden = this._consecutivo + this._dependencia + this._solicitud;
			}
		}else if(this._tipoOrden.equals("REVISION")){
			this._observacionTrabajo = "AUTOGESTIONR";
			if(this._consecutivo.isEmpty()){
				Toast.makeText(this._ctxAutogestion,"No ha seleccionado un consecutivo.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else{
				this._id_orden = this._solicitud;
			}
		}	
		return _retorno;
	}
	
	
	
	public boolean crearAutogestion(){
		boolean _retorno = false;		
		if(this.AutogestionSQL.ExistRegistros("amd_nodo", "id_nodo='"+this._nodo+"'")){
			if(this.crearOrden() && this.crearContadorClienteOrden()){
				_retorno = true;
			}
		}else{
			if(this.crearNodo() && this.crearContadorClienteOrden() && this.crearOrden()){
				_retorno = true;
			}
		}
		return _retorno;
	}
	
	
	/**Funcion para crear un nodo generico necesario para poder crear los servicios nuevos**/
	private boolean crearNodo(){
		this._tempRegistro.clear();
		this._tempRegistro.put("id_nodo", this._nodo);
		this._tempRegistro.put("direccion", "SIN DIRECCION");
		this._tempRegistro.put("ubicacion", "U");
		this._tempRegistro.put("municipio", "SM");
		this._tempRegistro.put("mapa", "");
		this._tempRegistro.put("fecha_ven", this.AutogestionDT.GetDateTimeHora());
		this._tempRegistro.put("progreso","0/1");
		this._tempRegistro.put("estado", "P");
		this._tempRegistro.put("observacion", "NODO GENERICO");
		this._tempRegistro.put("tipo", "O");
		
		return this.AutogestionSQL.InsertRegistro("amd_nodo", this._tempRegistro);
	}
	
	
	private boolean crearOrden(){
		this._tempRegistro.clear();
		this._tempRegistro.put("id_orden", this._id_orden);
		this._tempRegistro.put("cuenta", this._cuenta);
		this._tempRegistro.put("propietario", this._propietario);
		this._tempRegistro.put("ubicacion", this._ubicacion);
		this._tempRegistro.put("direccion", this._direccion);
		this._tempRegistro.put("clase_servicio", this._claseServicio);
		this._tempRegistro.put("estrato", this._estrato);
		this._tempRegistro.put("id_nodo", this._nodo);
		this._tempRegistro.put("ruta", "1");
		this._tempRegistro.put("estado_cuenta", "ACTIVA");
		this._tempRegistro.put("pda", this._NPDA);
		this._tempRegistro.put("estado", "P");
		this._tempRegistro.put("observacion_trabajo", this._observacionTrabajo);
		this._tempRegistro.put("observacion_pad", "AUTOGESTION");
		this._tempRegistro.put("tipo", "C");
		this._tempRegistro.put("municipio", this._municipio);
		this._tempRegistro.put("codigo_apertura", this.generarContrasena(this._solicitud));
		this._tempRegistro.put("solicitud", this._solicitud);
		this._tempRegistro.put("clase_solicitud", this._claseSolicitud);
		this._tempRegistro.put("tipo_solicitud", this._tipoSolicitud);
		this._tempRegistro.put("dependencia", this._dependencia);
		this._tempRegistro.put("tipo_accion", this._tipoAccion);
		this._tempRegistro.put("dependencia_asignada", "62020");
		this._tempRegistro.put("consecutivo_accion", this._consecutivo);
		this._tempRegistro.put("fecha_ven", AutogestionDT.GetDateTimeHora());
		this._tempRegistro.put("carga_contratada", this._carga);
		this._tempRegistro.put("ciclo", this._ciclo);
		this._tempRegistro.put("bodega", "1-SYPELC SOLICITUDES");		
		return this.AutogestionSQL.InsertRegistro("amd_ordenes_trabajo", this._tempRegistro);
	}
	
	
	private boolean crearContadorClienteOrden(){
		this._tempRegistro.clear();
		this._tempRegistro.put("cuenta", this._cuenta);
		this._tempRegistro.put("marca", this._marcaContador);
		this._tempRegistro.put("serie", this._serieContador);
		this._tempRegistro.put("estado", this._estado);
		this._tempRegistro.put("lectura", this._lecturaContador);
		this._tempRegistro.put("digitos", "5");		
		this._tempRegistro.put("con_contador", this._conContador);
		this._tempRegistro.put("tipo", this._tipoConexion);
		this._tempRegistro.put("desinstalado", 0);
		return this.AutogestionSQL.InsertRegistro("amd_contador_cliente_orden", this._tempRegistro);		
	}

	
	private String generarContrasena(String _consecutivo){
		return String.valueOf((Integer.parseInt(_consecutivo) * Integer.parseInt(this._NPDA))+ Integer.parseInt(_consecutivo + this._NPDA)) + this._NPDA;
	}
}
