package form_amdata;

import java.io.File;
import java.util.ArrayList;

import class_amdata.Class_Inconsistencias;
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

public class IrregularidadesObservaciones extends Activity implements OnClickListener, OnItemSelectedListener{
	private Class_Irregularidades 	FcnIrregularidad;
	private Class_Inconsistencias 	FcnInconsistencias;
	SQLite 			IrreSQL;
	Util   			IrreUtil;
	Tablas			GraphIrreTabla, GraphAnomaliasTabla;
	
	private TableLayout		TablaIrregularidades;
	private LinearLayout 	FilaTablaIrre;
	
	private TableLayout		TablaAnomalias;
	private LinearLayout 	FilaTablaAnomalias;
	
	private String		NombreUsuario	= "";
	private String		CedulaUsuario	= "";
	private String 		NivelUsuario	= "";
	private String 		OrdenTrabajo 	= "";
	private String 		CuentaCliente 	= "";
	private String 		FolderAplicacion= "";
	
	ArrayList<ContentValues> _tablaTemp 	= new ArrayList<ContentValues>();
	ContentValues _registroTemp = new ContentValues();
	
	ArrayList<String> StringIrregularidades= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorIrregularidades;	
	
	
	//Variables para consultar las observaciones y hacer el respectivo adapter
	ArrayList<ContentValues> Grupo 	= new ArrayList<ContentValues>();
	ArrayList<String> StringGrupo 	= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorGrupo;
	
	
	//Variables para consultar las observaciones y hacer el respectivo adapter
	ArrayList<ContentValues> SubGrupo 	= new ArrayList<ContentValues>();
	ArrayList<String> StringSubGrupo 	= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorSubGrupo;	
	
	EditText	_txtObservacion;
	Spinner 	_cmbGrupoInconsistencia, _cmbSubGrupoInconsistencia, _cmbIrregularidades; 
	Button		_btnRegistrarObservacion, _btnEditarObservacion, _btnEliminarObservacion, _btnRegistrarIrregularidad, _btnRemoveIrregularidad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_irregularidades_observaciones);
			
		Bundle bundle 	= getIntent().getExtras();
		this.NombreUsuario		= bundle.getString("NombreUsuario");
		this.CedulaUsuario		= bundle.getString("CedulaUsuario");
		this.NivelUsuario		= bundle.getString("NivelUsuario");
		this.OrdenTrabajo		= bundle.getString("OrdenTrabajo");
		this.CuentaCliente 		= bundle.getString("CuentaCliente");		
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");
		
		IrreSQL 		= new SQLite(this, this.FolderAplicacion);
		IrreUtil 		= new Util();
		FcnIrregularidad	= new Class_Irregularidades(this, this.FolderAplicacion, this.CedulaUsuario, this.OrdenTrabajo, this.CuentaCliente);
		FcnInconsistencias	= new Class_Inconsistencias(this, this.FolderAplicacion, this.CedulaUsuario, this.OrdenTrabajo, this.CuentaCliente);
		
		GraphIrreTabla 		= new Tablas(this, "descripcion", "600", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");
		GraphAnomaliasTabla	= new Tablas(this, "id_nodo,codigo,valor", "100,110,420", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");
		
		FilaTablaIrre				= (LinearLayout) findViewById(R.id.TablaIrregularidades);
		FilaTablaAnomalias			= (LinearLayout) findViewById(R.id.TablaObservaciones);
		_txtObservacion 			= (EditText) findViewById(R.id.IrrObsTxtObservacion);
		
		_cmbGrupoInconsistencia 	= (Spinner) findViewById(R.id.IrrObsCmbGrupo);
		_cmbSubGrupoInconsistencia 	= (Spinner) findViewById(R.id.IrrObsCmbSubgrupo);
		_cmbIrregularidades			= (Spinner) findViewById(R.id.IrreObsCmbIrregularidades);
		
		_btnRegistrarObservacion 	= (Button) findViewById(R.id.IrrObsBtnRegistrarObservacion);
		_btnEditarObservacion 		= (Button) findViewById(R.id.IrrObsBtnEditarObservacion);
		_btnEliminarObservacion 	= (Button) findViewById(R.id.IrrObsBtnEliminarObservacion);
		_btnRegistrarIrregularidad	= (Button) findViewById(R.id.IrreObsBtnAddIrregularidad);
		_btnRemoveIrregularidad		= (Button) findViewById(R.id.IrreObsBtnRemoveIrregularidad);
		
		_tablaTemp = IrreSQL.SelectData("amd_param_irregularidades", "descripcion", "id_irregularidad IS NOT NULL ORDER BY id_irregularidad ASC");
		IrreUtil.ArrayContentValuesToString(StringIrregularidades, _tablaTemp, "descripcion");
		AdaptadorIrregularidades= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringIrregularidades);
		_cmbIrregularidades.setAdapter(AdaptadorIrregularidades);
		
		_tablaTemp = IrreSQL.SelectData("amd_param_incosistencias", "descripcion", "tipo IN('I','O') AND aplicacion !='S' AND identificadorpadre=0");
		IrreUtil.ArrayContentValuesToString(StringGrupo, _tablaTemp, "descripcion");
		AdaptadorGrupo= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringGrupo);
		_cmbGrupoInconsistencia.setAdapter(AdaptadorGrupo);
		
		AdaptadorSubGrupo= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringSubGrupo);
		_cmbSubGrupoInconsistencia.setAdapter(AdaptadorSubGrupo);
		
		this.VerIrregularidades();
		this.VerAnomalias();
		_btnRegistrarIrregularidad.setOnClickListener(this);
		_btnRemoveIrregularidad.setOnClickListener(this);
		_btnRegistrarObservacion.setOnClickListener(this);
		_btnEditarObservacion.setOnClickListener(this);
		_btnEliminarObservacion.setOnClickListener(this);
		_cmbGrupoInconsistencia.setOnItemSelectedListener(this);
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_irregularidades_observaciones, menu);
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

	private void VerIrregularidades(){
		TablaIrregularidades = GraphIrreTabla.CuerpoTabla(this.FcnIrregularidad.getIrregularidades());
		FilaTablaIrre.removeAllViews();
		FilaTablaIrre.addView(TablaIrregularidades);
	}
	
	
	private void VerAnomalias(){
		TablaAnomalias = GraphAnomaliasTabla.CuerpoTabla(this.FcnInconsistencias.getInconsistencias());
		FilaTablaAnomalias.removeAllViews();
		FilaTablaAnomalias.addView(TablaAnomalias);
	}
	
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch(parent.getId()){
			case R.id.IrrObsCmbGrupo:
				_tablaTemp = IrreSQL.SelectData("amd_param_incosistencias", "descripcion", "tipo IN('I','O') AND aplicacion !='S' AND identificadorpadre='"+IrreSQL.StrSelectShieldWhere("amd_param_incosistencias","identificadornodo", "tipo IN ('I','O') AND aplicacion != 'S' AND descripcion ='"+_cmbGrupoInconsistencia.getSelectedItem().toString()+"'")+"'");
				IrreUtil.ArrayContentValuesToString(StringSubGrupo, _tablaTemp, "descripcion");
				_cmbSubGrupoInconsistencia.setAdapter(AdaptadorSubGrupo);
				AdaptadorSubGrupo.setNotifyOnChange(true);
				break;
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		String cod_inconsistencia[];
		String id_nodo;
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.IrreObsBtnAddIrregularidad:
				this.FcnIrregularidad.registrarIrregularidad(IrreSQL.StrSelectShieldWhere("amd_param_irregularidades", "id_irregularidad", "descripcion='"+_cmbIrregularidades.getSelectedItem().toString()+"'"));
				this.VerIrregularidades();
				break;
				
			case R.id.IrreObsBtnRemoveIrregularidad:
				this.FcnIrregularidad.eliminarIrregularidad(IrreSQL.StrSelectShieldWhere("amd_param_irregularidades", "id_irregularidad", "descripcion='"+_cmbIrregularidades.getSelectedItem().toString()+"'"));
				this.VerIrregularidades();
				break;
				
			case R.id.IrrObsBtnRegistrarObservacion:
				cod_inconsistencia = _cmbSubGrupoInconsistencia.getSelectedItem().toString().split("_");
				if(_cmbGrupoInconsistencia.getSelectedItem().toString().equals("05_administrativa")){
					this.FcnInconsistencias.registrarInconsistencia(cod_inconsistencia[1], cod_inconsistencia[0], "M");
				}else{
					this.FcnInconsistencias.registrarInconsistencia(_txtObservacion.getText().toString().replace("'", " "), cod_inconsistencia[0], "M");
				}	
				this.VerAnomalias();
				break;
				
			case R.id.IrrObsBtnEliminarObservacion:
				cod_inconsistencia = _cmbSubGrupoInconsistencia.getSelectedItem().toString().split("_");
				this.FcnInconsistencias.eliminarInconsistencia(cod_inconsistencia[0]);
				this.VerAnomalias();
				break;
				
			case R.id.IrrObsBtnEditarObservacion:
				id_nodo = IrreSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "id_nodo", "id_orden='"+OrdenTrabajo+"'");
				cod_inconsistencia = _cmbSubGrupoInconsistencia.getSelectedItem().toString().split("_");
				_txtObservacion.setText(IrreSQL.StrSelectShieldWhere("amd_inconsistencias", "valor", "id_orden='"+OrdenTrabajo+"' AND id_nodo='"+id_nodo+"' AND cod_inconsistencia='"+cod_inconsistencia[0]+"'"));
				if(IrreSQL.DeleteRegistro("amd_inconsistencias", "id_orden='"+OrdenTrabajo+"' AND id_nodo='"+id_nodo+"' AND cod_inconsistencia='"+cod_inconsistencia[0]+"'")){
					Toast.makeText(this,"Inconsistencia eliminada y lista para ser editada.", Toast.LENGTH_SHORT).show();  
					this.VerAnomalias();
				}
				break;
				
		}
	}
}


