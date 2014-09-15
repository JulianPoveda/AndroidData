package form_amdata;

import java.io.File;

import dialogos.DialogInformacion;
import dialogos.Modal_FileExplorer;
import sistema.GPS;
import sistema.Network;
import sypelc.androidamdata.R;
import thread_timer.Beacon;
import ws_asynchronous.DownLoadTrabajo;
import ws_asynchronous.DownLoadParametros;
import miscelanea.SQLite;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Form_Loggin extends Activity implements OnClickListener{
	private static int  GPS_ACTIVADO 	= 1;
	private static int 	ARCHIVO_LOCAL 	= 555;

	Intent DialogInformacion; 	
	
	private Network		Conexion;	
	private SQLite 		SQL; 		
	private GPS 		milocListener;
	
	private boolean LogginUser  	= false; 
	private String 	CedulaLoggin	= "";
	private String	NombreLoggin	= "";
	private String	NivelLoggin		= "";
	private String 	FolderAplicacion= "";	
	private String 	ArchivoLocal 	= "";
	private int 	PDA 			= 0;
	
	Beacon	envioActas;
	TextView 		_lblPDA, _lblVersion;
	EditText 		_txtUsuario, _txtContrasena;
	Button 			_btnLoggin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loggin);
		
		DialogInformacion	= new Intent(this,DialogInformacion.class);
		
		if(getIntent().getExtras() != null){
			Bundle bundle = getIntent().getExtras();
			this.FolderAplicacion= bundle.getString("FolderAplicacion");		
			this.NombreLoggin	= bundle.getString("NombreUsuario");
			this.CedulaLoggin	= bundle.getString("CedulaUsuario");
			this.NivelLoggin	= bundle.getString("NivelUsuario");
			
			_txtUsuario.setText(this.NombreLoggin);
			_txtContrasena.setText(SQL.StrSelectShieldWhere("amd_usuarios", "password", "login='"+this.NombreLoggin+"'"));
			_txtUsuario.setEnabled(false);
			_txtContrasena.setEnabled(false);
			_btnLoggin.setEnabled(false);
			this.LogginUser = true;
			invalidateOptionsMenu();     		
		}else{
			this.FolderAplicacion = Environment.getExternalStorageDirectory() + File.separator + "EMSA";
		}		
		
		Conexion	= new Network(this);
		SQL 		= new SQLite(this, this.FolderAplicacion);
		
		/********************************************Funciones para la captura de datos GPS****************************************/
		this.milocListener = new GPS(this, SQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'"), this.FolderAplicacion);		
		LocationManager milocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		milocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, milocListener);
		
		_lblPDA 		= (TextView) findViewById(R.id.LblPDA);
		_lblVersion 	= (TextView) findViewById(R.id.LblVersion);
		_btnLoggin 		= (Button) findViewById(R.id.BtnIngresar);;
		_txtUsuario 	= (EditText) findViewById(R.id.TxtUsuario);
		_txtContrasena 	= (EditText) findViewById(R.id.TxtContrasena);
		
		this.PDA = SQL.IntSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'");		
		_lblPDA.setText("PDA " + this.PDA);
        _lblVersion.setText("Version " +SQL.StrSelectShieldWhere("db_parametros", "valor", "item='version'"));
       
        /***Instrucciones para iniciar el beacon el cual es el encargado de conectarse repetitivamente al servidor***/
        envioActas 	= new Beacon(this, this.FolderAplicacion, 86400000, 60000);
        envioActas.start();
        
        /*if(!milocListener.getEstadoGPS()){
        	DialogInformacion.putExtra("informacion", "No se puede iniciar la aplicacion sin tener activado el GPS");
        	startActivityForResult(DialogInformacion, GPS_ACTIVADO);
        }*/
        
        _btnLoggin.setOnClickListener(this);
	}
	
	//Control de eventos al momento de seleccionar una opcion del menu del formulario
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {				
			case R.id.Recepcion:
				if(Conexion.chkStatusNetWork()){
					new DownLoadTrabajo(this, this.FolderAplicacion).execute("1",this.PDA+"");
				}else{
					Toast.makeText(this,"No hay conexion a internet.", Toast.LENGTH_LONG).show();	
				}
				return true;
			
			case R.id.RecepcionArchivo:
				Intent file_explorer = new Intent(this,Modal_FileExplorer.class);
		        startActivityForResult(file_explorer, ARCHIVO_LOCAL);
				return true;
				
			case R.id.OrdenesTrabajo:
				Intent k;
				k = new Intent(this, Form_Solicitudes.class);
				k.putExtra("NombreUsuario", NombreLoggin);
				k.putExtra("CedulaUsuario", CedulaLoggin);
				k.putExtra("NivelUsuario", NivelLoggin);
				k.putExtra("FolderAplicacion", this.FolderAplicacion);
				startActivity(k);
				return true;
				
			case R.id.ParametrosSistema:
				Intent parametros;
				parametros = new Intent(this, Parametros.class);
				parametros.putExtra("NombreUsuario", NombreLoggin);
				parametros.putExtra("CedulaUsuario", CedulaLoggin);
				parametros.putExtra("NivelUsuario", NivelLoggin);
				parametros.putExtra("FolderAplicacion", this.FolderAplicacion);
				startActivity(parametros);
				return true;
			
			case R.id.NuevoServicio:
				Intent servicioNuevo;
				servicioNuevo = new Intent(this, Form_ServicioNuevo.class);
				servicioNuevo.putExtra("NombreUsuario", NombreLoggin);
				servicioNuevo.putExtra("CedulaUsuario", CedulaLoggin);
				servicioNuevo.putExtra("NivelUsuario", NivelLoggin);
				servicioNuevo.putExtra("FolderAplicacion", this.FolderAplicacion);
				startActivity(servicioNuevo);
				return true;					
				
			case R.id.Autogestion:
				Intent Autogestion;
				Autogestion = new Intent(this, Form_Autogestion.class);
				Autogestion.putExtra("NombreUsuario", NombreLoggin);
				Autogestion.putExtra("CedulaUsuario", CedulaLoggin);
				Autogestion.putExtra("NivelUsuario", NivelLoggin);
				Autogestion.putExtra("FolderAplicacion", this.FolderAplicacion);
				startActivity(Autogestion);
				return true;	
			
			
			case R.id.SalirSistema:
				this.finish();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == ARCHIVO_LOCAL) {
			if (data.hasExtra("archivo_seleccionado")) {
				ArchivoLocal = data.getExtras().getString("archivo_seleccionado");
				if(ArchivoLocal!=null){
					new DownLoadParametros(this, this.FolderAplicacion).execute(ArchivoLocal);
				}				
			}
		}else if(resultCode == RESULT_OK && requestCode == GPS_ACTIVADO) {
			finish();
		}
    }
	
	
	private void ValidarUsuario(){
    	if(SQL.ExistRegistros("amd_usuarios", "login='"+ _txtUsuario.getText()+ "' AND password='"+ _txtContrasena.getText() +"'")){
			NombreLoggin	= _txtUsuario.getText().toString();
    		CedulaLoggin	= SQL.StrSelectShieldWhere("amd_usuarios", "documento", "login='"+_txtUsuario.getText()+"'");
    		NivelLoggin 	= SQL.StrSelectShieldWhere("amd_usuarios", "perfil", "login='"+_txtUsuario.getText()+"'");
			if(NivelLoggin.equals("U")){
				Toast.makeText(getApplicationContext(),"Acceso Concedido, inicio de sesion como tecnico.", Toast.LENGTH_SHORT).show();
			}else if(NivelLoggin.equals("A")){
				Toast.makeText(getApplicationContext(),"Acceso Concedido, inicio de sesion como administrador.", Toast.LENGTH_SHORT).show();	
			}
			_btnLoggin.setEnabled(false);
			_txtUsuario.setEnabled(false);
			_txtContrasena.setEnabled(false);
			LogginUser = true;
			invalidateOptionsMenu(); 
		}else{
			Toast.makeText(getApplicationContext(),"Acceso Denegado", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu) {
		 if(LogginUser){
			 menu.findItem(R.id.OrdenesTrabajo).setEnabled(true);
			 menu.findItem(R.id.ImpresionPrueba).setEnabled(true);
			 menu.findItem(R.id.Servidor).setEnabled(true);
			 menu.findItem(R.id.Bodega).setEnabled(true);
			 menu.findItem(R.id.Sistema).setEnabled(true);
			 menu.findItem(R.id.Autogestion).setEnabled(true);
			 menu.findItem(R.id.NuevoServicio).setEnabled(true);
		 }else{
			 menu.findItem(R.id.OrdenesTrabajo).setEnabled(false);
			 menu.findItem(R.id.ImpresionPrueba).setEnabled(false);
			 menu.findItem(R.id.Servidor).setEnabled(false);
			 menu.findItem(R.id.Bodega).setEnabled(false);
			 menu.findItem(R.id.Sistema).setEnabled(false);
			 menu.findItem(R.id.Autogestion).setEnabled(false);
			 menu.findItem(R.id.NuevoServicio).setEnabled(false);
		 }    	
		 return true;  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_loggin, menu);
		return true;
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.BtnIngresar:
				ValidarUsuario();
				break;
		}
	}
}
