package class_amdata;

import java.util.ArrayList;

import miscelanea.SQLite;
import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

public class Class_Sellos {
	private SQLite 					SellosSQL;
	
	private ContentValues 			_tempRegistro = new ContentValues();
	private ArrayList<ContentValues>_tempTabla	= new ArrayList<ContentValues>();
	private Context 				_ctxSellos;
	private String					_folderSellos;
	private String 					CedulaTecnico;
	private String 					OrdenTrabajo;
	private String 					CuentaCliente;

	
	
	public Class_Sellos(Context _ctx, String _folderAplicacion, String _cedulaTecnico, String _ordenTrabajo, String _cuentaCliente){
		this._ctxSellos		= _ctx;
		this._folderSellos	= _folderAplicacion;
		this.CedulaTecnico 	= _cedulaTecnico;
		this.OrdenTrabajo	= _ordenTrabajo;
		this.CuentaCliente	= _cuentaCliente;
		SellosSQL	= new SQLite(this._ctxSellos, this._folderSellos);
	}
	
	
	
	/**Funcion encargada de realizar los registros de sellos en las tablas respectivas**/
	public boolean registrarSello(String _tipoIngreso, String _tipoSello, String _ubicacionSello, String _colorSello, String _serieSello, String _estadoSello){
		boolean _retorno = false;
		if(_tipoIngreso.equals("RETIRADO")){
			if(!this.validarSelloRetirado(_serieSello)){
				//Registrar irregularidad
			}
			this.guardarDatosSello(_tipoIngreso, _tipoSello, _ubicacionSello, _colorSello, _serieSello, _estadoSello);
			_retorno = true;
		}else if(_tipoIngreso.equals("INSTALADO")){
			if(_ubicacionSello.equals("L-Tapa principal")){
				_retorno = this.guardarDatosSello(_tipoIngreso, _tipoSello, _ubicacionSello, _colorSello, _serieSello, _estadoSello);
			}else if(this.validarExistenciaBodega(_tipoSello, _serieSello, _colorSello)){
				this.guardarDatosSello(_tipoIngreso, _tipoSello, _ubicacionSello, _colorSello, _serieSello, _estadoSello);
				this.registrarUsoSelloBodega(_serieSello, _tipoSello, _colorSello);
				_retorno = true;
			}else{
				Toast.makeText(this._ctxSellos,"No existe registro del sello en bodega.",Toast.LENGTH_SHORT).show();
			}
		}else if(_tipoIngreso.equals("EXISTENTE")){
			if(!this.validarSelloRetirado(_serieSello)){
				//Registrar irregularidad
			}
			_retorno = this.guardarDatosSello(_tipoIngreso, _tipoSello, _ubicacionSello, _colorSello, _serieSello, _estadoSello);
		}		
		return _retorno;		
	}
	
	
	/**Funcion encargada de realizar la eliminacion del registro el de las tablas respectivas**/
	public boolean eliminarSello(String _tipoIngreso,  String _serieSello){
		boolean _retorno = false;
		if(this.validarMovimientoSelloEliminado(_serieSello)){
			if(_tipoIngreso.equals("RETIRADO")){
				_retorno = this.eliminarDatosSello(_serieSello);			
			}else if(_tipoIngreso.equals("INSTALADO")){
				_retorno = this.eliminarDatosSello(_serieSello) && this.eliminarUsoSelloBodega(_serieSello);
			}else if(_tipoIngreso.equals("EXISTENTE")){
				_retorno = this.eliminarDatosSello(_serieSello);		
			}
		}
		return _retorno;
	}
	
	
	/**Funcion para guardar el registro del sello**/
	private boolean guardarDatosSello(String _tipoIngreso, String _tipoSello, String _ubicacionSello, String _colorSello,  String _serieSello, String _estadoSello){
		this._tempRegistro.clear();
		this._tempRegistro.put("id_orden", this.OrdenTrabajo);
		this._tempRegistro.put("estado",_tipoIngreso);
		this._tempRegistro.put("tipo", _tipoSello);
		this._tempRegistro.put("numero", _serieSello);
		this._tempRegistro.put("color", _colorSello);
		this._tempRegistro.put("irregularidad", _estadoSello);
		this._tempRegistro.put("ubicacion", _ubicacionSello);
		this._tempRegistro.put("usuario_ins", this.CedulaTecnico);		
		return this.SellosSQL.InsertRegistro("amd_sellos", this._tempRegistro);
	}
	
	
	/**funcion para eliminar el registro del sello**/
	private boolean eliminarDatosSello(String _serieSello){
		return this.SellosSQL.DeleteRegistro("amd_sellos", "id_orden='"+this.OrdenTrabajo+"' AND numero='"+_serieSello+"'");
	}
	
	
	
	/**Funcion para realizar la validacion que el sellos instalado exista en bodega**/
	private boolean validarExistenciaBodega(String _tipoSello, String _serieSello, String _colorSello){
		return this.SellosSQL.ExistRegistros("amd_bodega_sellos", "tipo='"+_tipoSello+"' AND numero='"+_serieSello+"' AND color='"+_colorSello+"' AND estado = 'P'");
	}
	
	
	/**Funcion para confrontar los sellos retirados y existentes con los registrados en el sistema**/
	private boolean validarSelloRetirado(String _serieSello){
		return this.SellosSQL.ExistRegistros("vista_sellos_contador", "cuenta='"+this.CuentaCliente+"' AND numero = '"+_serieSello+"'");
	}
	
	
	/**Funcion para validar el movimiento del sello a eliminar, en efecto corresponda al registrado en amd_sellos**/
	private boolean validarMovimientoSelloEliminado(String _serieSello){
		return this.SellosSQL.ExistRegistros("amd_sellos", "numero='"+_serieSello+"'" );
	}
	
	
	/**funcion para registrar el uso del sello que se tenia como disponible en bodega**/
	private boolean registrarUsoSelloBodega(String _serieSello, String _tipoSello, String _colorSello){
		this._tempRegistro.clear();
		this._tempRegistro.put("estado", "I");
		return this.SellosSQL.UpdateRegistro("amd_bodega_sellos", this._tempRegistro, "numero='"+_serieSello+"' AND tipo='"+_tipoSello+"' AND color='"+_colorSello+"'");
	}
	
	
	/**funcion para eliminar el registro del sello en la bodega**/
	private boolean eliminarUsoSelloBodega(String _serieSello){
		this._tempRegistro.clear();
		this._tempRegistro.put("estado", "P");
		return this.SellosSQL.UpdateRegistro("amd_bodega_sellos", this._tempRegistro, "numero='"+_serieSello+"'");
	}
	
	/**Funcion para confirmar la serie**/
	public boolean getConfirmacionSerie(String _serie1, String _serie2){
		boolean _retorno = false;
		if(!_serie1.equals(_serie2)){
			Toast.makeText(this._ctxSellos,"Error en la confirmacion de la serie.",Toast.LENGTH_SHORT).show();
		}else{
			_retorno = true;
		}
		return _retorno;
	}	
	
	/**Funcion que retorna todos los sellos que se han registrado**/
	public ArrayList<ContentValues> getSellosRegistrados(){
		this._tempTabla.clear();
		this._tempTabla = SellosSQL.SelectData("amd_sellos", "estado as tipo_ingreso, tipo as tipo_sello, numero as serie, color, ubicacion, irregularidad", "id_orden='"+this.OrdenTrabajo+"'");
		return this._tempTabla;
	}
}
