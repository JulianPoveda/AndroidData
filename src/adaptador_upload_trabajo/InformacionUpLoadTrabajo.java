package adaptador_upload_trabajo;

public class InformacionUpLoadTrabajo {
	protected String Solicitud;
	protected String Cuenta;
	protected String Nodo;
	protected String Estado;
	protected long Id;
	
	public InformacionUpLoadTrabajo(String solicitud, String cuenta, String nodo, String estado){
		super();
		this.Solicitud 	= solicitud;
		this.Cuenta		= cuenta;
		this.Nodo 		= nodo;
		this.Estado 	= estado;
	}
	
	
	public String getSolicitud(){
		return this.Solicitud;
	}
	
	public String getNodo(){
		return this.Nodo;
	}
	
	public String getCuenta(){
		return this.Cuenta;
	}
	
	public String getEstado(){
		return this.Estado;
	}
	
	//public String getCheck(){
		//331
		//cll 5c #29a
	//}
	
	public long getId(){
		return this.Id;
	}
	
	public void setSolicitud(String solicitud){
		this.Solicitud = solicitud;
	}
	
	public void setCuenta(String cuenta){
		this.Cuenta = cuenta;
	}
	
	public void setNodo(String nodo){
		this.Nodo = nodo;
	}
	
	public void setTipo(String estado){
		this.Estado = estado;
	}
	
	public void setId(long id){
		this.Id= id;
	}
}