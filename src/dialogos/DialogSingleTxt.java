package dialogos;

import sypelc.androidamdata.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogSingleTxt extends Activity implements OnClickListener{
	String _parametros[];
	private Button 		_aceptar, _cancelar;
	private TextView 	_lbl1, _lblTitulo;
	private EditText	_txt1;

	
	public void finish(boolean _caso) {
		Intent data = new Intent();
		data.putExtra("txt1", _txt1.getText().toString());
		data.putExtra("response", _caso);	 
		setResult(RESULT_OK, data);
		super.finish();
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog_single);
		
		Bundle bundle 	= getIntent().getExtras();
		
		_aceptar = (Button) findViewById(R.id.ds_btn_aceptar);
		_cancelar= (Button) findViewById(R.id.ds_btn_cancelar);
		
		_lblTitulo = (TextView) findViewById(R.id.ds_lbl_titulo);
		_lbl1 = (TextView) findViewById(R.id.ds_lbl_text1);		
		_txt1 = (EditText) findViewById(R.id.ds_txt_text1);		
		
		_lbl1.setText(bundle.getString("lbl1"));
		_txt1.setText(bundle.getString("txt1"));
		_lblTitulo.setText(bundle.getString("titulo"));		
		
		_aceptar.setOnClickListener(this);
		_cancelar.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.ds_btn_aceptar:
				finish(true);
				break;
			case R.id.ds_btn_cancelar:
				finish(false);
				break;
				
		}			
	}
}
