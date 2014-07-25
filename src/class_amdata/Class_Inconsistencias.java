package class_amdata;

//import java.util.ArrayList;

import java.util.ArrayList;

import miscelanea.SQLite;
import android.content.ContentValues;
import android.content.Context;

public class Class_Inconsistencias {
	private SQLite 				InconsistenciaSQL;
	private Class_Solicitudes 	FcnSolicitudes;
	
	private ContentValues 			_tempRegistro = new ContentValues();
	//private ArrayList<ContentValues>_tempTabla	= new ArrayList<ContentValues>();
	private Context 				_ctxInconsistencia;
	private String					_folderInconsistencia;
	private String 					CedulaTecnico;
	private String 					OrdenTrabajo;
	private String 					CuentaCliente;
	
	public Class_Inconsistencias(Context _ctx, String _folderAplicacion, String _cedulaTecnico, String _ordenTrabajo, String _cuentaCliente){
		this._ctxInconsistencia		= _ctx;
		this._folderInconsistencia	= _folderAplicacion;
		this.CedulaTecnico 	= _cedulaTecnico;
		this.OrdenTrabajo	= _ordenTrabajo;
		this.CuentaCliente	= _cuentaCliente;
		
		InconsistenciaSQL	= new SQLite(this._ctxInconsistencia, this._folderInconsistencia);
		FcnSolicitudes		= new Class_Solicitudes(this._ctxInconsistencia, this._folderInconsistencia);
	}
	
	
	private int getNumeroInconsistencia(){
		return InconsistenciaSQL.IntSelectShieldWhere("amd_inconsistencias", "max(cast(id_inconsistencia AS INTEGER)) as id_inconsistencia","id_inconsistencia IS NOT NULL")+1;
	}
	
	
	public boolean registrarInconsistencia(String _strValor, String _strInconsistencia, String _tipo){
		eliminarInconsistencia(_strInconsistencia);
		
		this._tempRegistro.clear();
		this._tempRegistro.put("id_inconsistencia", this.getNumeroInconsistencia());
		this._tempRegistro.put("id_orden", this.OrdenTrabajo);
		this._tempRegistro.put("id_nodo", FcnSolicitudes.getNodo(this.OrdenTrabajo));
		this._tempRegistro.put("usuario_ins", this.CedulaTecnico);
		this._tempRegistro.put("cuenta", this.CuentaCliente);
		this._tempRegistro.put("tipo", _tipo);
		this._tempRegistro.put("trabajo", FcnSolicitudes.getIdTrabajoContrato(this.OrdenTrabajo));
		this._tempRegistro.put("valor", _strValor);
		this._tempRegistro.put("cod_inconsistencia", _strInconsistencia);
		return InconsistenciaSQL.InsertRegistro("amd_inconsistencias", this._tempRegistro);
	}

	
	public boolean eliminarInconsistencia(String _strInconsistencia){
		return InconsistenciaSQL.DeleteRegistro("amd_inconsistencias", "id_orden='"+this.OrdenTrabajo+"' AND cod_inconsistencia='"+_strInconsistencia+"'");
	}
	
	
	public ArrayList<ContentValues> getInconsistencias(){
		return InconsistenciaSQL.SelectData("amd_inconsistencias", "id_nodo,valor,cod_inconsistencia as codigo", "id_orden='"+this.OrdenTrabajo+"' AND tipo='M' ORDER BY cod_inconsistencia ASC");
	}
	
}
