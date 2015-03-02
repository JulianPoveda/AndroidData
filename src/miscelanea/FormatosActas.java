package miscelanea;


import impresora.zebra_QL420;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import sistema.Bluetooth;
import miscelanea.SQLite;
import android.content.ContentValues;
import android.content.Context;
//import android.widget.Toast;
import android.widget.Toast;
	
public class FormatosActas {
	private Context 					context;
	private String 						_folderAplicacion;
	private ArrayList<ContentValues> 	_infTabla		= new ArrayList<ContentValues>();
	private ArrayList<ContentValues> 	_infTabla1		= new ArrayList<ContentValues>();
	private ArrayList<String>			_infoCodigoQR	= new ArrayList<String>();
	private ArrayList<String>			_infoCodigoQRR	= new ArrayList<String>();
	private ArrayList<String>			_materialInstalado	= new ArrayList<String>();
	private ArrayList<String>			_materialRetirado	= new ArrayList<String>();
	private ContentValues 				_infRegistro1	= new ContentValues();
	private ContentValues				_infRegistro2	= new ContentValues();
	private ContentValues				_infRegistro3	= new ContentValues();
	
	private String 						_tempStr1[];
	private String 						_tempStr2[];
	
	private String						_variable1;
	//private String						_NombreTecnico;
	//private String 						_CedulaTecnico;
	
	private Archivos 			ImpArchivos;
	private Bluetooth 			MnBt;
	private SQLite 				ImpSQL;
	private DateTime 			DT;
	private zebra_QL420			FcnZebra;
	
	private String Impresora = null;
	private boolean _copiaSistema;
	   
    DecimalFormat decimales = new DecimalFormat("0.000"); 
	
	public FormatosActas(Context context, String FolderAplicacion, boolean _copiaSistema){
		this.context = context;
		this._folderAplicacion 	= FolderAplicacion;
		this._copiaSistema 		= _copiaSistema;
		
		ImpSQL = new SQLite(this.context,this._folderAplicacion);
		this.ImpArchivos= new Archivos(this.context,this._folderAplicacion, 10);
		this.Impresora 	= ImpSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='BLUETOOTH'");
	    
		MnBt 	= new Bluetooth(this.context);
		ImpSQL 	= new SQLite(this.context, this._folderAplicacion);
		DT 		= new DateTime();
		FcnZebra= new zebra_QL420(800, 40, 10, 10, 100, _copiaSistema);
	}
	
	
	/********************************************************************************************************************
	 * 
	 * @param ordenTrabajo
	 * @param tipoImpresion
	 * @param copiaImpresion
	 * @param CedulaTecnico
	 *******************************************************************************************************************/
	public void FormatoVerificacion(String ordenTrabajo, String cuentaCliente, String tipoImpresion, int copiaImpresion, String CedulaTecnico){	
		FcnZebra.clearInformacion();
		FcnZebra.WrTitulo("ELECTRIFICADORA DEL META E.S.P.", 0, 1);
		FcnZebra.DrawImage("IMAGES.PCX", 30, 25);
		FcnZebra.WrTitulo("ACTA DE REVISION E INSTALACION RUTINARIA", 0, 3);
		 
		 
		 
		 /********************************Validaciones necesarias para el encabezado del acta**************************************/
		FcnZebra.WrLabel("N.          ", ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "num_acta", "id_orden='"+ordenTrabajo+"'"), 200, 0, 1.2);
		if(Double.parseDouble(ordenTrabajo) >= 0){
			if(ordenTrabajo.length()>6){
				FcnZebra.WrLabel("Solicitud:   ", ordenTrabajo, 200, 0, 1.2);	
			}else{
				FcnZebra.WrLabel("Revision:    ", ordenTrabajo,200, 0, 1.2);	
			}
		}else{
			FcnZebra.WrLabel("Orden:   ","____________________", 200, 0, 1.2);
		}	
		
		
		if(Double.parseDouble(cuentaCliente)>=0){
			FcnZebra.WrLabel("Codigo:     ", ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "cuenta", "id_orden='"+ordenTrabajo+"'"), 200, 0, 1.2);	
		}else{
			FcnZebra.WrLabel("Codigo:     ", "____________________", 200, 0, 1.2);			
		}
			
		this._infRegistro1 = ImpSQL.SelectDataRegistro("vista_ordenes_trabajo", "municipio", "id_orden='"+ordenTrabajo+"'");
		FcnZebra.WrLabel("", this._infRegistro1.getAsString("municipio"), 300, 0, 1.2);
		FcnZebra.WrLabel("Contratista:","SYPELC LTDA", 200, 0, 1.2);
		
		this._infRegistro1.clear();
		this._infRegistro2.clear();
		this._infRegistro1 = ImpSQL.SelectDataRegistro("vista_actas", "dia,mes,anno,hora,propietario,nombre_enterado,cedula_enterado,tipo_enterado,cedula_testigo,nombre_testigo", "id_orden='"+ordenTrabajo+"'");
		this._infRegistro2 = ImpSQL.SelectDataRegistro("amd_impresiones_inf", "uso_derecho", "id_orden='"+ordenTrabajo+"'");
		FcnZebra.JustInformation("A los "+this._infRegistro1.getAsString("dia")+" dias del mes "+this._infRegistro1.getAsString("mes")+" de "+this._infRegistro1.getAsString("anno")+", siendo las "+this._infRegistro1.getAsString("hora")+", se hacen presentes en el inmueble de: "+this._infRegistro1.getAsString("propietario")+" los representantes de EMSA E.S.P. "+this.ImpSQL.StrSelectShieldWhere("amd_param_sistema","valor", "codigo='NOM_TECNICO'")+" con Cod/C.C: "+CedulaTecnico+" y en presencia del senor(a): "+this._infRegistro1.getAsString("nombre_enterado")+" con cedula "+this._infRegistro1.getAsString("cedula_enterado")+" en calidad de "+this._infRegistro1.getAsString("tipo_enterado")+", con el fin de efectuar una revision de los equipos de medida e instalaciones electricas del inmueble con el codigo indicado. Habiendose identificado los empleados/contratistas informan al usuario que de acuerdo al contrato de servicios publicos con condiciones uniformes vigente su derecho a solicitar asesoria y/o participacion de un tecnico particular, o de cualquier persona para que sirva de testigo en el proceso de revision. Sin embargo, si transcurre un plazo de 15 minutos sin hacerse presente se hara la revision sin su presencia, el cliente/usuario hace uso de su derecho:("+this._infRegistro2.getAsString("uso_derecho")+") Transcurridos 15 minutos , procede a hacer la revision, con los siguientes resultados:", 10, 2, 1);
		 
		/***************************************************************Datos del suscriptor****************************************************/
		FcnZebra.WrSubTitulo("DATOS GENERALES DEL SUSCRIPTOR",10,1,1);
		FcnZebra.WrLabel("USUARIO O SUSCRIPTOR: ", this._infRegistro1.getAsString("propietario"),10,0,1);
		this._infRegistro1 = ImpSQL.SelectDataRegistro("vista_ordenes_trabajo", "id_nodo,estrato,ciclo,carga_instalada,clase_servicio,direccion,municipio,cuenta,clase_servicio,carga_contratada,carga_instalada", "id_orden='"+ordenTrabajo+"'");
		FcnZebra.WrLabel("DIRECCION: ", this._infRegistro1.getAsString("direccion"),10,0,1); 
		FcnZebra.WrLabel("MUNICIPIO: ", this._infRegistro1.getAsString("municipio"),10,0,1);
		if(Double.parseDouble(this._infRegistro1.getAsString("cuenta"))<=0){
			this._infRegistro3 = ImpSQL.SelectDataRegistro("amd_servicio_nuevo", "cuenta_vecina1", "id_orden='"+ordenTrabajo+"'");
			FcnZebra.WrLabel("CUENTA REF: ", this._infRegistro3.getAsString("cuenta_vecina1"),10,0,0);
		}
		else FcnZebra.WrLabel("CUENTA: ", this._infRegistro1.getAsString("cuenta"),10,0,0);
		FcnZebra.WrLabel("SERVICIO: ",this._infRegistro1.getAsString("clase_servicio"),300,0,1);
		if(this._infRegistro1.getAsString("carga_contratada") == null){
			FcnZebra.WrLabel("CARGA CONTRATADA: ","",10,0,0);
		}else{
			FcnZebra.WrLabel("CARGA CONTRATADA: ",this._infRegistro1.getAsString("carga_contratada"),10,0,0);
		}
		
		if(ImpSQL.ExistRegistros("amd_contador_cliente_orden", "cuenta='"+ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "cuenta", "id_orden='"+ordenTrabajo+"'")+"' AND desinstalado IN (0,1)")){
			this._infRegistro2 = ImpSQL.SelectDataRegistro("amd_contador_cliente_orden", "marca,serie", "cuenta='"+ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "cuenta", "id_orden='"+ordenTrabajo+"'")+"' AND desinstalado IN (0,1)");		
			if((this._infRegistro2.getAsString("marca").equals("SIN_SERVICIO"))||(this._infRegistro2.getAsString("serie").equals("-1"))||(this._infRegistro2.getAsString("serie").equals("0"))){
				FcnZebra.WrLabel("SERIE: ","",300,0,0);
				FcnZebra.WrLabel("MARCA: ","",600,0,0);
			}else{
				FcnZebra.WrLabel("SERIE: ",this._infRegistro2.getAsString("serie"),300,0,0);
				FcnZebra.WrLabel("MARCA: ",this._infRegistro2.getAsString("marca"),600,0,0);		
			}
		}
		
		this._infRegistro2.clear();
		this._infRegistro2=ImpSQL.SelectDataRegistro("vista_ordenes_trabajo", "carga_instalada", "id_orden='"+ordenTrabajo+"'");
		
		if(this._infRegistro2.getAsString("carga_instalada") == null){
			FcnZebra.WrLabel("CARGA INSTALADA: "," ",10,1,0);
		}else{
			FcnZebra.WrLabel("CARGA INSTALADA: ",this._infRegistro1.getAsString("carga_instalada"),10,1,0);
		}
		
		if(this._infRegistro1.getAsString("ciclo") == null){
			FcnZebra.WrLabel("CICLO: ","",300,0,0);
		}else{
			FcnZebra.WrLabel("CICLO: ",this._infRegistro1.getAsString("ciclo"),300,0,0);
		}
		
		FcnZebra.WrLabel("ESTRATO: ",this._infRegistro1.getAsString("estrato"),450,0,0);
		FcnZebra.WrLabel("NODO: ",this._infRegistro1.getAsString("id_nodo"),600,0,2);
		
		
		 
		/**************************************************Datos del suscriptor y equipo de medida********************************************/
		FcnZebra.WrSubTitulo("DATOS DEL SUSCRIPTOR Y EQUIPO DE MEDIDA",10,1,1);		
		FcnZebra.WrLabel("Ubicacion Medidor:", ImpSQL.StrSelectShieldWhere("amd_impresiones_inf", "ubicacion", "id_orden='"+ordenTrabajo+"'"), 10, 0, 1);
				
		if(ImpSQL.ExistRegistros("imp_equipo_medida", "id_orden='"+ordenTrabajo+"'")){
			this._infTabla= ImpSQL.SelectData("imp_equipo_medida", "tipo, marca, serie, lectura, fecha_ins, conexion", "id_orden='"+ordenTrabajo+"' AND tipo<>'NM'");
			for(int i=0;i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);				
				if(this._infRegistro1.getAsString("tipo").equals("SD")){
					FcnZebra.WrLabel("SERVICIO DIRECTO","Se Deja Instalado Servicio Directo.", 10, 0 ,1);				
				}else if((this._infRegistro1.getAsString("tipo").equals("R"))&&(this._infRegistro1.getAsString("serie").equals("-1"))){
					FcnZebra.WrLabel("RETIRADO","Se Encuentra Predio Sin Servicio.", 10, 0 ,1);				
				}else if((this._infRegistro1.getAsString("tipo").equals("R"))&&(this._infRegistro1.getAsString("serie").equals("0"))){
					FcnZebra.WrLabel("RETIRADO","Se Encuentra Predio Con Servicio Directo.", 10, 0 ,1);				
				}else if((this._infRegistro1.getAsString("tipo").equals("R"))&&(this._infRegistro1.getAsString("lectura").equals("-1"))){
					FcnZebra.WrLabel("Medidor:","RETIRADO", 10, 0.5 ,0);
					FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("conexion"), 280, 0 ,1);				
				}else if(this._infRegistro1.getAsString("tipo").equals("R")){
					FcnZebra.WrLabel("Medidor:","RETIRADO", 10, 0.5 ,0);
					FcnZebra.WrLabel("Serie:",this._infRegistro1.getAsString("serie"), 280, 0 ,1);
					FcnZebra.WrLabel("Marca:",this._infRegistro1.getAsString("marca"), 10, 0 ,0);
					FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("conexion"), 280, 0 ,1);	
					FcnZebra.WrLabel("Lectura",this._infRegistro1.getAsString("lectura"), 10, 0 ,1);
				}else if(this._infRegistro1.getAsString("tipo").equals("CM")){
					FcnZebra.WrLabel("Medidor:","CANCELACION MATRICULA", 10, 0.5 ,0);
					FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("conexion"), 370, 0.5 ,1);
				}else if(this._infRegistro1.getAsString("tipo").equals("P")){
					FcnZebra.WrLabel("Medidor:","PROVISIONAL",10, 0.5, 0);
					FcnZebra.WrLabel("Serie:",this._infRegistro1.getAsString("serie"),280, 0, 1);
					FcnZebra.WrLabel("Marca:",this._infRegistro1.getAsString("marca"),10, 0.5, 0);
					FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("conexion"),280, 0, 1);
					FcnZebra.WrLabel("Lectura:",this._infRegistro1.getAsString("lectura"),10, 0.5, 0);
					FcnZebra.WrLabel("Propietario:","EMSA",280, 0, 1);
				}else if(this._infRegistro1.getAsString("tipo").equals("D")||this._infRegistro1.getAsString("tipo").equals("DP")){
					FcnZebra.WrLabel("Medidor:","INSTALADO",10, 0.5, 0);
					FcnZebra.WrLabel("Serie:",this._infRegistro1.getAsString("serie"),280, 0, 1);
					FcnZebra.WrLabel("Marca:",this._infRegistro1.getAsString("marca"),10, 0.5, 0);
					FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("conexion"),280, 0, 1);
					FcnZebra.WrLabel("Lectura:",this._infRegistro1.getAsString("lectura"),10, 0.5, 0);
					//Este if se implemento para realizar el cruce directamente con la tabla amd_param_material_usuario, el cual contiene los codigos
					//de materiales que son de propiedad de usuario.
					if(this.ImpSQL.ExistRegistros(	"amd_param_material_usuario", 
													"codigo='"+this.ImpSQL.StrSelectShieldWhere("amd_bodega_contadores", 
																								"codigo_elemento", 
																								"serie='"+this._infRegistro1.getAsString("serie")+"' AND marca='"+this._infRegistro1.getAsString("marca")+"'")+"'")){
						FcnZebra.WrLabel("Propietario:","Usuario",280, 0, 1);
					}else{
						FcnZebra.WrLabel("Propietario:","EMSA",280, 0, 1);
					}					
				}
			}
		}else{
			FcnZebra.WrLabel("Medidor:","ENCONTRADO",10, 0.5, 0);
			
			if(this.ImpSQL.ExistRegistros("amd_medidor_encontrado", "id_orden='"+ordenTrabajo+"'")){
				this._infRegistro1 = this.ImpSQL.SelectDataRegistro("amd_medidor_encontrado", "marca,serie,lectura,tipo", "id_orden='"+ordenTrabajo+"'");
				FcnZebra.WrLabel("Marca:",this._infRegistro1.getAsString("marca"),280, 0, 1);
				FcnZebra.WrLabel("Serie:",this._infRegistro1.getAsString("serie"),10, 0, 0);
				FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("tipo"),280, 0, 0);
				FcnZebra.WrLabel("Lectura:",this._infRegistro1.getAsString("lectura"),550, 0, 1);	
				
			}else{
				FcnZebra.WrLabel("Marca:","",280, 0, 1);
				FcnZebra.WrLabel("Serie:","",10, 0, 0);
				FcnZebra.WrLabel("Tipo:","",280, 0, 0);
				FcnZebra.WrLabel("Lectura:","",550, 0, 1);	
			}
		}
		
		//En caso de que no se encuentre ningun movimiento de medidor
		/*try{
			if(!ImpSQL.ExistRegistros("imp_equipo_medida","id_orden='"+ordenTrabajo+"' AND tipo<>'NM'")){
				FcnZebra.WrLabel("Medidor:","ENCONTRADO",10, 0.5, 0);
				FcnZebra.WrLabel("Marca:",this._infRegistro1.getAsString("marca"),280, 0, 1);
				FcnZebra.WrLabel("Serie:",this._infRegistro1.getAsString("serie"),10, 0, 0);
				FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("conexion"),280, 0, 0);
				FcnZebra.WrLabel("Lectura:",this._infRegistro1.getAsString("lectura"),550, 0, 1);
				FcnZebra.WrLabel("",ImpSQL.StrSelectShieldWhere("amd_inconsistencias", "valor", "id_orden='"+ordenTrabajo+"' AND cod_inconsistencia='AD15'"),10, 0, 1);
			}
		}catch(Exception e){
			Toast.makeText(this.context,"Error "+e.toString(), Toast.LENGTH_SHORT).show();
			
		}*/
		
		/****************************************************************Datos de Acometidas*********************************************************/
		FcnZebra.WrSubTitulo("ACOMETIDAS",10,1,1);
		 
		this._infTabla = ImpSQL.SelectData("vista_acometida", "tipo,calibre,tipo_ingreso,fase,clase,longitud", "id_orden='"+ordenTrabajo+"'");
		for(int i=0;i<this._infTabla.size();i++){
			this._infRegistro1 = this._infTabla.get(i);
			FcnZebra.WrLabel("Estado: ",this._infRegistro1.getAsString("tipo_ingreso"),10,1,1);
			FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("tipo"),10,0,0);
			FcnZebra.WrLabel("Calibre",this._infRegistro1.getAsString("calibre"),260,0,0);
			FcnZebra.WrLabel("Long.:",this._infRegistro1.getAsString("longitud"),430,0,1);
			FcnZebra.WrLabel("Fase:",this._infRegistro1.getAsString("fase"),10,0,0);
			FcnZebra.WrLabel("Clase:",this._infRegistro1.getAsString("clase"),260,0,1);			
		}
		 
		try{
			if(!ImpSQL.ExistRegistros("vista_acometida", "id_orden='"+ordenTrabajo+"'")){
				//_tempStr1 = ImpSQL.StrSelectShieldWhere("amd_inconsistencias", "valor", "id_orden='"+ordenTrabajo+"' AND cod_inconsistencia='AD10'").split("-");
				FcnZebra.WrLabel("",ImpSQL.StrSelectShieldWhere("amd_inconsistencias", "valor", "id_orden='"+ordenTrabajo+"' AND cod_inconsistencia='AD10'"),10, 0, 1);
			}
		}catch(Exception e){
			Toast.makeText(this.context,"Error "+e.toString(), Toast.LENGTH_SHORT).show();			
		}
		
		
		
		/***********************************************Datos de los Sellos existentes, retirados e instalados***************************************/		 
		FcnZebra.WrSubTitulo("SELLOS",10,1,1);
		
		//Consulta de los sellos existentes
		this._infTabla = ImpSQL.SelectData("amd_sellos", "numero,tipo,ubicacion,irregularidad", "id_orden='"+ordenTrabajo+"'AND estado = 'EXISTENTE' ORDER BY estado DESC");
		if(this._infTabla.size()>0){
			FcnZebra.WrLabel("Estado:","EXISTENTE",10,0,1);		
			for(int i=0;i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				if((this._infRegistro1.getAsInteger("numero")<0)&&(this._infRegistro1.getAsString("irregularidad").equals("SIN SELLO"))){
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("ubicacion")+" - "+this._infRegistro1.getAsString("irregularidad"),10,0,1);
				}else if(this._infRegistro1.getAsInteger("numero")<0){
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("tipo")+" - "+this._infRegistro1.getAsString("ubicacion")+" - "+this._infRegistro1.getAsString("irregularidad"),10,0,1);
				}else{
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("numero")+" - "+this._infRegistro1.getAsString("tipo")+" - "+this._infRegistro1.getAsString("ubicacion")+" - "+this._infRegistro1.getAsString("irregularidad"),10,0,1);
				}
			}
		}
		
		
		//Consulta de los sellos instalados
		this._infTabla = ImpSQL.SelectData("amd_sellos", "numero,tipo,ubicacion,irregularidad", "id_orden='"+ordenTrabajo+"'AND estado = 'INSTALADO' ORDER BY estado DESC");
		if(this._infTabla.size()>0){
			FcnZebra.WrLabel("Estado:","INSTALADOS",10,0,1);		
			for(int i=0;i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				if((this._infRegistro1.getAsInteger("numero")<0)&&(this._infRegistro1.getAsString("irregularidad").equals("SIN SELLO"))){
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("ubicacion"),10,0,1);
				}else if(this._infRegistro1.getAsInteger("numero")<0){
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("tipo")+" - "+this._infRegistro1.getAsString("ubicacion"),10,0,1);
				}else{
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("numero")+" - "+this._infRegistro1.getAsString("tipo")+" - "+this._infRegistro1.getAsString("ubicacion"),10,0,1);
				}
			}
		}
		
		
		//Consulta de los sellos retirados
		this._infTabla = ImpSQL.SelectData("amd_sellos", "numero,tipo,ubicacion,irregularidad", "id_orden='"+ordenTrabajo+"'AND estado = 'RETIRADO' ORDER BY estado DESC");
		if(this._infTabla.size()>0){
			FcnZebra.WrLabel("Estado:","RETIRADOS",10,0,1);		
			for(int i=0;i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				if((this._infRegistro1.getAsInteger("numero")<0)&&(this._infRegistro1.getAsString("irregularidad").equals("SIN SELLO"))){
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("ubicacion")+" - "+this._infRegistro1.getAsString("irregularidad"),10,0,1);
				}else if(this._infRegistro1.getAsInteger("numero")<0){
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("tipo")+" - "+this._infRegistro1.getAsString("ubicacion")+" - "+this._infRegistro1.getAsString("irregularidad"),10,0,1);
				}else{
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("numero")+" - "+this._infRegistro1.getAsString("tipo")+" - "+this._infRegistro1.getAsString("ubicacion")+" - "+this._infRegistro1.getAsString("irregularidad"),10,0,1);
				}
			}
		}
		
		
		/*****************************************************************Inconsistencia del no registro de sellos**********************************************************************/
		try{
			if(!ImpSQL.ExistRegistros("amd_sellos", "id_orden='"+ordenTrabajo+"'")){
				FcnZebra.WrLabel("",ImpSQL.StrSelectShieldWhere("amd_inconsistencias", "valor", "id_orden='"+ordenTrabajo+"' AND cod_inconsistencia='AD13'"),10, 0, 1);
			}
		}catch(Exception e){
			Toast.makeText(this.context,"Error "+e.toString(), Toast.LENGTH_SHORT).show();
		}
			
		
		/*****************************************************Inicio de impresion de pruebas de factor de potencia*********************************************************/
		this._infTabla = ImpSQL.SelectData("amd_pct_error", "tipo_carga,voltaje,corriente,tiempo,vueltas,rev,total,fp,fase", "id_orden='"+ordenTrabajo+"' ORDER BY tipo_carga,fase ASC");
		if(this._infTabla.size()>0){
			FcnZebra.WrSubTitulo("PRUEBAS DE FACTOR DE POTENCIA",10,1,1.5);
			for(int i=0;i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				FcnZebra.WrLabel("Tipo de Carga:",this._infRegistro1.getAsString("tipo_carga"),10,0,0);
				switch(this._infRegistro1.getAsInteger("fase")){
					case 1:
						FcnZebra.WrLabel("Fase:","A",300,0,0);
						break;
					case 2:
						FcnZebra.WrLabel("Fase:","B",300,0,0);
						break;
					case 3:
						FcnZebra.WrLabel("Fase:","C",300,0,0);
						break;	
				}
				
				FcnZebra.WrLabel("Volt:",this._infRegistro1.getAsString("voltaje"),460,0,1);
				
				FcnZebra.WrLabel("Corr:",this._infRegistro1.getAsString("corriente"),10,0,0);
				FcnZebra.WrLabel("Tmpo:",this._infRegistro1.getAsString("tiempo"),300,0,0);
				FcnZebra.WrLabel("Vuel:",this._infRegistro1.getAsString("vueltas"),460,0,1);
				
				FcnZebra.WrLabel("Revo:",this._infRegistro1.getAsString("rev"),10,0,0);
				FcnZebra.WrLabel("Fp:  ",this._infRegistro1.getAsString("fp")+"%",300,0,0);
				FcnZebra.WrLabel("Err: ",this._infRegistro1.getAsString("total")+"%",460,0,1);
			}
		}
		
		/*********************************************Inconsistencia del no registro de pruebas de pct error*******************************************************/
		try{
			if(!ImpSQL.ExistRegistros("amd_sellos", "id_orden='"+ordenTrabajo+"'")){
				FcnZebra.WrLabel("",ImpSQL.StrSelectShieldWhere("amd_inconsistencias", "valor", "id_orden='"+ordenTrabajo+"' AND cod_inconsistencia='AD13'"),10, 0, 1);
			}
		}catch(Exception e){
			Toast.makeText(this.context,"Error "+e.toString(), Toast.LENGTH_SHORT).show();
		}
		
		
		
		/**************************************************************Inicio de impresion del censo de carga tomado***********************************************/
		this._infTabla = ImpSQL.SelectData("imp_censo_carga", "id_elemento,descripcion,capacidad,cantidad,tipo_carga", "id_orden='"+ordenTrabajo+"'");
		if(this._infTabla.size()>0){
			FcnZebra.WrSubTitulo("CENSO",10,1,1.5);
			FcnZebra.WrLabel("ELEMENTO","",10, 0, 0);
			FcnZebra.WrLabel("CAR","",370, 0, 0);
			FcnZebra.WrLabel("CAP","",440, 0, 0);
			FcnZebra.WrLabel("CNT","",550, 0, 1);
			
			for(int i=0;i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				FcnZebra.WrLabel("",this._infRegistro1.getAsString("descripcion"),10, 0, 0);
				FcnZebra.WrLabel("",this._infRegistro1.getAsString("tipo_carga"),370, 0, 0);
				FcnZebra.WrLabel("",decimales.format(this._infRegistro1.getAsDouble("capacidad")/1000)+" Kw",440, 0, 0);
				FcnZebra.WrLabel("",this._infRegistro1.getAsString("cantidad"),550, 0, 1);
			}
			FcnZebra.WrLabel("Registrada:",decimales.format(ImpSQL.DoubleSelectShieldWhere("amd_censo_carga", "sum(capacidad*cantidad)", "id_orden='"+ordenTrabajo+"' AND tipo_carga='R'")/1000)+" Kw",10, 0, 0);
			FcnZebra.WrLabel("Directa:",decimales.format(ImpSQL.DoubleSelectShieldWhere("amd_censo_carga", "sum(capacidad*cantidad)", "id_orden='"+ordenTrabajo+"' AND tipo_carga='D'")/1000)+" Kw",300, 0, 0);
			FcnZebra.WrLabel("Total:",decimales.format(ImpSQL.DoubleSelectShieldWhere("amd_censo_carga", "sum(capacidad*cantidad)", "id_orden='"+ordenTrabajo+"'")/1000)+" Kw",550, 0, 1);
		}else{
			//_tempStr1 = ImpSQL.StrSelectShieldWhere("amd_inconsistencias", "valor", "id_orden='"+ordenTrabajo+"' AND cod_inconsistencia='AD11'").split("-");
			FcnZebra.WrLabel("",ImpSQL.StrSelectShieldWhere("amd_inconsistencias", "valor", "id_orden='"+ordenTrabajo+"' AND cod_inconsistencia='AD11'"),10, 0, 1);
		}
		
		
		
		
		/*************************************Impresion del diagrama de conexion y de acometida segun lo selecciono el tecnico************************************/
		FcnZebra.WrSubTitulo("Unifilar Conexion al Medidor",25,2,0);
		FcnZebra.WrSubTitulo("Grafico Red Acometida",430,0,1.5);
		
		FcnZebra.WrRectangle(15, FcnZebra.getCurrentLine(), 385, FcnZebra.getCurrentLine() + 370, 0, 10);
		this._infRegistro1 = ImpSQL.SelectDataRegistro("amd_impresiones_inf", "diagrama,acometida", "id_orden='"+ordenTrabajo+"'");
		if(this._infRegistro1.getAsString("diagrama").toString().equals("Monofasico Simetrico")){
			FcnZebra.DrawImage("MONOSIME.PCX",17,90);			
		}else if(this._infRegistro1.getAsString("diagrama").toString().equals("Monofasico Asimetrico")){
			FcnZebra.DrawImage("MONOASIM.PCX",17,90);			
		}else if(this._infRegistro1.getAsString("diagrama").toString().equals("Bifasico Simetrico")){
			FcnZebra.DrawImage("BIFASIME.PCX",17,74);			
		}else if(this._infRegistro1.getAsString("diagrama").toString().equals("Bifasico Asimetrico")){
			FcnZebra.DrawImage("BIFAASIM.PCX",17,74);
		}else if(this._infRegistro1.getAsString("diagrama").toString().equals("Trifasico Simetrico")){
			FcnZebra.DrawImage("TRISIME.PCX",17,55);
		}else if(this._infRegistro1.getAsString("diagrama").toString().equals("Trifasico Asimetrico")){
			FcnZebra.DrawImage("TRIASIME.PCX",17,55);
		}
				
		if(this._infRegistro1.getAsString("acometida").equals("Abierta")){
			FcnZebra.DrawImage("REDABIER.PCX",398,10);			
		}else if(this._infRegistro1.getAsString("acometida").toString().equals("Trensada")){
			FcnZebra.DrawImage("REDTRENS.PCX",398,10);			
		}
		
		FcnZebra.WrRectangle(390, FcnZebra.getCurrentLine(), 760,FcnZebra.getCurrentLine()+370, 1, 10);
		
		/**************************************************************Inicio de impresion de los datos generales***************************************************/
		FcnZebra.WrSubTitulo("DATOS GENERALES",10,1,1.5);
		double tempCurrentLine = FcnZebra.getCurrentLine();
		
		this._infTabla = ImpSQL.SelectData("imp_datos_generales", "descripcion, valor", "id_orden='"+ordenTrabajo+"' AND descripcion_titulo = 'datos acta'");
		if(this._infTabla.size()>0){
			FcnZebra.WrLabel("", "DATOS ACTA",10, 1, 1.5);
			for(int i=0;i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				this._tempStr1 	= this._infRegistro1.getAsString("descripcion").split("_");
				this._tempStr2	= this._infRegistro1.getAsString("valor").split("_");
				FcnZebra.WrLabel(this._tempStr1[1]+": "+this._tempStr2[1], "", 10, 0, 1);
			}
		}
		
		FcnZebra.setCurrentLine(tempCurrentLine);
		this._infTabla = ImpSQL.SelectData("imp_datos_generales", "descripcion, valor", "id_orden='"+ordenTrabajo+"' AND descripcion_titulo = 'ADECUACIONES A REALIZAR'");
		
		if(this._infTabla.size()>0){
			FcnZebra.WrLabel("", "ADECUACIONES A REALIZAR",360, 1, 1.5);
			for(int i=0;i<this._infTabla.size();i++){
				
				this._infRegistro1 = this._infTabla.get(i);
				this._tempStr1 	= this._infRegistro1.getAsString("descripcion").split("_");
				this._tempStr2	= this._infRegistro1.getAsString("valor").split("_");
 				FcnZebra.WrLabel(this._tempStr1[1]+": "+this._tempStr2[1], "", 360, 0, 1);
			}
		}
		
		/***************************************************Inicio de impresion de pruebas al medidor*****************************************/	
		this._infRegistro1 = ImpSQL.SelectDataRegistro("amd_pruebas", "p_conexion, p_continuidad, p_puentes, p_integrador, p_vacio, p_frenado, p_retirado, rozamiento, aplomado", "id_orden='"+ordenTrabajo+"'");
		if(this._infRegistro1.size()>0){
			FcnZebra.WrSubTitulo("PRUEBAS AL MEDIDOR",10,3,1);
			FcnZebra.WrLabel("Conexion:    "+this._infRegistro1.getAsString("p_conexion"), "", 10, 0, 0);
			FcnZebra.WrLabel("Continuidad: "+this._infRegistro1.getAsString("p_continuidad"), "", 350, 0, 1);
			
			FcnZebra.WrLabel("Puentes:     "+this._infRegistro1.getAsString("p_puentes"), "", 10, 0, 0);
			FcnZebra.WrLabel("Integracion: "+this._infRegistro1.getAsString("p_integrador"), "", 350, 0, 1);
			
			FcnZebra.WrLabel("Giro Vacio:  "+this._infRegistro1.getAsString("p_vacio"), "", 10, 0, 0);
			FcnZebra.WrLabel("Frenado:     "+this._infRegistro1.getAsString("p_frenado"), "", 350, 0, 1);
			
			FcnZebra.WrLabel("Retirado:    "+this._infRegistro1.getAsString("p_retirado"), "", 10, 0, 0);
			FcnZebra.WrLabel("Rozamiento:  "+this._infRegistro1.getAsString("rozamiento"), "", 350, 0, 1);
			
			FcnZebra.WrLabel("Aplomado:    "+this._infRegistro1.getAsString("aplomado"), "", 10, 0, 1);
		}
		
		this._infRegistro1 = ImpSQL.SelectDataRegistro("amd_prueba_integracion", "lect_ini, lect_final, giros", "id_orden='"+ordenTrabajo+"'");
		if(this._infRegistro1.size()>0){
			FcnZebra.WrSubTitulo("PRUEBA DE INTEGRACION",10,3,1);
			FcnZebra.WrLabel("Lect Inicial: "+this._infRegistro1.getAsString("lect_ini"), "", 10, 0, 0);
			FcnZebra.WrLabel("Lect Final: "+this._infRegistro1.getAsString("lect_final"), "", 300, 0, 0);
			FcnZebra.WrLabel("Giros: "+this._infRegistro1.getAsString("giros"), "", 600, 0, 1);
		}
		
		
		/*******************************************Inicio de impresion de datos del transformador***********************************/
		this._infRegistro1 = ImpSQL.SelectDataRegistro("amd_datos_tranfor", "numero,marca,kva,propietario,anno,voltaje1,voltaje2,circuito", "id_orden='"+ordenTrabajo+"'");
		if(this._infRegistro1.size()>0){
			FcnZebra.WrSubTitulo("DATOS DEL TRANSFORMADOR",10,1,1.5);
			FcnZebra.WrLabel("Numero:      "+this._infRegistro1.getAsString("numero"), "", 10, 0, 0);
			FcnZebra.WrLabel("Marca :      "+this._infRegistro1.getAsString("marca"), "", 350, 0, 1);
			
			FcnZebra.WrLabel("Kwa:         "+this._infRegistro1.getAsString("kva"), "", 10, 0, 0);
			FcnZebra.WrLabel("Propietario: "+this._infRegistro1.getAsString("propietario"), "", 350, 0, 1);
			
			FcnZebra.WrLabel("Ano:         "+this._infRegistro1.getAsString("anno"), "", 10, 0, 0);
			FcnZebra.WrLabel("Voltaje:     "+this._infRegistro1.getAsString("voltaje1"), "", 350, 0, 1);
			
			FcnZebra.WrLabel("Voltaje2:    "+this._infRegistro1.getAsString("voltaje2"), "", 10, 0, 0);
			FcnZebra.WrLabel("Nodo:        "+this._infRegistro1.getAsString("circuito"), "", 350, 0, 1);
		}
		
		
		/**************************************************Inicio de impresion de las irregularidades**********************************/
		this._infTabla = ImpSQL.SelectData("vista_irregularidades", "descripcion", "id_orden='"+ordenTrabajo+"' ORDER BY descripcion");
		if(this._infTabla.size()>0){
			FcnZebra.WrSubTitulo("IRREGULARIDADES",10,1.5,1.5);
			for(int i=0;i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				FcnZebra.WrLabel(this._infRegistro1.getAsString("descripcion"), "", 10, 0, 1);
			}
		}
		
		
		/*********************************************Inicion de la impresion de las observaciones************************************/
		if(ImpSQL.ExistRegistros("imp_observaciones", "id_orden='"+ordenTrabajo+"'")){
			FcnZebra.WrSubTitulo("OBSERVACIONES",10,1,1.5);
			this._infTabla = ImpSQL.SelectData("imp_observaciones", "descripcion", "id_orden='"+ordenTrabajo+"' AND id_inconsistencia LIKE 'AD%'");
			for(int i=0; i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				FcnZebra.JustInformation(this._infRegistro1.getAsString("descripcion"),10, 0, 1);				
			}
			
			FcnZebra.JustInformation("",0, 0, 1);
			
			this._infTabla = ImpSQL.SelectData("imp_observaciones", "valor", "id_orden='"+ordenTrabajo+"' AND id_inconsistencia IN ('GEN01','GEN02','GEN03')");
			for(int i=0; i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				FcnZebra.JustInformation(this._infRegistro1.getAsString("valor"),10, 0, 1);				
			}
		}
		
		/********************************************************Impresion de leyenda al final del acta********************************************/	
		FcnZebra.JustInformation("En caso de detectarse irregularidad(es) esta acta se constituye en acta de irregularidades, por lo cual procede como tal ante el cliente o usuario del servicio de energia electrica.",10, 2, 1);
		FcnZebra.JustInformation("Señor usuario en caso de no estar de acuerdo con el presente resultado de esta revision, puede presentar descargos que justifiquen la presencia de las anomalias detectadas por escrito al momento de firmar el acta o dentro de los cinco (5) dias habiles siguientes, en comunicacion dirigida al area de control de energia de la empresa, citando el No. de esta acta y radicando su comunicacion en la oficina de atencio al cliente mas cercana, la no presentacion de los descargos indica a la empresa que el usuario (suscriptor) acepta el concepto tecnico emitido. Los abajo firmantes reconocen haber leido y aceptado el contenido de esta acta y mediante su firma la dan por levantada. EL USO INDEBIDO DEL SERVICIO, LA ADULTERACION O MANIPULACION DEL EQUIPO DE MEDIDA SE CONSTITUYE EN DELITO DE DEFRAUDACION DE FUIDOS (articulo 256 del codigo penal).",10, 1, 1);
		FcnZebra.JustInformation("La EMPRESA, con base a lo establecido en la LEY 142 de 1994 y en su Contrato de Servicios Publicos con Condiciones Uniformes, se permite informarle que usted dispone a partir de la fecha un periodo de facturacion de 30 dias calendario para instalar, cambiar o adecuar las anomalias aqui indicadas, cumpliendo con las NORMAS TECNICAS exigidas por la EMPRESA; pasado este periodo y de no tomar las medidas necesarias para adquirirlos, las instalacion(es) provisional(es) pasaran a ser definitiva(s) con cargo a su cuenta.",10, 1, 2);
		 
		if(ImpSQL.ExistRegistros("amd_actas", "id_orden='"+ordenTrabajo+"' AND nombre_enterado <>'' AND nombre_testigo =''")){
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_actas", "nombre_enterado", "id_orden='"+ordenTrabajo+"'"), 10, 5, 0);
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_param_sistema","valor", "codigo='NOM_TECNICO'"), 400, 0, 1);
			FcnZebra.WrLabel("CC.", ImpSQL.StrSelectShieldWhere("amd_actas", "cedula_enterado", "id_orden='"+ordenTrabajo+"'"), 10, 0, 0);
			FcnZebra.WrLabel("CC.", CedulaTecnico, 400, 0, 1);
			FcnZebra.WrLabel("", "Usuario", 10, 0, 0);
			FcnZebra.WrLabel("", "Empleado y/o Contratista", 400, 0, 1);			
		}else if(ImpSQL.ExistRegistros("amd_actas", "id_orden='"+ordenTrabajo+"' AND nombre_enterado ='' AND nombre_testigo <>''")){
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_actas", "nombre_testigo", "id_orden='"+ordenTrabajo+"'"), 10, 5, 0);
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_param_sistema","valor", "codigo='NOM_TECNICO'"), 400, 0, 1);
			FcnZebra.WrLabel("CC.", ImpSQL.StrSelectShieldWhere("amd_actas", "cedula_testigo", "id_orden='"+ordenTrabajo+"'"), 10, 0, 0);
			FcnZebra.WrLabel("CC.", CedulaTecnico, 400, 0, 1);
			FcnZebra.WrLabel("", "Testigo", 10, 0, 0);
			FcnZebra.WrLabel("", "Empleado y/o Contratista", 400, 0, 1);
		}else if(ImpSQL.ExistRegistros("amd_actas", "id_orden='"+ordenTrabajo+"' AND nombre_enterado <>'' AND nombre_testigo <>''")){
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_actas", "nombre_enterado", "id_orden='"+ordenTrabajo+"'"), 10, 5, 0);
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_actas", "nombre_testigo", "id_orden='"+ordenTrabajo+"'"), 400, 0, 1);
			FcnZebra.WrLabel("CC.", ImpSQL.StrSelectShieldWhere("amd_actas", "cedula_enterado", "id_orden='"+ordenTrabajo+"'"), 10, 0, 0);
			FcnZebra.WrLabel("CC.", ImpSQL.StrSelectShieldWhere("amd_actas", "cedula_testigo", "id_orden='"+ordenTrabajo+"'"), 400, 0, 1);
			FcnZebra.WrLabel("", "Usuario", 10, 0, 0);
			FcnZebra.WrLabel("", "Testigo", 400, 0, 1);
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_param_sistema","valor", "codigo='NOM_TECNICO'"), 10, 6, 1);
			FcnZebra.WrLabel("CC.", CedulaTecnico, 10, 0, 1);
			FcnZebra.WrLabel("", "Empleado y/o Contratista", 10, 0, 1);
		}
		FcnZebra.JustInformation("NO PAGAR NI REALIZAR NEGOCIACIONES CON EL OPERARIO POR NINGUN CONCEPTO. DENUNCIE CUALQUIER IRREGULARIDAD AL TELEFONO"+" 6614000", 10, 1, 1);	
		FcnZebra.WrSubTitulo("ITEMS DE PAGO APLICADOS: "+ImpSQL.StrSelectShieldWhere("amd_impresiones_inf", "items", "id_orden='"+ordenTrabajo+"'"),10,1.2,1.2);

	    
		//_infImpresion = WrLabel(_infImpresion, "", copiaImpresion, 10, 0, 1);
		switch(copiaImpresion){
			case 1:
				FcnZebra.WrLabel("", "Original Empresa", 10, 0, 1);
				break;
			case 2:
				FcnZebra.WrLabel("", "Copia Usuario", 10, 0, 1);
				break;
			case 3:
				FcnZebra.WrLabel("", "Copia Archivo", 10, 0, 1);
				break;
		}
		
		String ubicacion = ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "ubicacion", "id_orden='"+ordenTrabajo+"'");	
		if (ubicacion.equals("U")){
			FcnZebra.WrSubTitulo("UBICACION URBANA",10,0,1);
		}
		else FcnZebra.WrSubTitulo("UBICACION RURAL",10,0,1);
	    
		FcnZebra.WrLabel("",ImpSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'")+" - "+ DT.GetDateTimeHora(), 10, 0, 1);
		FcnZebra.WrLabel("","Supervisor/Interventoria:________________________________________", 10, 0, 1);
		
		
		//String nombreArchivo = DT.GetFecha();
		if((_copiaSistema)&&((copiaImpresion==1)||(copiaImpresion==2))){
			if(!this.ImpArchivos.ExistFolderOrFile(this._folderAplicacion+File.separator+ordenTrabajo)){
				this.ImpArchivos.MakeDirectory(ordenTrabajo);
			}
			int num_impresion = ImpSQL.IntSelectShieldWhere("amd_impresiones_inf", "id_impresion", "id_orden='"+ordenTrabajo+"'")+1;
			this.ImpArchivos.DoFile(ordenTrabajo, tipoImpresion+"_"+copiaImpresion+"_"+num_impresion,FcnZebra.getInfArchivo());
			
			this._infRegistro1.clear();
			this._infRegistro1.put("id_impresion", num_impresion);
			this.ImpSQL.UpdateRegistro("amd_impresiones_inf", this._infRegistro1, "id_orden='"+ordenTrabajo+"'");
		}
		
		
		
		if(copiaImpresion==3){
				String _fechaQR = DT.GetFecha();
				String Info2=_fechaQR+"|"+ImpSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'")+"|"+ordenTrabajo+"|"+ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "num_acta", "id_orden='"+ordenTrabajo+"'")+"|"+"I#";
				this._materialInstalado = generarCodigoQRMI(ordenTrabajo);
				for(int k=0;k<this._materialInstalado.size();k++){
					Info2+=this._materialInstalado.get(k)+"@";
				}
				
				String Info= _fechaQR+"|"+ImpSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'")+"|"+ordenTrabajo+"|"+ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "num_acta", "id_orden='"+ordenTrabajo+"'")+"|"+"R#";
				this._materialRetirado = generarCodigoQRMR(ordenTrabajo);
				for(int k=0;k<this._materialRetirado.size();k++){
					Info+=this._materialRetirado.get(k)+"@";
				}
				
				String infoPrinterQRR = Info.substring(0, Info.length()-1);
				FcnZebra.printerQRCode(infoPrinterQRR, 20, 4, 0);
				
				String infoPrinterQR = Info2.substring(0, Info2.length()-1);
				FcnZebra.printerQRCode(infoPrinterQR, 300, 0, 15);
			}
		
		MnBt.IntentPrint(this.Impresora,FcnZebra.getDoLabel());
		FcnZebra.resetEtiqueta();
	}
	
	/**********************************************************************************************************************************
	 * 
	 * @param ordenTrabajo
	 *********************************************************************************************************************************/
	
	public ArrayList<String> generarCodigoQRMI(String ordenTrabajo){
		
		this._infTabla.clear();
		this._infTabla1.clear();
		this._infRegistro1.clear();
		this._infRegistro2.clear();
		
		String[] arrayJoin 	= {"amd_materiales_trabajo_orden as b"};
		String[] arrayOn	= {"a.codigo_material = b.id_material"};
		
		this._infTabla = ImpSQL.SelectNJoinLeftData("amd_param_materiales_seriado as a", "a.codigo_material,b.cantidad, a.seriado",arrayJoin,arrayOn, "b.id_orden='"+ordenTrabajo+"'");
		
		for(int i=0; i<this._infTabla.size();i++){
			this._infRegistro1 = this._infTabla.get(i);
				if(this._infRegistro1.getAsString("seriado").equals("SI") && this._infRegistro1.getAsString("codigo_material").equals("99999")){
							this._infTabla1 = ImpSQL.SelectData("amd_sellos", "numero", "estado='INSTALADO' AND tipo='S-ROTOANCLA' and id_orden='"+ordenTrabajo+"'");
							for (int j=0;j<this._infTabla1.size();j++){
								this._infRegistro2 = this._infTabla1.get(j);
								this._infoCodigoQR.add(this._infRegistro1.getAsString("codigo_material")+"|"+"1"+"|"+this._infRegistro2.getAsString("numero")+"|"+"D");								
							}
					
				}else if(this._infRegistro1.getAsString("seriado").equals("SI")){
							String numero = ImpSQL.StrSelectShieldWhere("amd_cambios_contadores","serie", "tipo='D' AND id_orden='"+ordenTrabajo+"'");
							this._infoCodigoQR.add(this._infRegistro1.getAsString("codigo_material")+"|"+"1"+"|"+numero+"|"+"D");
					
						}else if(this._infRegistro1.getAsString("seriado").equals("NO")){
							this._infoCodigoQR.add(this._infRegistro1.getAsString("codigo_material")+"|"+this._infRegistro1.getAsString("cantidad")+"|"+"0"+"|"+"D");
							
						}
			}
		if(this._infTabla.size()==0){
			this._infTabla1 = ImpSQL.SelectData("amd_sellos", "numero", "estado='INSTALADO' AND tipo='S-ROTOANCLA' and id_orden='"+ordenTrabajo+"'");
			for (int j=0;j<this._infTabla1.size();j++){
				this._infRegistro2 = this._infTabla1.get(j);
				this._infoCodigoQR.add("99999"+"|"+"1"+"|"+this._infRegistro2.getAsString("numero")+"|"+"D");								
			}

		}
		if(ImpSQL.ExistRegistros("amd_cambios_contadores", "tipo='P' AND id_orden='"+ordenTrabajo+"'")){
			String numeroM	 = ImpSQL.StrSelectShieldWhere("amd_cambios_contadores","serie", "tipo='P' AND id_orden='"+ordenTrabajo+"'");
			String idMaterial= ImpSQL.StrSelectShieldWhere("amd_materiales_provisionales","elemento", "id_orden='"+ordenTrabajo+"'");
			this._infoCodigoQR.add(idMaterial+"|"+"1"+"|"+numeroM+"|"+"P");
		}
		
		if(ImpSQL.ExistRegistros("amd_sellos", "tipo='O-Sticker' AND id_orden='"+ordenTrabajo+"'")){
			String numeroS	 = ImpSQL.StrSelectShieldWhere("amd_sellos","numero", "tipo='O-Sticker' AND id_orden='"+ordenTrabajo+"'");
			this._infoCodigoQR.add("10013"+"|"+"1"+"|"+numeroS+"|"+"D");
		}
		
		return this._infoCodigoQR; 		
	}
	
	/**********************************************************************************************************************************
	 * 
	 * @param ordenTrabajo
	 *********************************************************************************************************************************/
	public ArrayList<String> generarCodigoQRMR(String ordenTrabajo){
		this._infTabla.clear();
		this._infTabla1.clear();
		this._infRegistro1.clear();
		this._infRegistro2.clear();
		boolean codigo = false;
		String[] arrayJoin 	= {"amd_cambios_contadores as b"};
		String[] arrayOn	= {"a.marca=b.marca and a.tipo=b.cobro"};
		
		this._infTabla = ImpSQL.SelectData("amd_sellos", "numero", "estado='RETIRADO' AND id_orden='"+ordenTrabajo+"'");
		for (int i=0;i<this._infTabla.size();i++){
			this._infRegistro1 = this._infTabla.get(i);
			this._infoCodigoQRR.add("99999"+"|"+"1"+"|"+this._infRegistro1.getAsString("numero")+"|"+"EC");								
		}
		
		if(ImpSQL.ExistRegistros("amd_cambios_contadores", "tipo='R' AND id_orden='"+ordenTrabajo+"'")){
			String numeroM	 = ImpSQL.StrSelectShieldWhere("amd_cambios_contadores","serie", "tipo='R' AND id_orden='"+ordenTrabajo+"'");
			if(numeroM.equals("-1")){
				this._infoCodigoQRR.add("");
			}
			else{
				this._infTabla1 = ImpSQL.SelectNJoinLeftData("amd_param_tipo_medidor as a", "a.id_material",arrayJoin,arrayOn, "b.id_orden='"+ordenTrabajo+"' and b.tipo='R'");
				for (int i=0;i<this._infTabla1.size();i++){
					this._infRegistro2 = this._infTabla1.get(i);
					codigo= true;
				}
				if(codigo){
					this._infoCodigoQRR.add(this._infRegistro2.getAsString("id_material")+"|"+"1"+"|"+numeroM+"|"+"EC");
					
				}
				else{this._infoCodigoQRR.add("10051"+"|"+"1"+"|"+numeroM+"|"+"EC");}
			}
			
		}
		this._infTabla.clear();
		this._infRegistro1.clear();
		this._infTabla = ImpSQL.SelectData("amd_material_usuario", "cantidad", "id_orden='"+ordenTrabajo+"'");
		for (int i=0;i<this._infTabla.size();i++){
			this._infRegistro1 = this._infTabla.get(i);
			this._infoCodigoQRR.add("10051"+"|"+this._infRegistro1.getAsString("cantidad")+"|"+"0"+"|"+"EA");								
		}
		
		return this._infoCodigoQRR;
	}
	
	/**********************************************************************************************************************************
	 * 
	 * @param ordenTrabajo
	 * @param copiaImpresion
	 * @param CedulaTecnico
	 *********************************************************************************************************************************/
	public void FormatoMateriales(String ordenTrabajo, String cuentaCliente, String tipoImpresion, int copiaImpresion, String CedulaTecnico){
		FcnZebra.clearInformacion();	
		
		FcnZebra.WrTitulo("ELECTRIFICADORA DEL META E.S.P.", 0, 1);
		FcnZebra.DrawImage("IMAGES.PCX", 30, 25);
		FcnZebra.WrTitulo("ACTA DE MATERIALES ELECTRICOS", 0, 3);
		 
		/********************************Validaciones necesarias para el encabezado del acta**************************************/
		FcnZebra.WrLabel("N.          ", ordenTrabajo + ImpSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'"), 200, 0, 1);
		FcnZebra.WrLabel("ACTA REF.   ", ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "num_acta", "id_orden='"+ordenTrabajo+"'"), 200, 0, 1);
		
		long number= Long.decode(ordenTrabajo);
		if(number >= 0){
			if(ordenTrabajo.length()>6){
				FcnZebra.WrLabel("Solicitud:   ", ordenTrabajo, 200, 0, 1.2);	
			}else{
				FcnZebra.WrLabel("Revision:    ", ordenTrabajo,200, 0, 1.2);	
			}
		}else{
			FcnZebra.WrLabel("Orden:   ","____________________", 200, 0, 1.2);
		}	
		
		
		if(Double.parseDouble(cuentaCliente)>=0){
			FcnZebra.WrLabel("Codigo:     ", ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "cuenta", "id_orden='"+ordenTrabajo+"'"), 200, 0, 1);	
		}else{
			FcnZebra.WrLabel("Codigo:     ", ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "____________________", "id_orden='"+ordenTrabajo+"'"), 200, 0, 1);			
		}
		
		this._infRegistro1 = ImpSQL.SelectDataRegistro("vista_ordenes_trabajo", "municipio", "id_orden='"+ordenTrabajo+"'");
		FcnZebra.WrLabel("", this._infRegistro1.getAsString("municipio"), 300, 0, 1);
		FcnZebra.WrLabel("Contratista:","SYPELC LTDA", 200, 0, 3);
		
		this._infRegistro1.clear();
		this._infRegistro1 = ImpSQL.SelectDataRegistro("vista_actas", "dia,mes,anno,hora,propietario,nombre_enterado,cedula_enterado,tipo_enterado,cedula_testigo,nombre_testigo", "id_orden='"+ordenTrabajo+"'");
		FcnZebra.JustInformation("A los "+this._infRegistro1.getAsString("dia")+" dias del mes "+this._infRegistro1.getAsString("mes")+" de "+this._infRegistro1.getAsString("anno")+", se hicieron presentes los senores:  "+ImpSQL.StrSelectShieldWhere("amd_param_sistema","valor", "codigo='NOM_TECNICO'")+" con Cod/C.C: "+CedulaTecnico+" en representacion de EMSA E.S.P, al inmueble ubicacion en la "+this.ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "direccion", "id_orden='"+ordenTrabajo+"'"), 10, 2, 3);
		
		
		
		this._infTabla = ImpSQL.SelectData("vista_materiales_trabajo_orden", "descripcion, cantidad", "id_orden='"+ordenTrabajo+"' ORDER BY descripcion");
		if(this._infTabla.size()>0){
			FcnZebra.WrLabel("","ELEMENTOS DEFINITIVOS", 10, 1, 1.2);
			FcnZebra.WrLabel("","CNT", 550, 0, 1.5);
			for(int i=0; i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				if(this._infRegistro1.getAsString("descripcion").length()>40){
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("descripcion").substring(0, 40), 10, 0, 0);
				}else{
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("descripcion"), 10, 0, 0);
				}				
				FcnZebra.WrLabel("",this._infRegistro1.getAsString("cantidad"), 550, 0, 1);
			}			
			FcnZebra.WrLabel("No de Cuotas Materiales Definitivos: ",String.valueOf(ImpSQL.IntSelectShieldWhere("vista_materiales_trabajo_orden", "max(cuotas)", "id_orden='"+ordenTrabajo+"'")),10, 1, 3);
		}
		
		
		this._infTabla = ImpSQL.SelectData("vista_materiales_provisionales", "descripcion, cantidad", "id_orden='"+ordenTrabajo+"' ORDER BY descripcion");
		if(this._infTabla.size()>0){
			FcnZebra.WrLabel("","ELEMENTOS PROVISIONALES", 10, 1, 0);
			FcnZebra.WrLabel("","CNT", 550, 0, 1.5);
			for(int i=0; i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				if(this._infRegistro1.getAsString("descripcion").length()>40){
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("descripcion").substring(0, 40), 10, 1, 0);
				}else{
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("descripcion"), 10, 1, 0);
				}				
				FcnZebra.WrLabel("",this._infRegistro1.getAsString("cantidad"), 550, 0, 1);
			}			
		}
		
		
		this._infTabla = ImpSQL.SelectData("amd_material_usuario", "material,cantidad,estado,disposicion", "id_orden='"+ordenTrabajo+"' ORDER BY material");
		if(this._infTabla.size()>0){
			FcnZebra.WrLabel("","MATERIAL USUARIO", 10, 1, 1.5);
			FcnZebra.WrLabel("","DESCRIPCION", 10, 0, 0);
			FcnZebra.WrLabel("","CNT", 460, 0, 0);
			FcnZebra.WrLabel("","ESTADO", 510, 0, 0);
			FcnZebra.WrLabel("","DISPOSICION", 630, 0, 1.2);
			for(int i=0; i<this._infTabla.size();i++){
				this._infRegistro1 = this._infTabla.get(i);
				if(this._infRegistro1.getAsString("material").length()>40){
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("material").substring(0, 40), 10, 1, 0);
				}else{
					FcnZebra.WrLabel("",this._infRegistro1.getAsString("material"), 10, 1, 0);
				}		
				FcnZebra.WrLabel("",this._infRegistro1.getAsString("cantidad"), 460, 0, 0);
				FcnZebra.WrLabel("",this._infRegistro1.getAsString("estado"), 510, 0, 0);
				//FcnZebra.WrLabel("",this._infRegistro1.getAsString("disposicion").substring(0, 2), 630, 0, 1);
				
				if(this._infRegistro1.getAsString("estado").equals("Retirado")){
					if(this._infRegistro1.getAsString("disposicion").length()>2){
						FcnZebra.WrLabel("",this._infRegistro1.getAsString("disposicion").substring(0, 2), 630, 0, 1);
					}else{
						FcnZebra.WrLabel("",this._infRegistro1.getAsString("disposicion"), 630, 0, 1);
					}
				}				
			}			
		}
		
		this._infRegistro1 = ImpSQL.SelectDataRegistro("imp_medidor_material", "serie,cobro,marca,lectura,tipo", "id_orden='"+ordenTrabajo+"' AND tipo IN ('DEFINITIVO','PROVISIONAL','INSTALADO')");
		if(this._infRegistro1.size()>0){
			FcnZebra.WrSubTitulo("MEDIDOR INSTALADO",10,1.5,1);
			if(this._infRegistro1.getAsString("cobro").equals("SERVICIO DIRECTO")){
				FcnZebra.WrLabel("","SERVICIO DIRECTO  Se deja instalado el servicio directo.", 10, 0, 1);
			}else{
				FcnZebra.WrLabel("",this._infRegistro1.getAsString("tipo"), 10, 0, 1);
				FcnZebra.WrLabel("Marca:",this._infRegistro1.getAsString("marca"), 10, 0, 0);
				FcnZebra.WrLabel("Serie:",this._infRegistro1.getAsString("serie"), 260, 0, 1);
				FcnZebra.WrLabel("Lectura:",this._infRegistro1.getAsString("lectura"), 10, 0, 0);
				FcnZebra.WrLabel("Tipo",this._infRegistro1.getAsString("tipo"), 260, 0, 1);
				//FcnZebra.WrLabel("Tipo",ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "observacion_pad", "id_orden='"+ordenTrabajo+"'"), 10, 0, 1);
			}
		}	
			
		if(ImpSQL.ExistRegistros("amd_cambios_contadores","id_orden='"+ordenTrabajo+"' AND tipo IN ('R')")){
			this._infRegistro1 = ImpSQL.SelectDataRegistro("amd_cambios_contadores", "marca,serie,lectura,tipo", "id_orden='"+ordenTrabajo+"' AND tipo IN ('R')");
			FcnZebra.WrSubTitulo("MEDIDOR RETIRADO",10,2,1.2);		
			FcnZebra.WrLabel("Marca:",this._infRegistro1.getAsString("marca"), 10, 0, 0);
			FcnZebra.WrLabel("Serie:",this._infRegistro1.getAsString("serie"), 260, 0, 1);
			FcnZebra.WrLabel("Lectura:",this._infRegistro1.getAsString("lectura"), 10, 0, 0);
			FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("tipo"), 260, 0, 1);
		}
		
		if(ImpSQL.ExistRegistros("imp_acometida_material","id_orden='"+ordenTrabajo+"' AND tipo_ingreso IN ('Nueva')")){
			this._infRegistro1 = ImpSQL.SelectDataRegistro("imp_acometida_material", "fase,calibre,longitud", "id_orden='"+ordenTrabajo+"' AND tipo_ingreso IN ('Existente')");
			FcnZebra.WrSubTitulo("ACOMETIDA RETIRADA",10,1,1.2);
			if(this._infRegistro1.size()>0){
				FcnZebra.WrLabel("Fase:",this._infRegistro1.getAsString("fase"), 10, 0, 0);
				FcnZebra.WrLabel("Calibre:",this._infRegistro1.getAsString("calibre"), 260, 0, 0);
				FcnZebra.WrLabel("Longitud:",this._infRegistro1.getAsString("longitud"), 400, 0, 1);
			}else{
				FcnZebra.WrLabel("Fase:","", 10, 0, 0);
				FcnZebra.WrLabel("Calibre:","", 260, 0, 0);
				FcnZebra.WrLabel("Longitud:","", 400, 0, 1);
			}
		}
		
		if(ImpSQL.ExistRegistros("amd_observacion_materiales", "id_orden='"+ordenTrabajo+"'")){
			FcnZebra.WrSubTitulo("OBSERVACIONES",10,1,1.2);
			FcnZebra.JustInformation(ImpSQL.StrSelectShieldWhere("amd_observacion_materiales", "observacion", "id_orden='"+ordenTrabajo+"'"), 10, 0, 2);	
		}else{
			Toast.makeText(this.context,"Falta registrar la observacion",Toast.LENGTH_SHORT).show();
		}
		
		
		
		if(ImpSQL.ExistRegistros("amd_actas", "id_orden='"+ordenTrabajo+"' AND nombre_enterado IS NOT NULL")){
			FcnZebra.JustInformation("ATENDIO LA VISITA EL (LOS) REPRESENTANTE(S) LEGAL(ES) DEL INMUEBLE, EL SENOR(ES): "+ImpSQL.StrSelectShieldWhere("amd_actas", "nombre_enterado", "id_orden='"+ordenTrabajo+"'")+" IDENTIFICADO CON CC. "+ImpSQL.StrSelectShieldWhere("amd_actas", "cedula_enterado", "id_orden='"+ordenTrabajo+"'")+", QUIEN AUTORIZA EL COBRO DE LOS ANTERIORES MATERIALES.", 10, 0, 3);
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_actas", "nombre_enterado", "id_orden='"+ordenTrabajo+"'"), 10, 5, 0);
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NOM_TECNICO'"), 400, 0, 1);
			FcnZebra.WrLabel("CC.", ImpSQL.StrSelectShieldWhere("amd_actas", "cedula_enterado", "id_orden='"+ordenTrabajo+"'"), 10, 0, 0);
			FcnZebra.WrLabel("CC.", CedulaTecnico, 400, 0, 1);
			FcnZebra.WrLabel("", "Usuario", 10, 0, 0);
			FcnZebra.WrLabel("", "Empleado y/o Contratista", 400, 0, 1);
		}else if(ImpSQL.ExistRegistros("amd_actas", "id_orden='"+ordenTrabajo+"' AND nombre_testigo IS NOT NULL")){
			FcnZebra.JustInformation("ATENDIO LA VISITA EL (LOS) REPRESENTANTE(S) LEGAL(ES) DEL INMUEBLE, EL SENOR(ES): "+ImpSQL.StrSelectShieldWhere("amd_actas", "nombre_testigo", "id_orden='"+ordenTrabajo+"'")+" IDENTIFICADO CON CC. "+ImpSQL.StrSelectShieldWhere("amd_actas", "cedula_testigo", "id_orden='"+ordenTrabajo+"'")+", COMO TESTIGO.", 10, 0, 3);
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_actas", "nombre_testigo", "id_orden='"+ordenTrabajo+"'"), 10, 5, 0);
			FcnZebra.WrLabel("", ImpSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NOM_TECNICO'"), 400, 0, 1);
			FcnZebra.WrLabel("CC.", ImpSQL.StrSelectShieldWhere("amd_actas", "cedula_testigo", "id_orden='"+ordenTrabajo+"'"), 10, 0, 0);
			FcnZebra.WrLabel("CC.", CedulaTecnico, 400, 0, 1);
			FcnZebra.WrLabel("", "Testigo", 10, 0, 0);
			FcnZebra.WrLabel("", "Empleado y/o Contratista", 400, 0, 1);
		}
		
		FcnZebra.JustInformation("NO PAGAR NI REALIZAR NEGOCIACIONES CON EL OPERARIO POR NINGUN CONCEPTO. DENUNCIE CUALQUIER IRREGULARIDAD AL TELEFONO"+"6614000", 10, 1, 1);	
			
		switch(copiaImpresion){
			case 1:
				FcnZebra.WrLabel("", "Original Empresa", 10, 0, 1);
				break;
			case 2:
				FcnZebra.WrLabel("", "Copia Usuario", 10, 0, 1);
				break;
			case 3:
				FcnZebra.WrLabel("", "Copia Archivo", 10, 0, 1);
				break;
		}
		
		String ubicacion = ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "ubicacion", "id_orden='"+ordenTrabajo+"'");	
		if (ubicacion.equals("U")){
			FcnZebra.WrSubTitulo("UBICACION URBANA",10,0,1);
		}
		else FcnZebra.WrSubTitulo("UBICACION RURAL",10,0,1);
		
		FcnZebra.WrLabel("",ImpSQL.StrSelectShieldWhere("amd_param_sistema", "valor", "codigo='NPDA'")+" - "+ DT.GetDateTimeHora(), 10, 0, 1);
		FcnZebra.WrLabel("","Supervisor/Interventoria:________________________________________", 10, 0, 1);
		
		if((_copiaSistema)&&((copiaImpresion==1)||(copiaImpresion==2))){
			if(!this.ImpArchivos.ExistFolderOrFile(this._folderAplicacion+File.separator+ordenTrabajo)){
				this.ImpArchivos.MakeDirectory(ordenTrabajo);
			}
			int num_impresion = ImpSQL.IntSelectShieldWhere("amd_impresiones_inf", "id_impresion", "id_orden='"+ordenTrabajo+"'")+1;
			this.ImpArchivos.DoFile(ordenTrabajo, tipoImpresion+"_"+copiaImpresion+"_"+num_impresion,FcnZebra.getInfArchivo());
			
			this._infRegistro1.clear();
			this._infRegistro1.put("id_impresion", num_impresion);
			this.ImpSQL.UpdateRegistro("amd_impresiones_inf", this._infRegistro1, "id_orden='"+ordenTrabajo+"'");
		}		
		 MnBt.IntentPrint(this.Impresora,FcnZebra.getDoLabel());
		 FcnZebra.resetEtiqueta();
	}
	
	
	/**
	 * 
	 */
	public void FormatoResumenDiario(String _pda, String _tecnico){		
		FcnZebra.clearInformacion();	
		
		FcnZebra.WrTitulo("ELECTRIFICADORA DEL META E.S.P.", 0, 1);
		FcnZebra.DrawImage("IMAGES.PCX", 30, 25);
		FcnZebra.WrTitulo("RESUMEN ORDENES DEL DIA", 0, 1);
		FcnZebra.WrTitulo("NUMERO DE PDA: "+ _pda, 0, 1);
		FcnZebra.WrTitulo("FECHA: "+ this.DT.GetDateTimeHora(), 0, 3);		
		
		//Relacion de actas realizadas por el tecnico
		FcnZebra.WrTitulo("ACTAS REALIZADAS", 0, 1.5);		
		FcnZebra.WrSubTitulo("ITEM", 5, 0, 0);
		FcnZebra.WrSubTitulo("ORDEN", 75, 0, 0);
		FcnZebra.WrSubTitulo("CUENTA", 230, 0, 0);
		FcnZebra.WrSubTitulo("ESTADO", 650, 0, 1.2);	
		
		this._infTabla = ImpSQL.SelectData("amd_ordenes_trabajo","id_orden,estado,cuenta", "estado IN ('E','T','P','TA') ORDER BY id_orden");
		for(int i=0; i<this._infTabla.size();i++){
			FcnZebra.WrLabel(i+"", "", 5, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("id_orden"), "", 75, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("cuenta"), "", 230, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("estado"), "", 650, 0, 1);
		}
		
		//Relacion de sellos retirados por el tecnico
		FcnZebra.WrTitulo("RELACION DE SELLOS RETIRADOS", 1, 1.5);		
		FcnZebra.WrSubTitulo("ORDEN", 5, 0, 0);
		FcnZebra.WrSubTitulo("TIPO", 160, 0, 0);
		FcnZebra.WrSubTitulo("SERIE", 320, 0, 0);
		FcnZebra.WrSubTitulo("FECHA", 440, 0, 1.2);
		
		this._infTabla = ImpSQL.SelectData(	"amd_sellos as a JOIN amd_ordenes_trabajo as b ON a.id_orden = b.id_orden", 
											"a.id_orden, a.tipo, a.numero, a.fecha_ins", 
											"(a.estado = 'RETIRADO') AND (b.estado IN ('T','E')) ORDER BY a.fecha_ins, a.id_orden");
		for(int i=0; i<this._infTabla.size();i++){
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("id_orden"), "", 5, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("tipo"), "", 160, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("numero"), "", 320, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("fecha_ins"), "", 440, 0, 1);
		}
		
		
		//Relacion de medidores retirados por el tecnico
		FcnZebra.WrTitulo("RELACION DE MEDIDORES RETIRADOS", 1, 1.5);		
		FcnZebra.WrSubTitulo("ORDEN", 5, 0, 0);
		FcnZebra.WrSubTitulo("TIPO", 110, 0, 0);
		FcnZebra.WrSubTitulo("MARCA", 200, 0, 0);
		FcnZebra.WrSubTitulo("SERIE", 350, 0, 0);
		FcnZebra.WrSubTitulo("CUENTA", 550, 0, 1.2);
		
		this._infTabla = ImpSQL.SelectData(	"amd_cambios_contadores as a JOIN amd_ordenes_trabajo as b ON a.id_orden = b.id_orden", 
											"a.id_orden, a.tipo, a.marca, a.serie, a.cuenta", 
											"(a.tipo = 'R') AND (b.estado IN ('T','E')) ORDER BY a.id_orden");
		for(int i=0; i<this._infTabla.size();i++){
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("id_orden"), "", 5, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("tipo"), "", 110, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("marca"), "", 200, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("serie"), "", 350, 0, 0);
			FcnZebra.WrLabel(this._infTabla.get(i).getAsString("cuenta"), "", 550, 0, 1);
		}
		
		
		FcnZebra.WrLabel("Observaciones:", "______________________________________________", 10, 2, 1);
		FcnZebra.WrLabel("", "___________________________________________________________", 10, 0, 1);
		FcnZebra.WrLabel("", "___________________________________________________________", 10, 0, 1);
		FcnZebra.WrLabel("", "___________________________________________________________", 10, 0, 1);
		FcnZebra.WrLabel("", "___________________________________________________________", 10, 0, 1);
		
		FcnZebra.WrLabel("Firma Quien Entrega", "", 10, 1, 2);
		FcnZebra.WrLabel("Firma Quien Recibe", "", 10, 0, 2);
		FcnZebra.WrLabel(_pda+"-"+DT.GetDateTimeHora()+"-"+_tecnico, "", 10, 0, 3);
	
		MnBt.IntentPrint(this.Impresora,FcnZebra.getDoLabel());
		FcnZebra.resetEtiqueta();
	}    
}
