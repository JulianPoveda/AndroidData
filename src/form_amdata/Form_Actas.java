package form_amdata;

import java.io.File;
import java.util.ArrayList;

import class_amdata.Class_Impresiones;
import dialogos.Modal_BodegaContadores;
import dialogos.Modal_MedidorSellosOrden;

import sypelc.androidamdata.R;
import miscelanea.FormatosActas;
import miscelanea.SQLite;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Form_Actas extends Activity implements OnClickListener{
	private static int  ACT_MEDIDOR_SELLOS_ORDEN = 1;
	Intent MedidorSellosOrden;
	
	Class_Impresiones 	FcnImpresion;
	FormatosActas 		ActaImpresa;
	SQLite 				ActasSQL;
	Util   				ActasUtil;
	
	private String		NombreUsuario	= "";
	private String		CedulaUsuario	= "";
	private String 		NivelUsuario	= "";
	private String 		OrdenTrabajo 	= "";
	private String 		CuentaCliente 	= "";
	private String 		FolderAplicacion= "";
	
	//Variable utilizada para realizar las operaciones en la base de datos 
	ArrayList<ContentValues> Query 	= new ArrayList<ContentValues>();
	ContentValues 	Registro 		= new ContentValues();
	
	
	//Variables para consultar los tipos de conexiones de medidores que hay y hacer el adapter respectivo
	ArrayList<ContentValues> ConexionMedidor 	= new ArrayList<ContentValues>();
	ArrayList<String> StringConexionMedidor 	= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorConexionMedidor;	
	
	
	//Variables para consultar los tipos de acometidas y hacer el adapter respectivo
	ArrayList<ContentValues> AcometidaMedidor 	= new ArrayList<ContentValues>();
	ArrayList<String> StringAcometidaMedidor 	= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorAcometidaMedidor;	
	
	
	//Variables para consultar los items de pagos y hacer el respectivo adapter
	ArrayList<ContentValues> ItemsPago 	= new ArrayList<ContentValues>();
	ArrayList<String> StringItemsPago 	= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorItemsPago;
	
	//Variables para consultar los items de pagos y hacer el respectivo adapter
	ArrayList<ContentValues> TipoEnterado 	= new ArrayList<ContentValues>();
	ArrayList<String> StringTipoEnterado 	= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorTipoEnterado;
	
	//Variables para consultar los items de pagos y hacer el respectivo adapter
	ArrayList<ContentValues> UbicacionMedidor 	= new ArrayList<ContentValues>();
	ArrayList<String> StringUbicacionMedidor 	= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorUbicacionMedidor;
	
	//Variables para consultar los items de pagos y hacer el respectivo adapter
	ArrayList<ContentValues> UsoDerecho 	= new ArrayList<ContentValues>();
	ArrayList<String> StringUsoDerecho 		= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorUsoDerecho;
	
	//Variables para consultar las posibles respuestas PQR y hacer el adapter respectivo
	ArrayList<ContentValues> RtaPQR		= new ArrayList<ContentValues>();
	ArrayList<String> StringRtaPQR 		= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorRtaPQR;
	
	
	//Instancias a objetos graficos
	TextView 	_lblOrden, _lblActa, _lblCuenta, _lblItemAplicados;
	EditText	_txtDocEnterado, _txtNombreEnterado, _txtDocTestigo, _txtNombreTestigo;
	Spinner 	_cmbTipoMedidores, _cmbAcometidaMedidor, _cmbItems, _cmbTipoEnterado, _cmbUbicacionMedidor, _cmbUsoDerecho, _cmbRtaPQR;
	Button 		_btnAddItem, _btnRemoveItem, _btnGuardarDatos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actas);
		
		Bundle bundle 	= getIntent().getExtras();
		this.NombreUsuario		= bundle.getString("NombreUsuario");
		this.CedulaUsuario		= bundle.getString("CedulaUsuario");
		this.NivelUsuario		= bundle.getString("NivelUsuario");
		this.OrdenTrabajo		= bundle.getString("OrdenTrabajo");
		this.CuentaCliente 		= bundle.getString("CuentaCliente");		
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");
		
		MedidorSellosOrden 	= new Intent(this,Modal_MedidorSellosOrden.class);
		FcnImpresion		= new Class_Impresiones(this, this.FolderAplicacion, this.CedulaUsuario);
		
		ActasSQL 	= new SQLite(this, this.FolderAplicacion);
		ActasUtil 	= new Util();
		ActaImpresa	= new FormatosActas(this, this.FolderAplicacion,true);
			
		_lblOrden 			= (TextView) findViewById(R.id.ActaLblNumOrden);
		_lblCuenta 			= (TextView) findViewById(R.id.ActaLblNumCuenta);
		_lblActa 			= (TextView) findViewById(R.id.ActaLblNumActa);
		_lblItemAplicados 	= (TextView) findViewById(R.id.ActaItemsAplicados);
		
		_lblOrden.setText(OrdenTrabajo);
		_lblActa.setText(ActasSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "num_acta", "id_orden='"+OrdenTrabajo+"'"));
		_lblCuenta.setText(CuentaCliente);	
		
		_txtDocEnterado 	= (EditText) findViewById(R.id.ActaTxtDocEnterado);
		_txtNombreEnterado 	= (EditText) findViewById(R.id.ActaTxtNombreEnterado);
		_txtDocTestigo		= (EditText) findViewById(R.id.ActaTxtDocTestigo);
		_txtNombreTestigo	= (EditText) findViewById(R.id.ActaTxtNombreTestigo);
				
		_cmbTipoMedidores 		= (Spinner) findViewById(R.id.ActaCmbDiagrama);
		_cmbAcometidaMedidor 	= (Spinner) findViewById(R.id.ActaCmbAcometida);
		_cmbItems 				= (Spinner) findViewById(R.id.ActaCmbItems);
		_cmbTipoEnterado 		= (Spinner) findViewById(R.id.ActaCmbTipoEnterado);
		_cmbUbicacionMedidor	= (Spinner) findViewById(R.id.ActaCmbUbicacionMedidor);
		_cmbUsoDerecho			= (Spinner)	findViewById(R.id.ActaCmbUsoDerecho);
		_cmbRtaPQR				= (Spinner) findViewById(R.id.ActaCmbRespuestaPQR);
		
		_btnAddItem 		= (Button) findViewById(R.id.ActaBtnAddItem);
		_btnRemoveItem 		= (Button) findViewById(R.id.ActaBtnRemoveItem);
		_btnGuardarDatos	= (Button) findViewById(R.id.ActaBtnGuardar);
		
		
		ConexionMedidor = ActasSQL.SelectData("amd_tipos_medidores", "descripcion", "id_serial IS NOT NULL");
		ActasUtil.ArrayContentValuesToString(StringConexionMedidor, ConexionMedidor, "descripcion");		
		AdaptadorConexionMedidor= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringConexionMedidor);
		_cmbTipoMedidores.setAdapter(AdaptadorConexionMedidor);
		
		AcometidaMedidor = ActasSQL.SelectData("amd_conexion_acometida", "descripcion", "id_serial IS NOT NULL");
		ActasUtil.ArrayContentValuesToString(StringAcometidaMedidor, AcometidaMedidor, "descripcion");		
		AdaptadorAcometidaMedidor= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringAcometidaMedidor);
		_cmbAcometidaMedidor.setAdapter(AdaptadorAcometidaMedidor);
		
		ItemsPago = ActasSQL.SelectData("amd_items_pago", "item", "item IS NOT NULL");	
		ActasUtil.ArrayContentValuesToString(StringItemsPago, ItemsPago, "item");	
		AdaptadorItemsPago= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringItemsPago);
		_cmbItems.setAdapter(AdaptadorItemsPago);
		
		TipoEnterado = ActasSQL.SelectData("amd_tipo_enterado", "descripcion", "id_serial IS NOT NULL");	
		ActasUtil.ArrayContentValuesToString(StringTipoEnterado, TipoEnterado, "descripcion");	
		AdaptadorTipoEnterado= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringTipoEnterado);
		_cmbTipoEnterado.setAdapter(AdaptadorTipoEnterado);
		
		UbicacionMedidor = ActasSQL.SelectData("amd_ubicacion_medidor", "descripcion", "id_serial IS NOT NULL");	
		ActasUtil.ArrayContentValuesToString(StringUbicacionMedidor, UbicacionMedidor, "descripcion");	
		AdaptadorUbicacionMedidor= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringUbicacionMedidor);
		_cmbUbicacionMedidor.setAdapter(AdaptadorUbicacionMedidor);
		
		UsoDerecho = ActasSQL.SelectData("amd_uso_derecho", "descripcion", "id_serial IS NOT NULL");	
		ActasUtil.ArrayContentValuesToString(StringUsoDerecho, UsoDerecho, "descripcion");	
		AdaptadorUsoDerecho= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringUsoDerecho);
		_cmbUsoDerecho.setAdapter(AdaptadorUsoDerecho);
		
		RtaPQR = ActasSQL.SelectData("vista_respuesta_pqr", "descripcion_tipo_recomendacion as descripcion", "id_orden='"+this.OrdenTrabajo+"'");	
		ActasUtil.ArrayContentValuesToString(StringRtaPQR, RtaPQR, "descripcion","-",1);	
		AdaptadorRtaPQR= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StringRtaPQR);
		_cmbRtaPQR.setAdapter(AdaptadorRtaPQR);
		
		_btnAddItem.setOnClickListener(this);
		_btnRemoveItem.setOnClickListener(this);
		_btnGuardarDatos.setOnClickListener(this);
		
		//Se consulta si hay informacion en la base de datos para que la cargue por defecto
		Query = ActasSQL.SelectData("amd_impresiones_inf", "diagrama,acometida,ubicacion,uso_derecho,items", "id_orden='"+OrdenTrabajo+"'");
		if(Query.size()>0){
			Registro = Query.get(0);
			_cmbTipoMedidores.setSelection(AdaptadorConexionMedidor.getPosition(Registro.getAsString("diagrama")));
			_cmbAcometidaMedidor.setSelection(AdaptadorAcometidaMedidor.getPosition(Registro.getAsString("acometida")));
			_cmbUbicacionMedidor.setSelection(AdaptadorUbicacionMedidor.getPosition(Registro.getAsString("ubicacion")));
			_cmbUsoDerecho.setSelection(AdaptadorUsoDerecho.getPosition(Registro.getAsString("uso_derecho")));
			_lblItemAplicados.setText(Registro.getAsString("items"));
		}
		
		Query = ActasSQL.SelectData("amd_actas", "nombre_enterado,cedula_enterado,nombre_testigo,cedula_testigo,tipo_enterado", "id_orden='"+OrdenTrabajo+"'");
		if(Query.size()>0){
			Registro = Query.get(0);
			_txtNombreEnterado.setText(Registro.getAsString("nombre_enterado"));
			_txtDocEnterado.setText(Registro.getAsString("cedula_enterado"));
			_txtNombreTestigo.setText(Registro.getAsString("nombre_testigo"));
			_txtDocTestigo.setText(Registro.getAsString("cedula_testigo"));		
			_cmbTipoEnterado.setSelection(AdaptadorTipoEnterado.getPosition(Registro.getAsString("tipo_enterado")));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_actas, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent k;
		switch (item.getItemId()) {	
			
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
				k.putExtra("FolderAplicacion",  Environment.getExternalStorageDirectory() + File.separator + "EMSA");
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
				
			case R.id.MedidorSellosOrden:
				MedidorSellosOrden.putExtra("CuentaCliente",  this.CuentaCliente);
				MedidorSellosOrden.putExtra("FolderAplicacion",  Environment.getExternalStorageDirectory() + File.separator + "EMSA");
				startActivityForResult(MedidorSellosOrden, ACT_MEDIDOR_SELLOS_ORDEN);
				return true;
				
			case R.id.ImpresionOriginal:
				if(this.FcnImpresion.validarDatosImpresionActa(OrdenTrabajo)){
					ActaImpresa.FormatoVerificacion(OrdenTrabajo, CuentaCliente, "Desviacion",1, CedulaUsuario);
				}				
				return true;
				
			case R.id.ImpresionCopia:
				if(this.FcnImpresion.validarDatosImpresionActa(OrdenTrabajo)){
					ActaImpresa.FormatoVerificacion(OrdenTrabajo, CuentaCliente, "Desviacion",2, CedulaUsuario);
				}				
				return true;
				
			case R.id.ImpresionArchivo:
				if(this.FcnImpresion.validarDatosImpresionActa(OrdenTrabajo)){
					ActaImpresa.FormatoVerificacion(OrdenTrabajo, CuentaCliente, "Desviacion",3, CedulaUsuario);
				}				
				return true;	
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
        	case R.id.ActaBtnAddItem:
        		if(ActasUtil.FindStringIntoArrayString(_cmbItems.getSelectedItem().toString(), _lblItemAplicados.getText().toString(), " ")==-1){
        			_lblItemAplicados.setText(_lblItemAplicados.getText()+" "+_cmbItems.getSelectedItem().toString());
        		}else{
        			Toast.makeText(this,"El item de pago ya se encuentra registrado.", Toast.LENGTH_SHORT).show();
        		}        		
        		break;
        	
        	case R.id.ActaBtnRemoveItem:
        		if(ActasUtil.FindStringIntoArrayString(_cmbItems.getSelectedItem().toString(), _lblItemAplicados.getText().toString(), " ")>=0){
        			_lblItemAplicados.setText(ActasUtil.RemoveStringIntoArrayString(_cmbItems.getSelectedItem().toString(), _lblItemAplicados.getText().toString(), " "));
        		}else{
        			Toast.makeText(this,"El item de pago no se encuentra registrado.", Toast.LENGTH_SHORT).show();
        		}        		
        		break;
        		
        	case R.id.ActaBtnGuardar:
        		if(_lblItemAplicados.getText().length()==0){
        			Toast.makeText(this,"No ha seleccionado los items de pago a aplicar.", Toast.LENGTH_SHORT).show();
        		}else if(_cmbUbicacionMedidor.getSelectedItem().toString().equals("...")){
        			Toast.makeText(this,"No ha seleccionado la ubicacion del medidor.", Toast.LENGTH_SHORT).show();        			
        		}else if(_cmbUsoDerecho.getSelectedItem().toString().equals("...")){
        			Toast.makeText(this,"No ha seleccionado si el usuario hace uso del derecho.", Toast.LENGTH_SHORT).show();        			
        		}else{
        			
        			/**********************************Se guarda la informacion de amd_impresiones_inf******************************/
        			Registro.clear();		
           			if(ActasSQL.ExistRegistros("amd_impresiones_inf", "id_orden='"+OrdenTrabajo+"'")){
        				Registro.put("diagrama", _cmbTipoMedidores.getSelectedItem().toString());
            			Registro.put("acometida", _cmbAcometidaMedidor.getSelectedItem().toString());
            			Registro.put("ubicacion", _cmbUbicacionMedidor.getSelectedItem().toString());
            			Registro.put("uso_derecho", _cmbUsoDerecho.getSelectedItem().toString());
            			Registro.put("items", _lblItemAplicados.getText().toString());
            			
        				if(ActasSQL.UpdateRegistro("amd_impresiones_inf", Registro, "id_orden='"+OrdenTrabajo+"'")){
        					Toast.makeText(this,"Registro actualizado correctamente en amd_impresiones_inf.", Toast.LENGTH_SHORT).show();
        				}else{
        					Toast.makeText(this,"Error al actualizar el registro en amd_impresiones_inf.", Toast.LENGTH_SHORT).show();
        				}
        			}else{
        				Registro.put("id_orden", OrdenTrabajo);
        				Registro.put("diagrama", _cmbTipoMedidores.getSelectedItem().toString());
            			Registro.put("acometida", _cmbAcometidaMedidor.getSelectedItem().toString());
            			Registro.put("ubicacion", _cmbUbicacionMedidor.getSelectedItem().toString());
            			Registro.put("uso_derecho", _cmbUsoDerecho.getSelectedItem().toString());
            			Registro.put("items", _lblItemAplicados.getText().toString());
            			
            			if(ActasSQL.InsertRegistro("amd_impresiones_inf", Registro)){
        					Toast.makeText(this,"Registro ingresado correctamente en amd_impresiones_inf.", Toast.LENGTH_SHORT).show();
        				}else{
        					Toast.makeText(this,"Error al ingresar el registro en amd_impresiones_inf.", Toast.LENGTH_SHORT).show();
        				}    			
            		} 
           			
           			/*****************************************Se guarda la informacion de amd_actas*********************************/
        			Registro.clear();    		
        			if(ActasSQL.ExistRegistros("amd_actas", "id_orden='"+OrdenTrabajo+"'")){
        				Registro.put("cedula_enterado",_txtDocEnterado.getText().toString());
        				Registro.put("nombre_enterado",_txtNombreEnterado.getText().toString());
        				Registro.put("tipo_enterado",_cmbTipoEnterado.getSelectedItem().toString());
        				Registro.put("cedula_testigo",_txtDocTestigo.getText().toString());
        				Registro.put("nombre_testigo",_txtNombreTestigo.getText().toString());
        				
        				if(ActasSQL.UpdateRegistro("amd_actas", Registro, "id_orden='"+OrdenTrabajo+"'")){
        					Toast.makeText(this,"Registro actualizado correctamente en amd_actas.", Toast.LENGTH_SHORT).show();
        				}else{
        					Toast.makeText(this,"Error al ingresar el registro en amd_actas.", Toast.LENGTH_SHORT).show();
        				}
        			}else{
        				Registro.put("id_orden",OrdenTrabajo);
        				Registro.put("id_acta",ActasSQL.IntSelectShieldWhere("amd_actas", "max(cast(id_acta AS INTEGER)) as id_acta", "id_acta IS NOT NULL")+1);
        				Registro.put("id_revision","0");
        				Registro.put("codigo_trabajo",ActasSQL.StrSelectShieldWhere("amd_param_trabajos_orden", "id_trabajo", "id_orden='"+OrdenTrabajo+"'"));
        				Registro.put("cedula_enterado",_txtDocEnterado.getText().toString());
        				Registro.put("nombre_enterado",_txtNombreEnterado.getText().toString());
        				Registro.put("evento","");
        				Registro.put("tipo_enterado",_cmbTipoEnterado.getSelectedItem().toString());
        				Registro.put("cedula_testigo",_txtDocTestigo.getText().toString());
        				Registro.put("nombre_testigo",_txtNombreTestigo.getText().toString());
        				Registro.put("estado","E");
        				Registro.put("usuario_ins",CedulaUsuario);
        				
        				if(ActasSQL.InsertRegistro("amd_actas", Registro)){
        					Toast.makeText(this,"Registro ingresado correctamente en amd_actas.", Toast.LENGTH_SHORT).show();
        				}else{
        					Toast.makeText(this,"Error al ingresar el registro en amd_actas.", Toast.LENGTH_SHORT).show();
        				}        				
        			}
        			
        			/*************************Se guarda la informacion de la respuesta PQR en amd_inconsistencias***********************/
        			if(_cmbRtaPQR.getSelectedItem() != null){
            			Registro.clear();
            			Registro.put("id_inconsistencia", ActasSQL.IntSelectShieldWhere("amd_inconsistencias","max(cast(id_inconsistencia AS INTEGER)) as id_inconsistencia","id_inconsistencia IS NOT NULL")+1);
            			Registro.put("id_orden", OrdenTrabajo);
            			Registro.put("id_nodo", ActasSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "id_nodo", "id_orden='"+OrdenTrabajo+"'"));
        				Registro.put("valor", ActasSQL.StrSelectShieldWhere("vista_respuesta_pqr", "recomendacion", "id_orden='"+OrdenTrabajo+"' AND descripcion_tipo_recomendacion LIKE '%"+_cmbRtaPQR.getSelectedItem().toString()+"'"));	
        				Registro.put("cod_inconsistencia", "PQR1");
        				Registro.put("cuenta",CuentaCliente);
        				Registro.put("usuario_ins", CedulaUsuario);
        				Registro.put("tipo","A");        			
        				ActasSQL.InsertOrUpdateRegistro("amd_inconsistencias", Registro, "id_orden='"+OrdenTrabajo+"' AND cod_inconsistencia='PQR1'");		
        			}
        		}
        	default:
        		break;
		}  
		
	}
}