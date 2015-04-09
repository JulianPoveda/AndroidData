package form_amdata;

import java.io.File;
import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;


public class Acometida extends Activity implements OnClickListener{
	Tablas	AcometidaTabla;
	SQLite 	AcometidaSQL;
	Util   	AcometidaUtil;
	
	private String		NombreUsuario	= "";
	private String		CedulaUsuario	= "";
	private String 		NivelUsuario	= "";
	private String 		OrdenTrabajo 	= "";
	private String 		CuentaCliente 	= "";
	private String 		FolderAplicacion= "";
	private String  	Id_Acometida	= "";
	
	
	/*Contantes para los combos del activity*/
	private String[] _strTipoIngreso=	{"...","Existente","Nueva"};
	private String[] _strConductor	= 	{"...","Aluminio","Cobre"};
	private String[] _strTipo		= 	{"...","Alambre","Cable","Concentrico"};
	private String[] _strClase		= 	{"...","Aerea","Subterranea"};
	private String[] _strFases		= 	{"...","AN","BN","CN","ABN","ACN","BCN","ABC","ABCN"};
	private String 	 _strCamposTabla=	"id_orden,tipo_ingreso,conductor,tipo,calibre,clase,fase,longitud";
	//String[] _campos= {"id_orden","tipo_ingreso","conductor","tipo","calibre","clase","fase","longitud"};
	
	private LinearLayout 	ll;
	private TableLayout 	InformacionTabla;
	
	//Variables para consultas en la base de datos
	private ContentValues 	Registro = new ContentValues();
	private ArrayList<ContentValues> Tabla = new ArrayList<ContentValues>();
	
	//Variables para el calibre de la acometida segun la seleccion de conductor y tipo
	ArrayList<ContentValues> CalibreAcometida 	= new ArrayList<ContentValues>();
	ArrayList<String> StringCalibreAcometida	= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorCalibreAcometida;
	
	ArrayAdapter<String> AdaptadorTipoIngreso;	
	ArrayAdapter<String> AdaptadorConductor;
	ArrayAdapter<String> AdaptadorTipo;
	ArrayAdapter<String> AdaptadorClase;
	ArrayAdapter<String> AdaptadorFases;
		
	NumberPicker 	_cmbUnidades, _cmbDecimas;
	Spinner 		_cmbIngreso, _cmbConductor, _cmbTipo, _cmbCalibre, _cmbClase, _cmbFases;
	Button			_btnAcometidaGuardar, _btnAcometidaEliminar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acometida);
		
		Bundle bundle 	= getIntent().getExtras();
		this.NombreUsuario		= bundle.getString("NombreUsuario");
		this.CedulaUsuario		= bundle.getString("CedulaUsuario");
		this.NivelUsuario		= bundle.getString("NivelUsuario");
		this.OrdenTrabajo		= bundle.getString("OrdenTrabajo");
		this.CuentaCliente 		= bundle.getString("CuentaCliente");		
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");
		
		AcometidaTabla 	= new Tablas(this, _strCamposTabla, "120,150,120,120,120,120,120,120", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");
		AcometidaSQL 	= new SQLite(this, FolderAplicacion);
		AcometidaUtil 	= new Util();
		
		ll = (LinearLayout) findViewById(R.id.tabla_cuerpo);
		
		_cmbIngreso		= (Spinner) findViewById(R.id.AcometidaCmbTipoIngreso);
		_cmbConductor	= (Spinner) findViewById(R.id.AcometidaCmbConductor);
		_cmbTipo		= (Spinner) findViewById(R.id.AcometidaCmbTipo);
		_cmbCalibre		= (Spinner)	findViewById(R.id.AcometidaCmbCalibre);
		_cmbClase		= (Spinner) findViewById(R.id.AcometidaCmbClase);
		_cmbFases		= (Spinner) findViewById(R.id.AcometidaCmbFases);		
		_cmbUnidades	= (NumberPicker) findViewById(R.id.AcometidaCmbUnidad);
		_cmbDecimas		= (NumberPicker) findViewById(R.id.AcometidaCmbDecima);
		
		_btnAcometidaGuardar	= (Button) findViewById(R.id.AcometidaBtnGuardar); 
		_btnAcometidaEliminar	= (Button) findViewById(R.id.AcometidaBtnEliminar);
		
		AdaptadorTipoIngreso= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strTipoIngreso);
		_cmbIngreso.setAdapter(AdaptadorTipoIngreso);
		
		AdaptadorConductor	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strConductor);
		_cmbConductor.setAdapter(AdaptadorConductor);		
		
		AdaptadorTipo	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strTipo);
		_cmbTipo.setAdapter(AdaptadorTipo);	
		
		AdaptadorClase 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strClase);
		_cmbClase.setAdapter(AdaptadorClase);
		
		AdaptadorFases 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strFases);
		_cmbFases.setAdapter(AdaptadorFases);
		
		//Adaptador para el combo del calibre del material segun el tipo  y el conductor
		CalibreAcometida= AcometidaSQL.SelectData("amd_param_acometidas", "calibre", "conductor='' AND tipo_acometida=''");
    	AcometidaUtil.ArrayContentValuesToString(StringCalibreAcometida, CalibreAcometida, "calibre");		
    	AdaptadorCalibreAcometida= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringCalibreAcometida);
    	_cmbCalibre.setAdapter(AdaptadorCalibreAcometida);
    	
    	_cmbUnidades.setMinValue(0);
        _cmbUnidades.setMaxValue(199);
        _cmbUnidades.setWrapSelectorWheel(false);
        _cmbUnidades.setValue(0);
        
        _cmbDecimas.setMinValue(0);
        _cmbDecimas.setMaxValue(9);
        _cmbDecimas.setWrapSelectorWheel(false);
        _cmbDecimas.setValue(0);
		
    	
		_cmbConductor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        public void onItemSelected(AdapterView<?> parent,android.view.View v, int position, long id) {
	        	MostrarOpcionCalibreAcometida();
	        }
	     
	        public void onNothingSelected(AdapterView<?> parent) {
	        }
		});
		
		_cmbTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        public void onItemSelected(AdapterView<?> parent,android.view.View v, int position, long id) {
	        	MostrarOpcionCalibreAcometida();
	        }
	     
	        public void onNothingSelected(AdapterView<?> parent) {
	        }
		});
		
		Tabla = AcometidaSQL.SelectData("vista_acometida", "id_orden, tipo_ingreso, conductor,tipo, calibre, clase,fase, longitud", "id_orden='"+OrdenTrabajo+"'");
		InformacionTabla = AcometidaTabla.CuerpoTabla(Tabla);
        ll.removeAllViews();
        ll.addView(InformacionTabla);
        
        _btnAcometidaGuardar.setOnClickListener(this);
        _btnAcometidaEliminar.setOnClickListener(this);
    }
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_acometida, menu);
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
				
			case R.id.ContadorTransformador:
				finish();
				k = new Intent(this, Form_CambioContador.class);
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
	
	public void MostrarOpcionCalibreAcometida(){
		CalibreAcometida= AcometidaSQL.SelectData("vista_param_acometida", "calibre", "conductor='"+_cmbConductor.getSelectedItem()+"' AND tipo='"+_cmbTipo.getSelectedItem()+"'");
    	AcometidaUtil.ArrayContentValuesToString(StringCalibreAcometida, CalibreAcometida, "calibre");		
    	AdaptadorCalibreAcometida.notifyDataSetChanged();
    }



	@Override
	public void onClick(View v) {
		switch(v.getId()){
	    	case R.id.AcometidaBtnGuardar:
	    		Id_Acometida = AcometidaSQL.StrSelectShieldWhere("vista_param_acometida", "id_acometida", "tipo LIKE '"+_cmbTipo.getSelectedItem()+"' AND conductor LIKE '"+_cmbConductor.getSelectedItem().toString()+"' AND calibre LIKE '"+_cmbCalibre.getSelectedItem()+"'");
	    		Registro.clear();
	    		Registro.put("id_orden", OrdenTrabajo);
	    		Registro.put("tipo_ingreso", _cmbIngreso.getSelectedItem().toString());
	    		Registro.put("id_acometida", Id_Acometida);
	    		Registro.put("fase",_cmbFases.getSelectedItem().toString());
	    		Registro.put("clase",_cmbClase.getSelectedItem().toString());
	    		Registro.put("longitud", _cmbUnidades.getValue()+"."+_cmbDecimas.getValue());
	    		Registro.put("usuario_ins", CedulaUsuario);
	    		
	    		Toast.makeText(this,AcometidaSQL.InsertOrUpdateRegistro("amd_acometida", Registro, "id_orden='"+OrdenTrabajo+"' AND tipo_ingreso ='"+_cmbIngreso.getSelectedItem()+"' AND id_acometida='"+Id_Acometida+"'"), Toast.LENGTH_SHORT).show();
    			Tabla = AcometidaSQL.SelectData("vista_acometida", "id_orden, tipo_ingreso, conductor,tipo, calibre, clase,fase, longitud", "id_orden='"+OrdenTrabajo+"'");
    			
    			InformacionTabla = AcometidaTabla.CuerpoTabla(Tabla);
    			ll.removeAllViews();
    	        ll.addView(InformacionTabla);
    	        
    	        _cmbIngreso.setSelection(0);
    	        _cmbConductor.setSelection(0);
    	        _cmbTipo.setSelection(0);
    	        _cmbCalibre.setSelection(0);
    	        _cmbClase.setSelection(0);
    	        _cmbFases.setSelection(0);
    	        
    	        break;
	    		
	    	case R.id.AcometidaBtnEliminar:
	    		Id_Acometida = AcometidaSQL.StrSelectShieldWhere("vista_param_acometida", "id_acometida", "tipo LIKE '"+_cmbTipo.getSelectedItem()+"' AND conductor LIKE '"+_cmbConductor.getSelectedItem().toString()+"' AND calibre LIKE '"+_cmbCalibre.getSelectedItem()+"'");
	    		if(AcometidaSQL.DeleteRegistro("amd_acometida", "id_orden='"+OrdenTrabajo+"' AND tipo_ingreso ='"+_cmbIngreso.getSelectedItem()+"' AND id_acometida='"+Id_Acometida+"'")){
	    			//Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
	    			Tabla = AcometidaSQL.SelectData("vista_acometida", "id_orden, tipo_ingreso, conductor,tipo, calibre, clase,fase, longitud", "id_orden='"+OrdenTrabajo+"'");
	    			InformacionTabla = AcometidaTabla.CuerpoTabla(Tabla);
	    			ll.removeAllViews();
	    	        ll.addView(InformacionTabla);
	    		}else{
	    			Toast.makeText(this, "Error al eliminar el registro.",Toast.LENGTH_SHORT).show();
	    		}
	    		break;
	    		
	    		
	    	default:
	    		break;
		}
	}
}
