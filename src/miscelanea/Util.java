package miscelanea;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {
	
	
	public boolean validacionCuenta(String _cuenta){
		boolean _retorno = false;
		if(_cuenta.length()<9){
			_retorno = false;
		}else{
			String _strLlave	= _cuenta.substring(0,6);
			String _strChequeo 	= _cuenta.substring(6);
			if(_strChequeo.equals(this.modulo10(_strLlave)+this.modulo11(_strLlave)+this.modulo10_3(_strLlave))){
				_retorno = true;
			}else{
				_retorno = false;
			}
		}	
		return _retorno;
	}
	
	
	private String modulo10(String _numero){
		String 	mi_numero = _numero;
		String  mi_resultado = "";
		int  	mi_digito;
		int 	mi_suma = 0;
		int 	mi_longitud = mi_numero.length();
		
		for(int i=1;i<=mi_longitud;i++){
			mi_digito = Integer.parseInt(mi_numero.substring(mi_longitud - i, mi_longitud - i + 1));
			
			if(i%2 == 0){
				mi_suma += mi_digito;
			}else{
				String 	mi_numero_parcial 	= String.valueOf(2*mi_digito);
				int 	mi_longitud_parcial	= mi_numero_parcial.length();
				
				for(int j=1;j<=mi_longitud_parcial;j++){
					mi_suma += Integer.parseInt(mi_numero_parcial.substring(j-1, j));
				}
			}
		}		
		mi_numero = String.valueOf(mi_suma);
		mi_resultado = mi_numero.substring(mi_numero.length() -1);
		
		if(!mi_resultado.equals("0")){
			mi_resultado = String.valueOf(10 - Integer.parseInt(mi_resultado));
		}
		return mi_resultado;
	}
	
	
	private String modulo11(String _numero){
		int Base 	= 11;
		int	Minimo	= 2;
		int Maximo 	= 7;
		
		String 	mi_numero 	= _numero;
		int		mi_longitud = mi_numero.length();
		int 	mi_suma 	= 0;
		int 	mi_factor 	= Minimo;
		int 	mi_resultado= 0;
		String	el_digito	= "";
		
		for(int i=1;i<=mi_longitud;i++){
			mi_suma += mi_factor * Integer.parseInt(mi_numero.substring(mi_longitud-i, mi_longitud-i+1));
			
			if(mi_factor == Maximo){
				mi_factor = Minimo;
			}else{
				mi_factor++;
			}
		}
		
		mi_resultado = Base - (mi_suma % Base);
		if(mi_resultado > 9){
			el_digito = "0";
		}else{
			el_digito = String.valueOf(mi_resultado);
		}
		return el_digito;		
	}
	
	
	
	private String modulo10_3(String _numero){
		String _retorno = "";
		int 	Factor_peso = 3;
		int 	mi_suma = 0;
		int 	mi_digito;
		String 	mi_numero = _numero;
		int 	mi_longitud = _numero.length();
		
		for(int i=1;i<=mi_longitud;i++){
			mi_digito = Integer.parseInt(mi_numero.substring(mi_longitud-i, mi_longitud-i+1));
			if(i%2 == 0){
				mi_suma += mi_digito;
			}else{
				mi_suma += Factor_peso * mi_digito;
			}
		}
		
		if(mi_suma%10 != 0){
			_retorno = String.valueOf(10 -(mi_suma%10));
		}else{
			_retorno = String.valueOf(mi_suma%10);
		}
		return _retorno;
	}
	
	
	
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	public String generarAleatorio(int _limite){
		int numero = 0;
		Random r1 = new Random(System.currentTimeMillis());
		numero = r1.nextInt(999999999)+1;
		
		StringBuilder builder = new StringBuilder(String.valueOf(numero));
		while (builder.length() < _limite) {
            builder.append('0');
        }
		return builder.toString().substring(0, _limite);
	}
	
	
	
	public ArrayList<String> getRangeAdapter(int _minimo, int _maximo, int _incremento){
		ArrayList<String> _rango = new ArrayList<String>();
		for(int i = _minimo; i<= _maximo;i+=_incremento){
			_rango.add(String.valueOf(i));
		}	
		return _rango;
	}
	
	
	
	public void ArrayContentValuesToString(ArrayList<String> _strInformacion, ArrayList<ContentValues> _informacion, String _campo){
		_strInformacion.clear();
		ContentValues _registro = new ContentValues();
		
		for(int i=0; i<_informacion.size();i++){
			_registro.clear();
			_registro = _informacion.get(i);
			_strInformacion.add(_registro.getAsString(_campo).toString());
		}
	}
	
	public void ArrayContentValuesToString(ArrayList<String> _strInformacion, ArrayList<ContentValues> _informacion, String _campo, String _split, int _item){
		String _tempStr[];
		_strInformacion.clear();
		ContentValues _registro = new ContentValues();
		
		for(int i=0; i<_informacion.size();i++){
			_registro.clear();
			_registro = _informacion.get(i);
			_tempStr = _registro.getAsString(_campo).toString().split("\\"+_split);
			_strInformacion.add(_tempStr[_item]);
		}
	}
	
	public ArrayList<ContentValues> ArrContentFromJSON(String Cadena){
		ArrayList<ContentValues> Query = new ArrayList<ContentValues>();
		String Tabla 	= null;
		String Registro = null;
		String Campos[] = null;
		String KeyValue[] = null;
		
		int InicioCadena=0;
		int FinCadena=0;
		if(!Cadena.isEmpty()){
			InicioCadena = Cadena.indexOf("[");
			FinCadena = Cadena.indexOf("]");
			
			if((InicioCadena!=-1)&&(FinCadena!=-1)){
				Tabla = Cadena.substring(InicioCadena+1,FinCadena);
			}
			
			while(Tabla.length()>0){	
				ContentValues Diccionario = new ContentValues();
				InicioCadena= Tabla.indexOf("{");
				FinCadena 	= Tabla.indexOf("}");		
				
				Registro = Tabla.substring(InicioCadena+1, FinCadena).replaceAll("\"", "");
				Campos = Registro.split(",");
				KeyValue = null;
				Diccionario.clear();
				for(int i=0;i<Campos.length;i++){
					KeyValue= Campos[i].split(":");
					Diccionario.put(KeyValue[0],KeyValue[1]);	
				}
				Tabla = Tabla.substring(FinCadena+1);
				Query.add(Diccionario);
			}
		}
		return Query;
	}
	
	
	public int FindStringIntoArrayString(String _aguja, String _pajar, String _separador){
		int _retorno = -1;
		try{
			String[] _arrayString = _pajar.split("\\"+_separador);
			for(int i=0; i<_arrayString.length; i++){
				if(_arrayString[i].equals(_aguja)){
					_retorno = i;
					break;
				}
			}	
		}catch(Exception e){
			
		}
		return _retorno;
	}
	
	public String RemoveStringIntoArrayString(String _aguja, String _pajar, String _separador){
		String _retorno = "";
		try{
			String[] _arrayString = _pajar.split("\\"+_separador);
			for(int i=0; i<_arrayString.length; i++){
				if(!_arrayString[i].equals(_aguja)){
					_retorno = _retorno+" "+_arrayString[i];
				}
			}	
		}catch(Exception e){
			
		}
		return _retorno;
	}
}
