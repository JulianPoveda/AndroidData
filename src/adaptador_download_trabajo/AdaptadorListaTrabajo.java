package adaptador_download_trabajo;

import java.util.ArrayList;

import sypelc.androidamdata.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdaptadorListaTrabajo extends BaseAdapter{
	protected Activity activity;
	protected ArrayList<InformacionSolicitudes> items;
	
	public AdaptadorListaTrabajo(Activity activity, ArrayList<InformacionSolicitudes> items){
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
			v = inf.inflate(R.layout.detalle_download_solicitudes, null);
		}
				
		InformacionSolicitudes Sol = items.get(position);
		
		TextView Solicitud = (TextView) v.findViewById(R.id.textView_orden_trabajo);
		TextView Nodo = (TextView) v.findViewById(R.id.textView_direccion);
		TextView Tipo = (TextView) v.findViewById(R.id.textView_tipo);
		
		/*******************************Generacion del color dependiendo del estado en el que se encuentre la orden**********************/
		if(Sol.getEstado().equals("P")){
			Solicitud.setBackgroundColor(Color.WHITE);
			Nodo.setBackgroundColor(Color.WHITE);
			Tipo.setBackgroundColor(Color.WHITE);			
		}else if(Sol.getEstado().equals("E")){
			Solicitud.setBackgroundColor(Color.GREEN);
			Nodo.setBackgroundColor(Color.GREEN);
			Tipo.setBackgroundColor(Color.GREEN);			
		}else if(Sol.getEstado().equals("T")){
			Solicitud.setBackgroundColor(Color.YELLOW);
			Nodo.setBackgroundColor(Color.YELLOW);
			Tipo.setBackgroundColor(Color.YELLOW);	
		}else{
			Solicitud.setBackgroundColor(Color.RED);
			Nodo.setBackgroundColor(Color.RED);
			Tipo.setBackgroundColor(Color.RED);	
		}
		
		Solicitud.setText(Sol.getSolicitud());				
		Nodo.setText(Sol.getDireccion());
		
		/******************************Generacion del tipo de revision segun la solicitud y la cuenta****************************/
		if((Double.parseDouble(Sol.getSolicitud())<0)&&(Double.parseDouble(Sol.getCuenta())<0)){
			Tipo.setText("Servicio Nuevo");
		}else if((Double.parseDouble(Sol.getSolicitud())<0)&&(Double.parseDouble(Sol.getCuenta())>0)){
			Tipo.setText("Autogestion");
		}else if(Sol.getSolicitud().length()>6){
			Tipo.setText("Solicitudes");
		}else{
			Tipo.setText("Revision");
		}
		return v;
	}
}
