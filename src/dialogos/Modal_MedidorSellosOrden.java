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

public class Modal_MedidorSellosOrden extends Activity implements OnClickListener, OnItemClickListener{
	private String Marca;
	private String Serie;
	private String Lectura;
	private String Tipo;
	
	private String CuentaCliente;
	private String SerieMedidor;
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
		setContentView(R.layout.activity_medidor_sellos_orden);
		
		Bundle bundle = getIntent().getExtras();
		
		this.CuentaCliente 		= bundle.getString("CuentaCliente"); 
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");		
		SellosSQL	= new SQLite(this,this.FolderAplicacion);
		
		AdaptadorSellos = new AdaptadorBodegaSellos(this, ArraySellos);
		ListaSellos = (ListView) findViewById(R.id.MedidorSellosLstDisponibles);
		ListaSellos.setAdapter(AdaptadorSellos);
		
		ArraySellos.clear();
		this._tempRegistro = SellosSQL.SelectDataRegistro("amd_contador_cliente_orden", "marca,serie,lectura", "cuenta='"+this.CuentaCliente+"'");
		ArraySellos.add(new InformacionBodegaSellos("Contador:",this._tempRegistro.getAsString("marca"),this._tempRegistro.getAsString("serie"),this._tempRegistro.getAsString("lectura")));
		this.SerieMedidor = this._tempRegistro.getAsString("serie");
		
		
		this._tempTabla = SellosSQL.SelectData("amd_sellos_contador", "numero", "serie='"+this.SerieMedidor+"' ORDER BY numero");
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			ArraySellos.add(new InformacionBodegaSellos("Sello:",this._tempRegistro.getAsString("numero"),"N/A","N/A"));
		}
		AdaptadorSellos.notifyDataSetChanged();
		
		_btnAceptar 	= (Button) findViewById(R.id.MedidorSellosBtnAceptar);
		_btnCancelar 	= (Button) findViewById(R.id.MedidorSellosBtnCancelar);
		
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
			case R.id.MedidorSellosBtnAceptar:
				finish(true);
				break;
			
			case R.id.MedidorSellosBtnCancelar:
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
			case R.id.MedidorSellosLstDisponibles:
				this.Marca 	= ArraySellos.get(position).getMarca();
				this.Serie 	= ArraySellos.get(position).getSerie();
				this.Lectura= ArraySellos.get(position).getLectura();
				this.Tipo 	= ArraySellos.get(position).getTipo();
				break;
		}	
	}
	
	
}
