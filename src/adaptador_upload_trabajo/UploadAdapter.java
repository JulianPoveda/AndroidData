package adaptador_upload_trabajo;

import java.util.ArrayList;

import sypelc.androidamdata.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class UploadAdapter extends BaseAdapter {
	
	
	private ArrayList<UploadData> data;
	private LayoutInflater inflater = null;
	
	private static final String TAG = "CustomAdapter";
	private static int convertViewCounter = 0;
	
	static class ViewHolder
	{
		TextView tvOrden;
		TextView tvNodo;
		TextView tvCuenta;
		TextView tvEstado;
		CheckBox cb;
	}
	
	public UploadAdapter(Context c, ArrayList<UploadData> d){
		Log.v(TAG, "Constructing CustomAdapter");
		this.data = d;
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.v(TAG, "in getCount()");
		return data.size();
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Log.v(TAG, "in getItem() for position " + position);
		return data.get(position);
		//return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Log.v(TAG, "in getItemId() for position " + position);
		return position;
		//return 0;
	}
	@Override
	public int getViewTypeCount()
	{
		Log.v(TAG, "in getViewTypeCount()");
		return 1;
	}

	@Override
	public int getItemViewType(int position)
	{
		Log.v(TAG, "in getItemViewType() for position " + position);
		return 0;
	}

	@Override
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder;

		Log.v(TAG, "in getView for position " + position + ", convertView is "
				+ ((convertView == null) ? "null" : "being recycled"));

		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.upload_trabajo, null);

			convertViewCounter++;
            Log.v(TAG, convertViewCounter + " convertViews have been created");
			
			holder = new ViewHolder();

			holder.tvOrden = (TextView) convertView.findViewById(R.id.textView1);
			holder.tvNodo = (TextView) convertView.findViewById(R.id.textView2);
			holder.tvCuenta = (TextView) convertView.findViewById(R.id.textView3);
			holder.tvEstado = (TextView) convertView.findViewById(R.id.textView4);
			holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.cb.setOnClickListener(checkListener);

			convertView.setTag(holder);

		} else
			holder = (ViewHolder) convertView.getTag();

		// Para porde hacer click en el checkbox
		UploadData d = (UploadData) getItem(position);
		holder.cb.setTag(d);
		// Setting all values in listview
		holder.tvOrden.setText(data.get(position).getOrden());
		holder.tvNodo.setText(data.get(position).getNodo());
		holder.tvCuenta.setText(data.get(position).getCuenta());
		holder.tvEstado.setText(data.get(position).getEstado());
		holder.cb.setChecked(data.get(position).getChecked());

		return convertView;

	}
	
	public void setCheck(int position)
	{
		UploadData d = data.get(position);

		d.setChecked(!d.getChecked());
		notifyDataSetChanged();
	}

	public void checkAll(boolean state)
	{
		for (int i = 0; i < data.size(); i++)
			data.get(i).setChecked(state);
	}

	public void cancelSelectedPost()
	{

		int i = 0;
		while (i < getCount())
		{
			if (data.get(i).getChecked())
			{
				data.remove(data.indexOf(data.get(i)));
			} else
				i++;
		}
		notifyDataSetChanged();

	}

	public boolean haveSomethingSelected()
	{
		for (int i = 0; i < data.size(); i++)
			if (data.get(i).getChecked())
				return true;
		return false;
	}

	/**
	 * Este método es para poder seleccionar una fila directamente con el
	 * checkbox en lugar de tener que pulsar en la liste en sí
	 */
	private OnClickListener checkListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			UploadData d = (UploadData) v.getTag();
			d.setChecked(!d.getChecked());
		}
	};

}
