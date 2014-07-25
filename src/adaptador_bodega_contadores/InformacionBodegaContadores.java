package adaptador_bodega_contadores;


public class InformacionBodegaContadores {
	protected String Marca;
	protected String Serie;
	protected String Lectura;
	protected String Tipo;
	protected long Id;
	
	public InformacionBodegaContadores(String _marca, String _serie, String _lectura, String _tipo){
		super();
		this.Marca 		= _marca;
		this.Serie		= _serie;
		this.Lectura 	= _lectura;
		this.Tipo 		= _tipo;
	}
		
		
	public String getMarca(){
		return this.Marca;
	}
		
	public String getSerie(){
		return this.Serie;
	}
		
	public String getLectura(){
		return this.Lectura;
	}
		
	public String getTipo(){
		return this.Tipo;
	}
	
	public long getId(){
		return this.Id;
	}
		
	public void setMarca(String _marca){
		this.Marca = _marca;
	}
		
	public void setSerie(String _serie){
		this.Serie = _serie;
	}
		
	public void setLectura(String _lectura){
		this.Lectura = _lectura;
	}
		
	public void setTipo(String _tipo){
		this.Tipo = _tipo;
	}

	public void setId(long id){
		this.Id= id;
	}
}