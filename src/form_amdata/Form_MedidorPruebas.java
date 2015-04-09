package form_amdata;

import java.io.File;
import java.util.ArrayList;

import class_amdata.Class_Irregularidades;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Form_MedidorPruebas extends Activity implements OnClickListener, OnItemSelectedListener{
	Tablas					PruebasTabla;
	SQLite 					PruebasSQL;
	Util   					PruebasUtil;
	Class_Irregularidades	FcnIrregularidad;
	
	ContentValues 			_tempRegistro 	= new ContentValues();
	ArrayList<ContentValues>_tempTabla 		= new ArrayList<ContentValues>();
	
	private String		NombreUsuario	= "";
	private String		CedulaUsuario	= "";
	private String 		NivelUsuario	= "";
	private String 		OrdenTrabajo 	= "";
	private String 		CuentaCliente 	= "";
	private String 		FolderAplicacion= "";
	
	private String  mi_marca, mi_serie;
	private String 	_strTipo[]		= {"MONOFASICO","BIFASICO","TRIFASICO"};
	
	
	private String  _strConexion[] 		= {"NO REALIZADA","CONFORME","NO CONFORME"," "};
	private String 	_strContinuidad[]	= {"NO REALIZADA","CONFORME","NO CONFORME"};
	private String  _strPuentes[]		= {"NO REALIZADA","CONFORME","NO CONFORME"};
	private String 	_strIntegrador[]	= {"NO REALIZADA","SI REGISTRA","NO REGISTRA"};
	private String  _strVacio[]			= {"NO REALIZADA","SI","NO"};
	private String  _strFrenado[]		= {"NO REALIZADA","SI","NO"};
	private String 	_strRetirado[]		= {"NO REALIZADA","SI","NO"};
	private String 	_strRozamiento[]	= {"NO REALIZADA","SI","NO"};
	private String 	_strAplomado[]		= {"NO REALIZADA","SI","NO"};
	
	ArrayAdapter<String> AdaptadorTipoMedidor;
	
	
	ArrayAdapter<String> AdaptadorConexion;
	ArrayAdapter<String> AdaptadorContinuidad;
	ArrayAdapter<String> AdaptadorPuentes;
	ArrayAdapter<String> AdaptadorIntegrador;
	ArrayAdapter<String> AdaptadorVacio;
	ArrayAdapter<String> AdaptadorFrenado;
	ArrayAdapter<String> AdaptadorRetirado;
	ArrayAdapter<String> AdaptadorRozamiento;
	ArrayAdapter<String> AdaptadorAplomado;
	
	
	private 	LinearLayout 	FilaMedidorEncontrado;
	private 	TableLayout 	TablamedidorEncontrado;
	private 	Spinner 		_cmbMarcaMedidor, _cmbTipoMedidor, _cmbConexion, _cmbContinuidad, _cmbPuentes, _cmbIntegrador, _cmbVacio, _cmbFrenado, _cmbRetirado, _cmbRozamiento, _cmbAplomado;
	private 	EditText		_txtSerieMedidor, _txtLectura1, _txtLectura2, _txtLectura3, _txtLectInicial, _txtLectFinal, _txtGiros;
	private 	Button 			_btnRegistrarEncontrado, _btnEliminarEncontrado, _btnGuardarPruebas, _btnEliminarPruebas;
	
	
	//Variables para la consulta de los medidores existentes en la base de datos
	ArrayList<ContentValues> MarcaMedidores = new ArrayList<ContentValues>();
	ArrayList<String> StringMarcaMedidores = new ArrayList<String>();
	ArrayAdapter<String> AdaptadorMarcaMedidores;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medidor_pruebas);
		
		Bundle bundle 	= getIntent().getExtras();
		this.NombreUsuario		= bundle.getString("NombreUsuario");
		this.CedulaUsuario		= bundle.getString("CedulaUsuario");
		this.NivelUsuario		= bundle.getString("NivelUsuario");
		this.OrdenTrabajo		= bundle.getString("OrdenTrabajo");
		this.CuentaCliente 		= bundle.getString("CuentaCliente");		
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");
		
		PruebasSQL 		= new SQLite(this, FolderAplicacion);
		PruebasUtil 	= new Util();
		FcnIrregularidad= new Class_Irregularidades(this, FolderAplicacion, this.CedulaUsuario, this.OrdenTrabajo, this.CuentaCliente);
		
		
		/***Instancias a objetos del Contador Encontrado***/
		_cmbMarcaMedidor= (Spinner) findViewById(R.id.EncontradoCmbMarca);
		_cmbTipoMedidor	= (Spinner) findViewById(R.id.EncontradoCmbTipo);		
		_txtSerieMedidor= (EditText) findViewById(R.id.EncontradoTxtSerie);
		_txtLectura1	= (EditText) findViewById(R.id.EncontradoTxtLectura1);
		_txtLectura2	= (EditText) findViewById(R.id.EncontradoTxtLectura2);
		_txtLectura3	= (EditText) findViewById(R.id.EncontradoTxtLectura3);		
		_btnRegistrarEncontrado	= (Button) findViewById(R.id.EncontradoBtnRegistrar);
		_btnEliminarEncontrado	= (Button) findViewById(R.id.EncontradoBtnEliminar);
		
		
		/***Instancias a objetos de pruebas al medidor encontrado***/
		_cmbConexion 	= (Spinner) findViewById(R.id.PruebasCmbConexion);
		_cmbContinuidad	= (Spinner) findViewById(R.id.PruebasCmbContinuidad);
		_cmbPuentes 	= (Spinner) findViewById(R.id.PruebasCmbPuentes);
		_cmbIntegrador 	= (Spinner) findViewById(R.id.PruebasCmbIntegrador);
		_cmbVacio	 	= (Spinner) findViewById(R.id.PruebasCmbVacio);
		_cmbFrenado 	= (Spinner) findViewById(R.id.PruebasCmbFrenado);
		_cmbRetirado	= (Spinner) findViewById(R.id.PruebasCmbRetirado);
		_cmbRozamiento 	= (Spinner) findViewById(R.id.PruebasCmbRozamiento);
		_cmbAplomado 	= (Spinner) findViewById(R.id.PruebasCmbAplomado);
		_txtLectInicial	= (EditText) findViewById(R.id.PruebasTxtLectInicial);
		_txtLectFinal	= (EditText) findViewById(R.id.PruebasTxtLectFinal);
		_txtGiros		= (EditText) findViewById(R.id.PruebasTxtGiros);
		_btnGuardarPruebas	= (Button) findViewById(R.id.PruebasBtnGuardar);
		_btnEliminarPruebas	= (Button) findViewById(R.id.PruebasBtnEliminar);
		
		
		
		PruebasTabla 			= new Tablas(this, "marca,serie,tipo,lectura", "250,130,140,100", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");
		FilaMedidorEncontrado	= (LinearLayout) findViewById(R.id.EncontradoTabla);
		
		MedidorEncontradoRegistrado();
		
		/***Adaptadores para el medidor encontrado***/
		AdaptadorTipoMedidor 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strTipo);
		_cmbTipoMedidor.setAdapter(AdaptadorTipoMedidor);
				
		MarcaMedidores	= PruebasSQL.SelectData("vista_marca_contador", "descripcion", "id_marca IS NOT NULL ORDER BY descripcion");
		PruebasUtil.ArrayContentValuesToString(StringMarcaMedidores, MarcaMedidores, "descripcion");
		AdaptadorMarcaMedidores = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringMarcaMedidores);
		_cmbMarcaMedidor.setAdapter(AdaptadorMarcaMedidores);
		
		
		/***Adaptadores para las pruebas al medidor***/
		AdaptadorConexion= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strConexion);
		_cmbConexion.setAdapter(AdaptadorConexion);
		
		AdaptadorContinuidad= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strContinuidad);
		_cmbContinuidad.setAdapter(AdaptadorContinuidad);
		
		AdaptadorPuentes= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strPuentes);
		_cmbPuentes.setAdapter(AdaptadorPuentes);
		
		AdaptadorIntegrador= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strIntegrador);
		_cmbIntegrador.setAdapter(AdaptadorIntegrador);
		
		AdaptadorVacio = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strVacio);
		_cmbVacio.setAdapter(AdaptadorVacio);
		
		AdaptadorFrenado= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strFrenado);
		_cmbFrenado.setAdapter(AdaptadorFrenado);
		
		AdaptadorRetirado= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strRetirado);
		_cmbRetirado.setAdapter(AdaptadorRetirado);
		
		AdaptadorRozamiento= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strRozamiento);
		_cmbRozamiento.setAdapter(AdaptadorRozamiento);
		
		AdaptadorAplomado= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strAplomado);
		_cmbAplomado.setAdapter(AdaptadorAplomado);	
		
		
		/***Declaracion de los listener de los botones***/
		_btnRegistrarEncontrado.setOnClickListener(this);
		_btnEliminarEncontrado.setOnClickListener(this);		
		_btnEliminarPruebas.setOnClickListener(this);
		_btnGuardarPruebas.setOnClickListener(this);
	}

	
	public void MedidorEncontradoRegistrado(){
		_tempTabla= PruebasSQL.SelectData("amd_medidor_encontrado", "marca,serie,tipo,lectura", "id_orden='"+OrdenTrabajo+"'");
		TablamedidorEncontrado = PruebasTabla.CuerpoTabla(_tempTabla);
		FilaMedidorEncontrado.removeAllViews();
		FilaMedidorEncontrado.addView(TablamedidorEncontrado);
	}
	
	
	
	/*public void GuardarIrregularidad(String _id_irregularidad){
		_tempRegistro.clear();
		_tempRegistro.put("id_anomalia", PruebasSQL.IntSelectShieldWhere("amd_irregularidades","max(cast(id_anomalia AS INTEGER)) as id_anomalia","id_anomalia IS NOT NULL")+1);
		_tempRegistro.put("id_orden",OrdenTrabajo);
		_tempRegistro.put("id_irregularidad", _id_irregularidad);
		_tempRegistro.put("usuario_ins", "XXX");		
		if(PruebasSQL.InsertarRegistro("amd_irregularidades", _tempRegistro)){
			Toast.makeText(this,"Irregularidad "+ _id_irregularidad+" registrada correctamente.", Toast.LENGTH_SHORT).show();
		}
	}*/
	
	
	
	public void ValidarMedidorEncontrado(){
		_tempRegistro = PruebasSQL.SelectDataRegistro("amd_contador_cliente_orden", "marca,serie,con_contador", "cuenta='"+CuentaCliente+"' AND desinstalado IN (0,1)");
		if(_tempRegistro.getAsString("con_contador").equals("N")){
			mi_marca = "SD (SERVICIO DIRECTO)";
			mi_serie = "";
		}else{
			mi_marca 	= _tempRegistro.getAsString("marca");
			mi_serie	= _tempRegistro.getAsString("serie");
		}
		
		if(!mi_marca.equals("SD (SERVICIO DIRECTO)")){
			if(!_cmbMarcaMedidor.getSelectedItem().toString().equals("SS (SIN SERVICIO)")||(!_cmbMarcaMedidor.getSelectedItem().toString().equals("SD (SERVICIO DIRECTO)"))){
				if(!mi_marca.equals(PruebasSQL.StrSelectShieldWhere("vista_marca_contador", "id_marca", "descripcion='"+_cmbMarcaMedidor.getSelectedItem().toString()+"'"))||!mi_serie.equals(_txtSerieMedidor.getText().toString())){
					this.FcnIrregularidad.registrarIrregularidad("18");
				}
			}else if(!mi_marca.isEmpty()&&(_cmbMarcaMedidor.getSelectedItem().toString().equals("SS (SIN SERVICIO)")||(_cmbMarcaMedidor.getSelectedItem().toString().equals("SD (SERVICIO DIRECTO)")))){
				this.FcnIrregularidad.registrarIrregularidad("55");				
			}
		}
		
		if(mi_marca.isEmpty()&&(!_cmbMarcaMedidor.getSelectedItem().toString().equals("SS (SIN SERVICIO)"))&&(!_txtSerieMedidor.getText().toString().isEmpty())){
			this.FcnIrregularidad.registrarIrregularidad("67");
		}
		
		if(mi_marca.equals("SD (SERVICIO DIRECTO)")&&(mi_marca.equals(_cmbMarcaMedidor.getSelectedItem().toString()))){
			this.FcnIrregularidad.registrarIrregularidad("67");	
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_medidor_pruebas, menu);
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
				k.putExtra("Nivel", NivelUsuario);
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
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.EncontradoBtnRegistrar:
				if(!PruebasSQL.ExistRegistros("amd_medidor_encontrado", "id_orden='"+OrdenTrabajo+"'")){
					_tempRegistro.clear();
					_tempRegistro.put("id_orden", OrdenTrabajo);
					_tempRegistro.put("marca", _cmbMarcaMedidor.getSelectedItem().toString());
					_tempRegistro.put("serie", _txtSerieMedidor.getText().toString());
					_tempRegistro.put("tipo", _cmbTipoMedidor.getSelectedItem().toString());
					_tempRegistro.put("lectura", _txtLectura1.getText().toString());
					_tempRegistro.put("lectura_2", _txtLectura2.getText().toString());
					_tempRegistro.put("lectura_3", _txtLectura3.getText().toString());
					if(PruebasSQL.InsertRegistro("amd_medidor_encontrado",_tempRegistro)){
						ValidarMedidorEncontrado();
					}
				}else{
					Toast.makeText(this,"Ya existe un registro de medidor encontrado.", Toast.LENGTH_SHORT).show();
				}
				break;
				
				
			case R.id.EncontradoBtnEliminar:
				PruebasSQL.DeleteRegistro("amd_medidor_encontrado", "id_orden='"+OrdenTrabajo+"'");
				break;
				
				
			case R.id.PruebasBtnGuardar:
				_tempRegistro.clear();
				_tempRegistro.put("id_orden", OrdenTrabajo);
				_tempRegistro.put("p_conexion", _cmbConexion.getSelectedItem().toString());
				_tempRegistro.put("p_continuidad", _cmbContinuidad.getSelectedItem().toString());
				_tempRegistro.put("p_puentes", _cmbPuentes.getSelectedItem().toString());
				_tempRegistro.put("p_integrador",_cmbIntegrador.getSelectedItem().toString());
				_tempRegistro.put("p_vacio", _cmbVacio.getSelectedItem().toString());
				_tempRegistro.put("p_frenado", _cmbFrenado.getSelectedItem().toString());
				_tempRegistro.put("p_retirado", _cmbRetirado.getSelectedItem().toString());
				_tempRegistro.put("rozamiento", _cmbRozamiento.getSelectedItem().toString());
				_tempRegistro.put("aplomado", _cmbAplomado.getSelectedItem().toString());
				Toast.makeText(this,PruebasSQL.InsertOrUpdateRegistro("amd_pruebas", _tempRegistro, "id_orden='"+OrdenTrabajo+"'"), Toast.LENGTH_SHORT).show();
				
				_tempRegistro.clear();
				_tempRegistro.put("id_orden", OrdenTrabajo);
				_tempRegistro.put("lect_ini", _txtLectInicial.getText().toString());
				_tempRegistro.put("lect_final", _txtLectFinal.getText().toString());
				_tempRegistro.put("giros", _txtGiros.getText().toString());
				Toast.makeText(this,PruebasSQL.InsertOrUpdateRegistro("amd_prueba_integracion", _tempRegistro, "id_orden='"+OrdenTrabajo+"'"), Toast.LENGTH_SHORT).show();
				
				_tempRegistro.clear();
				_tempRegistro.put("id_inconsistencia", PruebasSQL.IntSelectShieldWhere("amd_inconsistencias","max(cast(id_inconsistencia AS INTEGER)) as id_inconsistencia","id_inconsistencia IS NOT NULL")+1);
				_tempRegistro.put("id_orden", OrdenTrabajo);
				_tempRegistro.put("id_nodo", "Integracion");
				_tempRegistro.put("valor", "1-Prueba Integracion Lectura Inicial:"+_txtLectInicial.getText().toString()+" Lectura Final:"+_txtLectFinal.getText().toString()+" Giros:"+_txtGiros.getText().toString());
				_tempRegistro.put("cod_inconsistencia", "GEN00");
				_tempRegistro.put("cuenta", CuentaCliente);
				_tempRegistro.put("usuario_ins", this.CedulaUsuario);
				_tempRegistro.put("tipo", "M");
				//Toast.makeText(this,PruebasSQL.InsertOrUpdateRegistro("amd_inconsistencias", _tempRegistro, "id_orden='"+OrdenTrabajo+"' AND cod_inconsistencia='GEN00'"), Toast.LENGTH_SHORT).show();				
				break;
				
				
			case R.id.PruebasBtnEliminar:
				if(PruebasSQL.DeleteRegistro("amd_pruebas", "id_orden='"+OrdenTrabajo+"'")){
					Toast.makeText(this, "Registro de pruebas eliminado correctamente.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "Error al eliminar el registro de pruebas.", Toast.LENGTH_SHORT).show();
				}
				
				if(PruebasSQL.DeleteRegistro("amd_prueba_integracion", "id_orden='"+OrdenTrabajo+"'")){
					Toast.makeText(this, "Prueba de integracion eliminada correctamente.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "Error al eliminar la prueba de integracion.", Toast.LENGTH_SHORT).show();
				}
				
				PruebasSQL.DeleteRegistro("amd_inconsistencias", "id_orden='"+OrdenTrabajo+"' AND cod_inconsistencia='GEN00'");		
				break;
		}
		MedidorEncontradoRegistrado();
	}
}
