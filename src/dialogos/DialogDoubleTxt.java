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

public class DialogDoubleTxt extends Activity implements OnClickListener{
	private Button 		_aceptar, _cancelar;
	private TextView 	_lbl1, _lbl2, _lblTitulo;
	private EditText	_txt1, _txt2;
	

	
	 public void finish(boolean _caso) {
		 Intent data = new Intent();
		 if(_caso){
			 data.putExtra("response", _caso);
			 data.putExtra("txt1", _txt1.getText().toString());
			 data.putExtra("txt2", _txt2.getText().toString());			 
		 }	 
		 setResult(RESULT_OK, data);
		 super.finish();
	 }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog_doble_txt);
		
		Bundle bundle 	= getIntent().getExtras();		
		
		_aceptar = (Button) findViewById(R.id.dd_btn_aceptar);
		_cancelar= (Button) findViewById(R.id.dd_btn_cancelar);
		
		_lblTitulo = (TextView) findViewById(R.id.dd_lbl_titulo);
		_lbl1 = (TextView) findViewById(R.id.dd_lbl_text1);
		_lbl2 = (TextView) findViewById(R.id.dd_lbl_text2);
		
		_txt1 = (EditText) findViewById(R.id.dd_txt_text1);
		_txt2 = (EditText) findViewById(R.id.dd_txt_text2);	
		
		_lbl1.setText(bundle.getString("lbl1"));
		_lbl2.setText(bundle.getString("lbl2"));
		_lblTitulo.setText(bundle.getString("titulo"));
		
		_txt1.setText(bundle.getString("txt1"));
		_txt2.setText(bundle.getString("txt2"));	
		
		_aceptar.setOnClickListener(this);
		_cancelar.setOnClickListener(this);
	}


	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.dd_btn_aceptar:
				finish(true);
				break;
			case R.id.dd_btn_cancelar:
				finish(false);
				break;
				
			default:
				finish(false);
				break;
				
		}	
	}
}
