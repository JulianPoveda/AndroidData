package form_amdata;

import java.util.ArrayList;

import class_amdata.Class_Autogestion;
import class_amdata.Class_Contador;
import sypelc.androidamdata.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Form_Autogestion extends Activity implements OnClickListener, OnItemSelectedListener{
	
	private Class_Autogestion 	FcnAutogestion;
	private Class_Contador		FcnContador;
	
	private ArrayList<String> 	_strDependencia;		
	private ArrayList<String> 	_strClaseSolicitud;	
	private ArrayList<String> 	_strTipoSolicitud;	
	private ArrayList<String> 	_strTipoAccion;
	private ArrayList<String> 	_strMarcaContador;	
	private String 	 			FolderAplicacion= "";	
	
	
	private String[] _strTipoOrden		={"","REVISION","SOLICITUD"};
	private String[] _strConsecutivo	={"","1","2","3","4","5","6","7","8","9"};
	private String[] _strUbicacion		={"","URBANO","RURAL"};
	private String[] _strMunicipio		={"","Acacias","Barranca de Upia","Cabuyaro","Castilla La Nueva","Cumaral","El Calvario","El Castillo","El Dorado","Fuente de Oro","Granada","Guamal","La Macarena"
										,"La Uribe","Lejanias","Mapiripan","Mesetas","Puerto Concordia","Puerto Gaitan","Puerto Lleras","Puerto Lopez","Puerto Rico","Restrepo","San Carlos de Guaroa"
										,"San Juan de Arama", "San Juanito","San Luis de Cubarral","San Martin","Villavicencio","Vista Hermosa"};
	private String[] _strEstrato		={"","1","2","3","4","5","6"};
	private String[] _strCiclo			={"","1","2","3","4","5","6","7","8","9","10","15","16","18","19","20","21","22","23","25","26","29","31","32","33","37","38","42","50","52"};
	private String[] _strClaseServicio 	={"","Agricolas","Alumbrado Publico","Area Comun","Bloque","Bombeo de Agua","Comercial","Comunal","Empleado Residencial","Externas","Gran Industria","Individual",
											"Industrial Regulada","Industrial","Macromedicion","No Regulada","No Residencial","Oficial","Otras Cargas","Provisional","PRUEBA_SECTOR_CONSUMO","Residencial",
											"SECTOR DE CONSUMO INDUSTRIAL","SECTOR DE CONSUMO OFICIAL","SECTOR DE CONSUMO PROVISIONAL","SECTOR DE CONSUMO RESIDENCIAL","SECTOR DE CONSUMO COMERCIAL", 
											"SECTOR DE CONSUMO NO RESIDENCIAL", "Servicios y Oficial","Uso del STR"};
	private String[] _strTipoConexion	={"","Monofasico","Bifasico","Trifasico","Trifilar Neutro Directo"};
	
	private ArrayAdapter<String>	_adapTipoOrden, _adapConsecutivo, _adapDependencia, _adapUbicacion, _adapMunicipio, _adapEstrato, _adapCiclo, _adapClaseServicio, _adapTipoConexion, _adapMarcaContador, _adapClaseSolicitud, _adapTipoSolicitud, _adapTipoAccion;
	
	
	TextView	_lblDependencia, _lblClaseSolicitud, _lblTipoSolicitud, _lblTipoAccion, _lblDependenciaAsignada;
	EditText	_txtPropietario, _txtCuenta, _txtDireccion, _txtNodo, _txtCargaContratada, _txtSerieContador, _txtLecturaContador, _txtDependenciaAsignada;
	Spinner 	_cmbTipoOrden, _cmbConsecutivo, _cmbDependencia, _cmbUbicacion, _cmbMunicipio, _cmbEstrato, _cmbCiclo, _cmbClaseServicio, _cmbTipoConexion, _cmbMarcaContador, _cmbClaseSolicitud, _cmbTipoSolicitud, _cmbTipoAccion;
	Button		_btnCrear, _btnCancelar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_gestion);
		
		Bundle bundle 	= getIntent().getExtras();
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");
		
		FcnAutogestion 	= new Class_Autogestion(this, this.FolderAplicacion);
		FcnContador 	= new Class_Contador(this, this.FolderAplicacion,"","","");
		
		this._lblDependencia		= (TextView) findViewById(R.id.AutogestionLblDependencia);
		this._lblClaseSolicitud		= (TextView) findViewById(R.id.AutogestionLblClaseSolicitud);
		this._lblTipoSolicitud		= (TextView) findViewById(R.id.AutogestionLblTipoSolicitud);
		this._lblTipoAccion			= (TextView) findViewById(R.id.AutogestionLblTipoAccion);
		this._lblDependenciaAsignada= (TextView) findViewById(R.id.AutogestionLblDependenciaAsignada);		
		
		this._txtPropietario		= (EditText) findViewById(R.id.AutogestionTxtPropietario);
		this._txtCuenta				= (EditText) findViewById(R.id.AutogestionTxtCuenta);
		this._txtDireccion			= (EditText) findViewById(R.id.AutogestionTxtDireccion);
		this._txtNodo				= (EditText) findViewById(R.id.AutogestionTxtNodo);
		this._txtCargaContratada	= (EditText) findViewById(R.id.AutogestionTxtCargaContratada);
		this._txtSerieContador		= (EditText) findViewById(R.id.AutogestionTxtSerieContador);
		this._txtLecturaContador	= (EditText) findViewById(R.id.AutogestionTxtLecturaContador);
		this._txtDependenciaAsignada= (EditText) findViewById(R.id.AutogestionTxtDependenciaAsignada);
		
		this._cmbTipoOrden		= (Spinner) findViewById(R.id.AutogestionCmbTipoOrden);
		this._cmbConsecutivo	= (Spinner) findViewById(R.id.AutogestionCmbConsecutivo);
		this._cmbDependencia	= (Spinner) findViewById(R.id.AutogestionCmbDependencia);
		this._cmbUbicacion		= (Spinner) findViewById(R.id.AutogestionCmbUbicacion);
		this._cmbMunicipio		= (Spinner) findViewById(R.id.AutogestionCmbMunicipio);
		this._cmbEstrato		= (Spinner) findViewById(R.id.AutogestionCmbEstrato);
		this._cmbCiclo			= (Spinner) findViewById(R.id.AutogestionCmbCiclo);
		this._cmbClaseServicio	= (Spinner) findViewById(R.id.AutogestionCmbClaseServicio);
		this._cmbTipoConexion	= (Spinner) findViewById(R.id.AutogestionCmbTipoConexion);
		this._cmbMarcaContador	= (Spinner) findViewById(R.id.AutogestionCmbMarcaContador);
		this._cmbClaseSolicitud	= (Spinner) findViewById(R.id.AutogestionCmbClaseSolicitud);
		this._cmbTipoSolicitud	= (Spinner) findViewById(R.id.AutogestionCmbTipoSolicitud);
		this._cmbTipoAccion		= (Spinner) findViewById(R.id.AutogestionCmbTipoAccion);
		
		this._btnCrear			= (Button) findViewById(R.id.AutogestionBtnCrear);
		this._btnCancelar		= (Button) findViewById(R.id.AutogestionBtnCancelar);	
				
		this._txtDependenciaAsignada.setText("62020");
		
		/**Inicio de adaptadores dinamicos para el caso de las solicitudes**/
		this._strDependencia	= FcnAutogestion.getDependencias();
		this._adapDependencia	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strDependencia);
		this._cmbDependencia.setAdapter(this._adapDependencia);
		
		this._strMarcaContador	= FcnContador.getOpcionesMedidores(1);
		this._adapMarcaContador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strMarcaContador);
		this._cmbMarcaContador.setAdapter(this._adapMarcaContador);
		
		/**Adaptadores fijos**/
		this._adapTipoOrden= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strTipoOrden);
		this._cmbTipoOrden.setAdapter(this._adapTipoOrden);
		
		this._adapConsecutivo= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strConsecutivo);
		this._cmbConsecutivo.setAdapter(this._adapConsecutivo);		
		
		this._adapUbicacion= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strUbicacion);
		this._cmbUbicacion.setAdapter(this._adapUbicacion);
		
		this._adapMunicipio= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strMunicipio);
		this._cmbMunicipio.setAdapter(this._adapMunicipio);
		
		this._adapEstrato= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strEstrato);
		this._cmbEstrato.setAdapter(this._adapEstrato);
		
		this._adapCiclo= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strCiclo);
		this._cmbCiclo.setAdapter(this._adapCiclo);
		
		this._adapClaseServicio= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strClaseServicio);
		this._cmbClaseServicio.setAdapter(this._adapClaseServicio);
		
		this._adapTipoConexion= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strTipoConexion);
		this._cmbTipoConexion.setAdapter(this._adapTipoConexion);
		
		
		
		/*this._adapTipoSolicitud= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strTipoSolicitud);
		this._cmbTipoSolicitud.setAdapter(this._adapTipoSolicitud);
		
		this._adapTipoAccion= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strTipoAccion);
		this._cmbTipoAccion.setAdapter(this._adapTipoAccion);*/
		
		
		this._cmbMarcaContador.setOnItemSelectedListener(this);
		this._cmbTipoOrden.setOnItemSelectedListener(this);
		this._cmbDependencia.setOnItemSelectedListener(this);
		this._cmbClaseSolicitud.setOnItemSelectedListener(this);
		this._cmbTipoSolicitud.setOnItemSelectedListener(this);
		
		this._btnCrear.setOnClickListener(this);
		this._btnCancelar.setOnClickListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch(parent.getId()){
			case R.id.AutogestionCmbTipoOrden:
				if(this._cmbTipoOrden.getSelectedItem().toString().equals("REVISION")){
					this._lblDependencia.setVisibility(View.INVISIBLE);
					this._lblClaseSolicitud.setVisibility(View.INVISIBLE);
					this._lblTipoSolicitud.setVisibility(View.INVISIBLE);
					this._lblTipoAccion.setVisibility(View.INVISIBLE);
					this._lblDependenciaAsignada.setVisibility(View.INVISIBLE);
					this._cmbDependencia.setVisibility(View.INVISIBLE);
					this._cmbClaseSolicitud.setVisibility(View.INVISIBLE);
					this._cmbTipoSolicitud.setVisibility(View.INVISIBLE);
					this._cmbTipoAccion.setVisibility(View.INVISIBLE);
					this._txtDependenciaAsignada.setVisibility(View.INVISIBLE);
				}else if(this._cmbTipoOrden.getSelectedItem().toString().equals("SOLICITUD")){
					this._lblDependencia.setVisibility(View.VISIBLE);
					this._lblClaseSolicitud.setVisibility(View.VISIBLE);
					this._lblTipoSolicitud.setVisibility(View.VISIBLE);
					this._lblTipoAccion.setVisibility(View.VISIBLE);
					this._cmbDependencia.setVisibility(View.VISIBLE);
					this._lblDependenciaAsignada.setVisibility(View.VISIBLE);
					this._cmbClaseSolicitud.setVisibility(View.VISIBLE);
					this._cmbTipoSolicitud.setVisibility(View.VISIBLE);
					this._cmbTipoAccion.setVisibility(View.VISIBLE);
					this._txtDependenciaAsignada.setVisibility(View.VISIBLE);
				}
				break;
				
				
			case R.id.AutogestionCmbMarcaContador:
				if(this._cmbMarcaContador.getSelectedItem().toString().equals("SD (SERVICIO DIRECTO)") || this._cmbMarcaContador.getSelectedItem().toString().equals("SS (SIN SERVICIO)")){
					this._txtNodo.setText("000000");
					this._txtSerieContador.setText("-1");
					this._txtLecturaContador.setText("-1");
					this._txtNodo.setEnabled(false);	
					this._txtSerieContador.setEnabled(false);
					this._txtLecturaContador.setEnabled(false);
				}else{
					this._txtNodo.setText("");
					this._txtNodo.setEnabled(true);
					this._txtSerieContador.setEnabled(true);
					this._txtLecturaContador.setEnabled(true);
				}
				break;
				
			
			case R.id.AutogestionCmbDependencia:
				this._strClaseSolicitud = FcnAutogestion.getClaseSolicitud(this._cmbDependencia.getSelectedItem().toString());
				this._adapClaseSolicitud = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strClaseSolicitud);	
				_cmbClaseSolicitud.setAdapter(this._adapClaseSolicitud);
				this._adapClaseSolicitud.notifyDataSetChanged();
				break;
				
			
			case R.id.AutogestionCmbClaseSolicitud:
				this._strTipoSolicitud = FcnAutogestion.getTipoSolicitud(this._cmbDependencia.getSelectedItem().toString(), this._cmbClaseSolicitud.getSelectedItem().toString());
				this._adapTipoSolicitud = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strTipoSolicitud);	
				_cmbTipoSolicitud.setAdapter(this._adapTipoSolicitud);
				this._adapTipoSolicitud.notifyDataSetChanged();
				break;
			
				
			case R.id.AutogestionCmbTipoSolicitud:
				this._strTipoAccion = FcnAutogestion.getTipoAccion(this._cmbDependencia.getSelectedItem().toString(), this._cmbClaseSolicitud.getSelectedItem().toString(), this._cmbTipoSolicitud.getSelectedItem().toString());
				this._adapTipoAccion = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strTipoAccion);	
				_cmbTipoAccion.setAdapter(this._adapTipoAccion);
				this._adapTipoAccion.notifyDataSetChanged();
				break;	
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.AutogestionBtnCrear:
				if(this._cmbTipoOrden.getSelectedItem().toString().equals("SOLICITUD")){
					if(this.FcnAutogestion.validarDatosAutogestion(	this._cmbTipoOrden.getSelectedItem().toString()+"", 	this._cmbConsecutivo.getSelectedItem().toString()+"", 
																	this._cmbDependencia.getSelectedItem().toString()+"", 	this._cmbClaseSolicitud.getSelectedItem().toString()+"",
																	this._cmbTipoSolicitud.getSelectedItem().toString()+"",	this._cmbTipoAccion.getSelectedItem().toString()+"", 
																	this._txtPropietario.getText().toString()+"", 			this._txtCuenta.getText().toString()+"", 
																	this._cmbUbicacion.getSelectedItem().toString()+"", 	this._txtDireccion.getText().toString()+"", 
																	this._cmbMunicipio.getSelectedItem().toString()+"", 	this._txtNodo.getText().toString()+"", 
																	this._txtCargaContratada.getText().toString()+"", 		this._cmbEstrato.getSelectedItem().toString()+"", 
																	this._cmbCiclo.getSelectedItem().toString()+"", 		this._cmbClaseServicio.getSelectedItem().toString()+"",
																	this._cmbTipoConexion.getSelectedItem().toString()+"", 	this._cmbMarcaContador.getSelectedItem().toString()+"",
																	this._txtSerieContador.getText().toString()+"", 		this._txtLecturaContador.getText().toString()+"")){								
						if(this.FcnAutogestion.crearAutogestion()){
							Toast.makeText(this,"Autogestion creada correctamente.",Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(this,"Error al crear la autogestion.",Toast.LENGTH_SHORT).show();
						}
					}
				}else if(this._cmbTipoOrden.getSelectedItem().toString().equals("REVISION")){
					if(this.FcnAutogestion.validarDatosAutogestion(	this._cmbTipoOrden.getSelectedItem().toString()+"", 	this._cmbConsecutivo.getSelectedItem().toString()+"", 
																	"", 													"",
																	"",														"", 
																	this._txtPropietario.getText().toString()+"", 			this._txtCuenta.getText().toString()+"", 
																	this._cmbUbicacion.getSelectedItem().toString()+"", 	this._txtDireccion.getText().toString()+"", 
																	this._cmbMunicipio.getSelectedItem().toString()+"", 	this._txtNodo.getText().toString()+"", 
																	this._txtCargaContratada.getText().toString()+"", 		this._cmbEstrato.getSelectedItem().toString()+"", 
																	this._cmbCiclo.getSelectedItem().toString()+"", 		this._cmbClaseServicio.getSelectedItem().toString()+"",
																	this._cmbTipoConexion.getSelectedItem().toString()+"", 	this._cmbMarcaContador.getSelectedItem().toString()+"",
																	this._txtSerieContador.getText().toString()+"", 		this._txtLecturaContador.getText().toString()+"")){
						if(this.FcnAutogestion.crearAutogestion()){
							Toast.makeText(this,"Autogestion creada correctamente.",Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(this,"Error al crear la autogestion.",Toast.LENGTH_SHORT).show();
						}
					}
				}				
				break;
		}		
	}
}
