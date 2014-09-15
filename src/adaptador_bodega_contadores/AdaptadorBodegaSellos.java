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

public class AdaptadorBodegaSellos extends BaseAdapter{
	protected Activity activity;
	protected ArrayList<InformacionBodegaSellos> items;
	
	public AdaptadorBodegaSellos(Activity activity, ArrayList<InformacionBodegaSellos> items){
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
			v = inf.inflate(R.layout.detalle_bodega_sellos, null);
		}
				
		InformacionBodegaSellos Sellos = items.get(position);
		
		TextView Marca 		= (TextView) v.findViewById(R.id.BodegaSelloTxtMarca);
		TextView Serie 		= (TextView) v.findViewById(R.id.BodegaSelloTxtSerie);
		TextView Lectura 	= (TextView) v.findViewById(R.id.BodegaSelloTxtLectura);
		TextView Tipo 		= (TextView) v.findViewById(R.id.BodegaSelloTxtTipo);
		
		/*******************************Generacion del color dependiendo del estado en el que se encuentre la orden**********************/
		Marca.setText(Sellos.getMarca());
		Serie.setText(Sellos.getSerie());
		Lectura.setText(Sellos.getLectura());
		Tipo.setText(Sellos.getTipo());
		return v;
	}
	
	
	public void setColorItem(int position){
		
	}
}