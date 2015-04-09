package form_amdata;

import java.io.File;
import java.util.ArrayList;

import class_amdata.Class_Sellos;
import sypelc.androidamdata.R;
import dialogos.DialogSingleTxt;
import miscelanea.SQLite;
import miscelanea.Tablas;
import miscelanea.Util;
import adaptador_rows.AdaptadorSixItems;
import adaptador_rows.DetalleSixItems;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Form_Sellos extends Activity implements OnClickListener, OnItemSelectedListener{
	private static int 		CONFIRM_SELLOS = 1;
	private Intent 			DialogoSimple; 
	
	
	private SQLite 			SellosSQL;
	private Util   			SellosUtil;
	private Class_Sellos 	FcnSellos;
		
	private AdaptadorSixItems			AdaptadorListadoSellos;
	private ArrayList<DetalleSixItems> 	ArrayListadoSellos = new ArrayList<DetalleSixItems>();
	
	private String 	NombreUsuario	= "";
	private String  CedulaUsuario	= "";
	private String 	NivelUsuario	= "";
	private String 	OrdenTrabajo 	= "";
	private String 	CuentaCliente 	= "";
	private String 	FolderAplicacion= "";
	
	private ContentValues SelloSeleccionado	= new ContentValues(); 
	
	ContentValues 			_tempRegistro 	= new ContentValues();
	ArrayList<ContentValues>_tempTabla 		= new ArrayList<ContentValues>();	
	
	private String _strTipoMovimiento[] = {"RETIRADO","INSTALADO","EXISTENTE"};
	private String _strEstado[] = {"N-NORMAL","R-ROTO","P-PEGADO","A-ANORMAL"};
	
	ArrayAdapter<String> AdaptadorTipoMovimiento;
	ArrayAdapter<String> AdaptadorEstado;
	
	ArrayList<String> strTipoSello 			= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorTipoSello;
	
	ArrayList<String> strUbicacion 			= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorUbicacion;
	
	ArrayList<String> strColor 			= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorColor;
	
	EditText	_txtSerie;
	TextView	_lblEstado;
	ListView	_lstSellos;
	Spinner 	_cmbTipoMovimiento, _cmbTipoSello, _cmbUbicacion, _cmbColor, _cmbEstado;
	Button		_btnRegistrarSello;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sellos);
		
		Bundle bundle 	= getIntent().getExtras();
		NombreUsuario	= bundle.getString("NombreUsuario");
		CedulaUsuario	= bundle.getString("CedulaUsuario");
		NivelUsuario	= bundle.getString("NivelUsuario");
		OrdenTrabajo	= bundle.getString("OrdenTrabajo");
		CuentaCliente 	= bundle.getString("CuentaCliente");		
		FolderAplicacion= bundle.getString("FolderAplicacion");
		
		SellosSQL 		= new SQLite(this, FolderAplicacion);
		SellosUtil 		= new Util();
		
		DialogoSimple 	= new Intent(this,DialogSingleTxt.class); 
		FcnSellos		= new Class_Sellos(this, this.FolderAplicacion, this.CedulaUsuario, this.OrdenTrabajo, this.CuentaCliente);
		
		_lblEstado 			= (TextView) findViewById(R.id.SellosLblEstado);
		_txtSerie			= (EditText) findViewById(R.id.SellosTxtSerie);		
		
		_cmbTipoMovimiento 	= (Spinner) findViewById(R.id.SellosCmbTipoIngreso);
		_cmbTipoSello		= (Spinner) findViewById(R.id.SellosCmbTipoSello);
		_cmbUbicacion		= (Spinner) findViewById(R.id.SellosCmbUbicacion);
		_cmbColor			= (Spinner) findViewById(R.id.SellosCmbColor);
		_cmbEstado			= (Spinner) findViewById(R.id.SellosCmbEstado);
		
		_lstSellos			= (ListView) findViewById(R.id.SellosLstSellos);
		
		_btnRegistrarSello 	= (Button) findViewById(R.id.SellosBtnRegistrar);
		
		AdaptadorTipoMovimiento = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strTipoMovimiento);
		_cmbTipoMovimiento.setAdapter(AdaptadorTipoMovimiento);
		
		_tempTabla = SellosSQL.SelectData("amd_param_tipo_sello", "descripcion", " codigo IS NOT NULL");
		SellosUtil.ArrayContentValuesToString(strTipoSello, _tempTabla, "descripcion");
		AdaptadorTipoSello 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, strTipoSello); 
		_cmbTipoSello.setAdapter(AdaptadorTipoSello);
		
		
		_tempTabla = SellosSQL.SelectData("amd_param_ubicacion_sello", "descripcion", " codigo IS NOT NULL");
		SellosUtil.ArrayContentValuesToString(strUbicacion, _tempTabla, "descripcion");
		AdaptadorUbicacion 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, strUbicacion); 
		_cmbUbicacion.setAdapter(AdaptadorUbicacion);
		
		
		_tempTabla = SellosSQL.SelectData("amd_param_color_sello", "descripcion", " codigo IS NOT NULL");
		SellosUtil.ArrayContentValuesToString(strColor, _tempTabla, "descripcion");
		AdaptadorColor 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, strColor); 
		_cmbColor.setAdapter(AdaptadorColor);
		
		AdaptadorEstado 		= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strEstado);
		_cmbEstado.setAdapter(AdaptadorEstado);
		
		this.VerSellosRegistrados();
		
		registerForContextMenu(this._lstSellos);
		//this._lstSellos.setOnItemClickListener(this);
		this._btnRegistrarSello.setOnClickListener(this);
		this._cmbTipoMovimiento.setOnItemSelectedListener(this);	
	}
	
	/**Funciones para el control del menu contextual del listview que muestra las ordenes de trabajo**/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        //this.ruta_seleccionada = arrayListadoRutas.get(info.position).getCodigoRuta();
        this.SelloSeleccionado.clear();
        this.SelloSeleccionado.put("tipo_ingreso",this.ArrayListadoSellos.get(info.position).getItem1());
        this.SelloSeleccionado.put("tipo_sello",this.ArrayListadoSellos.get(info.position).getItem2());
        this.SelloSeleccionado.put("serie",this.ArrayListadoSellos.get(info.position).getItem3());
        this.SelloSeleccionado.put("color",this.ArrayListadoSellos.get(info.position).getItem4());
        this.SelloSeleccionado.put("ubicacion",this.ArrayListadoSellos.get(info.position).getItem5());
        this.SelloSeleccionado.put("irregularidad",this.ArrayListadoSellos.get(info.position).getItem6());
                
        switch(v.getId()){
            case R.id.SellosLstSellos:
                menu.setHeaderTitle("Sello " + this.SelloSeleccionado.getAsString("serie")+" \nTipo "+this.SelloSeleccionado.getAsString("tipo_sello")+" \nColor "+this.SelloSeleccionado.getAsString("color"));
                super.onCreateContextMenu(menu, v, menuInfo);
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu_sellos, menu);
                break;
        }
    }
    
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.SellosCtxEliminar:
                if(this.FcnSellos.eliminarSello(this.SelloSeleccionado.getAsString("tipo_ingreso"), 
                								this.SelloSeleccionado.getAsString("serie"))){
                	this.VerSellosRegistrados();
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sellos, menu);
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
		switch(parent.getId()){
			case R.id.SellosCmbTipoIngreso:
				if(_cmbTipoMovimiento.getSelectedItem().toString().equals("INSTALADO")){
					_cmbEstado.setEnabled(false);
					_cmbEstado.setSelection(AdaptadorEstado.getPosition("N-NORMAL"));
				}else{
					_cmbEstado.setEnabled(true);
				}
				break;				
		}		
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.SellosBtnRegistrar:
				DialogoSimple.putExtra("titulo", "CONFIRMACION DE LA SERIE DEL SELLO");
				DialogoSimple.putExtra("lbl1","Serie:");
				DialogoSimple.putExtra("txt1","");
		        startActivityForResult(DialogoSimple, CONFIRM_SELLOS);
				break;
				
			default:
				break;
		}		
	}

	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try{
			if (resultCode == RESULT_OK && requestCode == CONFIRM_SELLOS) {
				if(data.getExtras().getBoolean("response")){
					if(FcnSellos.getConfirmacionSerie(_txtSerie.getText().toString(), data.getExtras().getString("txt1"))){
						this.FcnSellos.registrarSello(_cmbTipoMovimiento.getSelectedItem().toString(), _cmbTipoSello.getSelectedItem().toString(), _cmbUbicacion.getSelectedItem().toString(), _cmbColor.getSelectedItem().toString(), _txtSerie.getText().toString(), _cmbEstado.getSelectedItem().toString());
						this.VerSellosRegistrados();
					}
				}
			}
		}catch(Exception e){
			
		}
    }
	
	private void VerSellosRegistrados(){
		this.ArrayListadoSellos.clear();
		this._tempTabla = this.FcnSellos.getSellosRegistrados();
		for(int i=0;i<this._tempTabla.size();i++){
			this._tempRegistro = this._tempTabla.get(i);
			this.ArrayListadoSellos.add(new DetalleSixItems(this._tempRegistro.getAsString("tipo_ingreso"),
															this._tempRegistro.getAsString("tipo_sello"),
															this._tempRegistro.getAsString("serie"),
															this._tempRegistro.getAsString("color"),
															this._tempRegistro.getAsString("ubicacion"),
															this._tempRegistro.getAsString("irregularidad")));
		}
		this.AdaptadorListadoSellos = new AdaptadorSixItems(Form_Sellos.this, ArrayListadoSellos);
		this._lstSellos.setAdapter(this.AdaptadorListadoSellos);
		this.AdaptadorListadoSellos.notifyDataSetChanged();
	}
	

	/*@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}*/
}
