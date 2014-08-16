/*********************************************************************************************************
 * Fecha: 	10-06-2014
 * Version:	1.0
 * Modifcaciones:	Se crea esta clase con el fin de poder cargar el archivo de parametros de forma local
 * 					y poder visualizar el estado de la carg del archivo.
*********************************************************************************************************/

package ws_asynchronous;

import java.util.ArrayList;
import miscelanea.Archivos;
import miscelanea.SQLite;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class DownLoadParametros extends AsyncTask<String, Integer, Integer>{ //doInBakGround, Progress, onPostExecute
	
	/**Instancias a clases**/
	private Archivos 	ArchParametros;
	private SQLite 		ParametrosSQL;
	
	/**Variables Locales**/
	private Context 			LoadParametrosContext;
	private String 				DirectorioLoadParametros;
	private ArrayList<String> InformacionArchivoLocal = new ArrayList<String>();
	
		
	//Variables con la informacion del web service
	ProgressDialog 	_pDialog;
	
		
	//Contructor de la clase
	public DownLoadParametros(Context context, String Directorio){
		this.LoadParametrosContext 		= context;
		this.DirectorioLoadParametros 	= Directorio;		
		this.ParametrosSQL		= new SQLite(this.LoadParametrosContext, this.DirectorioLoadParametros);
		this.ArchParametros		= new Archivos(this.LoadParametrosContext,this.DirectorioLoadParametros,10);
	}


	//Operaciones antes de realizar la conexion con el servidor
	protected void onPreExecute(){		
		Toast.makeText(this.LoadParametrosContext,"Leyendo archivo de parametros...", Toast.LENGTH_SHORT).show();	
		_pDialog = new ProgressDialog(this.LoadParametrosContext);
        _pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _pDialog.setMessage("Ejecutando operaciones...");
        _pDialog.setCancelable(false);
        _pDialog.setProgress(0);
        _pDialog.setMax(100);
        _pDialog.show();
	}
	

	
	//Conexion con el servidor en busca de actualizaciones de trabajo programado
	@Override
	protected Integer doInBackground(String... params) {
		int _retorno = 0;		
		InformacionArchivoLocal = ArchParametros.FileToArrayString(params[0],true);		
		try{
			for(int i=0; i<InformacionArchivoLocal.size();i++){
				ParametrosSQL.EjecutarSQL(InformacionArchivoLocal.get(i));
				publishProgress((int)((i+1)*100/this.InformacionArchivoLocal.size()));
			}
			_retorno = 1;			
		}catch(Exception ex){
			_retorno = -3;
		}		
		return _retorno;
	}
	
	
	
	//Operaciones despues de finalizar la conexion con el servidor
	@Override
	protected void onPostExecute(Integer rta) {
		if(rta==1){
			Toast.makeText(this.LoadParametrosContext,"Carga de parametros finalizada.", Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(this.LoadParametrosContext,"Error cargando los parametros.", Toast.LENGTH_SHORT).show();
		}
		_pDialog.dismiss();
	}	
	
	
	@Override
    protected void onProgressUpdate(Integer... values) {
        int progreso = values[0].intValue();
        _pDialog.setProgress(progreso);
    }
}

