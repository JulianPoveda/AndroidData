package adaptador_upload_trabajo;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadData implements Parcelable {
	
	private String orden;
	private String nodo;
	private String cuenta;
	private String estado;
	private boolean leido;
	
    public UploadData (String strO,String strN,String strC,String strE, boolean checked){
    	this.orden=strO;
    	this.nodo=strN;
    	this.cuenta=strC;
    	this.estado=strE;
    	this.leido=checked;
    }
    
    public UploadData (Parcel in){
    	this.orden=in.readString();
    	this.nodo=in.readString();
    	this.cuenta=in.readString();
    	this.estado=in.readString();
    	this.leido = in.readInt() == 1 ? true:false;
    }
	
    public void setOrden(String ordenv) {
		this.orden=ordenv;
	}

	public String getOrden() {
		return orden;
	}
	
	public void setNodo(String nodov){
		this.nodo=nodov;
	}
	
	public String getNodo(){
		return nodo;
	}
    
	public void setCuenta(String cuentav){
		this.cuenta=cuentav;
	}
	
	public String getCuenta(){
	return cuenta;	
	}
	
	public void setEstado(String estadov){
		this.estado=estadov;
	}
	
	public String getEstado(){
		return estado;
	}
	
	public void setChecked(boolean value) {
		this.leido = value;
	}

	public boolean getChecked() {
		return leido;
	}

	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getOrden());
		dest.writeString(getNodo());
		dest.writeString(getEstado());
		dest.writeString(getCuenta());
		dest.writeInt(getChecked() ? 1 : 0);
	}
	
	public static final Parcelable.Creator<UploadData> CREATOR = new Parcelable.Creator<UploadData>() {
		public UploadData createFromParcel(Parcel in) {
			return new UploadData(in);
		}

		public UploadData[] newArray(int size) {
			return new UploadData[size];
		}
	};


}
