package sistema;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.*;
import android.content.Context;
import android.widget.Toast;

public class Bluetooth {
	Context 			context;
	private String 		mydeviceaddress;
	private String 		mydevicename;
	BluetoothAdapter 	bluetooth = BluetoothAdapter.getDefaultAdapter();
	BluetoothDevice 	mmDevice= null;
	BluetoothSocket 	mmSocket;
	OutputStream 		mmOutputStream;
	//private UIHelper helper = new UIHelper(this);
	
	public Bluetooth(Context context){
		this.context = context;
	}
	
	//Funcion para detectar si el equipo tiene bluetooth
	public boolean ExistBluetooth(){
		boolean result = false;
		if(bluetooth != null){
			EnabledBluetoth();
			result = true;
		}
		return result;
	}
	
	
	//Funcion para detectar si el bluetooth esta encendido
	public void EnabledBluetoth(){
		if(!bluetooth.isEnabled()){
			bluetooth.enable();
		}
	}
	
	//Funcion para mostrar el nombre del dispositivo y la direccion
	public void GetOurDevice(){
		String status;
		if (ExistBluetooth()) {		
		    mydeviceaddress = bluetooth.getAddress();
		    mydevicename = bluetooth.getName();
		    status = mydevicename + " : " + mydeviceaddress;
		}else{
		    status = "Bluetooth is not Enabled.";
		}
		Toast.makeText(context, status, Toast.LENGTH_LONG).show();
	}
	
	
	//Funcion para mostrar el estado del bluetooth
	public void StatusBluetooth(){
		int state = bluetooth.getState();
		Toast.makeText(context, mydevicename + ":" + mydeviceaddress + " : " + state, Toast.LENGTH_LONG).show();
	}
	
	
	//Funcion para listar los dispositivos bluetooth asociados con el equipo
	public void ListDevice(){
		Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				Toast.makeText(context, "Equipo " +  device.getName() + " : " + device.getAddress(), Toast.LENGTH_LONG).show();
		    }
		}
	}
	
	
	
	//Funcion para realizar el intento de impresion
	public void IntentPrint(String Impresora, String txtvalue){
		try{
			byte[] buffer = txtvalue.getBytes("UTF-8");
			InitPrinter(Impresora);
			for(int i=0;i<=buffer.length-1;i++){
				mmOutputStream.write(buffer[i]);
				if((i%512)==0){
					Thread.sleep(100);
				}
			}
			mmOutputStream.close();
			mmSocket.close();
		}catch(Exception ex){
		}
	} 
	
	
	//Funcion para imprimir 
	private void InitPrinter(String Impresora){
		try{
			Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
			if(pairedDevices.size() > 0){
				for(BluetoothDevice device : pairedDevices){
					//modificar para capturar el nombre de la impresora directamente de la base de datos
					if(device.getName().equals(Impresora)){ 
						mmDevice = device;
    				  	break;
					}
				}
				if(mmDevice!=null){
					UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
					mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
					bluetooth.cancelDiscovery();
					if(mmDevice.getBondState()==12){	//Condicion para saber si esta apareada la impresora
						try{
							mmSocket.connect();
							if(mmSocket.isConnected()){
								mmOutputStream = mmSocket.getOutputStream();
							}
						}catch(Exception e){
							Toast.makeText(this.context, "Error al conectarse a la impresora.", Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(this.context, "No se encontro la impresora no esta sincronizada.", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(this.context, "No se encontro la impresora en la lista de dispositivos apareados.", Toast.LENGTH_LONG).show();
				}
				
			}else{
				Toast.makeText(this.context, "No se detectaron dispositivos sincronizados.", Toast.LENGTH_LONG).show();
			}
		}catch(Exception ex){
			Toast.makeText(this.context, "No se detecto bluetooth en el equipo.", Toast.LENGTH_LONG).show();
		}
	}
	
	
	public ArrayList<String> GetDeviceBluetooth(){
		ArrayList<String> _lstDevice= new ArrayList<String>();
		_lstDevice.add("");
		try{
			Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
			if(pairedDevices.size() > 0){
				for(BluetoothDevice device : pairedDevices){
					_lstDevice.add(device.getName());
				}
			}
		}catch(Exception e){
			
		}
		return _lstDevice;
	}
}


