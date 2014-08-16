package sistema;

import miscelanea.SQLite;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPS implements LocationListener{
	private ContentValues _tempRegistro = new ContentValues();
	private SQLite  gpsSQL;
	private Context ctxGPS;
	
	private boolean _estadoGPS;
	private String  pdaGPS;
	private String  folderGPS;
	private String 	_latitudGPS;
	private String 	_longitudGPS;
	
	public GPS(Context ctx, String Equipo, String Folder){
		this.ctxGPS 	= ctx;
		this.pdaGPS		= Equipo;
		this.folderGPS	= Folder;
		
		this._estadoGPS		= false;
		this._latitudGPS	= "0.0";
		this._longitudGPS	= "0.0";
		gpsSQL = new SQLite(this.ctxGPS,this.folderGPS);	
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(!this._latitudGPS.equals(String.valueOf(location.getLatitude()))||!this._longitudGPS.equals(String.valueOf(location.getLongitude()))){
			this._latitudGPS = String.valueOf(location.getLatitude());
			this._longitudGPS= String.valueOf(location.getLongitude());
			
			this._tempRegistro.clear();
			this._tempRegistro.put("id_tablet", this.pdaGPS);
			this._tempRegistro.put("latitud", this._latitudGPS);
			this._tempRegistro.put("longitud",this._longitudGPS);
			gpsSQL.InsertRegistro("db_gps", this._tempRegistro);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onProviderEnabled(String provider) {
		this._estadoGPS = true;
	}

	@Override
	public void onProviderDisabled(String provider) {
		this._estadoGPS = false;
	}
	
	public boolean getEstadoGPS(){
		return this._estadoGPS;
	}

}
