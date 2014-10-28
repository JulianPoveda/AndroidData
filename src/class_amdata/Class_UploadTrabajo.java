package class_amdata;

import java.util.ArrayList;

import miscelanea.Archivos;
import miscelanea.SQLite;
import sypelc.androidamdata.R;
import sypelc.androidamdata.UploadTrabajo;
import adaptador_upload_trabajo.UploadAdapter;
import adaptador_upload_trabajo.UploadData;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

public class Class_UploadTrabajo {
	

	private UploadAdapter adapter;
	private ArrayList<UploadData> data = new ArrayList<UploadData>();
	private ListView _lstPrueba;
	private SQLite 		UploadSQL;
	private String 				StringSQL;
	private Context contextU;
	private String 				FolderAplicacion;
	private ContentValues				_tempRegistro 	= new ContentValues();
	private ArrayList<ContentValues>	_tempTabla		= new ArrayList<ContentValues>();
	private ArrayList<ContentValues>	_tempChecked	= new ArrayList<ContentValues>();
	private ArrayList<ContentValues>	_tempTabla1		= new ArrayList<ContentValues>();
	private ContentValues				_tempRegistro1	= new ContentValues();
	private ArrayList<String>           _Ordenes        = new ArrayList<String>();
	ArrayList<String> _OrdenesS = new ArrayList<String>();
	private ArrayList<String> 	InformacionCarga = new ArrayList<String>(); //arrary donde se esta colocando toda la informacion
	private String _NPDA;
	
	public Class_UploadTrabajo(Context context, String Directorio){
		this.contextU= context;
		this.FolderAplicacion 	= Directorio;	
		UploadSQL	= new SQLite(contextU,FolderAplicacion);
		}
	
	public void MostrarOrdenesTrabajo(){
		_tempTabla.clear();
		_tempTabla = UploadSQL.SelectData("amd_ordenes_trabajo", "id_orden,id_nodo,cuenta,estado", "estado in ('T','TA') ORDER BY id_orden");
		for(int i=0;i<_tempTabla.size();i++){
			_tempRegistro = _tempTabla.get(i);
			data.add(new UploadData(_tempRegistro.getAsString("id_orden"),_tempRegistro.getAsString("id_nodo"),_tempRegistro.getAsString("cuenta"),_tempRegistro.getAsString("estado"), false));
		}
		
	}
	
  public ArrayList<String> OrdenesEnviar(){
	  ArrayList<String> _Ordenes = new ArrayList<String>(); 
	  for(int i=0;i<data.size();i++){
		  if(data.get(i).getChecked()){
			  _Ordenes.add(data.get(i).getOrden());  
		  }	  
	  }
	  return _Ordenes;
  }
  
  public void DescargarActas(String PDA, ArrayList<String> ordenes){
	  
		_OrdenesS=ordenes;
				this.InformacionCarga.clear();
				//Incio archivo
				StringSQL="INICIO ARCHIVO" + "&ENT&";
				this.InformacionCarga.add(StringSQL);
				
				//se consulta la vista con las ordenes seleccionadas 
				for(int j=0; j<this._OrdenesS.size();j++){
				this._tempTabla	= this.UploadSQL.SelectData("upload_nodos_exp", "id_acta, id_orden,id_revision, codigo_trabajo, nombre_enterado, cedula_enterado, evento, tipo_enterado, fecha_revision, login, fecha_ins, cedula_testigo, nombre_testigo","id_orden='"+this._OrdenesS.get(j)+"'");
				for(int i=0; i<this._tempTabla.size();i++){
					this._tempRegistro = this._tempTabla.get(i);
						  this.InformacionCarga.add("INSERT INTO SGD_ACTAS_PDA VALUES "+"('"+this._tempRegistro.getAsString("id_acta")+"','"+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("id_revision")+"','"
									+this._tempRegistro.getAsString("codigo_trabajo")+"','"+this._tempRegistro.getAsString("cedula_enterado")+"','"+this._tempRegistro.getAsString("nombre_enterado")+"','"
									+this._tempRegistro.getAsString("id_orden")+"','"+this._tempRegistro.getAsString("tipo_enterado")+"','"+this._tempRegistro.getAsString("fecha_revision")+"','"
									+this._tempRegistro.getAsString("usuario_ins")+"','"+this._tempRegistro.getAsString("fecha_ins")+"','"+this._tempRegistro.getAsString("cedula_testigo")+"','"
									+this._tempRegistro.getAsString("nombre_testigo")+"','"+_NPDA+"','"+this._tempRegistro.getAsString("1")+"','"+this._tempRegistro.getAsString("@")+"')"+";"+"&ENT&");
					  
				}
			}
				
				this.InformacionCarga.add("FIN ARCHIVO");
				int tam=this.InformacionCarga.size();
				Archivos cmd = new Archivos(contextU,FolderAplicacion,tam);
				String listString = "";
				for (String s : InformacionCarga)
				{
				    listString += s;
				}
				cmd.DoFile(FolderAplicacion,"SGD_ACTAS_PDA",listString);
	}

}
