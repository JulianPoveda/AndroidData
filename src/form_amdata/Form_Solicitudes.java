package form_amdata;

import java.io.File;
import java.util.ArrayList;

import class_amdata.Class_Solicitudes;
import dialogos.DialogConfirm;
import dialogos.DialogInformacion;
import dialogos.DialogSingleTxt;
import dialogos.Modal_BodegaContadores;
import dialogos.Modal_BodegaSellos;
import sypelc.androidamdata.R;
import miscelanea.SQLite;
import miscelanea.Util;
import adaptador_download_trabajo.AdaptadorListaTrabajo;
import adaptador_download_trabajo.InformacionSolicitudes;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class Form_Solicitudes extends Activity implements OnItemSelectedListener, OnItemClickListener{	
	Intent DialogConfirmacion; 	
	Intent DialogInformacion; 	
	Intent DialogApertura;
		
	Class_Solicitudes 	FcnSolicitudes;
	
	Util			SolicitudesUtil;
	SQLite 			SolicitudesSQL; 
	private String 	FolderAplicacion = "";
	
	private static int    	CONFIRMACION_INICIO_ORDEN	= 1;
	private static int 		CONFIRMACION_INFORMACION 	= 2;
	private static int		CONFIRMACION_CERRAR_ORDEN	= 3;
	private static int 		CONFIRMACION_COD_APERTURA	= 4;
	
	private boolean enabledMenu = false;
	
	
	/**Variables para del usuario que inicio sesion, lo cuales son utilizados para registrar todos los movimientos realizados**/
	private String 	NombreUsuario = "";
	private String 	CedulaUsuario= "";
	private String 	NivelUsuario = "";
	
	//Variable para consulta general de la base de datos
	ArrayList<ContentValues> _tempTabla = new ArrayList<ContentValues>();
	ContentValues _tempRegistro = new ContentValues(); 
	
	
	//Variable para consulta general de la base de datos
	ArrayList<ContentValues> TablaQuery = new ArrayList<ContentValues>();
	ContentValues RegistroQuery = new ContentValues(); 
	
	//Variables para control del combo de nodos y adaptadores
	private ArrayAdapter<String> AdaptadorNodos;
	private ArrayList<String> Nodos = new ArrayList<String>();
	private Spinner ListaNodos, _cmbIdTrabajo;
	
	
	/**Variables Id Trabajo**/
	//private String _strIdTrabajo;
	ArrayList<String> _strIdTrabajo = new ArrayList<String>();
	ArrayAdapter<String> AdaptadorIdTrabajo;
	
	//Variables para control del combo de solicitudes por nodo y adaptadores
	AdaptadorListaTrabajo AdaptadorSolicitudes;
	ArrayList<InformacionSolicitudes> ArraySolicitudes = new ArrayList<InformacionSolicitudes>();
	ListView ListaSolicitudes;
	
	
	//Instancias a objetos graficos
	TextView _txtRuta, _txtSerie, _txtDireccion, _txtPropietario, _txtCuenta, _txtOrden, _txtEstado, _txtTipo, _txtObservacion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_solicitudes);
		
		Bundle bundle = getIntent().getExtras();
		this.FolderAplicacion= bundle.getString("FolderAplicacion");		
		this.NombreUsuario	= bundle.getString("NombreUsuario");
		this.CedulaUsuario	= bundle.getString("CedulaUsuario");
		this.NivelUsuario	= bundle.getString("NivelUsuario");
		
		/*************************Instacias a dialogos para mensajes de confirmacion e informacion********************/
		DialogConfirmacion 	= new Intent(this,DialogConfirm.class);
		DialogInformacion	= new Intent(this,DialogInformacion.class);
		DialogApertura		= new Intent(this,DialogSingleTxt.class);
		/*************************************Instancias a clases para las funciones *********************************/
		FcnSolicitudes	= new Class_Solicitudes(this,this.FolderAplicacion);
		SolicitudesSQL	= new SQLite(this,FolderAplicacion);
		SolicitudesUtil = new Util();
		
		
		/**Instrucciones para el adaptador del id de trabajo**/
		AdaptadorIdTrabajo = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,_strIdTrabajo);
		_cmbIdTrabajo = (Spinner) findViewById(R.id.SolicitudesCmbIdTrabajo);
		_cmbIdTrabajo.setAdapter(AdaptadorIdTrabajo);
		
		//Adapter para los nodos	
		this.Nodos = FcnSolicitudes.getNodosSolicitudes();
		this.AdaptadorNodos = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.Nodos);
		this.ListaNodos = (Spinner) findViewById(R.id.CmbNodoSolicitud);
		this.ListaNodos.setAdapter(this.AdaptadorNodos);
		
		//AdaptadorListaTrabajo adapter = new AdaptadorListaTrabajo(this,ArraySolicitudes);
		AdaptadorSolicitudes = new AdaptadorListaTrabajo(this,ArraySolicitudes);
		ListaSolicitudes = (ListView) findViewById(R.id.LstSolicitudes);
		ListaSolicitudes.setAdapter(AdaptadorSolicitudes);
		

		
		_txtRuta 		= (TextView) findViewById(R.id.TxtRuta);
		_txtSerie		= (TextView) findViewById(R.id.TxtSerie);
		_txtDireccion	= (TextView) findViewById(R.id.TxtDireccion);
		_txtPropietario = (TextView) findViewById(R.id.TxtPropietario);
		_txtCuenta		= (TextView) findViewById(R.id.TxtCuenta); 
		_txtOrden		= (TextView) findViewById(R.id.TxtOrden);
		_txtEstado		= (TextView) findViewById(R.id.TxtEstado); 
		_txtTipo		= (TextView) findViewById(R.id.TxtTipo);
		_txtObservacion	= (TextView) findViewById(R.id.TxtObservacion);
		
		_txtRuta.setEnabled(false);
		_txtSerie.setEnabled(false);
		_txtDireccion.setEnabled(false);
		_txtPropietario.setEnabled(false);
		_txtCuenta.setEnabled(false);
		_txtOrden.setEnabled(false);
		_txtEstado.setEnabled(false);
		_txtTipo.setEnabled(false);
		_txtObservacion.setEnabled(false);		
		
		ListaNodos.setOnItemSelectedListener(this);
		ListaSolicitudes.setOnItemClickListener(this);
	}
	
	
	public void CargarIdTrabajo(String _solicitudSeleccionada){
		_tempTabla = SolicitudesSQL.SelectData("vista_ordenes_trabajo", "descripcion" ,"id_orden='"+_solicitudSeleccionada+"'");
		SolicitudesUtil.ArrayContentValuesToString(_strIdTrabajo, _tempTabla, "descripcion");
		
		AdaptadorIdTrabajo = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,_strIdTrabajo);
		_cmbIdTrabajo.setAdapter(AdaptadorIdTrabajo);
		AdaptadorIdTrabajo.setNotifyOnChange(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_solicitudes, menu);
		return true;
	}
	
	
	@Override
	 public boolean onPrepareOptionsMenu(Menu menu) {
		if(this.enabledMenu){
			menu.findItem(R.id.InicioOrden).setEnabled(true);
			menu.findItem(R.id.AperturaOrdenes).setEnabled(true);
			menu.findItem(R.id.CerrarOrden).setEnabled(true);
			menu.findItem(R.id.TrasladarOrden).setEnabled(true);
		}else{
			menu.findItem(R.id.InicioOrden).setEnabled(false);
			menu.findItem(R.id.AperturaOrdenes).setEnabled(false);
			menu.findItem(R.id.CerrarOrden).setEnabled(false);
			menu.findItem(R.id.TrasladarOrden).setEnabled(false);
		}    	
		return true;  
	}
	
	
	public void CrearActualizarIdTrabajo(String _orden, String _cuenta){
		_tempRegistro.clear();
		_tempRegistro.put("id_revision",SolicitudesSQL.IntSelectShieldWhere("amd_param_trabajos_orden","max(cast(id_revision AS INTEGER)) as id_revision", "id_orden='"+_orden+"' AND cuenta='"+_cuenta+"'")+1);
		_tempRegistro.put("id_orden", _orden);
		_tempRegistro.put("id_trabajo", SolicitudesSQL.StrSelectShieldWhere("vista_ordenes_trabajo", "id_trabajo", "id_orden='"+_orden+"'"));
		_tempRegistro.put("nodo", SolicitudesSQL.StrSelectShieldWhere("vista_ordenes_trabajo", "id_nodo", "id_orden='"+_orden+"'"));
		_tempRegistro.put("cuenta", _cuenta);
		_tempRegistro.put("estado", "E");
		_tempRegistro.put("cantidad", "1");
		_tempRegistro.put("usuario_ins",CedulaUsuario);
		SolicitudesSQL.InsertOrUpdateRegistro("amd_param_trabajos_orden", _tempRegistro, "id_orden='"+_orden+"' AND id_trabajo='"+_tempRegistro.getAsString("id_trabajo")+"'");		
	}
	
	
	
	//Control de eventos al momento de seleccionar una opcion del menu del formulario
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {				
			case R.id.InicioOrden:
				/***********************Validacion de si existe una solicitud previamente abierta*****************/
				if(FcnSolicitudes.IniciarOrden(_txtOrden.getText().toString())){
					DialogConfirmacion.putExtra("informacion", "Desea Iniciar La Orden "+ _txtOrden.getText().toString());
					startActivityForResult(DialogConfirmacion, CONFIRMACION_INICIO_ORDEN);
				}else{
					DialogInformacion.putExtra("informacion", "No se puede iniciar la actividad, comprobar:\n->Que no exista otra orden abierta\n->Que la orden seleccionada no este en estado 'TERMINADA'");
					startActivityForResult(DialogInformacion, CONFIRMACION_INFORMACION);
				}
				return true;
			
			
			case R.id.CerrarOrden:
				if(!_txtOrden.getText().toString().isEmpty()){
					if(FcnSolicitudes.getEstadoOrden(_txtOrden.getText().toString()).equals("E")){
						DialogConfirmacion.putExtra("informacion", "Desea Cerrar La Orden "+_txtOrden.getText().toString());
						startActivityForResult(DialogConfirmacion, CONFIRMACION_CERRAR_ORDEN);	
					}
				}else{
					DialogInformacion.putExtra("informacion","No ha seleccionado una orden para cerrar.");
					startActivityForResult(DialogInformacion, CONFIRMACION_INFORMACION);
				}
				return true;
			
			case R.id.AperturaOrdenes:
				if(FcnSolicitudes.IniciarAperturaOrden(_txtOrden.getText().toString())){
					DialogApertura.putExtra("titulo","INGRESE EL CODIGO DE APERTURA");
					DialogApertura.putExtra("lbl1", "Codigo:");
					DialogApertura.putExtra("txt1", "");
					startActivityForResult(DialogApertura, CONFIRMACION_COD_APERTURA);
				}else{
					DialogInformacion.putExtra("informacion", "No es posible abrir la orden "+_txtOrden.getText().toString()+", verifique que:\n->Este en estado terminada.\n->No exista otra orden abierta.");
					startActivityForResult(DialogInformacion, CONFIRMACION_INFORMACION);
				}
				return true;
				
			case R.id.EliminarAut:
				if(FcnSolicitudes.getEstadoOrden(_txtOrden.getText().toString()).equals("P") && (FcnSolicitudes.isAutogestion(_txtOrden.getText().toString()))){
					String _id_nodo= SolicitudesSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "id_nodo", "id_orden='"+_txtOrden.getText().toString()+"'");
					if(SolicitudesSQL.IntSelectShieldWhere("amd_ordenes_trabajo", "count(*) as cantidad", "id_nodo='"+_id_nodo+"'")==1){
						SolicitudesSQL.DeleteRegistro("amd_nodo", "id_nodo='"+_id_nodo+"'");
					}
					
					SolicitudesSQL.DeleteRegistro("amd_contador_cliente_orden", "cuenta='"+_txtCuenta.getText().toString()+"'");
					SolicitudesSQL.DeleteRegistro("amd_ordenes_trabajo", "id_orden='"+_txtOrden.getText().toString()+"'");
					Toast.makeText(this,"Orden Eliminada Correctamente",Toast.LENGTH_SHORT).show();
					CargarOrdenesTrabajo();				
				}else{
					Toast.makeText(this,"No es posible eliminar la orden, verifique que sea una orden no cargada y que se encuentre en estado P",Toast.LENGTH_SHORT).show();
				}
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	public void CargarOrdenesTrabajo(){
		ArraySolicitudes.clear();
		if(ListaNodos.getSelectedItem().toString().equals("Todos")){
			TablaQuery = SolicitudesSQL.SelectData("amd_ordenes_trabajo", "direccion,cuenta,id_orden,estado", "estado in ('P','E','T','TA') ORDER BY id_orden");
			for(int i=0;i<TablaQuery.size();i++){
				RegistroQuery = TablaQuery.get(i);
				ArraySolicitudes.add(new InformacionSolicitudes(RegistroQuery.getAsString("id_orden"),RegistroQuery.getAsString("cuenta"),RegistroQuery.getAsString("direccion"),RegistroQuery.getAsString("estado")));
			}
		}else{
			TablaQuery = SolicitudesSQL.SelectData("amd_ordenes_trabajo", "direccion,cuenta,id_orden,estado", "id_nodo ='"+ListaNodos.getSelectedItem().toString()+"' AND estado in ('P','E','T','TA') ORDER BY id_orden");
			for(int i=0;i<TablaQuery.size();i++){
				RegistroQuery = TablaQuery.get(i);
				ArraySolicitudes.add(new InformacionSolicitudes(RegistroQuery.getAsString("id_orden"),RegistroQuery.getAsString("cuenta"),RegistroQuery.getAsString("direccion"),RegistroQuery.getAsString("estado")));
			}
		}
		AdaptadorSolicitudes.notifyDataSetChanged();
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == CONFIRMACION_INICIO_ORDEN) {
			if(data.getExtras().getBoolean("accion")){
				/**procedimiento para generar el numero del acta y guardarlo en la base de datos**/
				String NumeroActa = String.valueOf(SolicitudesSQL.IntSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'")-280)+SolicitudesSQL.MinutesBetweenDateAndNow("2012-04-09 00:00:00");
				_tempRegistro.clear();
				_tempRegistro.put("num_acta", NumeroActa);
				_tempRegistro.put("estado", "E");
				SolicitudesSQL.UpdateRegistro("amd_ordenes_trabajo", _tempRegistro, "id_orden='"+_txtOrden.getText().toString()+"'");
				/**Fin de la generacion del numero de acta y actualizacion en la base de datos**/
				
				CrearActualizarIdTrabajo(_txtOrden.getText().toString(), _txtCuenta.getText().toString());
				finish();
				Intent k;
				k = new Intent(this, Form_Actas.class);
				k.putExtra("NombreUsuario", NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("NivelUsuario", NivelUsuario);
				k.putExtra("OrdenTrabajo",_txtOrden.getText().toString());
				k.putExtra("CuentaCliente", _txtCuenta.getText().toString());
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivity(k);
			}
		}else if(resultCode == RESULT_OK && requestCode == CONFIRMACION_CERRAR_ORDEN){
			if(data.getExtras().getBoolean("accion")){
				_tempRegistro.clear();
				_tempRegistro.put("estado","T");
				SolicitudesSQL.UpdateRegistro("amd_ordenes_trabajo", _tempRegistro, "id_orden='"+_txtOrden.getText().toString()+"'");	
				CargarOrdenesTrabajo();
			}			
		}else if(resultCode == RESULT_OK && requestCode == CONFIRMACION_COD_APERTURA){
			if(data.getExtras().getBoolean("response")){
				if(FcnSolicitudes.verificarCodigoApertura(_txtOrden.getText().toString(),data.getExtras().getString("txt1"))){
					_tempRegistro.clear();
					_tempRegistro.put("estado","E");
					SolicitudesSQL.UpdateRegistro("amd_ordenes_trabajo", _tempRegistro, "id_orden='"+_txtOrden.getText().toString()+"'");	
					CargarOrdenesTrabajo();
				}else{
					DialogInformacion.putExtra("informacion", "Codigo De Apertura Erroneo");
					startActivityForResult(DialogInformacion, CONFIRMACION_INFORMACION);
				}
			}			
		}
    }


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch(parent.getId()){
			case R.id.CmbNodoSolicitud:
				CargarOrdenesTrabajo();
				break;
		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		switch(parent.getId()){
			case R.id.LstSolicitudes:
				CargarIdTrabajo(ArraySolicitudes.get(position).getSolicitud());
				
				String Join[] 	= {"amd_contador_cliente_orden as b"};
				String On[] 	= {"a.cuenta = b.cuenta"};
				TablaQuery = SolicitudesSQL.SelectNJoinLeftData("amd_ordenes_trabajo as a", 
																"a.ruta, b.serie, a.direccion, a.propietario, a.cuenta, a.id_orden, a.estado, a.tipo, a.observacion_trabajo", 
																Join, 
																On, 
																"a.id_orden='"+ArraySolicitudes.get(position).getSolicitud()+"'");
				ContentValues Registro = new ContentValues();
				for(int i=0;i<TablaQuery.size();i++){
					Registro.clear();
					Registro = TablaQuery.get(i);
					_txtRuta.setText(Registro.get("ruta").toString());
					_txtSerie.setText(Registro.get("serie").toString());
					_txtDireccion.setText(Registro.get("direccion").toString());
					_txtPropietario.setText(Registro.get("propietario").toString());
					_txtCuenta.setText(Registro.get("cuenta").toString());
					_txtOrden.setText(Registro.get("id_orden").toString());
					_txtEstado.setText(Registro.get("estado").toString());
					_txtTipo.setText(Registro.get("tipo").toString());
					_txtObservacion.setText(Registro.get("observacion_trabajo").toString());
					this.enabledMenu = true;
					invalidateOptionsMenu(); 
				}
				break;
		}		
	}
}
