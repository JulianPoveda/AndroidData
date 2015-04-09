package form_amdata;


import class_amdata.Class_ServicioNuevo;
import dialogos.DialogConfirm;
import sypelc.androidamdata.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class Form_ServicioNuevo extends Activity implements OnClickListener{
	private static int CONFIRM_NUEVO_SERVICIO	= 1;
	
	Intent DialogConfirmacion; 
	
	private Class_ServicioNuevo FcnServicioNuevo;
	
	private String 		FolderAplicacion= "";
	
	private ArrayAdapter<String> 	_adapTipoUsuario, _adapUbicacion, _adapMunicipio, _adapEstrato,  _adapClaseServicio, _adapEstadoServicio, _adapTipoConexion;	
	private ArrayAdapter<String>	_adapDocEscritura, _adapDocEstratificacion, _adapDocCedula, _adapDocRecibo, _adapDocInstalacion, _adapDocFactura, _adapDocAutorizacion;
	
	private String[] _strTipoUsuario={"","PROPIETARIO","NO_PROPIETARIO"};
	private String[] _strUbicacion	={"","URBANO","RURAL"};
	private String[] _strMunicipio	={"","Acacias","Barranca de Upia","Cabuyaro","Castilla La Nueva","Cumaral","El Calvario","El Castillo","El Dorado","Fuente de Oro","Granada","Guamal","La Macarena"
										,"La Uribe","Lejanias","Mapiripan","Mesetas","Puerto Concordia","Puerto Gaitan","Puerto Lleras","Puerto Lopez","Puerto Rico","Restrepo","San Carlos de Guaroa"
										,"San Juan de Arama", "San Juanito","San Luis de Cubarral","San Martin","Villavicencio","Vista Hermosa"};
	private String[] _strEstrato	={"","1","2","3","4","5","6"};
	private String[] _strClaseServicio ={"","Agricolas","Alumbrado Publico","Area Comun","Bloque","Bombeo de Agua","Comercial","Comunal","Empleado Residencial","Externas","Gran Industria","Individual",
											"Industrial Regulada","Industrial","Macromedicion","No Regulada","No Residencial","Oficial","Otras Cargas","Provisional","PRUEBA_SECTOR_CONSUMO","Residencial",
											"SECTOR DE CONSUMO INDUSTRIAL","SECTOR DE CONSUMO OFICIAL","SECTOR DE CONSUMO PROVISIONAL","SECTOR DE CONSUMO RESIDENCIAL","SECTOR DE CONSUMO COMERCIAL", 
											"SECTOR DE CONSUMO NO RESIDENCIAL", "Servicios y Oficial","Uso del STR"};
	private String[] _strEstadoServicio	={"","SERVICIO_DIRECTO","SIN_SERVICIO"};
	private String[] _strTipoConexion	={"","Monofasico","Bifasico","Trifasico","Trifilar Neutro Directo"};
	private String[] _strDocumentacion	={"NO","SI"};		
	
	
	EditText	_txtPropietario, _txtDireccion, _txtSerieContador, _txtLecturaContador, _txtCuenta1, _txtCuenta2, _txtNodoTransformador, _txtNodoSecundario;
	Spinner  	_cmbTipoUsuario, _cmbUbicacion, _cmbMunicipio, _cmbEstrato, _cmbClaseServicio, _cmbEstadoServicio, _cmbTipoConexion;
	Spinner		_cmbDocEscritura, _cmbDocEstratificacion, _cmbDocCedula, _cmbDocRecibo, _cmbDocInstalacion, _cmbDocFactura, _cmbDocAutorizacion; 
	Button		_btnCrear, _btnCancelar;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servicio_nuevo);	
		
		Bundle bundle 	= getIntent().getExtras();
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");
		
		DialogConfirmacion 	= new Intent(this,DialogConfirm.class);
		FcnServicioNuevo = new Class_ServicioNuevo(this, this.FolderAplicacion);
		
		this._txtPropietario	= (EditText) findViewById(R.id.ServicioNuevoTxtPropietario);
		this._txtDireccion		= (EditText) findViewById(R.id.ServicioNuevoTxtDireccion);
		this._txtSerieContador	= (EditText) findViewById(R.id.ServicioNuevoTxtSerieContador);
		this._txtLecturaContador= (EditText) findViewById(R.id.ServicioNuevoTxtLecturaContador);
		this._txtCuenta1		= (EditText) findViewById(R.id.ServicioNuevoTxtCuenta1);
		this._txtCuenta2		= (EditText) findViewById(R.id.ServicioNuevoTxtCuenta2);
		this._txtNodoTransformador	= (EditText) findViewById(R.id.ServicioNuevoTxtNodoTransformador);
		this._txtNodoSecundario		= (EditText) findViewById(R.id.ServicioNuevoTxtNodoSecundario);
		
		this._cmbTipoUsuario= (Spinner) findViewById(R.id.ServicioNuevoCmbTipoUsuario);
		this._cmbUbicacion	= (Spinner) findViewById(R.id.ServicioNuevoCmbUbicacion);
		this._cmbMunicipio	= (Spinner)	findViewById(R.id.ServicioNuevoCmbMunicipio);
		this._cmbEstrato		= (Spinner)	findViewById(R.id.ServicioNuevoCmbEstrato);
		this._cmbClaseServicio	= (Spinner) findViewById(R.id.ServicioNuevoCmbClaseServicio);
		this._cmbEstadoServicio	= (Spinner) findViewById(R.id.ServicioNuevoCmbEstadoServicio);
		this._cmbTipoConexion	= (Spinner) findViewById(R.id.ServicioNuevoCmbTipoConexion);
		this._cmbDocEscritura	= (Spinner) findViewById(R.id.ServicioNuevoCmbDocEscritura);
		this._cmbDocEstratificacion	= (Spinner) findViewById(R.id.ServicioNuevoCmbDocEstratificacion);
		this._cmbDocCedula			= (Spinner) findViewById(R.id.ServicioNuevoCmbDocCedulaPropietario);
		this._cmbDocRecibo			= (Spinner)	findViewById(R.id.ServicioNuevoCmbDocReciboVecino);
		this._cmbDocInstalacion		= (Spinner) findViewById(R.id.ServicioNuevoCmbDocRealizaInstalacion);
		this._cmbDocFactura			= (Spinner) findViewById(R.id.ServicioNuevoCmbDocCompraContador);
		this._cmbDocAutorizacion	= (Spinner) findViewById(R.id.ServicioNuevoCmbDocAutorizacionUsuario); 
		
		this._btnCrear				= (Button) findViewById(R.id.ServicioNuevoBtnCrear);
		this._btnCancelar			= (Button) findViewById(R.id.ServicioNuevoBtnCancelar);
		
		
		this._adapTipoUsuario= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strTipoUsuario);
		this._cmbTipoUsuario.setAdapter(this._adapTipoUsuario);
		
		this._adapUbicacion= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strUbicacion);
		this._cmbUbicacion.setAdapter(this._adapUbicacion);
		
		this._adapMunicipio	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strMunicipio);
		this._cmbMunicipio.setAdapter(this._adapMunicipio);
		
		this._adapEstrato	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strEstrato);
		this._cmbEstrato.setAdapter(this._adapEstrato);
		
		this._adapClaseServicio	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strClaseServicio);
		this._cmbClaseServicio.setAdapter(this._adapClaseServicio);
		
		this._adapEstadoServicio	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strEstadoServicio);
		this._cmbEstadoServicio.setAdapter(this._adapEstadoServicio);
		
		this._adapTipoConexion	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strTipoConexion);
		this._cmbTipoConexion.setAdapter(this._adapTipoConexion);
		
		this._adapDocEscritura	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strDocumentacion);
		this._cmbDocEscritura.setAdapter(this._adapDocEscritura);

		this._adapDocEstratificacion	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strDocumentacion);
		this._cmbDocEstratificacion.setAdapter(this._adapDocEstratificacion);
		
		this._adapDocCedula	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strDocumentacion);
		this._cmbDocCedula.setAdapter(this._adapDocCedula);
		
		this._adapDocRecibo	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strDocumentacion);
		this._cmbDocRecibo.setAdapter(this._adapDocRecibo);
		
		this._adapDocInstalacion	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strDocumentacion);
		this._cmbDocInstalacion.setAdapter(this._adapDocInstalacion);
		
		this._adapDocFactura	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strDocumentacion);
		this._cmbDocFactura.setAdapter(this._adapDocFactura);
		
		this._adapDocAutorizacion	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this._strDocumentacion);
		this._cmbDocAutorizacion.setAdapter(this._adapDocAutorizacion);
		
		this._txtSerieContador.setEnabled(false);
		this._txtLecturaContador.setEnabled(false);
		this._cmbTipoConexion.setEnabled(false);
		
		this._btnCrear.setOnClickListener(this);
		this._btnCancelar.setOnClickListener(this);
	}

	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.ServicioNuevoBtnCrear:
				DialogConfirmacion.putExtra("informacion", "Desea crear el servicio nuevo?");
				startActivityForResult(DialogConfirmacion, CONFIRM_NUEVO_SERVICIO);
				break;
				
				
			case R.id.ServicioNuevoBtnCancelar:
				finish();
				break;
		}		
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == CONFIRM_NUEVO_SERVICIO) {
			if(data.getExtras().getBoolean("accion")){
				if(FcnServicioNuevo.validarDatos(_txtPropietario.getText().toString(), _cmbTipoUsuario.getSelectedItem().toString(), _cmbUbicacion.getSelectedItem().toString(), _txtDireccion.getText().toString(), _cmbMunicipio.getSelectedItem().toString(), _cmbEstrato.getSelectedItem().toString(), _cmbClaseServicio.getSelectedItem().toString(), _cmbEstadoServicio.getSelectedItem().toString(), _txtCuenta1.getText().toString(), _txtCuenta2.getText().toString(), _txtNodoTransformador.getText().toString(), _txtNodoSecundario.getText().toString())){
					if(FcnServicioNuevo.validarDocumentacion(_cmbDocEscritura.getSelectedItem().toString(), _cmbDocEstratificacion.getSelectedItem().toString(), _cmbDocCedula.getSelectedItem().toString(), _cmbDocRecibo.getSelectedItem().toString(), _cmbDocInstalacion.getSelectedItem().toString(), _cmbDocFactura.getSelectedItem().toString(), _cmbDocAutorizacion.getSelectedItem().toString())){
						if(FcnServicioNuevo.crearServicioNuevo()){
							Toast.makeText(this,"Nuevo Servicio creado correctamente.",Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		}
    }	
}
