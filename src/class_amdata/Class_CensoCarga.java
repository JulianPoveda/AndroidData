package class_amdata;

import java.util.ArrayList;

import miscelanea.SQLite;
import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

public class Class_CensoCarga {
	private Context _ctxCenso;
	private String  _folderCenso;  
	private SQLite 	CensoSQL;
	
	private ContentValues			_tempRegistro = new ContentValues();
	//private ArrayList<ContentValues>_tempTabla = new ArrayList<ContentValues>(); 
	
	public Class_CensoCarga(Context _ctx, String _folderAplicacion){
		this._ctxCenso		= _ctx;
		this._folderCenso	= _folderAplicacion;
		this.CensoSQL		= new SQLite(this._ctxCenso,this._folderCenso);
	}
	
	public ArrayList<String> getRangoVatios(String _elemento, int _incremento){
		ArrayList<String> _rango = new ArrayList<String>();
		this._tempRegistro = CensoSQL.SelectDataRegistro("amd_elementos_censo","capacidad_min,capacidad_max", "descripcion='"+_elemento+"'");
		for(int i=this._tempRegistro.getAsInteger("capacidad_min");i<=this._tempRegistro.getAsInteger("capacidad_max");i+=_incremento){
			_rango.add(String.valueOf(i));
		}	
		return _rango;
	}
	
	
	public String getVatioMinimo(String _elemento){
		return this.CensoSQL.StrSelectShieldWhere("amd_elementos_censo", "capacidad_min", "descripcion='"+_elemento+"'");
	}
	
	
	public boolean verificarRango(String _elemento, String _vatios){
		boolean _retorno = true;
		this._tempRegistro = CensoSQL.SelectDataRegistro("amd_elementos_censo", "capacidad_min,capacidad_max", "descripcion='"+_elemento+"'");
		if(Double.parseDouble(_vatios)==0){
			Toast.makeText(this._ctxCenso,"El valor minimo debe ser diferente de 0.",Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(Double.parseDouble(_vatios)>this._tempRegistro.getAsDouble("capacidad_max")){
			Toast.makeText(this._ctxCenso,"El valor maximo debe ser igual o menor de "+this._tempRegistro.getAsString("capacidad_max"),Toast.LENGTH_SHORT).show();
			_retorno = false;
		}else if(Double.parseDouble(_vatios)<this._tempRegistro.getAsDouble("capacidad_min")){
			Toast.makeText(this._ctxCenso,"El valor minimo debe ser igual o mayor de "+this._tempRegistro.getAsString("capacidad_min"),Toast.LENGTH_SHORT).show();
			_retorno = false;
		}
		return _retorno;
	}

}
