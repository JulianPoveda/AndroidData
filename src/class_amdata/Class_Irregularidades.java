package class_amdata;

//import java.util.ArrayList;

import java.util.ArrayList;

import miscelanea.SQLite;
import android.content.ContentValues;
import android.content.Context;

public class Class_Irregularidades {
	private SQLite 				IrregularidadSQL;
	//private Class_Solicitudes 	FcnSolicitudes;
	
	private ContentValues 		_tempRegistro = new ContentValues();
	//private ArrayList<ContentValues>_tempTabla	= new ArrayList<ContentValues>();
	private Context 			_ctxIrreglaridad;
	private String				_folderIrregularidad;
	private String 				CedulaTecnico;
	private String 				OrdenTrabajo;
	//private String 				CuentaCliente;
	
	public Class_Irregularidades(Context _ctx, String _folderAplicacion, String _cedulaTecnico, String _ordenTrabajo, String _cuentaCliente){
		this._ctxIrreglaridad		= _ctx;
		this._folderIrregularidad	= _folderAplicacion;
		this.CedulaTecnico 	= _cedulaTecnico;
		this.OrdenTrabajo	= _ordenTrabajo;
		//this.CuentaCliente	= _cuentaCliente;
		
		IrregularidadSQL	= new SQLite(this._ctxIrreglaridad, this._folderIrregularidad);
		//FcnSolicitudes		= new Class_Solicitudes(this._ctxIrreglaridad, this._folderIrregularidad);
	}
	
	
	private int getNumeroIrregularidad(){
		return IrregularidadSQL.IntSelectShieldWhere("amd_irregularidades", "max(cast(id_anomalia AS INTEGER)) as id_anomalia","id_anomalia IS NOT NULL")+1;
	}
	
	
	public boolean registrarIrregularidad(String _strIrregularidad){
		this._tempRegistro.clear();
		this._tempRegistro.put("id_anomalia", this.getNumeroIrregularidad());
		this._tempRegistro.put("id_orden", this.OrdenTrabajo);
		this._tempRegistro.put("id_irregularidad", _strIrregularidad);
		this._tempRegistro.put("usuario_ins", this.CedulaTecnico);
		return IrregularidadSQL.InsertRegistro("amd_irregularidades", this._tempRegistro);
	}

	
	public boolean eliminarIrregularidad(String _strIrregularidad){
		return IrregularidadSQL.DeleteRegistro("amd_irregularidades", "id_orden='"+this.OrdenTrabajo+"' AND id_irregularidad='"+_strIrregularidad+"'");
	}	
	
	
	public ArrayList<ContentValues> getIrregularidades(){
		return IrregularidadSQL.SelectData("vista_irregularidades", "descripcion", "id_orden='"+this.OrdenTrabajo+"' ORDER BY id_irregularidad ASC");
	}
}
