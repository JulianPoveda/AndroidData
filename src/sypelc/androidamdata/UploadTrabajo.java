package sypelc.androidamdata;

import java.util.ArrayList;

import ws_asynchronous.DownLoadTrabajo;
import ws_asynchronous.UpLoadTrabajo;
import class_amdata.Class_UploadTrabajo;
import miscelanea.SQLite;
import adaptador_download_trabajo.InformacionSolicitudes;
import adaptador_upload_trabajo.UploadAdapter;
import adaptador_upload_trabajo.UploadData;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class UploadTrabajo extends Activity {
	
	static final int DIALOG_CONFIRM = 0;
	protected static final int REQUEST_CODE = 10;
	
	private UploadAdapter adapter;
	private ArrayList<UploadData> data = new ArrayList<UploadData>();;
	private ListView _lstPrueba;
	private SQLite 		UploadSQL;
	private String 	FolderAplicacion = "";
	private Context c;
	ws_asynchronous.UpLoadTrabajo _upOrdenes = new ws_asynchronous.UpLoadTrabajo(c,FolderAplicacion);
	
	private ContentValues				_tempRegistro 	= new ContentValues();
	private ArrayList<ContentValues>	_tempTabla		= new ArrayList<ContentValues>();
	private ArrayList<ContentValues>	_tempChecked	= new ArrayList<ContentValues>();
	ArrayList<String> _Ordenes = new ArrayList<String>();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_trabajo);
		
		Bundle bundle = getIntent().getExtras();
		this.FolderAplicacion= bundle.getString("FolderAplicacion");
		UploadSQL	= new SQLite(this,FolderAplicacion);
		
		_lstPrueba=(ListView)findViewById(R.id.SellosLstSellos);
		adapter = new UploadAdapter(UploadTrabajo.this, data);
		
		if (savedInstanceState == null){
			adapter = new UploadAdapter(UploadTrabajo.this, data);
		} else{
			data = savedInstanceState.getParcelableArrayList("savedData");
			adapter = new UploadAdapter(UploadTrabajo.this, data);	
		}
		
		_lstPrueba.setAdapter(adapter);
		
		MostrarOrdenesTrabajo();
		adapter.notifyDataSetChanged();
	}

	public void MostrarOrdenesTrabajo(){
		_tempTabla.clear();
		data.clear();
		_tempTabla = UploadSQL.SelectData("amd_ordenes_trabajo", "id_orden,id_nodo,cuenta,estado", "estado in ('T','TA') ORDER BY id_orden");
		for(int i=0;i<_tempTabla.size();i++){
			_tempRegistro = _tempTabla.get(i);
			data.add(new UploadData(_tempRegistro.getAsString("id_orden"),_tempRegistro.getAsString("id_nodo"),_tempRegistro.getAsString("cuenta"),_tempRegistro.getAsString("estado"), false));
		}
		
	}
	
  public ArrayList<String> OrdenesEnviar(){ 
	  for(int i=0;i<data.size();i++){
		  if(data.get(i).getChecked()){
			  _Ordenes.add(data.get(i).getOrden());  
		  }	  
	  }
	  return _Ordenes;
   }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload_trabajo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.select_all:
			adapter.checkAll(true);
			adapter.notifyDataSetChanged();
			break;

		case R.id.unselect_all:
			adapter.checkAll(false);
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		
		case R.id.envio:
			//adapter.checkAll(true);
			OrdenesEnviar();
			new UpLoadTrabajo(this, this.FolderAplicacion).execute(this._Ordenes);
			break;
			
		}

		return true;
	}
	
	
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("savedData", data);
		super.onSaveInstanceState(outState);
	}
}
