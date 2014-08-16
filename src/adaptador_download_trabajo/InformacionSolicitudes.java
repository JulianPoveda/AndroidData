package adaptador_download_trabajo;

public class InformacionSolicitudes {
	protected String Solicitud;
	protected String Cuenta;
	protected String Direccion;
	protected String Estado;
	protected long Id;
	
	public InformacionSolicitudes(String solicitud, String cuenta, String direccion, String estado){
		super();
		this.Solicitud 	= solicitud;
		this.Cuenta		= cuenta;
		this.Direccion 	= direccion;
		this.Estado 	= estado;
	}
	
	
	public String getSolicitud(){
		return this.Solicitud;
	}
	
	public String getDireccion(){
		return this.Direccion;
	}
	
	public String getCuenta(){
		return this.Cuenta;
	}
	
	public String getEstado(){
		return this.Estado;
	}
	
	public long getId(){
		return this.Id;
	}
	
	public void setSolicitud(String solicitud){
		this.Solicitud = solicitud;
	}
	
	public void setCuenta(String cuenta){
		this.Cuenta = cuenta;
	}
	
	public void setDireccion(String direccion){
		this.Direccion = direccion;
	}
	
	public void setTipo(String estado){
		this.Estado = estado;
	}
	
	public void setId(long id){
		this.Id= id;
	}
}