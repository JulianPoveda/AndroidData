package class_amdata;

import java.util.ArrayList;

import miscelanea.SQLite;
import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

public class Class_Impresiones {
	private SQLite 					ActasSQL;	
	private ContentValues 			_tempRegistro = new ContentValues();
	private ArrayList<ContentValues>_tempTabla	= new ArrayList<ContentValues>();
	private Context 				_ctxActas;
	private String					_folderActas;
	private String 					CedulaTecnico;
	private String 					OrdenTrabajo;
	private String 					CuentaCliente;

	
	
	public Class_Impresiones(Context _ctx, String _folderAplicacion, String _cedulaTecnico){
		this._ctxActas		= _ctx;
		this._folderActas	= _folderAplicacion;
		this.CedulaTecnico 	= _cedulaTecnico;
		ActasSQL	= new SQLite(this._ctxActas, this._folderActas);
	}
	
	
	
	/**Funcion encargada de validar los datos antes de imprimir el acta de revision**/
	public boolean validarDatosImpresionActa(String _ordenTrabajo){
		boolean _retorno = true;
		
		/**validacion de los sellos**/
		if(!this.ActasSQL.ExistRegistros("amd_sellos", "id_orden='"+_ordenTrabajo+"'")){
			if(!this.ActasSQL.ExistRegistros("amd_inconsistencias", "id_orden='"+_ordenTrabajo+"' AND cod_inconsistencia='AD13'")){
				Toast.makeText(this._ctxActas,"No existen registro de sellos ni observacion administrativa AD13, no es posible imprimir.", Toast.LENGTH_SHORT).show();
				_retorno = false;
			}
		}
			/**validacion medidor instalado**/
			if(!this.ActasSQL.ExistRegistros("amd_cambios_contadores", "id_orden='"+_ordenTrabajo+"'")){
				if(!this.ActasSQL.ExistRegistros("amd_inconsistencias", "id_orden='"+_ordenTrabajo+"' AND cod_inconsistencia='AD15'")){
					Toast.makeText(this._ctxActas,"No existen registro de instalacion de contador ni observacion administrativa AD15, no es posible imprimir.", Toast.LENGTH_SHORT).show();
					_retorno = false;
				}
	
			}
			/**validacion acometida**/
				if(!this.ActasSQL.ExistRegistros("amd_acometida", "id_orden='"+_ordenTrabajo+"'")){
					if(!this.ActasSQL.ExistRegistros("amd_inconsistencias", "id_orden='"+_ordenTrabajo+"' AND cod_inconsistencia='AD10'")){
						Toast.makeText(this._ctxActas,"No existen registro de acometida ni observacion administrativa AD10, no es posible imprimir.", Toast.LENGTH_SHORT).show();
						_retorno = false;
					}
				}
		/**validacion aforo**/
					if(!this.ActasSQL.ExistRegistros("amd_censo_carga", "id_orden='"+_ordenTrabajo+"'")){
						if(!this.ActasSQL.ExistRegistros("amd_inconsistencias", "id_orden='"+_ordenTrabajo+"' AND cod_inconsistencia='AD11'")){
							Toast.makeText(this._ctxActas,"No existen registro de censo de carga ni observacion administrativa AD11, no es posible imprimir.", Toast.LENGTH_SHORT).show();
							_retorno = false;
						}
					}
		/**validacion porcentaje error**/
		if(!this.ActasSQL.ExistRegistros("amd_pct_error", "id_orden='"+_ordenTrabajo+"'")){
			if(!this.ActasSQL.ExistRegistros("amd_inconsistencias", "id_orden='"+_ordenTrabajo+"' AND cod_inconsistencia='AD12'")){
				Toast.makeText(this._ctxActas,"No existen registro de Pct Error ni observacion administrativa AD12, no es posible imprimir.", Toast.LENGTH_SHORT).show();
				_retorno = false;
			}
		}		

		/**validacion IRREGULARIDADES**/
		if(!this.ActasSQL.ExistRegistros("amd_irregularidades", "id_orden='"+_ordenTrabajo+"'")){
			if(!this.ActasSQL.ExistRegistros("amd_inconsistencias", "id_orden='"+_ordenTrabajo+"' AND cod_inconsistencia='AD14'")){
				Toast.makeText(this._ctxActas,"No existen registro de Irregularidades ni observacion administrativa AD14, no es posible imprimir.", Toast.LENGTH_SHORT).show();
				_retorno = false;
		}
	}		
		
		
		return _retorno;		
	}

}
