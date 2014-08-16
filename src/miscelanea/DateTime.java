package miscelanea;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTime {
	public String GetFecha(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
		String formattedDate = df1.format(c.getTime());
		return formattedDate;
	}
	
	public String GetHora(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df1 = new SimpleDateFormat("HH:mm:ss a");
		String formattedDate = df1.format(c.getTime());
		return formattedDate;
	}

	public String GetDateTimeHora(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String formattedDate = df1.format(c.getTime());
		return formattedDate;
	}

}
