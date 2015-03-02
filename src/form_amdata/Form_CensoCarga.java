package form_amdata;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import class_amdata.Class_CensoCarga;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

public class Form_CensoCarga extends Activity implements OnClickListener, OnCheckedChangeListener, OnItemSelectedListener{
	Tablas			CensoTabla;
	SQLite 			CensoSQL;
	Util   			CensoUtil;
	
	Class_CensoCarga FcnCenso;
	
	private String	NombreUsuario	= "";
	private String	CedulaUsuario	= "";
	private String 	NivelUsuario	= "";
	private String 	OrdenTrabajo 	= "";
	private String 	CuentaCliente 	= "";
	private String 	FolderAplicacion= "";
	
	//private String 	IdElemento		= "";
	private float	errorImpulsos;
	private float 	errorNumeradorA;
	private float 	errorNumeradorB;
	private float	errorNumeradorC;
	private float   errorVoltajeLinea;
	private float 	errorCorrienteLinea;
	private float	errorTiempoLinea;
	private float   errorVueltasLinea;
	
	
	private String[] _strEstadoCarga 	=	{"...","Registrada","Directa"}; 	
	private String 	 _strCamposTabla	=	"carga,elemento,vatios,cant,total";
	
	private String[] _strConexion		= 	{"Monofasico","Bifasico","Trifasico"};
	private String[] _strPrueba			= 	{"Baja","Alta"};
	
	private ArrayList<String> 		_strCantidad	= new ArrayList<String>();
	private ArrayAdapter<String> 	AdaptadorCantidad;
	
	CheckBox		_chkFaseA, _chkFaseB, _chkFaseC;
	Spinner 		_cmbElementos, _cmbEstadoElemento, _cmbConexion, _cmbPrueba, _cmbCantidad;
	TextView		_lblVb, _lblVc, _lblIb, _lblIc, _lblTb, _lblTc, _lblNvb, _lblNvc, _lblFp1, _lblFp2, _lblFp3, _lbltcr, _lbltcd, _lbltcc;
	EditText 		_txtVa, _txtVb, _txtVc, _txtIa, _txtIb, _txtIc, _txtTa, _txtTb, _txtTc, _txtNva, _txtNvb, _txtNvc, _txtRevUnidades, _txtVatios;
	Button 			_btnRegistrar, _btnEliminar, _btnErrorCalcular, _btnErrorGuardar;
	
	private 		LinearLayout 	ll;
	private 		TableLayout 	InformacionTabla;
	
	//Variables para consultas en la base de datos
	private ContentValues 	Registro = new ContentValues();
	private ArrayList<ContentValues> Tabla = new ArrayList<ContentValues>();
	
	DecimalFormat decimales = new DecimalFormat("0.000"); 
	
	ArrayAdapter<String> AdaptadorEstadoCarga;
	ArrayAdapter<String> AdaptadorTipoConexion;
	ArrayAdapter<String> AdaptadorTipoPrueba;
	
	//Variables para la consulta de los elementos disponibles del censo de carga 
	ArrayList<ContentValues> ElementosCenso = new ArrayList<ContentValues>();
	ArrayList<String> StringElementosCenso	= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorElementosCenso;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_censo_carga);
		
		Bundle bundle 	= getIntent().getExtras();
		this.NombreUsuario		= bundle.getString("NombreUsuario");
		this.CedulaUsuario		= bundle.getString("CedulaUsuario");
		this.NivelUsuario		= bundle.getString("NivelUsuario");
		this.OrdenTrabajo		= bundle.getString("OrdenTrabajo");
		this.CuentaCliente 		= bundle.getString("CuentaCliente");		
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");
		
		CensoTabla 	= new Tablas(this, _strCamposTabla, "110,275,80,70,90", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");
		CensoSQL 	= new SQLite(this, FolderAplicacion);
		CensoUtil 	= new Util();
	
		
		FcnCenso 	= new Class_CensoCarga(this, FolderAplicacion);
		
		_cmbElementos 		= (Spinner) findViewById(R.id.CensoCmbElemento);
		_cmbEstadoElemento	= (Spinner) findViewById(R.id.CensoCmbCarga);
		_cmbConexion		= (Spinner) findViewById(R.id.ErrorCmbTipoConexion);
		_cmbPrueba			= (Spinner) findViewById(R.id.ErrorCmbTipoPrueba);
		_cmbCantidad		= (Spinner) findViewById(R.id.CensoCmbCantidad);
		
		_chkFaseA			= (CheckBox) findViewById(R.id.ErrorChkFaseA);
		_chkFaseB			= (CheckBox) findViewById(R.id.ErrorChkFaseB);
		_chkFaseC			= (CheckBox) findViewById(R.id.ErrorChkFaseC);
		
		_lblVb				= (TextView) findViewById(R.id.CensoLblVb);
		_lblVc				= (TextView) findViewById(R.id.CensoLblVc);
		_lblIb				= (TextView) findViewById(R.id.CensoLblIb);
		_lblIc				= (TextView) findViewById(R.id.CensoLblIc);
		_lblTb				= (TextView) findViewById(R.id.CensoLblTb);
		_lblTc				= (TextView) findViewById(R.id.CensoLblTc);
		_lblNvb				= (TextView) findViewById(R.id.CensoLblNvb);
		_lblNvc				= (TextView) findViewById(R.id.CensoLblNvc);
		
		_lblFp1				= (TextView) findViewById(R.id.ErrorLblFp1Value);
		_lblFp2				= (TextView) findViewById(R.id.ErrorLblFp2Value);
		_lblFp3				= (TextView) findViewById(R.id.ErrorLblFp3Value); 
		_lbltcr 			= (TextView) findViewById(R.id.CensoLblTCR2);
		_lbltcd				= (TextView) findViewById(R.id.CensoLblTCD);
		_lbltcc				= (TextView) findViewById(R.id.CensoLblTCC);
		
		_txtVa				= (EditText) findViewById(R.id.CensoTxtVa);
		_txtVb				= (EditText) findViewById(R.id.CensoTxtVb);
		_txtVc				= (EditText) findViewById(R.id.CensoTxtVc);
		_txtIa				= (EditText) findViewById(R.id.CensoTxtIa);
		_txtIb				= (EditText) findViewById(R.id.CensoTxtIb);
		_txtIc				= (EditText) findViewById(R.id.CensoTxtIc);
		_txtTa				= (EditText) findViewById(R.id.CensoTxtTa);
		_txtTb				= (EditText) findViewById(R.id.CensoTxtTb);
		_txtTc				= (EditText) findViewById(R.id.CensoTxtTc);
		_txtNva				= (EditText) findViewById(R.id.CensoTxtNva);
		_txtNvb				= (EditText) findViewById(R.id.CensoTxtNvb);
		_txtNvc				= (EditText) findViewById(R.id.CensoTxtNvc);
		_txtRevUnidades		= (EditText) findViewById(R.id.ErrorTxtRevUnidades);
		_txtVatios			= (EditText) findViewById(R.id.CensoTxtVatios);
		
		
		_btnRegistrar		= (Button) findViewById(R.id.CensoBtnRegistrar);
		_btnEliminar		= (Button) findViewById(R.id.CensoBtnEliminar);
		_btnErrorCalcular 	= (Button) findViewById(R.id.ErrorBtnCalcular);
		_btnErrorGuardar 	= (Button) findViewById(R.id.ErrorBtnGuardar);
		
		ll = (LinearLayout) findViewById(R.id.CensoTablaElementos);
		
		
		_strCantidad 			= CensoUtil.getRangeAdapter(0, 100, 1);
		AdaptadorCantidad	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strCantidad);
		_cmbCantidad.setAdapter(AdaptadorCantidad);
				
		AdaptadorEstadoCarga 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strEstadoCarga);
		_cmbEstadoElemento.setAdapter(AdaptadorEstadoCarga);
		
		AdaptadorTipoConexion	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strConexion);
		_cmbConexion.setAdapter(AdaptadorTipoConexion);
		
		AdaptadorTipoPrueba 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strPrueba);
		_cmbPrueba.setAdapter(AdaptadorTipoPrueba);
				
		_lbltcr.setText("TCR:  "+this.CensoSQL.StrSelectShieldWhere("amd_censo_carga","sum(capacidad*cantidad)"," id_orden='"+this.OrdenTrabajo+"' AND tipo_carga='R'"));
		_lbltcd.setText("TCD:  "+this.CensoSQL.StrSelectShieldWhere("amd_censo_carga","sum(capacidad*cantidad)"," id_orden='"+this.OrdenTrabajo+"' AND tipo_carga='D'"));
		_lbltcc.setText("TCC:  "+this.CensoSQL.StrSelectShieldWhere("amd_censo_carga","sum(capacidad*cantidad)"," id_orden='"+this.OrdenTrabajo+"'"));
	
		
		//Adaptador para el combo del calibre del material segun el tipo  y el conductor
		ElementosCenso	= CensoSQL.SelectData("amd_elementos_censo", "descripcion", "id_elemento IS NOT NULL ORDER BY descripcion");
		CensoUtil.ArrayContentValuesToString(StringElementosCenso, ElementosCenso, "descripcion");		
		AdaptadorElementosCenso= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringElementosCenso);
		_cmbElementos.setAdapter(AdaptadorElementosCenso);
		
		
		Tabla = CensoSQL.SelectData("vista_censo_carga", "carga,elemento,vatios,cant,total", "id_orden='"+OrdenTrabajo+"'");
		InformacionTabla = CensoTabla.CuerpoTabla(Tabla);
        ll.removeAllViews();
        ll.addView(InformacionTabla);
		
		_chkFaseA.setOnCheckedChangeListener(this);
		_chkFaseB.setOnCheckedChangeListener(this);
		_chkFaseC.setOnCheckedChangeListener(this);
		
		_cmbPrueba.setOnItemSelectedListener(this);
		_cmbElementos.setOnItemSelectedListener(this);
		_cmbConexion.setOnItemSelectedListener(this);
		
		_btnRegistrar.setOnClickListener(this);
		_btnEliminar.setOnClickListener(this);
		_btnErrorCalcular.setOnClickListener(this);
		_btnErrorGuardar.setOnClickListener(this);
	
	
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_censo_carga, menu);
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
	
	
	public void OcultarItemsGraficos(){
		if(_cmbConexion.getSelectedItem().toString().equals("Monofasico")){
			_chkFaseB.setVisibility(View.INVISIBLE);
			_chkFaseC.setVisibility(View.INVISIBLE);
			
			_lblVb.setVisibility(View.INVISIBLE);
			_lblVc.setVisibility(View.INVISIBLE);
			_lblIb.setVisibility(View.INVISIBLE);
			_lblIc.setVisibility(View.INVISIBLE);
			_lblTb.setVisibility(View.INVISIBLE);
			_lblTc.setVisibility(View.INVISIBLE);
			_lblNvb.setVisibility(View.INVISIBLE);
			_lblNvc.setVisibility(View.INVISIBLE);
			_lblFp2.setVisibility(View.INVISIBLE);
			_lblFp3.setVisibility(View.INVISIBLE);
			
			_txtVb.setVisibility(View.INVISIBLE);
			_txtVc.setVisibility(View.INVISIBLE);
			_txtIb.setVisibility(View.INVISIBLE);
			_txtIc.setVisibility(View.INVISIBLE);
			_txtTb.setVisibility(View.INVISIBLE);
			_txtTc.setVisibility(View.INVISIBLE);
			_txtNvb.setVisibility(View.INVISIBLE);
			_txtNvc.setVisibility(View.INVISIBLE);			
		}else if(_cmbConexion.getSelectedItem().toString().equals("Bifasico")){
			_chkFaseB.setVisibility(View.VISIBLE);
			_chkFaseC.setVisibility(View.INVISIBLE);
			
			_lblVb.setVisibility(View.VISIBLE);
			_lblVc.setVisibility(View.INVISIBLE);
			_lblIb.setVisibility(View.VISIBLE);
			_lblIc.setVisibility(View.INVISIBLE);
			_lblTb.setVisibility(View.VISIBLE);
			_lblTc.setVisibility(View.INVISIBLE);
			_lblNvb.setVisibility(View.VISIBLE);
			_lblNvc.setVisibility(View.INVISIBLE);
			_lblFp2.setVisibility(View.VISIBLE);
			_lblFp3.setVisibility(View.INVISIBLE);
			
			_txtVb.setVisibility(View.VISIBLE);
			_txtVc.setVisibility(View.INVISIBLE);
			_txtIb.setVisibility(View.VISIBLE);
			_txtIc.setVisibility(View.INVISIBLE);
			_txtTb.setVisibility(View.VISIBLE);
			_txtTc.setVisibility(View.INVISIBLE);
			_txtNvb.setVisibility(View.VISIBLE);
			_txtNvc.setVisibility(View.INVISIBLE);
		}else if(_cmbConexion.getSelectedItem().toString().equals("Trifasico")){
			_chkFaseB.setVisibility(View.VISIBLE);
			_chkFaseC.setVisibility(View.VISIBLE);
			
			_lblVb.setVisibility(View.VISIBLE);
			_lblVc.setVisibility(View.VISIBLE);
			_lblIb.setVisibility(View.VISIBLE);
			_lblIc.setVisibility(View.VISIBLE);
			_lblTb.setVisibility(View.VISIBLE);
			_lblTc.setVisibility(View.VISIBLE);
			_lblNvb.setVisibility(View.VISIBLE);
			_lblNvc.setVisibility(View.VISIBLE);
			_lblFp2.setVisibility(View.VISIBLE);
			_lblFp3.setVisibility(View.VISIBLE);
			
			_txtVb.setVisibility(View.VISIBLE);
			_txtVc.setVisibility(View.VISIBLE);
			_txtIb.setVisibility(View.VISIBLE);
			_txtIc.setVisibility(View.VISIBLE);
			_txtTb.setVisibility(View.VISIBLE);
			_txtTc.setVisibility(View.VISIBLE);
			_txtNvb.setVisibility(View.VISIBLE);
			_txtNvc.setVisibility(View.VISIBLE);
			
		}
	}
	

	@Override
	public void onClick(View v) {
				
		switch(v.getId()){
    		case R.id.CensoBtnRegistrar:
    			if(_cmbEstadoElemento.getSelectedItem().toString().equals("...")){
    				Toast.makeText(this,"No ha seleccionado el estado de la carga del elemento.",Toast.LENGTH_SHORT).show();
    			}else if(_cmbCantidad.getSelectedItem().toString().equals("0")){
    				Toast.makeText(this,"No ha ingresado una cantidad valida.",Toast.LENGTH_SHORT).show();
    			}else if(_txtVatios.getText().toString().isEmpty()||!FcnCenso.verificarRango(_cmbElementos.getSelectedItem().toString(), _txtVatios.getText().toString())){
    				Toast.makeText(this,"No ha ingresado un valor de vatios validos.",Toast.LENGTH_SHORT).show();
    			}else{
    				Registro.clear();
    				Registro.put("id_orden", OrdenTrabajo);
    				Registro.put("id_elemento",CensoSQL.StrSelectShieldWhere("amd_elementos_censo", "id_elemento", "descripcion='"+_cmbElementos.getSelectedItem().toString()+"'"));
    				Registro.put("capacidad", _txtVatios.getText().toString());
    				Registro.put("cantidad", _cmbCantidad.getSelectedItem().toString());
    				Registro.put("tipo_carga", _cmbEstadoElemento.getSelectedItem().toString().substring(0,1));
    				Registro.put("usuario_ins", CedulaUsuario);
    				if(CensoSQL.InsertRegistro("amd_censo_carga", Registro)){
    					Tabla = CensoSQL.SelectData("vista_censo_carga", "carga,elemento,vatios,cant,total", "id_orden='"+OrdenTrabajo+"'");
    					InformacionTabla = CensoTabla.CuerpoTabla(Tabla);
    			        ll.removeAllViews();
    			        ll.addView(InformacionTabla);
    					Toast.makeText(this,"Elemento ingresado correctamente.",Toast.LENGTH_SHORT).show();
    				}else{
    					Toast.makeText(this,"Error al registrar el elemento.",Toast.LENGTH_SHORT).show();
    				}
    			}
    			
    			_lbltcr.setText("TCR:  "+this.CensoSQL.StrSelectShieldWhere("amd_censo_carga","sum(capacidad*cantidad)"," id_orden='"+this.OrdenTrabajo+"' AND tipo_carga='R'"));
    			_lbltcd.setText("TCD:  "+this.CensoSQL.StrSelectShieldWhere("amd_censo_carga","sum(capacidad*cantidad)"," id_orden='"+this.OrdenTrabajo+"' AND tipo_carga='D'"));
    			_lbltcc.setText("TCC:  "+this.CensoSQL.StrSelectShieldWhere("amd_censo_carga","sum(capacidad*cantidad)"," id_orden='"+this.OrdenTrabajo+"'"));
    			
    			
    			break;
    			
    		case R.id.CensoBtnEliminar:
    			if(CensoSQL.DeleteRegistro("amd_censo_carga", "id_orden='"+OrdenTrabajo+"' AND id_elemento='"+CensoSQL.StrSelectShieldWhere("amd_elementos_censo", "id_elemento", "descripcion='"+_cmbElementos.getSelectedItem().toString()+"'")+"' AND tipo_carga='"+_cmbEstadoElemento.getSelectedItem().toString().substring(0,1)+"'")){
    				Tabla = CensoSQL.SelectData("vista_censo_carga", "carga,elemento,vatios,cant,total", "id_orden='"+OrdenTrabajo+"'");
					InformacionTabla = CensoTabla.CuerpoTabla(Tabla);
			        ll.removeAllViews();
			        ll.addView(InformacionTabla);
			        
			        _lbltcr.setText("TCR:  "+this.CensoSQL.StrSelectShieldWhere("amd_censo_carga","sum(capacidad*cantidad)"," id_orden='"+this.OrdenTrabajo+"' AND tipo_carga='R'"));
	    			_lbltcd.setText("TCD:  "+this.CensoSQL.StrSelectShieldWhere("amd_censo_carga","sum(capacidad*cantidad)"," id_orden='"+this.OrdenTrabajo+"' AND tipo_carga='D'"));
	    			_lbltcc.setText("TCC:  "+this.CensoSQL.StrSelectShieldWhere("amd_censo_carga","sum(capacidad*cantidad)"," id_orden='"+this.OrdenTrabajo+"'"));
	    			
			    }else{
    				Toast.makeText(this,"Error al eliminar el elemento.",Toast.LENGTH_SHORT).show();
    			}
    				
    			break;
    			
    		case R.id.ErrorBtnCalcular:
    			if(_txtRevUnidades.getText().toString().isEmpty()){
    				Toast.makeText(this,"No ha ingresado el parametro del medidor de Impulsos o Revoluciones Kw/h.",Toast.LENGTH_SHORT).show();
    			}else{
    				errorImpulsos = Float.parseFloat(_txtRevUnidades.getText()+"");
        			if(errorImpulsos>=0){
        				ValidarCamposLinea();    				
        			}else{
        				Toast.makeText(this,"No ha ingresado el parametro del medidor de Impulsos o Revoluciones Kw/h.",Toast.LENGTH_SHORT).show();
        			}
    			}
    			
    			break;
    		
    		case R.id.ErrorBtnGuardar:
    			CensoSQL.DeleteRegistro("amd_pct_error", "id_orden='"+OrdenTrabajo+"' AND tipo_carga='"+_cmbPrueba.getSelectedItem().toString()+"'");
    			if((_cmbConexion.getSelectedItemPosition()==0)||(_cmbConexion.getSelectedItemPosition()==1)||(_cmbConexion.getSelectedItemPosition()==2)){
    				Registro.clear();
    				Registro.put("id_orden", OrdenTrabajo);
        			Registro.put("tipo_carga", _cmbPrueba.getSelectedItem().toString());
        			Registro.put("voltaje", _txtVa.getText().toString());
        			Registro.put("corriente", _txtIa.getText().toString());
        			Registro.put("tiempo", _txtTa.getText().toString());
        			Registro.put("vueltas", _txtNva.getText().toString());
        			Registro.put("total", decimales.format(Math.abs(Float.parseFloat(_lblFp1.getText().toString().replace(",","."))-1)));
        			Registro.put("rev",errorImpulsos);
        			Registro.put("usuario_ins", CedulaUsuario);
        			Registro.put("fp", _lblFp1.getText().toString());
        			Registro.put("fase", "1");
        			if(_txtVa.getText().toString().equals("")){
    		    		Toast.makeText(this,"No ha ingresado el Voltaje",Toast.LENGTH_SHORT).show();
    		    	}else{
    		    		if(_txtIa.getText().toString().equals("")){
        		    		Toast.makeText(this,"No ha ingresado la Corriente",Toast.LENGTH_SHORT).show();
        		    	}else{
    		    			if(CensoSQL.InsertRegistro("amd_pct_error", Registro)){
    	        				Toast.makeText(this,"Factor de potencia de la fase A guardado correctamente",Toast.LENGTH_SHORT).show();
    	        			}else{
    	        				Toast.makeText(this,"Error al guardar el factor de potencia de la fase A.",Toast.LENGTH_SHORT).show();
    	        			}	
    		    		}
    		    	}
        			
    			}
    			
    			if((_cmbConexion.getSelectedItemPosition()==1)||(_cmbConexion.getSelectedItemPosition()==2)){
    				Registro.clear();
    				Registro.put("id_orden", OrdenTrabajo);
        			Registro.put("tipo_carga", _cmbPrueba.getSelectedItem().toString());
        			Registro.put("voltaje", _txtVb.getText().toString());
        			Registro.put("corriente", _txtIb.getText().toString());
        			Registro.put("tiempo", _txtTb.getText().toString());
        			Registro.put("vueltas", _txtNvb.getText().toString());
        			Registro.put("total", decimales.format(Math.abs(Float.parseFloat(_lblFp2.getText().toString().replace(",","."))-1)));
        			Registro.put("rev",errorImpulsos);
        			Registro.put("usuario_ins", CedulaUsuario);
        			Registro.put("fp", _lblFp2.getText().toString());
        			Registro.put("fase", "2");
        			if(CensoSQL.InsertRegistro("amd_pct_error", Registro)){
        				Toast.makeText(this,"Factor de potencia de la fase B guardado correctamente",Toast.LENGTH_SHORT).show();
        			}else{
        				Toast.makeText(this,"Error al guardar el factor de potencia de la fase B.",Toast.LENGTH_SHORT).show();
        			}
    			}
    			
    			if(_cmbConexion.getSelectedItemPosition()==2){
    				Registro.clear();
    				Registro.put("id_orden", OrdenTrabajo);
        			Registro.put("tipo_carga", _cmbPrueba.getSelectedItem().toString());
        			Registro.put("voltaje", _txtVc.getText().toString());
        			Registro.put("corriente", _txtIc.getText().toString());
        			Registro.put("tiempo", _txtTc.getText().toString());
        			Registro.put("vueltas", _txtNvc.getText().toString());
        			Registro.put("total", decimales.format(Math.abs(Float.parseFloat(_lblFp3.getText().toString().replace(",","."))-1)));
        			Registro.put("rev",errorImpulsos);
        			Registro.put("usuario_ins", CedulaUsuario);
        			Registro.put("fp", _lblFp3.getText().toString());
        			Registro.put("fase", "3");
        			if(CensoSQL.InsertRegistro("amd_pct_error", Registro)){
        				Toast.makeText(this,"Factor de potencia de la fase C guardado correctamente",Toast.LENGTH_SHORT).show();
        			}else{
        				Toast.makeText(this,"Error al guardar el factor de potencia de la fase C.",Toast.LENGTH_SHORT).show();
        			}
    			}
    			break;
    			
    		default:
    			break;
		}
	}
	
	
	public boolean ValidarCamposLinea(){
		boolean _retorno = true;
		if((_cmbConexion.getSelectedItemPosition()==0)||(_cmbConexion.getSelectedItemPosition()==1)||(_cmbConexion.getSelectedItemPosition()==2)){
			errorVoltajeLinea 	= Float.parseFloat(_txtVa.getText().toString());
			errorCorrienteLinea = Float.parseFloat(_txtIa.getText().toString());
			errorTiempoLinea 	= Float.parseFloat(_txtTa.getText().toString());
			errorVueltasLinea 	= Float.parseFloat(_txtNva.getText().toString());
			if(errorVoltajeLinea>150){
				Toast.makeText(this,"El voltaje ingresado en la linea A esta fuera del rango.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(errorCorrienteLinea>15){
				Toast.makeText(this,"La corrente ingresada en la linea A esta fuera del rango.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(_chkFaseA.isChecked()){
				errorNumeradorA = 0;
				_lblFp1.setText(""+decimales.format(errorNumeradorA));
			}else{
				errorNumeradorA = (3600000*errorVueltasLinea)/(errorVoltajeLinea*errorCorrienteLinea*errorTiempoLinea*errorImpulsos);
				_lblFp1.setText(""+decimales.format(errorNumeradorA));
			}
		}
		
		if((_cmbConexion.getSelectedItemPosition()==1)||(_cmbConexion.getSelectedItemPosition()==2)){
			errorVoltajeLinea = Float.parseFloat(_txtVb.getText().toString());
			errorCorrienteLinea = Float.parseFloat(_txtIb.getText().toString());
			errorTiempoLinea 	= Float.parseFloat(_txtTb.getText().toString());
			errorVueltasLinea 	= Float.parseFloat(_txtNvb.getText().toString());
			if(errorVoltajeLinea>150){
				Toast.makeText(this,"El voltaje ingresado en la linea B esta fuera del rango.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(errorCorrienteLinea>15){
				Toast.makeText(this,"La corrente ingresada en la linea B esta fuera del rango.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(_chkFaseB.isChecked()){
				errorNumeradorB = 0;
				_lblFp2.setText(""+decimales.format(errorNumeradorB));
			}else{
				errorNumeradorB = (3600000*errorVueltasLinea)/(errorVoltajeLinea*errorCorrienteLinea*errorTiempoLinea*errorImpulsos);
				_lblFp2.setText(""+decimales.format(errorNumeradorB));
			}
		}
		
		if(_cmbConexion.getSelectedItemPosition()==2){
			errorVoltajeLinea = Float.parseFloat(_txtVc.getText().toString());
			errorCorrienteLinea = Float.parseFloat(_txtIc.getText().toString());
			errorTiempoLinea 	= Float.parseFloat(_txtTc.getText().toString());
			errorVueltasLinea 	= Float.parseFloat(_txtNvc.getText().toString());
			if(errorVoltajeLinea>150){
				Toast.makeText(this,"El voltaje ingresado en la linea C esta fuera del rango.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(errorCorrienteLinea>15){
				Toast.makeText(this,"La corrente ingresada en la linea C esta fuera del rango.",Toast.LENGTH_SHORT).show();
				_retorno = false;
			}else if(_chkFaseC.isChecked()){
				errorNumeradorC = 0;
				_lblFp3.setText(""+decimales.format(errorNumeradorC));
			}else{
				errorNumeradorC = (3600000*errorVueltasLinea)/(errorVoltajeLinea*errorCorrienteLinea*errorTiempoLinea*errorImpulsos);
				_lblFp3.setText(""+decimales.format(errorNumeradorC));
			}
		}
		return _retorno;
	}
	
	/**Funcion encargada de capturar los cambios al hacer click en cualquier checkbox del activity**/
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch(buttonView.getId()){
			case R.id.ErrorChkFaseA:
				 if ( isChecked ){
					 _txtTa.setText("0");
					 _txtNva.setText("0");
					 _txtTa.setEnabled(false);
					 _txtNva.setEnabled(false);
				 }else{
					 _txtTa.setEnabled(true);
					 _txtNva.setEnabled(true);
				 }
				break;
				
				
			case R.id.ErrorChkFaseB:
				if ( isChecked ){
		            _txtTb.setText("0");
		            _txtNvb.setText("0");
		            _txtTb.setEnabled(false);
		            _txtNvb.setEnabled(false);
		        }else{
		        	_txtTb.setEnabled(true);
		            _txtNvb.setEnabled(true);
		        }
				break;
			
				
			case R.id.ErrorChkFaseC:
				if ( isChecked ){
		            _txtTc.setText("0");
		            _txtNvc.setText("0");
		            _txtTc.setEnabled(false);
		            _txtNvc.setEnabled(false);
		        }else{
		        	_txtTc.setEnabled(true);
		            _txtNvc.setEnabled(true);
		        }
				break;
		}		
	}
	
	
	/**Control de eventos de cambios en los combos**/	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch(parent.getId()){
			case R.id.ErrorCmbTipoPrueba:
				if(_cmbPrueba.getSelectedItem().toString().equals("Baja")){
	        		_cmbConexion.setSelection(AdaptadorTipoPrueba.getPosition("Baja"));
	        		_cmbConexion.setEnabled(false);
	        	}else{
	        		_cmbConexion.setEnabled(true);
	        	}
				break;
				
			case R.id.CensoCmbElemento:
				_txtVatios.setText(FcnCenso.getVatioMinimo(_cmbElementos.getSelectedItem().toString()));
				/*_strVatios = FcnCenso.getRangoVatios(_cmbElementos.getSelectedItem().toString(),10);
				AdaptadorVatios 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strVatios);	
				_cmbVatios.setAdapter(AdaptadorVatios);
				AdaptadorVatios.notifyDataSetChanged();*/
				break;
						
			case R.id.ErrorCmbTipoConexion:
				OcultarItemsGraficos();
				break;				
		}
	}

	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
}
