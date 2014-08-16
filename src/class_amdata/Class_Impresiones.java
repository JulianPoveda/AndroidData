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
		
		
		return _retorno;		
	}
}
