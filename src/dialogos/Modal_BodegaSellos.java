package dialogos;

import java.util.ArrayList;

import miscelanea.SQLite;
import sypelc.androidamdata.R;
import adaptador_bodega_contadores.AdaptadorBodegaSellos;
import adaptador_bodega_contadores.InformacionBodegaSellos;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Modal_BodegaSellos extends Activity implements OnClickListener, OnItemClickListener{
	private String Marca;
	private String Serie;
	private String Lectura;
	private String Tipo;
	
	private String FolderAplicacion;
	private ContentValues _tempRegistro 		= new ContentValues();
	private ArrayList<ContentValues> _tempTabla = new ArrayList<ContentValues>();
	
	SQLite 			SellosSQL; 
	Button			_btnAceptar, _btnCancelar;
	
	AdaptadorBodegaSellos AdaptadorSellos;
	ArrayList<InformacionBodegaSellos> ArraySellos = new ArrayList<InformacionBodegaSellos>();
	ListView ListaSellos;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bodega_sellos);
		
		Bundle bundle = getIntent().getExtras();
		this.FolderAplicacion= bundle.getString("FolderAplicacion");		
		SellosSQL	= new SQLite(this,this.FolderAplicacion);
		
		AdaptadorSellos = new AdaptadorBodegaSellos(this, ArraySellos);
		ListaSellos = (ListView) findViewById(R.id.BodegaSellosLstDisponibles);
		ListaSellos.setAdapter(AdaptadorSellos);
		
		ArraySellos.clear();
		this._tempTabla = SellosSQL.SelectData("amd_bodega_sellos", "tipo,numero,color,estado", "estado IS NOT NULL ORDER BY tipo,numero,color,estado");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			ArraySellos.add(new InformacionBodegaSellos(this._tempRegistro.getAsString("tipo"),this._tempRegistro.getAsString("numero"),this._tempRegistro.getAsString("color"),this._tempRegistro.getAsString("estado")));
		}
		AdaptadorSellos.notifyDataSetChanged();
		
		_btnAceptar 	= (Button) findViewById(R.id.BodegaSellosBtnAceptar);
		_btnCancelar 	= (Button) findViewById(R.id.BodegaSellosBtnCancelar);
		
		ListaSellos.setOnItemClickListener(this);
		_btnAceptar.setOnClickListener(this);
		_btnCancelar.setOnClickListener(this);
	}
	
	
	public void finish(boolean _caso) {
		Intent data = new Intent();
		data.putExtra("response", _caso);
		data.putExtra("marca", this.Marca);
		data.putExtra("serie", this.Serie);
		data.putExtra("lectura", this.Lectura);
		data.putExtra("tipo", this.Tipo);		
		setResult(RESULT_OK, data);
		super.finish();
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.BodegaSellosBtnAceptar:
				finish(true);
				break;
			
			case R.id.BodegaSellosBtnCancelar:
				finish(false);
				break;
				
			default:
				finish(false);
				break;
		}	
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		switch(parent.getId()){
			case R.id.BodegaSellosLstDisponibles:
				this.Marca 	= ArraySellos.get(position).getMarca();
				this.Serie 	= ArraySellos.get(position).getSerie();
				this.Lectura= ArraySellos.get(position).getLectura();
				this.Tipo 	= ArraySellos.get(position).getTipo();
				break;
		}	
	}
}
