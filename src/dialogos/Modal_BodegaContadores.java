package dialogos;

import java.util.ArrayList;

import miscelanea.SQLite;
import sypelc.androidamdata.R;
import adaptador_bodega_contadores.AdaptadorBodegaContadores;
import adaptador_bodega_contadores.InformacionBodegaContadores;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class Modal_BodegaContadores extends Activity implements OnClickListener, OnItemClickListener {
	private String Marca;
	private String Serie;
	private String Lectura;
	private String Tipo;
	
	private String FolderAplicacion;
	private ContentValues _tempRegistro 		= new ContentValues();
	private ArrayList<ContentValues> _tempTabla = new ArrayList<ContentValues>();
	
	SQLite 			ContadoresSQL; 
	Button			_btnAceptar, _btnCancelar;
	
	AdaptadorBodegaContadores AdaptadorContadores;
	ArrayList<InformacionBodegaContadores> ArrayContadores = new ArrayList<InformacionBodegaContadores>();
	ListView ListaContadores;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bodega_contadores);
		
		Bundle bundle = getIntent().getExtras();
		this.FolderAplicacion= bundle.getString("FolderAplicacion");		
		ContadoresSQL	= new SQLite(this,this.FolderAplicacion);
		
		AdaptadorContadores = new AdaptadorBodegaContadores(this, ArrayContadores);
		ListaContadores = (ListView) findViewById(R.id.BodegaContadoresLstDisponibles);
		ListaContadores.setAdapter(AdaptadorContadores);
		
		ArrayContadores.clear();
		this._tempTabla = ContadoresSQL.SelectData("vista_bodega_contadores", "medidores,serie,lectura,descripcion", "instalado = 1 ORDER BY medidores,serie,lectura,descripcion");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			ArrayContadores.add(new InformacionBodegaContadores(this._tempRegistro.getAsString("medidores"),this._tempRegistro.getAsString("serie"),this._tempRegistro.getAsString("lectura"),this._tempRegistro.getAsString("descripcion")));
		}
		AdaptadorContadores.notifyDataSetChanged();
		
		_btnAceptar 	= (Button) findViewById(R.id.BodegaContadoresBtnAceptar);
		_btnCancelar 	= (Button) findViewById(R.id.BodegaContadoresBtnCancelar);
		
		ListaContadores.setOnItemClickListener(this);
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
			case R.id.BodegaContadoresBtnAceptar:
				finish(true);
				break;
			
			case R.id.BodegaContadoresBtnCancelar:
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
			case R.id.BodegaContadoresLstDisponibles:
				this.Marca 	= ArrayContadores.get(position).getMarca();
				this.Serie 	= ArrayContadores.get(position).getSerie();
				this.Lectura= ArrayContadores.get(position).getLectura();
				this.Tipo 	= ArrayContadores.get(position).getTipo();
				break;
		}	
	}
}
