package class_amdata;

import miscelanea.DateTime;
import miscelanea.SQLite;
import miscelanea.Util;
import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

public class Class_ServicioNuevo {
	private Util	 	ServicioNuevoUtil;
	private DateTime 	ServicioNuevoDT;
	private SQLite 		ServicioNuevoSQL;
	private Context 	_ctxServicioNuevo;
	private String 		_propietario, _ubicacion, _direccion, _claseServicio, _estrato, _municipio, _tipoEnterado, _estadoServicio;
	private String 		_cuenta1, _cuenta2, _nodoTransformador, _nodoSecundario, _docEscritura, _docEstratificacion, _docCedula, _docRecibo, _docInstalacion, _docFactura, _docAutorizacion;
	private String 		_consecutivo, _NPDA, _folderServicioNuevo;
	
	
	private ContentValues			_tempRegistro = new ContentValues();
	//private ArrayList<ContentValues>_tempTabla = new ArrayList<ContentValues>(); 

	public Class_ServicioNuevo(Context _ctx, String _folderAplicacion){
		this._ctxServicioNuevo		= _ctx;
		this._folderServicioNuevo	= _folderAplicacion;
		this.ServicioNuevoUtil		= new Util();
		this.ServicioNuevoDT		= new DateTime();
		this.ServicioNuevoSQL		= new SQLite(this._ctxServicioNuevo,this._folderServicioNuevo);
		
		this._NPDA 	= this.ServicioNuevoSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'");
		
	}
	
	/**Validacion De la Informacion Basica**/
	public boolean validarDatos(String _propietario, String _tipoEnterado, String _ubicacion, String _direccion, String _municipio, String _estrato, String _claseServicio,  String _estadoServicio, String _cuenta1, String _cuenta2, String _nodoTransformador, String _nodoSecundario){
		boolean _retorno = false;
		
		this._propietario 		= _propietario;
		this._tipoEnterado		= _tipoEnterado;
		this._ubicacion 		= _ubicacion.substring(0,1);		
		this._direccion			= _direccion;
		this._municipio			= _municipio;
		this._estrato			= _estrato;
		this._claseServicio 	= _claseServicio;
		this._estadoServicio	= _estadoServicio;
		this._cuenta1			= _cuenta1;
		this._cuenta2			= _cuenta2;
		this._nodoTransformador	= _nodoTransformador;
		this._nodoSecundario	= _nodoSecundario;

		
		if(_propietario.equals("")){
			Toast.makeText(this._ctxServicioNuevo,"No ha ingresado un nombre valido.",Toast.LENGTH_SHORT).show();
		}else if(_ubicacion.equals("")){
			Toast.makeText(this._ctxServicioNuevo,"No ha seleccionado una ubicacion valida.",Toast.LENGTH_SHORT).show();
		}else if(_direccion.equals("")){
			Toast.makeText(this._ctxServicioNuevo,"No ha ingresado una direccion valida.",Toast.LENGTH_SHORT).show();
		}else if(_municipio.equals("")){
			Toast.makeText(this._ctxServicioNuevo,"No ha seleccionado un municipio valido.",Toast.LENGTH_SHORT).show();
		}else if(_claseServicio.equals("")){
			Toast.makeText(this._ctxServicioNuevo,"No ha seleccionado un nombre valido.",Toast.LENGTH_SHORT).show();
		}else if(_estadoServicio.equals("")){
			Toast.makeText(this._ctxServicioNuevo,"No ha seleccionado el estado del servicio.",Toast.LENGTH_SHORT).show();
		}else if(_tipoEnterado.equals("")){
			Toast.makeText(this._ctxServicioNuevo,"No ha seleccionado un tipo de enterado.",Toast.LENGTH_SHORT).show();
		}else if(_cuenta1.equals("")){
			Toast.makeText(this._ctxServicioNuevo,"No ha ingresado una cuenta de referencia.",Toast.LENGTH_SHORT).show();
		}else {
			_retorno = true;
		}
		return _retorno;
	}
	
	
	public boolean validarDocumentacion(String _docEscritura, String _docEstratificacion, String _docCedula, String _docRecibo, String _docInstalacion, String _docFactura, String _docAutorizacion){
		boolean _retorno = true;
		this._docEscritura 		= _docEscritura;
		this._docEstratificacion= _docEstratificacion;
		this._docCedula 		= _docCedula;
		this._docRecibo 		= _docRecibo;
		this._docInstalacion 	= _docInstalacion;
		this._docFactura		= _docFactura;
		this._docAutorizacion 	= _docAutorizacion;
		
		
		if(this._ubicacion.equals("RURAL")&& _docCedula.isEmpty()){
			Toast.makeText(this._ctxServicioNuevo,"Falta la fotocopia de la cedula.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else{
			if(this._tipoEnterado.equals("PROPIETARIO")){
				if(_docEscritura.isEmpty() || _docCedula.isEmpty()){
					Toast.makeText(this._ctxServicioNuevo,"Falta la copia de la escritura y/o fotocopia de la cedula.",Toast.LENGTH_SHORT).show();
					_retorno = false;
				}else if(!this._estadoServicio.equals("SERVICIO_DIRECTO") && _docFactura.isEmpty()){
					Toast.makeText(this._ctxServicioNuevo,"Falta la factura de compra del contador.",Toast.LENGTH_SHORT).show();
					_retorno = false;
				}
			}else{
				if(_docEscritura.isEmpty() || _docCedula.isEmpty() || _docAutorizacion.isEmpty()){
					Toast.makeText(this._ctxServicioNuevo,"Falta la copia de la escritura, fotocopia de la cedula y/o autorizacion de propietario.",Toast.LENGTH_SHORT).show();
					_retorno = false;
				}else if(!this._estadoServicio.equals("SERVICIO_DIRECTO") && _docFactura.isEmpty()){
					Toast.makeText(this._ctxServicioNuevo,"Falta la factura de compra del contador.",Toast.LENGTH_SHORT).show();
					_retorno = false;
				}
			}			
		}		
		return _retorno;
	}
	
	
	
	
	/**Funcion para crear un nodo generico necesario para poder crear los servicios nuevos**/
	private boolean crearNodo(){
		this._tempRegistro.clear();
		this._tempRegistro.put("id_nodo", "000000");
		this._tempRegistro.put("direccion", "SIN DIRECCION");
		this._tempRegistro.put("ubicacion", "U");
		this._tempRegistro.put("municipio", "SM");
		this._tempRegistro.put("mapa", "");
		this._tempRegistro.put("fecha_ven", ServicioNuevoDT.GetDateTimeHora());
		this._tempRegistro.put("progreso","0/1");
		this._tempRegistro.put("estado", "P");
		this._tempRegistro.put("observacion", "NODO GENERICO");
		this._tempRegistro.put("tipo", "O");
		
		return ServicioNuevoSQL.InsertRegistro("amd_nodo", this._tempRegistro);
	}
	
	
	private boolean crearOrden(){
		this._consecutivo = this.ServicioNuevoUtil.generarAleatorio(4, this._NPDA);	
		this._tempRegistro.clear();
		this._tempRegistro.put("id_orden", this._consecutivo + this._NPDA);
		this._tempRegistro.put("cuenta", this._consecutivo + this._NPDA);
		this._tempRegistro.put("propietario", this._propietario);
		this._tempRegistro.put("ubicacion", this._ubicacion);
		this._tempRegistro.put("direccion", this._direccion);
		this._tempRegistro.put("clase_servicio", this._claseServicio);
		this._tempRegistro.put("estrato", this._estrato);
		this._tempRegistro.put("id_nodo", "000000");
		this._tempRegistro.put("ruta", "1");
		this._tempRegistro.put("estado_cuenta", "ACTIVA");
		this._tempRegistro.put("pda", this._NPDA);
		this._tempRegistro.put("estado", "P");
		this._tempRegistro.put("observacion_trabajo", "SERVICIO_NUEVO");
		this._tempRegistro.put("observacion_pad", "SERVICIO_NUEVO");
		this._tempRegistro.put("tipo", "C");
		this._tempRegistro.put("municipio", this._municipio);
		this._tempRegistro.put("codigo_apertura", this.generarContrasena(this._consecutivo.substring(1,5)));
		this._tempRegistro.put("bodega", "1-SYPELC SOLICITUDES");
		this._tempRegistro.put("solicitud", "");
		this._tempRegistro.put("clase_solicitud", "");
		this._tempRegistro.put("tipo_solicitud", "");
		this._tempRegistro.put("dependencia", "");
		this._tempRegistro.put("tipo_accion", "");
		this._tempRegistro.put("dependencia_asignada", "");
		this._tempRegistro.put("consecutivo_accion", "");
		return this.ServicioNuevoSQL.InsertRegistro("amd_ordenes_trabajo", this._tempRegistro);
	}
	
	
	private boolean crearContadorClienteOrden(){
		this._tempRegistro.clear();
		this._tempRegistro.put("cuenta", this._consecutivo + this._NPDA);
		this._tempRegistro.put("marca", this._estadoServicio);
		
		if(this._estadoServicio.equals("SIN_SERVICIO")){
			this._tempRegistro.put("serie", "");			
		}else{
			this._tempRegistro.put("serie", "-1");
		}
		
		this._tempRegistro.put("estado", "");
		this._tempRegistro.put("lectura", "0");
		this._tempRegistro.put("digitos", "5");
		this._tempRegistro.put("con_contador", "N");
		this._tempRegistro.put("tipo", "");
		this._tempRegistro.put("desinstalado", 0);
		return this.ServicioNuevoSQL.InsertRegistro("amd_contador_cliente_orden", this._tempRegistro);		
	}
	
	
	private boolean registrarServicioNuevo(){
		this._tempRegistro.clear();
		this._tempRegistro.put("id_orden",this._consecutivo + this._NPDA);
		this._tempRegistro.put("cuenta",this._consecutivo + this._NPDA);
		this._tempRegistro.put("cuenta_vecina1",this._cuenta1);
		this._tempRegistro.put("cuenta_vecina2",this._cuenta2);
		this._tempRegistro.put("nodo_transformador",this._nodoTransformador);
		this._tempRegistro.put("nodo_secundario",this._nodoSecundario);
		this._tempRegistro.put("doc1",this._docEscritura);
		this._tempRegistro.put("doc2",this._docEstratificacion);
		this._tempRegistro.put("doc3",this._docCedula);
		this._tempRegistro.put("doc4",this._docRecibo);
		this._tempRegistro.put("doc5",this._docInstalacion);
		this._tempRegistro.put("doc6",this._docFactura);
		this._tempRegistro.put("doc7",this._docAutorizacion);
		return this.ServicioNuevoSQL.InsertRegistro("amd_servicio_nuevo", this._tempRegistro);
	}
	
	
	
	private String generarContrasena(String _consecutivo){
		return String.valueOf((Integer.parseInt(_consecutivo) * Integer.parseInt(this._NPDA))+ Integer.parseInt(_consecutivo + this._NPDA)) + this._NPDA;
	}
	
	
	public boolean crearServicioNuevo(){
		boolean _retorno = false;		
		if(ServicioNuevoSQL.ExistRegistros("amd_nodo", "id_nodo='000000'")){
			if(this.crearOrden() && this.crearContadorClienteOrden() && this.registrarServicioNuevo()){
				_retorno = true;
			}
		}else{
			if(this.crearNodo() && this.crearContadorClienteOrden() && this.crearOrden() && this.registrarServicioNuevo()){
				_retorno = true;
			}
		}
		return _retorno;
	}
	
}
