package thread_timer;

import miscelanea.SQLite;
import ws_asynchronous.UpLoadActa;
import ws_asynchronous.UpLoadInformacionOrdenActual;
import ws_asynchronous.UpLoadUbicacion;
import android.content.Context;
import android.os.CountDownTimer;

public class Beacon extends CountDownTimer{
	private String 	FolderAplicacion;
	private Context	TemporizadorCtx;
	
	private SQLite	BeaconSQL; 

	public Beacon(Context _ctx, String _folder, long _millisInFuture, long _countDownInterval) {
		// TODO Auto-generated constructor stub
		super(_millisInFuture, _countDownInterval);
		this.TemporizadorCtx 	= _ctx;
		this.FolderAplicacion	= _folder;		
		this.BeaconSQL = new SQLite(this.TemporizadorCtx, this.FolderAplicacion);
	}

	
	@Override
	public void onTick(long millisUntilFinished) {
		/**Web Service para notificar al servidor el acta la cual se encuentra en ejecucion**/
		if(this.BeaconSQL.ExistRegistros("amd_ordenes_trabajo", "estado='E'")){
			new UpLoadInformacionOrdenActual(this.TemporizadorCtx, this.FolderAplicacion).execute();	
		}
		
		/**Web Service para subir al servidor las actas impresas**/
		new UpLoadActa(this.TemporizadorCtx, this.FolderAplicacion).execute();	
		
		/**Web service para subir al servidor los registros de posiciones GPS**/
		if(this.BeaconSQL.ExistRegistros("db_gps", "id_serial IS NOT NULL")){
			new UpLoadUbicacion(this.TemporizadorCtx, this.FolderAplicacion).execute();	
		}		
		
		
	}
	

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
	}
}
