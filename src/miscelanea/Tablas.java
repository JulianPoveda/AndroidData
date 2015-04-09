package miscelanea;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Tablas {
	private Context 	_tablaCtx;
	private String[] 	_camposTabla;
	private int[]		_anchoColumnas;
	private String[]	_anchoTabla;
	private int 		_anchoBorde;
	private String 		_colorCabecera;
	private String 		_colorCuerpo;
	private String 		_colorBorde;
	private int 		_numColumnas;
	//private int 		_ancho;
	public  TableLayout _tabla;
	
	
	public Tablas(Context _ctx, String _campos, String _anchoTabla, int _anchoBorde, String _colorCabecera, String _colorCuerpo, String _colorBorde){
		this._tablaCtx 		= _ctx;
		this._camposTabla 	= _campos.replaceAll(" ","").split("\\,");
		this._anchoTabla	= _anchoTabla.split("\\,");
		this._anchoBorde	= _anchoBorde;
		this._colorCabecera = _colorCabecera;
		this._colorCuerpo	= _colorCuerpo;
		this._colorBorde	= _colorBorde;
		this._numColumnas 	= _camposTabla.length;
		this._anchoColumnas = new int[this._anchoTabla.length];
		for(int i = 0;i<this._anchoTabla.length;i++){
			this._anchoColumnas[i] = Integer.parseInt(this._anchoTabla[i]);
		}
	}
	
	
	/****Metodo para dibujar la informacion de una consulta en la tabla**/
    public TableLayout CuerpoTabla(ArrayList<ContentValues> _informacion){
    	ContentValues _registro = new ContentValues();
    	this._tabla 			= new TableLayout(this._tablaCtx);
    	this.CabeceraTabla();
    	
    	for(int i=0;i<_informacion.size();i++){
    		TableRow fila = new TableRow(this._tablaCtx);
    		_registro = _informacion.get(i);
    		
    		for(int j=0;j<_registro.size();j++){
    			RelativeLayout borde = new RelativeLayout(this._tablaCtx);
    			if(j==_registro.size()-1){
    				borde.setPadding(this._anchoBorde, this._anchoBorde, this._anchoBorde, 0);
    			}else{
    				borde.setPadding(this._anchoBorde,this._anchoBorde,0,0);
    			}
    			borde.setBackgroundColor(Color.parseColor(this._colorBorde));
    			TextView texto = new TextView(this._tablaCtx);
    			texto.setText(_registro.getAsString(this._camposTabla[j]));

    			texto.setHeight(30);
    			texto.setWidth(this._anchoColumnas[j]);
    			texto.setGravity(Gravity.CLIP_HORIZONTAL);
    			texto.setPadding(2, 2, 2, 2);
    			texto.setBackgroundColor(Color.parseColor(this._colorCuerpo));
    			borde.addView(texto);
    			fila.addView(borde);
    		}
    		this._tabla.addView(fila);    		
    	}
    	return this._tabla;   	
    }
	
	

   
	private void CabeceraTabla(){     
		if(this._numColumnas>0){
			TableRow fila = new TableRow(this._tablaCtx);      
			//int ancho=(_ancho/this._numColumnas)-(_anchoBorde+(_anchoBorde/(this._numColumnas)));
			//ancho--;
			
			for (int i = 0; i < this._numColumnas; i++) {
				RelativeLayout borde = new RelativeLayout(this._tablaCtx);
				if(i==this._numColumnas-1){
					borde.setPadding(_anchoBorde, _anchoBorde, _anchoBorde, _anchoBorde);
				}else{
					borde.setPadding(_anchoBorde,_anchoBorde,0,_anchoBorde);
				}
				borde.setBackgroundColor(Color.parseColor(_colorBorde));
	          
				TextView texto = new TextView(this._tablaCtx);    
				texto.setText(this._camposTabla[i].toUpperCase());
	
				texto.setWidth(this._anchoColumnas[i]);
				texto.setGravity(Gravity.CENTER_HORIZONTAL);
				texto.setPadding(2, 2, 2, 2);
				texto.setBackgroundColor(Color.parseColor(this._colorCabecera));
				borde.addView(texto);
				fila.addView(borde);
			}
			this._tabla.addView(fila);
		}else{
			TextView error= new TextView(this._tablaCtx);
			error.setText("Valores de columnas o filas deben ser mayor de 0");
			this._tabla.addView(error);
		}
	    //return this._tabla;
	}
}

