package class_amdata;

import java.util.ArrayList;

import miscelanea.SQLite;
import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

public class Class_Contador {
	final int NO_RETIRADO 	= 1;
	final int RETIRADO 		= 2;
	final int INSTALADO		= 3;
	
	private Context _ctxContador;
	private String  _folderContador;  
	private SQLite 	ContadorSQL;
	private String  OrdenTrabajo;
	private String 	CuentaCliente;
	private String  CedulaTecnico;
	
	private String _tipo;
	private String _cobro;
	private String _marca; 
	private String _serie;
	private String _lectura;
	private String _instalado;
	
	private ContentValues			_tempRegistro = new ContentValues();
	private ArrayList<ContentValues>_tempTabla = new ArrayList<ContentValues>(); 

	public Class_Contador(Context _ctx, String _folderAplicacion, String _cedulaTecnico, String _ordenTrabajo, String _cuentaCliente){
		this._ctxContador	= _ctx;
		this._folderContador= _folderAplicacion;
		this.CedulaTecnico 	= _cedulaTecnico;
		this.OrdenTrabajo	= _ordenTrabajo;
		this.CuentaCliente	= _cuentaCliente;
		ContadorSQL	= new SQLite(this._ctxContador,this._folderContador);
	}
	
	
	public ContentValues movimientoContador(int _estadoMovimiento, String _movimiento){
		this._tempRegistro.clear();
		if(_estadoMovimiento==NO_RETIRADO && _movimiento.equals("SERVICIO_DIRECTO")){
			this._tempRegistro.put("textSerie","0");
			this._tempRegistro.put("enableSerie", false);
			this._tempRegistro.put("textLectura","-1");
			this._tempRegistro.put("enableLectura", false);
			this._tempRegistro.put("valorTipoConexion","Servicio Directo");
			this._tempRegistro.put("enableTipoConexion", false);
			this._tempRegistro.put("valorMarcaMedidor", "SD (SERVICIO DIRECTO)");
			this._tempRegistro.put("enableMarcaMedidor", false);
			this._tempRegistro.put("visibleBodega", false);
		}else if(_estadoMovimiento==NO_RETIRADO && _movimiento.equals("RETIRADO")){
			this._tempRegistro.put("textSerie",this.ContadorSQL.StrSelectShieldWhere("vista_contador_cliente_orden", "serie", "cuenta='"+this.CuentaCliente+"'"));
			this._tempRegistro.put("enableSerie", false);
			this._tempRegistro.put("textLectura","");
			this._tempRegistro.put("enableLectura", true);
			this._tempRegistro.put("valorTipoConexion", this.ContadorSQL.StrSelectShieldWhere("vista_contador_cliente_orden", "descripcion", "cuenta='"+this.CuentaCliente+"' AND desinstalado=0 "));
			this._tempRegistro.put("enableTipoConexion", false);
			this._tempRegistro.put("valorMarcaMedidor", this.ContadorSQL.StrSelectShieldWhere("amd_param_marca_contador", "id_marca||' ('||nombre||')' as medidores", "id_marca='"+ContadorSQL.StrSelectShieldWhere("vista_contador_cliente_orden", "marca", "cuenta='"+this.CuentaCliente+"'")+"'"));
			this._tempRegistro.put("enableMarcaMedidor", false);
			this._tempRegistro.put("visibleBodega", false);
		}else if(_estadoMovimiento==NO_RETIRADO && _movimiento.equals("SIN_SERVICIO")){
			this._tempRegistro.put("textSerie","-1");
			this._tempRegistro.put("enableSerie", false);
			this._tempRegistro.put("textLectura","-1");
			this._tempRegistro.put("enableLectura", false);
			this._tempRegistro.put("valorTipoConexion","Sin Servicio");
			this._tempRegistro.put("enableTipoConexion", false);
			this._tempRegistro.put("valorMarcaMedidor","SS (SIN SERVICIO)");
			this._tempRegistro.put("enableMarcaMedidor", false);
			this._tempRegistro.put("visibleBodega", false);
		}else if(_estadoMovimiento==NO_RETIRADO && _movimiento.equals("MEDIDOR_DIFERENTE")){
			this._tempRegistro.put("textSerie","");
			this._tempRegistro.put("enableSerie", true);
			this._tempRegistro.put("textLectura","");
			this._tempRegistro.put("enableLectura", true);
			this._tempRegistro.put("valorTipoConexion","...");
			this._tempRegistro.put("enableTipoConexion", true);
			this._tempRegistro.put("valorMarcaMedidor","...");
			this._tempRegistro.put("enableMarcaMedidor", true);
			this._tempRegistro.put("visibleBodega", false);
		}else if(_estadoMovimiento==RETIRADO && _movimiento.equals("PROVISIONAL")){
			this._tempRegistro.put("textSerie","");
			this._tempRegistro.put("enableSerie", false);
			this._tempRegistro.put("textLectura","");
			this._tempRegistro.put("enableLectura", true);
			this._tempRegistro.put("valorTipoConexion","...");
			this._tempRegistro.put("enableTipoConexion", false);
			this._tempRegistro.put("valorMarcaMedidor","...");
			this._tempRegistro.put("enableMarcaMedidor", false);
			this._tempRegistro.put("visibleBodega", true);
		}else if(_estadoMovimiento==RETIRADO && _movimiento.equals("DEFINITIVO")){
			this._tempRegistro.put("textSerie","");
			this._tempRegistro.put("enableSerie", false);
			this._tempRegistro.put("textLectura","");
			this._tempRegistro.put("enableLectura", true);
			this._tempRegistro.put("valorTipoConexion","...");
			this._tempRegistro.put("enableTipoConexion", false);
			this._tempRegistro.put("valorMarcaMedidor","...");
			this._tempRegistro.put("enableMarcaMedidor", false);
			this._tempRegistro.put("visibleBodega", true);
		}else if(_estadoMovimiento==RETIRADO && _movimiento.equals("CANCELACION_MATRIC")){
			this._tempRegistro.put("textSerie","-1");
			this._tempRegistro.put("enableSerie", false);
			this._tempRegistro.put("textLectura","-1");
			this._tempRegistro.put("enableLectura", false);
			this._tempRegistro.put("valorTipoConexion","...");
			this._tempRegistro.put("enableTipoConexion", false);
			this._tempRegistro.put("valorMarcaMedidor","...");
			this._tempRegistro.put("enableMarcaMedidor", false);
			this._tempRegistro.put("visibleBodega", false);
		}else{
			this._tempRegistro.put("textSerie","");
			this._tempRegistro.put("enableSerie", false);
			this._tempRegistro.put("textLectura","");
			this._tempRegistro.put("enableLectura", false);
			this._tempRegistro.put("valorTipoConexion","");
			this._tempRegistro.put("enableTipoConexion", false);
			this._tempRegistro.put("valorMarcaMedidor", "");
			this._tempRegistro.put("enableMarcaMedidor", false);
			this._tempRegistro.put("visibleBodega", false);
		}		
		return this._tempRegistro;
	}
	
	
	public ArrayList<ContentValues> getMovimientos(){
		//this._tempTabla.clear();
		//this._tempTabla = ContadorSQL.SelectData("amd_cambios_contadores", "tipo,marca,serie,lectura", "id_orden='"+this.OrdenTrabajo+"'");
		return this._tempTabla = ContadorSQL.SelectData("amd_cambios_contadores", "tipo,marca,serie,lectura", "id_orden='"+this.OrdenTrabajo+"'");
	}
	
	
	public ArrayList<String> getOpcionesMovimiento(int _estadoMovimiento){
		ArrayList<String> opcionesMovimiento = new ArrayList<String>();
		opcionesMovimiento.clear();
		opcionesMovimiento.add("...");
		if(_estadoMovimiento == NO_RETIRADO){
			opcionesMovimiento.add("MEDIDOR_DIFERENTE");
			opcionesMovimiento.add("RETIRADO");
			opcionesMovimiento.add("SERVICIO_DIRECTO");
			opcionesMovimiento.add("SIN_SERVICIO");
		}else if(_estadoMovimiento == RETIRADO){
			opcionesMovimiento.add("CANCELACION_MATRIC");
			opcionesMovimiento.add("DEFINITIVO");
			opcionesMovimiento.add("PROVISIONAL");
			opcionesMovimiento.add("SERVICIO_DIRECTO");			
		}
		return opcionesMovimiento;
	}
	
	
	public ArrayList<String> getOpcionesMedidores(int _estadoMovimiento){
		ArrayList<String> opcionesMedidores= new ArrayList<String>();
		opcionesMedidores.clear();
		if(_estadoMovimiento == NO_RETIRADO){
			this._tempTabla = ContadorSQL.SelectData("amd_param_marca_contador", "id_marca||' ('||nombre||')' as medidores", "id_marca IS NOT NULL");
			for(int i=0;i<this._tempTabla.size();i++){
				opcionesMedidores.add(this._tempTabla.get(i).getAsString("medidores"));				
			}			
		}else if(_estadoMovimiento == RETIRADO){
			this._tempTabla = ContadorSQL.SelectData("vista_bodega_contadores", "medidores", "instalado = 1 ORDER BY medidores");
			for(int i=0;i<this._tempTabla.size();i++){
				opcionesMedidores.add(this._tempTabla.get(i).getAsString("medidores"));				
			}			
		}
		return opcionesMedidores;
	}
	
	
	public ArrayList<String> getOpcionesConexion(int _estadoMovimiento){
		ArrayList<String> opcionesConexion= new ArrayList<String>();
		opcionesConexion.clear();
		opcionesConexion.add("...");
		opcionesConexion.add("Servicio Directo");
		opcionesConexion.add("Sin Servicio");
		opcionesConexion.add("Monofasico");
		opcionesConexion.add("Bifasico");
		opcionesConexion.add("Trifasico");		
		return opcionesConexion;
	}
	
	
	public boolean getConfirmacionSerieLectura(String _serie1, String _serie2, String _lectura1, String _lectura2){
		boolean _retorno = false;
		if(!_lectura1.equals(_lectura2)){
			Toast.makeText(this._ctxContador,"Error en la confirmacion de la lectura.",Toast.LENGTH_SHORT).show();
		}else if(!_serie1.equals(_serie2)){
			Toast.makeText(this._ctxContador,"Error en la confirmacion de la serie.",Toast.LENGTH_SHORT).show();
		}else{
			_retorno = true;
		}
		return _retorno;
	}
	
	
	/**Funcion encargada de ejecutar las acciones para registrar el movimiento del medidor ya sea retirado o instalado**/
	public int registrarMovimientoMedidor(int _estadoMovimiento, String _movimiento, String _marca, String _serie, String _lectura){
		int _retorno = _estadoMovimiento;
		switch(_estadoMovimiento){
			case NO_RETIRADO:		
				if(_movimiento.equals("MEDIDOR_DIFERENTE")){
					this._tipo 		= "R";
					this._marca 	= ContadorSQL.StrSelectShieldWhere("amd_param_marca_contador", "id_marca", "id_marca||' ('||nombre||')'='"+_marca+"'");
					this._serie 	= _serie;
					this._cobro 	= ContadorSQL.StrSelectShieldWhere("vista_contador_cliente_orden", "tipo", "cuenta='"+this.CuentaCliente+"'");
					this._lectura 	= _lectura;					
				}else if(_movimiento.equals("RETIRADO")){
					this._tipo 		= "R";
					this._marca 	= ContadorSQL.StrSelectShieldWhere("amd_param_marca_contador", "id_marca", "id_marca||' ('||nombre||')'='"+_marca+"'");
					this._serie 	= _serie;
					this._cobro 	= ContadorSQL.StrSelectShieldWhere("vista_contador_cliente_orden", "tipo", "cuenta='"+this.CuentaCliente+"'");
					this._lectura 	= _lectura;
				}else if(_movimiento.equals("SERVICIO_DIRECTO")){
					this._tipo 		= "R";
					this._marca 	= "SD";
					this._serie 	= _serie;
					this._cobro 	= "SD";
					this._lectura 	= _lectura;
				}else if(_movimiento.equals("SIN_SERVICIO")){
					this._tipo 		= "R";
					this._marca 	= "SS";
					this._serie 	= _serie;
					this._cobro 	= "SS";
					this._lectura 	= _lectura;
				}
				if(this.guardarDatosMedidor(this._tipo, this._marca, this._serie, this._lectura, this._cobro)){
					if(_movimiento.equals("MEDIDOR_DIFERENTE")){
						//Registrar irregularidad 18;
					}
					_retorno = RETIRADO;
				}
				break;
				
			case RETIRADO:
				if(_movimiento.equals("CANCELACION_MATRICULA")){
					this._tipo 	= "CM";
					this._marca = "CM";
					this._serie = "1";
					this._cobro = "CM";
					this._lectura = "-1";
				}else if(_movimiento.equals("DEFINITIVO")){
					this._tipo 	= "D";
					this._marca = ContadorSQL.StrSelectShieldWhere("amd_param_marca_contador", "id_marca", "id_marca||' ('||nombre||')'='"+_marca+"'");
					this._serie = _serie;
					this._cobro = ContadorSQL.StrSelectShieldWhere("amd_bodega_contadores", "tipo", "marca='"+this._marca+"' AND serie='"+this._serie+"' AND instalado='1'");
					this._lectura = _lectura;
					this._instalado = "2";
				}else if(_movimiento.equals("PROVISIONAL")){
					this._tipo 	= "P";
					this._marca = ContadorSQL.StrSelectShieldWhere("amd_param_marca_contador", "id_marca", "id_marca||' ('||nombre||')'='"+_marca+"'");
					this._serie = _serie;
					this._cobro = ContadorSQL.StrSelectShieldWhere("amd_bodega_contadores", "tipo", "marca='"+this._marca+"' AND serie='"+this._serie+"' AND instalado='1'");
					this._lectura = _lectura;
					this._instalado = "3";
				}else if(_movimiento.equals("SERVICIO_DIRECTO")){
					this._tipo 	= "SD";
					this._marca = "SD";
					this._serie = "1";
					this._cobro = "SD";
					this._lectura = "-1";
				}
				
				if(this.guardarDatosMedidor(this._tipo, this._marca, this._serie, this._lectura, this._cobro)){
					this.registrarUsoMedidor(this._marca, this._serie);
					_retorno = INSTALADO;
				}
				break;
		}
		return _retorno;
	}
	
	
	public int eliminarMovimientoMedidor(int _estadoMovimiento){
		int _retorno = _estadoMovimiento;
		switch(_estadoMovimiento){
			case NO_RETIRADO:
				break;
				
			case RETIRADO:
				if(this.eliminarDatosMedidor("tipo='R'")){
					_retorno = NO_RETIRADO;
				}
				break;
				
			case INSTALADO:
				this._marca = ContadorSQL.StrSelectShieldWhere("amd_cambios_contadores", "marca", "id_orden='"+this.OrdenTrabajo+"' AND tipo<>'R'");
				this._serie = ContadorSQL.StrSelectShieldWhere("amd_cambios_contadores", "serie", "id_orden='"+this.OrdenTrabajo+"' AND tipo<>'R'");
				if(this.eliminarDatosMedidor("tipo<>'R'")){
					this.eliminarUsoMedidor(this._marca, this._serie);
					_retorno = RETIRADO;
				}
				break;
		}		
		return _retorno;
	}
	
	
	/*********************************Funciones para guardar o eliminar datos del medidor segun el movimiento que se haga*********************************************/
	private boolean guardarDatosMedidor(String _tipo, String _marca, String _serie, String _lectura, String _cobro){
		boolean _retorno = false;
		this._tempRegistro.clear();
		this._tempRegistro.put("id_orden",this.OrdenTrabajo);
		this._tempRegistro.put("tipo",_tipo);
		this._tempRegistro.put("marca",_marca);
		this._tempRegistro.put("serie",_serie);
		this._tempRegistro.put("lectura",_lectura);
		this._tempRegistro.put("cobro",_cobro);
		this._tempRegistro.put("cuenta",this.CuentaCliente);
		this._tempRegistro.put("usuario_ins",this.CedulaTecnico);
		if(ContadorSQL.InsertRegistro("amd_cambios_contadores", this._tempRegistro)){
			_retorno = true;
		}
		return _retorno;		
	}
		
	private boolean eliminarDatosMedidor(String _condicion){
		return ContadorSQL.DeleteRegistro("amd_cambios_contadores", "id_orden='"+this.OrdenTrabajo+"' AND "+_condicion);
	}
		
	
	/************************************Funciones para registrar los movimientos de los medidores que hay en bodega del tecnico*************************************/
	private boolean registrarUsoMedidor(String _marca, String _serie){
		this._tempRegistro.clear();
		this._tempRegistro.put("instalado", this._instalado);
		return ContadorSQL.UpdateRegistro("amd_bodega_contadores", this._tempRegistro, "marca='"+_marca+"' AND serie='"+_serie+"'");
	}
	
	private boolean eliminarUsoMedidor(String _marca, String _serie){
		this._tempRegistro.clear();
		this._tempRegistro.put("instalado", "1");
		return ContadorSQL.UpdateRegistro("amd_bodega_contadores", this._tempRegistro, "marca='"+_marca+"' AND serie='"+_serie+"'");
	}
}
