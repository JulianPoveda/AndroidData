package sistema;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Network {
	//private static int  _ConexionFallida= 0;
	//private static int 	_ConexionDatos 	= 1;
	//private static int  _ConexionWifi	= 2;
	
	private static Context				_netContext;
	private static ConnectivityManager 	_netMannager;
//private static State 				_internet_movil;
	//private static State 				_internet_wifi;
	
	
	public Network(Context _context){
		_netContext 	= _context;
	}
	
	
	public static boolean chkNetwork(){
		boolean _retorno = false; 
		_netMannager 	= (ConnectivityManager) _netContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i  = _netMannager.getActiveNetworkInfo();
		
		if((i==null)||(!i.isConnected())||(!i.isAvailable())){
			_retorno = false;
		}else{
			_retorno = true;
		}
		return _retorno;
	}
	
	
	public boolean chkStatusNetWork() {
		boolean _retorno = false;
	    final ConnectivityManager connMgr = (ConnectivityManager) _netContext
	            .getSystemService(Context.CONNECTIVITY_SERVICE);
	    final android.net.NetworkInfo wifi = connMgr
	            .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    final android.net.NetworkInfo mobile = connMgr
	            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    if (wifi.isAvailable()) {
	        _retorno = true;
	    	//Toast.makeText(_netContext, "Wifi", Toast.LENGTH_LONG).show();
	    } else if (mobile.isAvailable()) {
	        _retorno = true;
	    	//Toast.makeText(_netContext, "Mobile 3G ", Toast.LENGTH_LONG).show();
	    } else {
	        _retorno = false;
	    	//Toast.makeText(_netContext, "No Network ", Toast.LENGTH_LONG).show();
	    }
	    return _retorno;
	}
	
	
	/*public void getTypeNetwork(){
		
		
	}
	
	public boolean chkNetWifi(){
		boolean _retorno = false;
		this._internet_wifi = _netMannager.getNetworkInfo(1).getState();
		if(this._internet_wifi==NetworkInfo.State.CONNECTED|| this._internet_wifi==NetworkInfo.State.CONNECTING){
			_retorno = true;
		}
		return _retorno; 
	}
	
	
	
	public boolean chkNetDatos(){
		boolean _valorRetorno = false; 
		this._internet_movil= _netMannager.getNetworkInfo(0).getState();		
		if(this._internet_movil==NetworkInfo.State.CONNECTED|| this._internet_movil==NetworkInfo.State.CONNECTING){
			_valorRetorno = true;
		}else{
			_valorRetorno = false;
		}
		return _valorRetorno;
	}
	
	
	
	public boolean chkNetwork(){
		boolean _valorRetorno = false; 
		this._internet_movil= _netMannager.getNetworkInfo(0).getState();
		this._internet_wifi = _netMannager.getNetworkInfo(1).getState();
		
		if(this._internet_movil==NetworkInfo.State.CONNECTED|| this._internet_movil==NetworkInfo.State.CONNECTING){
			_valorRetorno = true;
		}else if(this._internet_wifi==NetworkInfo.State.CONNECTED|| this._internet_wifi==NetworkInfo.State.CONNECTING){
			_valorRetorno = true;
		}else{
			_valorRetorno = false;
		}
		return _valorRetorno;
	}*/
}
