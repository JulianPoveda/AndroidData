package form_amdata;


import java.io.File;
import java.util.ArrayList;

import class_amdata.Class_Contador;
import dialogos.DialogDoubleTxt;
import dialogos.Modal_BodegaContadores;
import sypelc.androidamdata.R;
import miscelanea.SQLite;
import miscelanea.Tablas;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

public class Form_CambioContador extends Activity implements OnClickListener, OnItemSelectedListener{
	private static int 	CONFIRM_MEDIDOR = 1;
	private static int  ACT_BODEGA_CONTADORES = 2;
	final int NO_RETIRADO 	= 1;
	final int RETIRADO 		= 2;
	final int INSTALADO		= 3;
	
	Intent ConfirmacionMedidor;
	Intent BodegaContadores;
	
	SQLite 			ContadorSQL;
	Util   			ContadorUtil;
	Tablas			GraphContadorTabla;
	
	Class_Contador	FcnContador;
	
	private String		NombreUsuario	= "";
	private String		CedulaUsuario	= "";
	private String 		NivelUsuario	= "";
	private String 		OrdenTrabajo 	= "";
	private String 		CuentaCliente 	= "";
	private String 		FolderAplicacion= "";
	
	private int RegistroMovimiento = 0;
	
	//private ArrayList<ContentValues> _tablaTemp = new ArrayList<ContentValues>();
	private ContentValues _registroTemp = new ContentValues();	

	ArrayAdapter<String> AdaptadorMovimiento;
	ArrayAdapter<String> AdaptadorMarcaMedidor;
	ArrayAdapter<String> AdaptadorTipoConexion;
	
	ArrayList<String> StringMovimiento	 	= new ArrayList<String>();
	ArrayList<String> StringMarcaMedidor 	= new ArrayList<String>();
	ArrayList<String> StringTipoConexion	= new ArrayList<String>();
	
	ArrayAdapter<String> AdaptadorTipoPropietario;
	
	String[] _strPropietarioTranfor	= {"EMSA","PARTICULAR"};
		
	Spinner 	_cmbMovimientoMedidor, _cmbMarcaMedidor, _cmbTipoConexion, _cmbPropietarioTranfor;
	EditText	_txtSerie, _txtLectura, _txtNumeroTranfor, _txtMarcaTranfor, _txtKvaTranfor, _txtAnnoTranfor, _txtVoltaje1Tranfor, _txtVoltaje2Tranfor, _txtNodoTranfor;
	Button 		_btnRegistrarMovimiento, _btnEliminarMovimiento, _btnVerBodega, _btnGuardarTranfor, _btnEliminarTranfor;
	
	private TableLayout		TablaContadores;
	private LinearLayout 	FilaTablaContadores;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cambio_contador);
		
		ConfirmacionMedidor	= new Intent(this,DialogDoubleTxt.class);
		BodegaContadores 	= new Intent(this,Modal_BodegaContadores.class);
				
		Bundle bundle 	= getIntent().getExtras();
		this.NombreUsuario		= bundle.getString("NombreUsuario");
		this.CedulaUsuario		= bundle.getString("CedulaUsuario");
		this.NivelUsuario		= bundle.getString("NivelUsuario");
		this.OrdenTrabajo		= bundle.getString("OrdenTrabajo");
		this.CuentaCliente 		= bundle.getString("CuentaCliente");		
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");
		
		ContadorSQL 		= new SQLite(this, FolderAplicacion);
		ContadorUtil 		= new Util();		
		FcnContador 		= new Class_Contador(this, FolderAplicacion, this.CedulaUsuario, this.OrdenTrabajo, this.CuentaCliente);
		
		GraphContadorTabla 		= new Tablas(this, "tipo,marca,serie,lectura", "90,220,100,100", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");		
		FilaTablaContadores	= (LinearLayout) findViewById(R.id.TablaMovimientosContadores);
		
		_cmbMovimientoMedidor 	= (Spinner) findViewById(R.id.ContadorCmbMovimiento);
		_cmbMarcaMedidor		= (Spinner) findViewById(R.id.ContadorCmbMarca);
		_cmbTipoConexion 		= (Spinner) findViewById(R.id.ContadorCmbTipo);
		_txtSerie				= (EditText) findViewById(R.id.ContadorTxtSerie);
		_txtLectura				= (EditText) findViewById(R.id.ContadorTxtLectura);
		_btnRegistrarMovimiento	= (Button) findViewById(R.id.ContadorBtnRegistrar);
		_btnEliminarMovimiento	= (Button) findViewById(R.id.ContadorBtnEliminar);
		_btnVerBodega			= (Button) findViewById(R.id.ContadorBtnBodega);
		
		_cmbPropietarioTranfor 	= (Spinner) findViewById(R.id.AcometidaCmbClase2);
		_txtNumeroTranfor		= (EditText) findViewById(R.id.TranforTxtNumero);
		_txtMarcaTranfor		= (EditText) findViewById(R.id.TranforTxtMarca);
		_txtKvaTranfor			= (EditText) findViewById(R.id.TranforTxtKva);
		_txtAnnoTranfor			= (EditText) findViewById(R.id.TranforTxtAnno); 
		_txtVoltaje1Tranfor		= (EditText) findViewById(R.id.TranforTxtVoltaje1);
		_txtVoltaje2Tranfor		= (EditText) findViewById(R.id.TranforTxtVoltaje2); 
		_txtNodoTranfor			= (EditText) findViewById(R.id.TranforTxtNodo);
		_btnGuardarTranfor		= (Button) findViewById(R.id.TranforBtnGuardar);
		_btnEliminarTranfor		= (Button) findViewById(R.id.TranforBtnEliminar);
				
		if(ContadorSQL.ExistRegistros("amd_cambios_contadores", "id_orden='"+OrdenTrabajo+"' AND tipo IN ('P','D','CM','SD')")){
			RegistroMovimiento = INSTALADO;
		}else if(ContadorSQL.ExistRegistros("amd_cambios_contadores", "id_orden='"+OrdenTrabajo+"' AND tipo='R'")){
			RegistroMovimiento = RETIRADO;
		}else{
			RegistroMovimiento = NO_RETIRADO;
		} 
		
		//CambioTipoMovimiento(RegistroMovimiento);
		AdaptadorMovimiento 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringMovimiento);
		_cmbMovimientoMedidor.setAdapter(AdaptadorMovimiento);		
		
		AdaptadorMarcaMedidor= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringMarcaMedidor);
		_cmbMarcaMedidor.setAdapter(AdaptadorMarcaMedidor);
		
		AdaptadorTipoConexion 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringTipoConexion);
		_cmbTipoConexion.setAdapter(AdaptadorTipoConexion);
		
		AdaptadorTipoPropietario= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strPropietarioTranfor);
		_cmbPropietarioTranfor.setAdapter(AdaptadorTipoPropietario);
		
		CargarCmb(RegistroMovimiento);
		VerMovimientosContadores();
		
		_cmbMovimientoMedidor.setOnItemSelectedListener(this);		
		_btnRegistrarMovimiento.setOnClickListener(this);
		_btnEliminarMovimiento.setOnClickListener(this);
		_btnVerBodega.setOnClickListener(this);
		
		_btnGuardarTranfor.setOnClickListener(this);
		_btnEliminarTranfor.setOnClickListener(this);
		
		if(ContadorSQL.ExistRegistros("amd_datos_tranfor", "id_orden='"+OrdenTrabajo+"'")){
			_registroTemp = ContadorSQL.SelectDataRegistro("amd_datos_tranfor", "numero,marca,kva,propietario,anno,voltaje1,voltaje2,circuito", "id_orden='"+OrdenTrabajo+"'");
			_txtNumeroTranfor.setText(_registroTemp.getAsString("numero"));
			_txtMarcaTranfor.setText(_registroTemp.getAsString("marca"));
			_txtKvaTranfor.setText(_registroTemp.getAsString("kva"));
			_cmbPropietarioTranfor.setSelection(AdaptadorTipoPropietario.getPosition(_registroTemp.getAsString("propietario")));			
			_txtAnnoTranfor.setText(_registroTemp.getAsString("anno"));
			_txtVoltaje1Tranfor.setText(_registroTemp.getAsString("voltaje1"));
			_txtVoltaje2Tranfor.setText(_registroTemp.getAsString("voltaje2"));
			_txtNodoTranfor.setText(_registroTemp.getAsString("nodo"));
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_contador_transformador, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent k;
		switch (item.getItemId()) {	
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
		
			case R.id.Acometida:
				finish();
				k = new Intent(this, Acometida.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("NivelUsuario", 	NivelUsuario);
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
				k.putExtra("NivelUsuario", 	NivelUsuario);
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
				k.putExtra("NivelUsuario", 	NivelUsuario);
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
				k.putExtra("NivelUsuario", 	NivelUsuario);
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
				k.putExtra("NivelUsuario", 	NivelUsuario);
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
				
			case R.id.DatosAdecuaciones:
				finish();
				k = new Intent(this, Form_DatosActa_Adecuaciones.class);
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
			case R.id.ContadorBtnRegistrar:
				if(_txtLectura.getText().toString().equals("")){
					Toast.makeText(this,"No ha ingresado la lectura del medidor.",Toast.LENGTH_SHORT).show();
				}else if(_txtSerie.getText().toString().equals("")){
					Toast.makeText(this,"No ha ingresado la serie del medidor.",Toast.LENGTH_SHORT).show();
				}else{
					ConfirmacionMedidor.putExtra("lbl1", "Serie:");
					ConfirmacionMedidor.putExtra("lbl2", "Lectura:");
					if((_cmbMovimientoMedidor.getSelectedItem().toString().equals("SERVICIO_DIRECTO"))||(_cmbMovimientoMedidor.getSelectedItem().toString().equals("SIN_SERVICIO"))){
						ConfirmacionMedidor.putExtra("txt1", _txtSerie.getText().toString());
						ConfirmacionMedidor.putExtra("txt2", _txtLectura.getText().toString());
					}else if((_cmbMovimientoMedidor.getSelectedItem().toString().equals("DEFINITIVO"))||(_cmbMovimientoMedidor.getSelectedItem().toString().equals("PROVISIONAL"))){
						ConfirmacionMedidor.putExtra("txt1", "");
						ConfirmacionMedidor.putExtra("txt2", _txtLectura.getText().toString());
					}else{
						ConfirmacionMedidor.putExtra("txt1", "");
						ConfirmacionMedidor.putExtra("txt2", "");
					}					
					ConfirmacionMedidor.putExtra("titulo", "CONFIRMAR SERIE Y LECTURA DEL MEDIDOR");
					startActivityForResult(ConfirmacionMedidor, CONFIRM_MEDIDOR);
				}
				break;
				
			case R.id.ContadorBtnEliminar:
				RegistroMovimiento = FcnContador.eliminarMovimientoMedidor(RegistroMovimiento);
				VerMovimientosContadores();
				CargarCmb(RegistroMovimiento);
				break;
				
				
			case R.id.ContadorBtnBodega:
				BodegaContadores.putExtra("FolderAplicacion",  Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivityForResult(BodegaContadores, ACT_BODEGA_CONTADORES);
				break;
				
			case R.id.TranforBtnGuardar:
				_registroTemp.clear();
				_registroTemp.put("id_orden", OrdenTrabajo);
				_registroTemp.put("numero", _txtNumeroTranfor.getText().toString());
				_registroTemp.put("marca", _txtMarcaTranfor.getText().toString());
				_registroTemp.put("kva", _txtKvaTranfor.getText().toString());
				_registroTemp.put("propietario", _cmbPropietarioTranfor.getSelectedItem().toString());
				_registroTemp.put("anno", _txtAnnoTranfor.getText().toString());
				_registroTemp.put("voltaje1", _txtVoltaje1Tranfor.getText().toString());
				_registroTemp.put("voltaje2", _txtVoltaje2Tranfor.getText().toString());
				_registroTemp.put("circuito", _txtNodoTranfor.getText().toString());
				Toast.makeText(this,ContadorSQL.InsertOrUpdateRegistro("amd_datos_tranfor", _registroTemp, "id_orden='"+OrdenTrabajo+"'"),Toast.LENGTH_SHORT).show();
				break;
				
			case R.id.TranforBtnEliminar:
				if(ContadorSQL.DeleteRegistro("amd_datos_tranfor", "id_orden='"+OrdenTrabajo+"'")){
					Toast.makeText(this,"Datos del transformador eliminados correctamente.",Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try{
			if (resultCode == RESULT_OK && requestCode == CONFIRM_MEDIDOR) {
				if(data.getExtras().getBoolean("response")){
					if(FcnContador.getConfirmacionSerieLectura(_txtSerie.getText().toString(), data.getExtras().getString("txt1"), _txtLectura.getText().toString(), data.getExtras().getString("txt2"))){
						RegistroMovimiento = FcnContador.registrarMovimientoMedidor(RegistroMovimiento, _cmbMovimientoMedidor.getSelectedItem().toString() , _cmbMarcaMedidor.getSelectedItem().toString(), _txtSerie.getText().toString(), _txtLectura.getText().toString());
						VerMovimientosContadores();
						CargarCmb(RegistroMovimiento);
					}
				}
			}else if(resultCode == RESULT_OK && requestCode == ACT_BODEGA_CONTADORES){
				if(data.getExtras().getBoolean("response")){
					_txtLectura.setText(data.getExtras().getString("lectura"));
					_txtSerie.setText(data.getExtras().getString("serie"));
					_cmbMarcaMedidor.setSelection(AdaptadorMarcaMedidor.getPosition(data.getExtras().getString("marca")));
					_cmbTipoConexion.setSelection(AdaptadorTipoConexion.getPosition(data.getExtras().getString("tipo")));
				}
			}
		}catch(Exception e){
			//e.toString();
		}
		
    }


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch(parent.getId()){
			case R.id.ContadorCmbMovimiento:
				this._registroTemp = FcnContador.movimientoContador(RegistroMovimiento, _cmbMovimientoMedidor.getSelectedItem().toString());
				_txtSerie.setText(this._registroTemp.getAsString("textSerie"));
				_txtLectura.setText(this._registroTemp.getAsString("textLectura"));				
				_txtSerie.setEnabled(this._registroTemp.getAsBoolean("enableSerie"));
				_txtLectura.setEnabled(this._registroTemp.getAsBoolean("enableLectura"));				
				_cmbTipoConexion.setSelection(AdaptadorTipoConexion.getPosition(this._registroTemp.getAsString("valorTipoConexion")));
				_cmbTipoConexion.setEnabled(this._registroTemp.getAsBoolean("enableTipoConexion"));				
				_cmbMarcaMedidor.setSelection(AdaptadorMarcaMedidor.getPosition(this._registroTemp.getAsString("valorMarcaMedidor")));
				_cmbMarcaMedidor.setEnabled(this._registroTemp.getAsBoolean("enableMarcaMedidor"));
				if(this._registroTemp.getAsBoolean("visibleBodega")){
					_btnVerBodega.setVisibility(View.VISIBLE);
				}else{
					_btnVerBodega.setVisibility(View.INVISIBLE);
				}
				break;
		}
	}
	
	
	public void VerMovimientosContadores(){
		TablaContadores 	= GraphContadorTabla.CuerpoTabla(FcnContador.getMovimientos());
		FilaTablaContadores.removeAllViews();
		FilaTablaContadores.addView(TablaContadores);
	}
	
	
	public void CargarCmb(int _estadoMovimiento){
		StringMovimiento = FcnContador.getOpcionesMovimiento(_estadoMovimiento);
		AdaptadorMovimiento= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringMovimiento);	
		_cmbMovimientoMedidor.setAdapter(AdaptadorMovimiento);
		AdaptadorMovimiento.notifyDataSetChanged();		
		
		StringMarcaMedidor = FcnContador.getOpcionesMedidores(_estadoMovimiento);
		AdaptadorMarcaMedidor = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringMarcaMedidor);	
		_cmbMarcaMedidor.setAdapter(AdaptadorMarcaMedidor);
		AdaptadorMarcaMedidor.notifyDataSetChanged();
		
		StringTipoConexion= FcnContador.getOpcionesConexion(_estadoMovimiento);
		AdaptadorTipoConexion = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringTipoConexion);	
		_cmbTipoConexion.setAdapter(AdaptadorTipoConexion);
		AdaptadorTipoConexion.notifyDataSetChanged();
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub		
	}	
}
