package form_amdata;

import java.io.File;
import java.util.ArrayList;

import class_amdata.Class_Inconsistencias;
import sypelc.androidamdata.R;
import miscelanea.SQLite;
import miscelanea.Util;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Form_DatosActa_Adecuaciones extends Activity implements OnClickListener{
	Class_Inconsistencias	FcnInconsistencia;
	SQLite 					AtencionSQL;
	Util   					AtencionUtil;
	
	//Variable utilizada para realizar las operaciones en la base de datos 
	ArrayList<ContentValues> 	_tempTabla 		= new ArrayList<ContentValues>();
	ContentValues 				_tempRegistro 	= new ContentValues();
	
	private String 	NombreUsuario	= "";
	private String 	CedulaUsuario 	= "";
	private String 	NivelUsuario	= "";
	private String 	OrdenTrabajo 	= "";
	private String 	CuentaCliente 	= "";
	private String 	FolderAplicacion= "";
	
	/**Datos acta**/
	private String _strFamilias[] 			= {"...","3","2","1","0"};
	private String _strFotos[] 				= {"...","No","Si"};
	private String _strElectricista[] 		= {"...","No","Si"};
	private String _strTipoMedidor[] 		= {"...","No Hay Medidor","Induccion","Electronico"};
	private String _strUbicacionMedidor[] 	= {"...","No Hay Medidor","Interno","Externo"};
	private String _strIrregularidades[] 	= {"...","No Hay Irreg","No","Si"};
	private String _strRegistrador[] 		= {"...","No Hay Medidor","Digital","Ciclometrico"};
	
	/**Adecuaciones a realizar**/
	private String _strSuspension[]			= {"...","No","Si"};
	private String _strTubo[]				= {"...","No","Si"};
	private String _strArmario[]			= {"...","No","Si"};
	private String _strSoporte[]			= {"...","No","Si"};
	private String _strTierra[]				= {"...","No","Si"};
	private String _strAcometida[]			= {"...","No","Si"};
	private String _strCaja[]				= {"...","No","Si"};
	private String _strMedidor[]			= {"...","No","Si"};
	
	/**Datos acta**/
	private ArrayAdapter<String> AdaptadorFamilias;
	private ArrayAdapter<String> AdaptadorFotos;
	private ArrayAdapter<String> AdaptadorElectricista;
	private ArrayAdapter<String> AdaptadorTipoMedidor;
	private ArrayAdapter<String> AdaptadorUbicacionMedidor;
	private ArrayAdapter<String> AdaptadorIrregularidades;
	private ArrayAdapter<String> AdaptadorRegistrador;
	
	
	/**Adecuaciones a realizar**/
	private ArrayAdapter<String> AdaptadorSuspension;
	private ArrayAdapter<String> AdaptadorTubo;
	private ArrayAdapter<String> AdaptadorArmario;
	private ArrayAdapter<String> AdaptadorSoporte;
	private ArrayAdapter<String> AdaptadorTierra;
	private ArrayAdapter<String> AdaptadorAcometida;
	private ArrayAdapter<String> AdaptadorCaja;
	private ArrayAdapter<String> AdaptadorMedidor;
	
	
	/**Datos acta**/
	private Spinner 	_cmbFamilias, _cmbFotos, _cmbElectricista, _cmbTipoMedidor, _cmbUbicacionMedidor, _cmbIrregularidades, _cmbRegistrador;
	private EditText 	_txtTelefono, _txtKwNoResidencial, _txtPorcentajeNoResidencial;
	private Button		_btnGuardarDatosActa;
	
	/**Adecuaciones a realizar**/
	private Spinner 	_cmbSuspension, _cmbTubo, _cmbArmario, _cmbSoporte, _cmbTierra, _cmbAcometida, _cmbCaja, _cmbMedidor;	
	private EditText	_txtOtros;
	private Button		_btnGuardarAdecuaciones;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datos_acta_adecuaciones);
		
		Bundle bundle 	= getIntent().getExtras();
		NombreUsuario	= bundle.getString("NombreUsuario");
		CedulaUsuario	= bundle.getString("CedulaUsuario");
		NivelUsuario	= bundle.getString("NivelUsuario");
		OrdenTrabajo	= bundle.getString("OrdenTrabajo");
		CuentaCliente 	= bundle.getString("CuentaCliente");		
		FolderAplicacion= bundle.getString("FolderAplicacion");
		
		AtencionSQL 		= new SQLite(this, FolderAplicacion);
		FcnInconsistencia 	= new Class_Inconsistencias(this, FolderAplicacion, this.CedulaUsuario, this.OrdenTrabajo, this.CuentaCliente);
		
		/**Datos acta**/
		_cmbFamilias 		= (Spinner) findViewById(R.id.DatosActaCmbFamilias); 
		_cmbFotos			= (Spinner) findViewById(R.id.DatosActaCmbFotos);
		_cmbElectricista	= (Spinner) findViewById(R.id.DatosActaCmbElectricista);
		_cmbTipoMedidor		= (Spinner) findViewById(R.id.DatosActaCmbTipoMedidor);
		_cmbUbicacionMedidor= (Spinner) findViewById(R.id.DatosActaCmbUbicacionMedidor); 
		_cmbIrregularidades	= (Spinner) findViewById(R.id.DatosActaCmbIrregularidades);
		_cmbRegistrador		= (Spinner) findViewById(R.id.DatosActaCmbRegistrador);		
		_txtTelefono		= (EditText) findViewById(R.id.DatosActaTxtTelefono); 
		_txtKwNoResidencial	= (EditText) findViewById(R.id.DatosActaTxtKwNoResidencial);  
		_txtPorcentajeNoResidencial = (EditText) findViewById(R.id.DatosActaTxtPorcentajeNoResidencial); 		
		_btnGuardarDatosActa	= (Button) findViewById(R.id.DatosActaBtnGuardar);
		
		
		/**Adecuaciones a realizar**/
		_cmbSuspension 	= (Spinner) findViewById(R.id.AdecuacionesCmbSuspension); 
		_cmbTubo		= (Spinner) findViewById(R.id.AdecuacionesCmbTubo);
		_cmbArmario		= (Spinner) findViewById(R.id.AdecuacionesCmbArmario);
		_cmbSoporte		= (Spinner) findViewById(R.id.AdecuacionesCmbSoporte);
		_cmbTierra		= (Spinner) findViewById(R.id.AdecuacionesCmbTierra);
		_cmbAcometida	= (Spinner) findViewById(R.id.AdecuacionesCmbAcometida); 
		_cmbCaja		= (Spinner) findViewById(R.id.AdecuacionesCmbCaja); 
		_cmbMedidor		= (Spinner) findViewById(R.id.AdecuacionesCmbMedidor);		
		_txtOtros		= (EditText) findViewById(R.id.AdecuacionesTxtOtros);		
		_btnGuardarAdecuaciones = (Button) findViewById(R.id.AdecuacionesBtnGuardar);
		
		
		AdaptadorFamilias 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strFamilias);
		_cmbFamilias.setAdapter(AdaptadorFamilias);
		
		AdaptadorFotos 			= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strFotos);
		_cmbFotos.setAdapter(AdaptadorFotos);
		
		AdaptadorElectricista 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strElectricista);
		_cmbElectricista.setAdapter(AdaptadorElectricista);
		
		AdaptadorTipoMedidor 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strTipoMedidor);
		_cmbTipoMedidor.setAdapter(AdaptadorTipoMedidor);
		
		AdaptadorUbicacionMedidor 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strUbicacionMedidor);
		_cmbUbicacionMedidor.setAdapter(AdaptadorUbicacionMedidor);
		
		AdaptadorIrregularidades= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strIrregularidades);
		_cmbIrregularidades.setAdapter(AdaptadorIrregularidades);
		
		AdaptadorRegistrador 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strRegistrador);
		_cmbRegistrador.setAdapter(AdaptadorRegistrador);
		
		
		/**Adecuaciones a realizar**/
		AdaptadorSuspension 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strSuspension);
		_cmbSuspension.setAdapter(AdaptadorSuspension);
		
		AdaptadorTubo 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strTubo);
		_cmbTubo.setAdapter(AdaptadorTubo);
		
		AdaptadorArmario 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strArmario);
		_cmbArmario.setAdapter(AdaptadorArmario);
		
		AdaptadorSoporte 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strSoporte);
		_cmbSoporte.setAdapter(AdaptadorSoporte);
		
		AdaptadorTierra 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strTierra);
		_cmbTierra.setAdapter(AdaptadorTierra);
		
		AdaptadorAcometida 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strAcometida);
		_cmbAcometida.setAdapter(AdaptadorAcometida);
		
		AdaptadorCaja 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strCaja);
		_cmbCaja.setAdapter(AdaptadorCaja);
		
		AdaptadorMedidor 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strMedidor);
		_cmbMedidor.setAdapter(AdaptadorMedidor);
		
		_btnGuardarDatosActa.setOnClickListener(this);
		_btnGuardarAdecuaciones.setOnClickListener(this);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_atencion_usuario, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent k;
		switch (item.getItemId()) {				
			case R.id.Acometida:
				finish();
				k = new Intent(this, Acometida.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("Nivel", NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
				return true;
			
			case R.id.CensoPruebas:
				finish();
				k = new Intent(this, Form_CensoCarga.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("Nivel", NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
				return true;	
				
			case R.id.ContadorTransformador:
				finish();
				k = new Intent(this, Form_CambioContador.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("Nivel", NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
				return true;
		
			case R.id.IrregularidadesObservaciones:
				finish();
				k = new Intent(this, IrregularidadesObservaciones.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("Nivel", NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
				return true;
				
			case R.id.Sellos:
				finish();
				k = new Intent(this, Form_Sellos.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("Nivel", NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
				return true;
				
			case R.id.EncontradoPruebas:
				finish();
				k = new Intent(this, Form_MedidorPruebas.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("Nivel", NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
				return true;	
			
			case R.id.Materiales:
				finish();
				k = new Intent(this, Materiales.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("NivelUsuario", 	NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
				return true;	
				
			case R.id.Acta:
				finish();
				k = new Intent(this, Form_Actas.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("NivelUsuario", 	NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
				return true;
				
				
			case R.id.Volver:
				finish();
				k = new Intent(this, Form_Solicitudes.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("NivelUsuario", 	NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion",  Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
				return true;	
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.DatosActaBtnGuardar:
			GuardarDatosActas();
			break;
			
		case R.id.AdecuacionesBtnGuardar:
			GuardarAdecuaciones();
			break;
		}
	}
	
	
	public void GuardarDatosActas(){
		if(_txtTelefono.getText().toString().isEmpty()){
			Toast.makeText(this,"No ha ingresado un numero telefonico valido.", Toast.LENGTH_SHORT).show();
		}else if(_txtKwNoResidencial.getText().toString().isEmpty()){
			Toast.makeText(this,"No ha ingresado el Kw no residencial.", Toast.LENGTH_SHORT).show();
		}else if(_txtPorcentajeNoResidencial.getText().toString().isEmpty()){
			Toast.makeText(this,"No ha ingresado el % no residencial.", Toast.LENGTH_SHORT).show();
		}else{
			FcnInconsistencia.registrarInconsistencia("295_"+_txtTelefono.getText().toString(), "ACT21", "A");
			FcnInconsistencia.registrarInconsistencia("294_"+_cmbFamilias.getSelectedItem().toString(), "ACT9", "A");
			FcnInconsistencia.registrarInconsistencia("293_"+_cmbFotos.getSelectedItem().toString(), "PM10", "A");
			FcnInconsistencia.registrarInconsistencia("292_"+_cmbElectricista.getSelectedItem().toString(), "ACT2", "A");
			FcnInconsistencia.registrarInconsistencia("281_"+_txtKwNoResidencial.getText().toString(), "ACT 9", "A");
			FcnInconsistencia.registrarInconsistencia("289_"+_cmbTipoMedidor.getSelectedItem().toString(), "ACT15", "A");
			FcnInconsistencia.registrarInconsistencia("287_"+_cmbUbicacionMedidor.getSelectedItem().toString(),	"ACT13", "A");
			FcnInconsistencia.registrarInconsistencia("301_"+_cmbIrregularidades.getSelectedItem().toString(), "AD99", "A");
			FcnInconsistencia.registrarInconsistencia("288_"+_cmbRegistrador.getSelectedItem().toString(), "ACT14", "A");
			FcnInconsistencia.registrarInconsistencia("282_"+_txtPorcentajeNoResidencial.getText().toString(), "ACT10", "A");
			Toast.makeText(this,"Datos Actas registrados correctamente.", Toast.LENGTH_SHORT).show();
		}	
	}

	
	public void GuardarAdecuaciones(){
		if(_txtOtros.getText().toString().isEmpty()){
			Toast.makeText(this,"No ha ingresado datos en otros.", Toast.LENGTH_SHORT).show();
		}else{
			FcnInconsistencia.registrarInconsistencia("272_"+_txtOtros.getText().toString(), "ACT38", "A");
			FcnInconsistencia.registrarInconsistencia("271_"+_cmbSuspension.getSelectedItem().toString(),"ACT37", "A");
			FcnInconsistencia.registrarInconsistencia("270_"+_cmbTubo.getSelectedItem().toString(),"ACT36", "A");
			FcnInconsistencia.registrarInconsistencia("269_"+_cmbArmario.getSelectedItem().toString(),"ACT35", "A");
			FcnInconsistencia.registrarInconsistencia("268_"+_cmbSoporte.getSelectedItem().toString(),"ACT33", "A");
			FcnInconsistencia.registrarInconsistencia("267_"+_cmbTierra.getSelectedItem().toString(),"ACT32", "A");
			FcnInconsistencia.registrarInconsistencia("266_"+_cmbAcometida.getSelectedItem().toString(),"ACT31", "A");
			FcnInconsistencia.registrarInconsistencia("265_"+_cmbCaja.getSelectedItem().toString(),"ACT39", "A");
			FcnInconsistencia.registrarInconsistencia("264_"+_cmbMedidor.getSelectedItem().toString(),"ACT30", "A");	
			Toast.makeText(this,"Adecuaciones registradas correctamente.", Toast.LENGTH_SHORT).show();
		}
	}
}








