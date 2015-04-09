package miscelanea;

import java.util.ArrayList;


public class ConvertTypes {
	
	public String[] ArrayListToArrayString(ArrayList<String> Lista){
		String[] strRetorno = new String[Lista.size()];
		strRetorno = Lista.toArray(strRetorno);
		return strRetorno;
	}
	
}