package form_amdata;

import java.io.File;
import java.util.ArrayList;

import class_amdata.Class_Impresiones;

import sypelc.androidamdata.R;
import miscelanea.FormatosActas;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Materiales extends Activity implements OnClickListener, OnItemSelectedListener, OnSeekBarChangeListener{
	FormatosActas 	ActaImpresa;
	Class_Impresiones FcnImpresion;
	SQLite 				MaterialesSQL;
	Util   				MaterialesUtil;
	
	private String 	NombreUsuario	= "";
	private String  CedulaUsuario	= "";
	private String 	NivelUsuario	= "";
	private String 	OrdenTrabajo 	= "";
	private String 	CuentaCliente 	= "";
	private String 	FolderAplicacion= "";
	
	/**Varibles para realizar Query's de forma general**/
	ContentValues 			_tempRegistro 	= new ContentValues();
	ArrayList<ContentValues>_tempTabla 		= new ArrayList<ContentValues>();
	
	
	/**Creacion de objetos para la tabla de materiales definitivos**/
	Tablas					TablaMaterialesDefinitivos;
	private TableLayout		TablaDefinitivos;
	private LinearLayout 	FilaTablaDefinitivos;
	
	/**ArrayList de los materiales definitivos**/
	private ArrayList<String> _strMaterialesDefinitivos= new ArrayList<String>();
	private String 	_strUnidades[];
	private String 	_strDecimas[]	={"0","1","2","3","4","5","6","7","8","9"};
	
	/**Declaracion de los adaptadores para los materiales definitivos**/
	private ArrayAdapter<String> 	AdaptadorUnidades;
	private ArrayAdapter<String> 	AdaptadorDecimas;
	private ArrayAdapter<String> 	AdaptadorMaterialesDefinitivos;
	
	/**Declaracion de las instancias a los objetos de materiales definitivos**/
	TextView	_lblDefinitivosCuotas;
	Spinner 	_cmbDefinitivosUnidades, _cmbDefinitivosDecimales, _cmbDefinitivosMateriales;
	Button 		_btnAgregarDefinitivo, _btnEliminarDefinitivo;
	SeekBar		_barDefinitivosCuotas;
	/**Fin de la declaracion de variables para los materiales definitivos**/
	
	
	
	/**Creacion de objetos para la tabla de materiales provisionales**/
	Tablas					TablaMaterialesProvisionales;
	private TableLayout		TablaProvisionales;
	private LinearLayout 	FilaTablaProvisionales;
	
	/**ArrayList de los materiales provisionales**/
	private ArrayList<String> _strMaterialesProvisionales= new ArrayList<String>();
	private String 	_strUnidadesProvisionales[];
	private String 	_strDecimasProvisionales[]	={"0","1","2","3","4","5","6","7","8","9"};
	
	/**Declaracion de los adaptadores para los materiales provisionales**/
	private ArrayAdapter<String> 	AdaptadorUnidadesProvisionales;
	private ArrayAdapter<String> 	AdaptadorDecimasProvisionales;
	private ArrayAdapter<String> 	AdaptadorMaterialesProvisionales;
	
	/**Declaracion de las instancias a los objetos de materiales provisionales**/
	TextView	_lblProvisionalesCuotas;
	Spinner 	_cmbProvisionalesUnidades, _cmbProvisionalesDecimales, _cmbProvisionalesMateriales;
	Button 		_btnAgregarProvisionales, _btnEliminarProvisionales;
	SeekBar		_barProvisionalesCuotas;
	/**Fin de la declaracion de variables para los materiales provisionales**/
	
	
	
	/**Creacion de objetos para la tabla de materiales de usuario**/
	Tablas					TablaMaterialesUsuario;
	private TableLayout		TablaUsuario;
	private LinearLayout 	FilaTablaUsuario;
	
	/**ArrayList de los materiales de usuario**/
	private ArrayList<String> _strMaterialesUsuario= new ArrayList<String>();
	private String 	_strUnidadesUsuario[];
	private String 	_strDecimasUsuario[]	={"0","1","2","3","4","5","6","7","8","9"};
	private String  _strEstadoMatUsuario[]	={"Retirado","Reutilizado","Instalado"};
	private String 	_strDisposicionMatUsuario[]	={"EA-Entregado al Usuario","BC-Bodega Contratista","BE-Bodega EMSA"};
	
	/**Declaracion de los adaptadores para los materiales de usuario**/
	private ArrayAdapter<String> 	AdaptadorEstadoMatUsuario;
	private ArrayAdapter<String> 	AdaptadorDisposicionMatUsuario;
	private ArrayAdapter<String> 	AdaptadorUnidadesUsuario;
	private ArrayAdapter<String> 	AdaptadorDecimasUsuario;
	private ArrayAdapter<String> 	AdaptadorMaterialesUsuario;
	
	/**Declaracion de las instancias a los objetos de materiales de usuario**/
	TextView	_lblUsuarioCuotas;
	Spinner 	_cmbUsuarioEstado, _cmbUsuarioMateriales, _cmbUsuarioUnidades, _cmbUsuarioDecimales, _cmbUsuarioDisposicion;
	Button 		_btnAgregarUsuario, _btnEliminarUsuario;
	/**Fin de la declaracion de variables para los materiales de usuario**/
	
	
	
	/**Objetos para la observacion de materiales**/
	EditText 	_txtObservacionMat;
	Button		_btnObservacionGuardar;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_materiales);
		
		Bundle bundle 	= getIntent().getExtras();
		NombreUsuario	= bundle.getString("NombreUsuario");
		CedulaUsuario	= bundle.getString("CedulaUsuario");
		NivelUsuario	= bundle.getString("NivelUsuario");
		OrdenTrabajo	= bundle.getString("OrdenTrabajo");
		CuentaCliente 	= bundle.getString("CuentaCliente");		
		FolderAplicacion= bundle.getString("FolderAplicacion");
		
		ActaImpresa		= new FormatosActas(this, this.FolderAplicacion, true);
		MaterialesSQL 	= new SQLite(this, FolderAplicacion);
		MaterialesUtil 	= new Util();
		FcnImpresion	= new Class_Impresiones(this, this.FolderAplicacion, this.CedulaUsuario);
		
		/**Inicializacion del tabhost**/
		TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
		tabs.setup();
		 
		TabHost.TabSpec spec=tabs.newTabSpec("DefintivosProvisionales");		
		spec.setContent(R.id.tab2);
		spec.setIndicator("Definitivos y/o Provisionales");
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("ClienteObservacion");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Cliente y Observacion");
		tabs.addTab(spec);				
		tabs.setCurrentTab(0);	
				
				
		
		/**Inicio de la creacion de las instancias a los objetos de los materiales definitivos**/
		TablaMaterialesDefinitivos	= new Tablas(this, "descripcion,cantidad", "510,110", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");
		FilaTablaDefinitivos 		= (LinearLayout) findViewById(R.id.TablaDefinitivos);
		
		SetStrUnidades();
		
		_cmbDefinitivosUnidades 	= (Spinner) findViewById(R.id.DefinitivosCmbUnidades);
		_cmbDefinitivosDecimales 	= (Spinner) findViewById(R.id.DefinitivosCmbDecimas);
		_cmbDefinitivosMateriales	= (Spinner) findViewById(R.id.DefinitivosCmbMaterial);
		_barDefinitivosCuotas		= (SeekBar) findViewById(R.id.DefinitivosBarCuotas);
		_lblDefinitivosCuotas		= (TextView) findViewById(R.id.DefinitivosLblNumCuotas);
		_btnAgregarDefinitivo		= (Button) findViewById(R.id.DefinitivosBtnAgregar);
		_btnEliminarDefinitivo		= (Button) findViewById(R.id.DefinitivosBtnEliminar);
		
		_barDefinitivosCuotas.setProgress(0);
		_barDefinitivosCuotas.setMax(11);
		
		_tempTabla = MaterialesSQL.SelectData("vista_material_id_trabajo_orden", "descripcion", "id_orden='"+OrdenTrabajo+"' ORDER BY descripcion ASC");
		MaterialesUtil.ArrayContentValuesToString(_strMaterialesDefinitivos, _tempTabla, "descripcion");
		AdaptadorMaterialesDefinitivos= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strMaterialesDefinitivos);
		_cmbDefinitivosMateriales.setAdapter(AdaptadorMaterialesDefinitivos);
				
		AdaptadorUnidades= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strUnidades);
		_cmbDefinitivosUnidades.setAdapter(AdaptadorUnidades);
		
		AdaptadorDecimas= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strDecimas);
		_cmbDefinitivosDecimales.setAdapter(AdaptadorDecimas);		
		
		_barDefinitivosCuotas.setOnSeekBarChangeListener(this);
		_btnAgregarDefinitivo.setOnClickListener(this);
		_btnEliminarDefinitivo.setOnClickListener(this);
		MostrarMaterialesDefinitivos();
		
		
		
		/**Inicio de la creacion de las instancias a los objetos de los materiales provisionales**/
		TablaMaterialesProvisionales= new Tablas(this, "descripcion,cantidad", "510,110", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");
		FilaTablaProvisionales 		= (LinearLayout) findViewById(R.id.TablaProvisionales);
		
		SetStrUnidadesProvisionales();
		
		_cmbProvisionalesUnidades 	= (Spinner) findViewById(R.id.ProvisionalesCmbUnidades);
		_cmbProvisionalesDecimales 	= (Spinner) findViewById(R.id.ProvisionalesCmbDecimas);
		_cmbProvisionalesMateriales	= (Spinner) findViewById(R.id.ProvisionalesCmbMaterial);
		_barProvisionalesCuotas		= (SeekBar) findViewById(R.id.ProvisionalesBarCuotas);
		_lblProvisionalesCuotas		= (TextView) findViewById(R.id.ProvisionalesLblNumCuotas);
		_btnAgregarProvisionales	= (Button) findViewById(R.id.ProvisionalesBtnAgregar);
		_btnEliminarProvisionales	= (Button) findViewById(R.id.ProvisionalesBtnEliminar);
		
		_barProvisionalesCuotas.setProgress(0);
		_barProvisionalesCuotas.setMax(11);
		
		_tempTabla = MaterialesSQL.SelectData("vista_material_id_trabajo_orden", "descripcion", "id_orden='"+OrdenTrabajo+"' ORDER BY descripcion ASC");
		MaterialesUtil.ArrayContentValuesToString(_strMaterialesProvisionales, _tempTabla, "descripcion");
		AdaptadorMaterialesProvisionales= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strMaterialesProvisionales);
		_cmbProvisionalesMateriales.setAdapter(AdaptadorMaterialesProvisionales);
				
		AdaptadorUnidadesProvisionales= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strUnidadesProvisionales);
		_cmbProvisionalesUnidades.setAdapter(AdaptadorUnidadesProvisionales);
		
		AdaptadorDecimasProvisionales= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strDecimasProvisionales);
		_cmbProvisionalesDecimales.setAdapter(AdaptadorDecimasProvisionales);		
		
		_barProvisionalesCuotas.setOnSeekBarChangeListener(this);
		_btnAgregarProvisionales.setOnClickListener(this);
		_btnEliminarProvisionales.setOnClickListener(this);
		MostrarMaterialesProvisionales();
		
		
		
		
		/**Inicio de la creacion de las instancias a los objetos de los materiales de usuario**/
		TablaMaterialesUsuario	= new Tablas(this, "material,cantidad", "510,110", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");
		FilaTablaUsuario		= (LinearLayout) findViewById(R.id.TablaUsuario);
		
		SetStrUnidadesUsuario();		
		_cmbUsuarioUnidades 	= (Spinner) findViewById(R.id.UsuarioCmbUnidades);
		_cmbUsuarioDecimales 	= (Spinner) findViewById(R.id.UsuarioCmbDecimas);
		_cmbUsuarioMateriales	= (Spinner) findViewById(R.id.UsuarioCmbMaterial);
		_cmbUsuarioEstado 		= (Spinner) findViewById(R.id.UsuarioCmbEstado);
		_cmbUsuarioDisposicion	= (Spinner) findViewById(R.id.UsuarioCmbDisposicion);
				
		_btnAgregarUsuario		= (Button) findViewById(R.id.UsuarioBtnAgregar);
		_btnEliminarUsuario		= (Button) findViewById(R.id.UsuarioBtnEliminar);
		
		
		_tempTabla = MaterialesSQL.SelectData("vista_material_id_trabajo_orden", "descripcion", "id_orden='"+OrdenTrabajo+"' ORDER BY descripcion ASC");
		MaterialesUtil.ArrayContentValuesToString(_strMaterialesUsuario, _tempTabla, "descripcion");
		AdaptadorMaterialesUsuario= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strMaterialesUsuario);
		_cmbUsuarioMateriales.setAdapter(AdaptadorMaterialesUsuario);
				
		
		AdaptadorEstadoMatUsuario = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strEstadoMatUsuario);
		_cmbUsuarioEstado.setAdapter(AdaptadorEstadoMatUsuario);
		
		AdaptadorDisposicionMatUsuario = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strDisposicionMatUsuario);
		_cmbUsuarioDisposicion.setAdapter(AdaptadorDisposicionMatUsuario);
		
		AdaptadorUnidadesUsuario= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strUnidadesUsuario);
		_cmbUsuarioUnidades.setAdapter(AdaptadorUnidadesUsuario);
		
		AdaptadorDecimasUsuario= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strDecimasUsuario);
		_cmbUsuarioDecimales.setAdapter(AdaptadorDecimasUsuario);		
		
		_btnAgregarUsuario.setOnClickListener(this);
		_btnEliminarUsuario.setOnClickListener(this);
		MostrarMaterialesUsuario();
		
		
		_txtObservacionMat		= (EditText) findViewById(R.id.ObservacionTxtObs);
		_btnObservacionGuardar 	= (Button) findViewById(R.id.ObservacionBtnGuardar);
		
		
		_txtObservacionMat.setText(MaterialesSQL.StrSelectShieldWhere("amd_observacion_materiales", "observacion", "id_orden='"+OrdenTrabajo+"'"));		
		_btnObservacionGuardar.setOnClickListener(this);
	}
	
	
	public void MostrarMaterialesProvisionales(){
		_tempTabla = MaterialesSQL.SelectData("vista_materiales_provisionales", "descripcion, cantidad", "id_orden='"+OrdenTrabajo+"'");
		TablaProvisionales = TablaMaterialesProvisionales.CuerpoTabla(_tempTabla);
		FilaTablaProvisionales.removeAllViews();
		FilaTablaProvisionales.addView(TablaProvisionales);
	}
	
	
	public void MostrarMaterialesDefinitivos(){
		_tempTabla = MaterialesSQL.SelectData("vista_materiales_trabajo_orden", "descripcion,cantidad", "id_orden='"+OrdenTrabajo+"'");
		TablaDefinitivos = TablaMaterialesDefinitivos.CuerpoTabla(_tempTabla);
		FilaTablaDefinitivos.removeAllViews();
		FilaTablaDefinitivos.addView(TablaDefinitivos);
	}
	
	
	public void MostrarMaterialesUsuario(){
		_tempTabla = MaterialesSQL.SelectData("amd_material_usuario", "material,cantidad", "id_orden='"+OrdenTrabajo+"'");
		TablaUsuario = TablaMaterialesUsuario.CuerpoTabla(_tempTabla);
		FilaTablaUsuario.removeAllViews();
		FilaTablaUsuario.addView(TablaUsuario);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_materiales, menu);
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
				
			case R.id.OriginalEmpresa:
				if(this.FcnImpresion.validarDatosImpresionMateriales(OrdenTrabajo)){
					ActaImpresa.FormatoMateriales(OrdenTrabajo,"Materiales", 1, CedulaUsuario);
				}	
				return true;	
				
			case R.id.CopiaUsuario:
				if(this.FcnImpresion.validarDatosImpresionMateriales(OrdenTrabajo)){
					ActaImpresa.FormatoMateriales(OrdenTrabajo,"Materiales", 2, CedulaUsuario);
				}		
				return true;				
				
				
			case R.id.CopiaArchivo:
				
				if(this.FcnImpresion.validarDatosImpresionMateriales(OrdenTrabajo)){
					ActaImpresa.FormatoMateriales(OrdenTrabajo,"Materiales", 3, CedulaUsuario);
				}	
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
	
	
	

	public void SetStrUnidades(){
		_strUnidades = new String[150];
		for(int i=0;i<150;i++){
			_strUnidades[i]=String.valueOf(i);
		}
	}
	
	public void SetStrUnidadesProvisionales(){
		_strUnidadesProvisionales = new String[150];
		for(int i=0;i<150;i++){
			_strUnidadesProvisionales[i]=String.valueOf(i);
		}
	}
	
	
	public void SetStrUnidadesUsuario(){
		_strUnidadesUsuario = new String[150];
		for(int i=0;i<150;i++){
			_strUnidadesUsuario[i]=String.valueOf(i);
		}
	}
	
	
	
	public void EliminarMaterial(String _tipo, String _material){
		String _id_material = MaterialesSQL.StrSelectShieldWhere("amd_param_materiales", "id_material", "descripcion='"+_material+"'");
		if(_tipo.equals("Definitivo")){
			if(MaterialesSQL.ExistRegistros("amd_materiales_trabajo_orden", "id_orden='"+OrdenTrabajo+"' AND id_material='"+_id_material+"'")){
				if(MaterialesSQL.DeleteRegistro("amd_materiales_trabajo_orden", "id_orden='"+OrdenTrabajo+"' AND id_material='"+_id_material+"'")){
					Toast.makeText(this,"Material eliminado correctamente.",Toast.LENGTH_SHORT).show();
					MostrarMaterialesDefinitivos();
				}else{
					Toast.makeText(this,"Error al intentar eliminar el material.",Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this,"No existe este material registrado como definitivo.",Toast.LENGTH_SHORT).show();
			}	
		}else if(_tipo.equals("Provisional")){
			if(MaterialesSQL.ExistRegistros("amd_materiales_provisionales", "id_orden='"+OrdenTrabajo+"' AND elemento='"+_id_material+"'")){
				if(MaterialesSQL.DeleteRegistro("amd_materiales_provisionales", "id_orden='"+OrdenTrabajo+"' AND elemento='"+_id_material+"'")){
					Toast.makeText(this,"Material eliminado correctamente.",Toast.LENGTH_SHORT).show();
					MostrarMaterialesProvisionales();
				}else{
					Toast.makeText(this,"Error al intentar eliminar el material.",Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this,"No existe este material registrado como provisional.",Toast.LENGTH_SHORT).show();
			}
		}else if(_tipo.equals("Usuario")){
			if(MaterialesSQL.ExistRegistros("amd_material_usuario", "id_orden='"+OrdenTrabajo+"' AND material='"+_material+"'")){
				if(MaterialesSQL.DeleteRegistro("amd_material_usuario", "id_orden='"+OrdenTrabajo+"' AND material='"+_material+"'")){
					Toast.makeText(this,"Material eliminado correctamente.",Toast.LENGTH_SHORT).show();
					MostrarMaterialesUsuario();
				}else{
					Toast.makeText(this,"Error al intentar eliminar el material.",Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this,"No existe este material registrado como de usuario.",Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	public void GuardarMaterial(String _tipo, String _material, double _cantidad, int _cuotas){
		if(_cantidad == 0){
			Toast.makeText(this,"No ha seleccionado una cantidad valida.",Toast.LENGTH_SHORT).show();
		}else if(_material.isEmpty()){
			Toast.makeText(this,"No ha seleccionado un material valido.",Toast.LENGTH_SHORT).show();
		}else{
			if(_tipo.equals("Definitivo")){
				_tempRegistro.clear();
				_tempRegistro.put("id_orden", OrdenTrabajo);
				_tempRegistro.put("id_trabajo", MaterialesSQL.StrSelectShieldWhere("vista_ordenes_trabajo", "id_trabajo", "id_orden='"+OrdenTrabajo+"'"));
				_tempRegistro.put("id_material", MaterialesSQL.StrSelectShieldWhere("amd_param_materiales", "id_material", "descripcion='"+_material+"'"));
				_tempRegistro.put("cantidad", _cantidad);
				_tempRegistro.put("cuotas", _cuotas);
				_tempRegistro.put("usuario_ins", this.CedulaUsuario);
				_tempRegistro.put("automatico",0);
				if(MaterialesSQL.InsertRegistro("amd_materiales_trabajo_orden", _tempRegistro)){
					Toast.makeText(this,_material + " registrado correctamente.",Toast.LENGTH_SHORT).show();
					MostrarMaterialesDefinitivos();
				}				
			}else if(_tipo.equals("Provisional")){
				_tempRegistro.clear();
				_tempRegistro.put("id_orden",OrdenTrabajo);
				_tempRegistro.put("elemento",MaterialesSQL.StrSelectShieldWhere("amd_param_materiales", "id_material", "descripcion='"+_material+"'"));
				_tempRegistro.put("marca", "");
				_tempRegistro.put("serie", "");
				_tempRegistro.put("valor", MaterialesSQL.StrSelectShieldWhere("amd_param_materiales", "valor", "descripcion='"+_material+"'"));
				_tempRegistro.put("id_agrupador","");
				_tempRegistro.put("cuenta",CuentaCliente);
				_tempRegistro.put("proceso", "D");
				_tempRegistro.put("estado", "P");
				_tempRegistro.put("usuario_ins", this.CedulaUsuario);
				_tempRegistro.put("cantidad", _cantidad);
				if(MaterialesSQL.InsertRegistro("amd_materiales_provisionales", _tempRegistro)){
					Toast.makeText(this,_material + " registrado correctamente.",Toast.LENGTH_SHORT).show();
					MostrarMaterialesProvisionales();
				}
			}else if(_tipo.equals("Usuario")){
				_tempRegistro.clear();
				_tempRegistro.put("id_orden", OrdenTrabajo);
				_tempRegistro.put("material",_material);
				_tempRegistro.put("cantidad", _cantidad);
				_tempRegistro.put("estado", _cmbUsuarioEstado.getSelectedItem().toString());
				_tempRegistro.put("disposicion", _cmbUsuarioDisposicion.getSelectedItem().toString());
				if(MaterialesSQL.InsertRegistro("amd_material_usuario", _tempRegistro)){
					Toast.makeText(this,_material + " registrado correctamente.",Toast.LENGTH_SHORT).show();
					MostrarMaterialesUsuario();
				}				
			}
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
			case R.id.DefinitivosBtnAgregar:
				GuardarMaterial("Definitivo", _cmbDefinitivosMateriales.getSelectedItem().toString(), Double.parseDouble(_cmbDefinitivosUnidades.getSelectedItem().toString()+"."+_cmbDefinitivosDecimales.getSelectedItem().toString()),Integer.parseInt(_lblDefinitivosCuotas.getText().toString()));
				break;
				
			case R.id.ProvisionalesBtnAgregar:
				GuardarMaterial("Provisional", _cmbProvisionalesMateriales.getSelectedItem().toString(), Double.parseDouble(_cmbProvisionalesUnidades.getSelectedItem().toString()+"."+_cmbProvisionalesDecimales.getSelectedItem().toString()),Integer.parseInt(_lblProvisionalesCuotas.getText().toString()));
				break;	
				
			case R.id.UsuarioBtnAgregar:
				GuardarMaterial("Usuario", _cmbUsuarioMateriales.getSelectedItem().toString(), Double.parseDouble(_cmbUsuarioUnidades.getSelectedItem().toString()+"."+_cmbUsuarioDecimales.getSelectedItem().toString()),0);
				break;		
			
			case R.id.DefinitivosBtnEliminar:
				EliminarMaterial("Definitivo",_cmbDefinitivosMateriales.getSelectedItem().toString());
				break;
			
			case R.id.ProvisionalesBtnEliminar:
				EliminarMaterial("Provisional",_cmbProvisionalesMateriales.getSelectedItem().toString());
				break;
				
			case R.id.UsuarioBtnEliminar:
				EliminarMaterial("Usuario",_cmbUsuarioMateriales.getSelectedItem().toString());
				break;	
				
			case R.id.ObservacionBtnGuardar:
				if(_txtObservacionMat.getText().toString().isEmpty()){
					Toast.makeText(this,"No ha ingresado una observacion de material valida.",Toast.LENGTH_SHORT).show();
				}else{
					_tempRegistro.clear();
					_tempRegistro.put("id_orden", OrdenTrabajo);
					_tempRegistro.put("observacion", _txtObservacionMat.getText().toString());
					Toast.makeText(this, MaterialesSQL.InsertOrUpdateRegistro("amd_observacion_materiales", _tempRegistro, "id_orden='"+OrdenTrabajo+"'"),Toast.LENGTH_SHORT).show();
				}
		}
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		switch(seekBar.getId()){
			case R.id.DefinitivosBarCuotas:
				_lblDefinitivosCuotas.setText(String.valueOf(progress+1));	
				break;
				
			case R.id.ProvisionalesBarCuotas:
				_lblProvisionalesCuotas.setText(String.valueOf(progress+1));	
				break;	
		}
			
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
