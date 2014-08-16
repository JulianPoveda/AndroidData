package miscelanea;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class Archivos {
	private Context ctx;
	private String 	Directory;
	private int SizeBuffer;	
	
	FileInputStream fis;
	FileReader file;
	
	public Archivos(Context ctx, String CurrentDirectory, int BufferKbytes){
		this.ctx = ctx;
		this.Directory = CurrentDirectory;
		this.SizeBuffer = BufferKbytes;
		
		if(!ExistFolderOrFile(this.Directory)){		
			MakeDirectory();
		}
	}
	
	
	//Metodo para crear una carpeta en el directorio raiz
	public boolean MakeDirectory(){
		File f = new File(this.Directory);
        if(f.mkdir()){
        	Toast.makeText(this.ctx,"Directorio "+this.Directory+" correctamente.", Toast.LENGTH_SHORT).show();
            return true;
        }else{
        	return false;
        }
	}
	

	//Metodo para crear una carpeta en el directorio raiz
	public boolean MakeDirectory(String _new_directory){
		File f = new File(this.Directory+File.separator+_new_directory);
		if(f.mkdir()){
			Toast.makeText(this.ctx,"Directorio "+_new_directory+" correctamente.", Toast.LENGTH_SHORT).show();
			return true;
		}else{
			return false;
		}
	}
	
	
	//Metodo para comprobar la existencia de un directorio y/o carpeta
	public boolean ExistFolderOrFile(String Carpeta){
		File f = new File(Carpeta);
		return f.exists();
	}
	
	
	//Metodo para comprobar la existencia de un directorio y/o carpeta
	public boolean DeleteFile(String Archivo){
		File f = new File(Archivo);
		return f.delete();
	}
	
	
	//Metodo para comprobar la exitencia de memoria externa
	public boolean MemoryExt(){
		boolean valorRetorno = false;
		String estado = Environment.getExternalStorageState();
		if (estado.equals(Environment.MEDIA_MOUNTED)){
		    valorRetorno = true;			//Montada y disponible para lectura/escritura
		}else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
		    valorRetorno = false;			//Montada y disponible solo para lectura
		}
		else{
		    valorRetorno = false;			//No se encuentra montada la memoria SD
		}
		return valorRetorno;
	}
	
	
	//Metodo para saber las lineas de un archivo 
	public int TamanoArchivo(String NombreArchivo){
		int CountBfrArchivo = 0;
		try {
			file = new FileReader(NombreArchivo);
			BufferedReader BfrArchivo = new BufferedReader(file);
			while(BfrArchivo.readLine()!=null){
				CountBfrArchivo++;
			}
		} catch (FileNotFoundException e) {
			CountBfrArchivo = 0;
		}  catch (IOException e) {
			CountBfrArchivo = 0;
		}
		return CountBfrArchivo;
	}
	
	
	//Metodo para convertir el contenido de un archivo a un array 
	public ArrayList<String> FileToArrayString(String Archivo, boolean ruta_completa){
		File file;
		String queryString;
		String storageState = Environment.getExternalStorageState();
		ArrayList<String> InformacionFile = new ArrayList<String>();
		
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
		    if(ruta_completa){
		    	file = new File(Archivo);
		    }else{
		    	file = new File(this.Directory + File.separator +Archivo);
		    }
		    		 
		    BufferedReader inputReader2;
			try {
				inputReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				while ((queryString = inputReader2.readLine()) != null) {
			    	InformacionFile.add(queryString);
			    }
				file.delete();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return InformacionFile;
	}
	
	
	
	/*public boolean ExisteArchivo(String NombreArchivo){
		return true;
	}*/
	
	
	
	public boolean DoFile(String _rutaArchivo, String NombreArchivo, String InformacionArchivo){
		boolean Retorno = false;
		File file;
		try {
			if(!_rutaArchivo.isEmpty()){
				if(!ExistFolderOrFile(this.Directory + File.separator + _rutaArchivo)){
					MakeDirectory(_rutaArchivo);
				}
				file = new File(this.Directory + File.separator + _rutaArchivo + File.separator + NombreArchivo);
			}else{
				file = new File(this.Directory + File.separator + NombreArchivo);
			}
			
    		file.createNewFile();
    		if (file.exists()&&file.canWrite()){
    			FileWriter filewriter = new FileWriter(file,false);
    			filewriter.write(InformacionArchivo);
    			filewriter.close();
    			Retorno = true;
    		}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			Retorno = false;
		}
		return Retorno;
	}
	
	
	public byte[] FileToArrayBytes(String NombreArchivo){
		int len = 0;
		InputStream is 	= null;
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024 * this.SizeBuffer);
		byte[] buffer = new byte[1024*this.SizeBuffer];
		
		try{
			if (NombreArchivo != null) {
				is = new FileInputStream(NombreArchivo);
				try {
					while ((len = is.read(buffer)) >= 0) {
		    			os.write(buffer, 0, len);
					}
				} finally {
					is.close();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			try {
				throw new IOException("Unable to open R.raw.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return os.toByteArray();
	}
	
	
	
	public void ByteArrayToFile(byte[] data, String NombreArchivo) throws IOException{
		FileOutputStream out = new FileOutputStream(this.Directory + File.separator + NombreArchivo);
		out.write(data);
		out.close();
	}
	
	
	
	
	public boolean CrearArchivo(String NombreArchivo, String Encabezado, ArrayList<ArrayList<String>> Informacion){
		ArrayList<String> Registro = new ArrayList<String>();
		String CadenaArchivo = Encabezado;
		
		for(int i=0; i<Informacion.size(); i++){
			Registro = Informacion.get(i);
			for(int j=0; j<Registro.size(); j++){
				CadenaArchivo += Registro.get(j)+";";				
			}
			CadenaArchivo += "\n";
		}
		
		try {
			File file = new File(this.Directory + File.separator + NombreArchivo);
    		file.createNewFile();
    		if (file.exists()&&file.canWrite()){
    			FileWriter filewriter = new FileWriter(file,false);
    			filewriter.write(CadenaArchivo);
    			filewriter.close();
    		}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return true;
	}

}
