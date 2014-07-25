package adaptador_bodega_contadores;

import java.util.ArrayList;

import sypelc.androidamdata.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdaptadorBodegaContadores extends BaseAdapter{
	protected Activity activity;
	protected ArrayList<InformacionBodegaContadores> items;
	
	public AdaptadorBodegaContadores(Activity activity, ArrayList<InformacionBodegaContadores> items){
		this.activity = activity;
		this.items = items;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return items.get(position).getId();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(convertView == null){
			LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inf.inflate(R.layout.detalle_bodega_contadores, null);
		}
				
		InformacionBodegaContadores Contadores = items.get(position);
		
		TextView Marca 		= (TextView) v.findViewById(R.id.BodegaContadorTxtMarca);
		TextView Serie 		= (TextView) v.findViewById(R.id.BodegaContadorTxtSerie);
		TextView Lectura 	= (TextView) v.findViewById(R.id.BodegaContadorTxtLectura);
		TextView Tipo 		= (TextView) v.findViewById(R.id.BodegaContadorTxtTipo);
		
		/*******************************Generacion del color dependiendo del estado en el que se encuentre la orden**********************/
		Marca.setText(Contadores.getMarca());
		Serie.setText(Contadores.getSerie());
		Lectura.setText(Contadores.getLectura());
		Tipo.setText(Contadores.getTipo());
		return v;
	}
	
	
	public void setColorItem(int position){
		
	}
}