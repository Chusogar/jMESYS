package jMESYS.drivers.Sinclair.Spectrum.formats;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Vector;

import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public class FormatTZX extends FileFormat{
	
	// extension
	private static String strExtension = ".TZX";
	
	private boolean painted = false;
		
	public String getExtension() {
		return strExtension;
	}

	public void loadFormat(String name, InputStream in, Spectrum48k computer) throws Exception {
		System.out.println("LOAD TZX");
		
		int pos = 0;
				
		byte[] b = new byte[1];
		int j = 0;
		String S = "";
		
		try {
		for(int i = 0; i < 7; i++){
			j = in.read(b, 0, 1);
			j = ((b[0] + 256) & 0xff);
			S = S.concat(S.valueOf((char) j));
		}
		
		System.out.println("Cabecera: "+S);
		in.skip(1);
		j = in.read(b, 0, 1);
		int mayor = ((b[0] + 256) & 0xff);
		j = in.read(b, 0, 1);
		int menor = ((b[0] + 256) & 0xff);
		
		System.out.println("Versión:  " + mayor + "." + menor);
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		
		//byte data[] = cinta_TZX( in );
		cinta_TZX( in,  computer );			
				
	}

	public Image getScreen(String name, InputStream is, jMESYSDisplay display, Graphics g) throws Exception {
		System.out.println("getScreen TZX!");
		
		
		if (!painted){
        	System.out.println("TZX NO PINTADO");
        	g.setColor(Color.RED);
			g.fillRect(0, 0, display.getWidth(), display.getHeight());
        }
		painted=false;
		
		return null;
	}

	public String getFileName() {
		return "D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/Play Schweppes.tzx";
	}
	
	//private byte[] cinta_TZX(InputStream entrada){
	private void cinta_TZX(InputStream entrada, Spectrum48k computer){
        try{
        	System.out.println("cinta_TZX");
        	byte[] dataTape = null;
        	int posiTape = 0;
          int j = 0;
          int mayor, menor;
          int pausa = 0;
          byte[] b = new byte[1];
          String S = "";
          //DefaultMutableTreeNode datos = null;
          //DefaultMutableTreeNode aux = null;
          Vector v = new Vector();
          while(entrada.available() > 0){
            j = entrada.read(b, 0, 1);
            j = ((b[0] + 256) & 0xff);
            switch(j){
              case 0x10:{
                j = entrada.read(b, 0, 1);
                pausa = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                pausa = pausa | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                j = entrada.read(b, 0, 1);
                j = ((b[0] + 256) & 0xff);
                if((j == 0) && (lon == 0x13)){
                  //bloque.add(datos = new DefaultMutableTreeNode("Bloque Velocidad Estandar: Cabecera"));
                	System.out.println("1-Bloque Velocidad Estandar: Cabecera");
                	//dataTape = new byte[0x13];
                  //datos.add(new DefaultMutableTreeNode("Cabecera"));
                	dataTape = buildHeader(entrada, lon);
                  
                  v.addElement(dataTape);
                }else if(j == 0xff){
                  //bloque.add(datos = new DefaultMutableTreeNode("Bloque Velocidad Estandar: Datos"));
                	System.out.println("2-Bloque Velocidad Estandar: Datos");
                	
                	dataTape = buildDataBlock(entrada, lon, j);

                     v.addElement(dataTape);
                }else{
                  //bloque.add(datos = new DefaultMutableTreeNode("Bloque Velocidad Estandar: Clave"));
                	System.out.println("3-Bloque Velocidad Estandar: Clave");

                  //datos.add(new DefaultMutableTreeNode("Datos con clave"));
                	System.out.println("Datos con clave");
                  //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
                	System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                  entrada.skip(lon - 1);
                }
                S = "Pausa tras bloque:  " + Integer.toString(pausa) + " ms";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                break;
              }
              case 0x11:{
                //bloque.add(datos = new DefaultMutableTreeNode("Bloque Datos Carga Turbo"));
            	  System.out.println("4-Bloque Datos Carga Turbo");

                /*j = entrada.read(b, 0, 1);
                int piloto = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                piloto = piloto | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int sync_1 = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                sync_1 = sync_1 | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int sync_2 = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                sync_2 = sync_2 | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int cero = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                cero = cero | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int uno = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                uno = uno | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int lon_p = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon_p = lon_p | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int last_b = ((b[0] + 256) & 0xff);

                j = entrada.read(b, 0, 1);
                pausa = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                pausa = pausa | (((b[0] + 256) & 0xff) << 8);
                */

                entrada.skip(15);
                
            	j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 16);
                j = entrada.read(b, 0, 1);
                j = ((b[0] + 256) & 0xff);
                if((j == 0) && (lon == 0x13)){
                  //datos.add(new DefaultMutableTreeNode("Cabecera"));
                	System.out.println("Cabecera");
                  /*j = entrada.read(b, 0, 1);
                  j = ((b[0] + 256) & 0xff);
                  S = "Tipo:  ";
                  switch (j){
                    case 0:{  S = S.concat("Programa"); break;}
                    case 1:{  S = S.concat("Vector Números"); break;}
                    case 2:{  S = S.concat("Vector Caracteres"); break;}
                    case 3:{  S = S.concat("Archivo de código"); break;}
                    default:{  S = S.concat("Desconocido"); break;}
                  }
                  //datos.add(new DefaultMutableTreeNode(S));
                  System.out.println(S);
                  S = "Nombre:  \"";
                  for(int i = 0; i <10; i++){
                    j = entrada.read(b, 0, 1);
                    j = ((b[0] + 256) & 0xff);
                    S = S.concat(S.valueOf((char) j));
                  }
                  S = S.concat("\"");
                  //datos.add(new DefaultMutableTreeNode(S));
                  System.out.println(S);
                  S = "Longitud Cabecera:  " + Integer.toHexString(lon).toUpperCase() + "H";
                  //datos.add(new DefaultMutableTreeNode(S));
                  System.out.println(S);
                  entrada.skip(lon - 12);*/
                	dataTape = buildHeader(entrada, lon);
                	v.addElement(dataTape);
                }else if(j == 0xff){
                  System.out.println("Datos TURBO "+lon);
                  /*System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                  entrada.skip(lon - 1);*/
                  
                	dataTape = buildDataBlockTurbo(entrada, lon, j);
                	//entrada.skip(1);
                    v.addElement(dataTape);
                }else{
                  System.out.println("Datos con clave "+(j&0xFF));
                  /*System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                  entrada.skip(lon - 1);*/
                  dataTape = buildDataBlock(entrada, lon, j);

                  v.addElement(dataTape);
                }
               /* S = "Longitud pulso PILOT:  " + Integer.toString(piloto) + " Ts";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Longitud tono PILOT:  " + Integer.toString(lon_p) + " pulsos";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Longitud primer pulso SYNC:  " + Integer.toString(sync_1) + " Ts";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Longitud segundo pulso SYNC:  " + Integer.toString(sync_2) + " Ts";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Longitud pulso bit 0:  " + Integer.toString(cero) + " Ts";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Longitud pulso bit 1:  " + Integer.toString(uno) + " Ts";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Bits empleados en último byte:  " + Integer.toString(last_b) + " bits";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Pausa tras bloque:  " + Integer.toString(pausa) + " ms";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);*/
                break;
              }
              case 0x12:{
                //bloque.add(datos = new DefaultMutableTreeNode("Tono"));
            	  System.out.println("Tono");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                //datos.add(new DefaultMutableTreeNode("Longitud pulso en T-Estados: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Longitud pulso en T-Estados: " + Integer.toHexString(lon).toUpperCase() + "H");
                j = entrada.read(b, 0, 1);
                lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                //datos.add(new DefaultMutableTreeNode("Número de pulsos: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Número de pulsos: " + Integer.toHexString(lon).toUpperCase() + "H");
                break;
              }
              case 0x13:{
                //bloque.add(datos = new DefaultMutableTreeNode("Secuencia Pulsos"));
            	  System.out.println("Secuencia Pulsos");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                //datos.add(new DefaultMutableTreeNode("Número de pulsos: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Número de pulsos: " + Integer.toHexString(lon).toUpperCase() + "H");
                for(int i = 0; i < lon; i++){
                  j = entrada.read(b, 0, 1);
                  int lonP = ((b[0] + 256) & 0xff);
                  j = entrada.read(b, 0, 1);
                  lonP = lonP | (((b[0] + 256) & 0xff) << 8);
                  //datos.add(new DefaultMutableTreeNode("Longitud pulso en T-Estados: " + Integer.toHexString(lonP).toUpperCase() + "H"));
                  System.out.println("Longitud pulso en T-Estados: " + Integer.toHexString(lonP).toUpperCase() + "H");
                }
                break;
              }
              case 0x14:{
                //bloque.add(datos = new DefaultMutableTreeNode("Bloque Datos Puro"));
            	  System.out.println("Bloque Datos Puro");

                j = entrada.read(b, 0, 1);
                int cero = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                cero = cero | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int uno = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                uno = uno | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int last_b = ((b[0] + 256) & 0xff);

                j = entrada.read(b, 0, 1);
                pausa = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                pausa = pausa | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 16);
                //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                //entrada.skip(lon);
                buildDataBlock(entrada, lon, 0xFF);
                S = "Longitud pulso bit 0:  " + Integer.toString(cero) + " Ts";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Longitud pulso bit 1:  " + Integer.toString(uno) + " Ts";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Bits empleados en último byte:  " + Integer.toString(last_b) + " bits";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Pausa tras bloque:  " + Integer.toString(pausa) + " ms";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                break;
              }
              case 0x15:{
                //bloque.add(datos = new DefaultMutableTreeNode("Grabado Directo"));
            	  System.out.println("Grabado Directo");

                j = entrada.read(b, 0, 1);
                int uno = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                uno = uno | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                pausa = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                pausa = pausa | (((b[0] + 256) & 0xff) << 8);

                j = entrada.read(b, 0, 1);
                int last_b = ((b[0] + 256) & 0xff);

                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 16);
                //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                S = "T estados por sample:  " + Integer.toString(uno) + " Ts";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Bits empleados en último byte:  " + Integer.toString(last_b) + " bits";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                S = "Pausa tras bloque:  " + Integer.toString(pausa) + " ms";
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                entrada.skip(lon);
                break;
              }
              case 0x16:{
                //bloque.add(datos = new DefaultMutableTreeNode("Bloque Datos Tipo ROM C64"));
            	  System.out.println("Bloque Datos Tipo ROM C64");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 16);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 24);
                //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon - 4).toUpperCase() + "H"));
                System.out.println("Longitud bloque: " + Integer.toHexString(lon - 4).toUpperCase() + "H");
                entrada.skip(lon - 4);
                break;
              }
              case 0x17:{
                //bloque.add(datos = new DefaultMutableTreeNode("Bloque Datos Cinta Turbo C64"));
            	  System.out.println("Bloque Datos Cinta Turbo C64");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 16);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 24);
                //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon - 4).toUpperCase() + "H"));
                System.out.println("Longitud bloque: " + Integer.toHexString(lon - 4).toUpperCase() + "H");
                entrada.skip(lon - 4);
                break;
              }
              case 0x20:{
                //bloque.add(datos = new DefaultMutableTreeNode("Pausa"));
            	  System.out.println("Pausa");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                //datos.add(new DefaultMutableTreeNode("Pausa en ms: " + Integer.toString(lon) + " ms"));
                System.out.println("Pausa en ms: " + Integer.toString(lon) + " ms");
                break;
              }
              case 0x21:{
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                S = "\"";
                for(int i = 0; i < lon; i++){
                  j = entrada.read(b, 0, 1);
                  j = ((b[0] + 256) & 0xff);
                  S = S.concat(S.valueOf((char) j));
                }
                S = S.concat("\"");
                //bloque.add(datos = new DefaultMutableTreeNode("Grupo " + S));
                System.out.println("Grupo " + S);
                break;
              }
              case 0x22:{
                //bloque.add(new DefaultMutableTreeNode("Fin Grupo"));
            	  System.out.println("Fin Grupo");
                break;
              }
              case 0x23:{
                //bloque.add(datos = new DefaultMutableTreeNode("Salto a Bloque"));
            	  System.out.println("Salto a Bloque");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                //datos.add(new DefaultMutableTreeNode("Salto: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Salto: " + Integer.toHexString(lon).toUpperCase() + "H");
                break;
              }
              case 0x24:{
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                //bloque.add(new DefaultMutableTreeNode("Bucle. Numero de repeticiones: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Bucle. Numero de repeticiones: " + Integer.toHexString(lon).toUpperCase() + "H");
                break;
              }
              case 0x25:{
                //bloque.add(new DefaultMutableTreeNode("Fin Bucle"));
            	  System.out.println("Fin Bucle");
                break;
              }
              case 0x26:{
                //bloque.add(datos = new DefaultMutableTreeNode("Secuencia Llamadas"));
            	  System.out.println("Secuencia Llamadas");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                //datos.add(new DefaultMutableTreeNode("Numero de llamadas: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Numero de llamadas: " + Integer.toHexString(lon).toUpperCase() + "H");
                for(int i = 0; i < lon; i++){
                  j = entrada.read(b, 0, 1);
                  int lonP = ((b[0] + 256) & 0xff);
                  j = entrada.read(b, 0, 1);
                  lonP = lonP | (((b[0] + 256) & 0xff) << 8);
                  //datos.add(new DefaultMutableTreeNode("Llamada: " + Integer.toHexString(lonP).toUpperCase() + "H"));
                  System.out.println("Llamada: " + Integer.toHexString(lonP).toUpperCase() + "H");
                }
                break;
              }
              case 0x27:{
                //bloque.add(datos = new DefaultMutableTreeNode("Retorno Secuencia Llamadas"));
            	  System.out.println("Retorno Secuencia Llamadas");
                break;
              }
              case 0x28:{
                //bloque.add(datos = new DefaultMutableTreeNode("Bloque Seleccion"));
            	  System.out.println("Bloque Seleccion");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                j = entrada.read(b, 0, 1);
                int data = ((b[0] + 256) & 0xff);
                //datos.add(new DefaultMutableTreeNode("Numero de selecciones: " + Integer.toHexString(data).toUpperCase() + "H"));
                System.out.println("Numero de selecciones: " + Integer.toHexString(data).toUpperCase() + "H");
                entrada.skip(lon - 1);
                break;
              }
              case 0x2A:{
                //bloque.add(datos = new DefaultMutableTreeNode("Parar Cinta"));
            	  System.out.println("Parar Cinta");
                entrada.skip(4);
                break;
              }
              case 0x30:{
                //bloque.add(datos = new DefaultMutableTreeNode("Descripcion Texto"));
            	  System.out.println("Descripcion Texto");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                S = "\"";
                for(int i = 0; i < lon; i++){
                  j = entrada.read(b, 0, 1);
                  j = ((b[0] + 256) & 0xff);
                  S = S.concat(S.valueOf((char) j));
                }
                S = S.concat("\"");
                //datos.add(new DefaultMutableTreeNode("Texto: " + S));
                System.out.println("Texto: " + S);
                break;
              }
              case 0x31:{
                //bloque.add(datos = new DefaultMutableTreeNode("Bloque Mensaje"));
            	  System.out.println("Bloque Mensaje");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                //datos.add(new DefaultMutableTreeNode("Tiempo mostrar: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Tiempo mostrar: " + Integer.toHexString(lon).toUpperCase() + "H");
                j = entrada.read(b, 0, 1);
                lon = ((b[0] + 256) & 0xff);
                //datos.add(new DefaultMutableTreeNode("Longitud mensaje: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Longitud mensaje: " + Integer.toHexString(lon).toUpperCase() + "H");
                entrada.skip(lon);
                break;
              }
              case 0x32:{
                //bloque.add(datos = new DefaultMutableTreeNode("Informacion Archivo"));
            	  System.out.println("Informacion Archivo");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                j = entrada.read(b, 0, 1);
                int lonP = ((b[0] + 256) & 0xff);
                //datos.add(new DefaultMutableTreeNode("Numero cadenas texto: " + Integer.toHexString(lonP).toUpperCase() + "H"));
                System.out.println("Numero cadenas texto: " + Integer.toHexString(lonP).toUpperCase() + "H");
                entrada.skip(lon - 1);
                break;
              }
              case 0x33:{
                //bloque.add(datos = new DefaultMutableTreeNode("Hardware"));
            	  System.out.println("Hardware");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                //datos.add(new DefaultMutableTreeNode("Numero tipos hardware: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Numero tipos hardware: " + Integer.toHexString(lon).toUpperCase() + "H");
                entrada.skip(lon * 3);
                break;
              }
              case 0x34:{
                //bloque.add(datos = new DefaultMutableTreeNode("Informacion Emulacion"));
            	  System.out.println("Informacion Emulacion");
                entrada.skip(0x08);
                break;
              }
              case 0x35:{
                //bloque.add(datos = new DefaultMutableTreeNode("Bloque Configurado a Medida"));
            	  System.out.println("Bloque Configurado a Medida");
                S = "Nombre:  \"";
                for(int i = 0; i <10; i++){
                  j = entrada.read(b, 0, 1);
                  j = ((b[0] + 256) & 0xff);
                  S = S.concat(S.valueOf((char) j));
                }
                S = S.concat("\"");
                //datos.add(new DefaultMutableTreeNode(S));
                System.out.println(S);
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 16);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 24);
                //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                entrada.skip(lon);
                break;
              }
              case 0x40:{
                //bloque.add(datos = new DefaultMutableTreeNode("Bloque Snapshot"));
            	  System.out.println("Bloque Snapshot");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                if(lon == 0x00){
                  //datos.add(new DefaultMutableTreeNode("Tipo:  Z80"));
                	System.out.println("Tipo:  Z80");
                }else if(lon == 0x01){
                  //datos.add(new DefaultMutableTreeNode("Tipo:  SNA"));
                	System.out.println("Tipo:  SNA");
                }else{
                  //datos.add(new DefaultMutableTreeNode("Tipo:  Desconocido"));
                	System.out.println("Tipo:  Desconocido");
                }
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 16);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 24);
                //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                entrada.skip(lon);
                break;
              }
              case 0x5A:{
                b = new byte[1];
                j = 0;
                S = "";
                for(int i = 0; i < 6; i++){
                  j = entrada.read(b, 0, 1);
                  j = ((b[0] + 256) & 0xff);
                  S = S.concat(S.valueOf((char) j));
                }
                //la_cinta.add(bloque = new DefaultMutableTreeNode(S));
                System.out.println(S);
                entrada.skip(1);
                j = entrada.read(b, 0, 1);
                mayor = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                menor = ((b[0] + 256) & 0xff);
                //bloque.add(new DefaultMutableTreeNode("Versión:  " + mayor + "." + menor));
                System.out.println("Versión:  " + mayor + "." + menor);
                break;
              }
              default:{
                //bloque.add(datos = new DefaultMutableTreeNode("Bloque Desconocido"));
            	  System.out.println("Bloque Desconocido");
                j = entrada.read(b, 0, 1);
                int lon = ((b[0] + 256) & 0xff);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 8);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 16);
                j = entrada.read(b, 0, 1);
                lon = lon | (((b[0] + 256) & 0xff) << 24);
                //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
                System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
                entrada.skip(lon);
                return;
              }
            }
            
            FormatTXT fTXT = new FormatTXT();
			try {
				System.out.println("Cargo TXT");
				fTXT.loadFormat("kk", new FileInputStream(new File("/b.txt")), computer);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
          }
          int tamArrayTotal = 0;
          int numBlocks = v.size();
          
        //numBlocks=2;
          
          System.out.println("Número bloques="+numBlocks);
          
          for (int i=0 ; i<numBlocks ; i++) {
        	  byte[] arr = (byte[]) v.elementAt(i);
        	  tamArrayTotal += arr.length;
        	  System.out.println("Tamaño bloque #"+i+"="+arr.length);
        	  /*if (arr.length==6916){
        		  
        	  }*/
        	  arr = null;
          }
          
          System.out.println("Tamaño TOTAL="+tamArrayTotal);
          
          byte[] dataTap = new byte[tamArrayTotal];
          int posi = 0;
          for (int i=0 ; i<numBlocks ; i++) {
        	  byte[] arr = (byte[]) v.elementAt(i);
        	  int tama = arr.length;
        	  for (j=0;j<tama;j++){
        		  dataTap[posi]=arr[j];
        		  posi++;
        	  }
          }
          System.out.println();
          for (j=0;j<tamArrayTotal;j++){
        	  System.out.print(dataTap[j]+" ");    		  
    	  }
          
          System.out.println();
          // cargo el fichero
          computer.tape(dataTap, true);
        }catch(Exception ex){
          ex.printStackTrace(System.out);
        }
      }

	private byte[] buildDataBlock(InputStream entrada, int lon, int type) throws Exception {
		byte[] dataTape = new byte[lon+2];
		byte[] b = new byte[1];
		int posiTape=0;
		int j=0;
		try{
    	dataTape[posiTape]=(byte) (lon & 0xFF); posiTape++;
    	//dataTape[posiTape]=(byte) 0x00; posiTape++;
    	dataTape[posiTape]=(byte)( (lon & 0xFF00)>>8); posiTape++;
      //datos.add(new DefaultMutableTreeNode("Datos"));
    	System.out.println("Datos");
    	dataTape[posiTape]=(byte) (type); posiTape++;
      //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
    	System.out.println("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H");
      //entrada.skip(lon - 1);
    	 int longo=lon-1;
         for (int i=0;i<longo;i++){
       	  j = entrada.read(b, 0, 1);
       	  dataTape[posiTape]=(byte) (b[0] & 0xFF); posiTape++;
         }
         posiTape=0;
         longo=dataTape.length;
         System.out.println("CONTENIDO BLOQUE");
         for (int i=0;i<longo;i++){
       	  System.out.print((dataTape[i] & 0xFF) +" "); 
         }
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
         return dataTape;
	}
	
	private byte[] buildDataBlockTurbo(InputStream entrada, int lon, int type) throws Exception {
		int longo=lon-1;
		//lon+=2;
		byte[] dataTape = new byte[lon+2];
		byte[] b = new byte[1];
		int posiTape=0;
		int j=0;
		try{
    	dataTape[posiTape]=(byte) (lon & 0xFF); posiTape++;
    	//dataTape[posiTape]=(byte) 0x00; posiTape++;
    	dataTape[posiTape]=(byte)( (lon & 0xFF00)>>8); posiTape++;
      //datos.add(new DefaultMutableTreeNode("Datos"));
    	System.out.println("Datos");
    	dataTape[posiTape]=(byte) (type); posiTape++;
    	//dataTape[posiTape]=(byte) (0x03); posiTape++;
      //datos.add(new DefaultMutableTreeNode("Longitud bloque: " + Integer.toHexString(lon).toUpperCase() + "H"));
    	System.out.println("Longitud bloque TURBO: " + lon);
      //entrada.skip(lon - 1);
    	//FileOutputStream fos = new FileOutputStream(new File("/A.scr"),true);
    	 
         for (int i=0;i<longo;i++){
       	  j = entrada.read(b, 0, 1);
       	  dataTape[posiTape]=(byte) (b[0] & 0xFF); posiTape++;
       	  /*if ((lon ==6914)){
       		System.out.println("Pinto en Fichero");
       		  fos.write(b);
       	  }*/
         }
         //fos.close();*/
         posiTape=0;
         longo=dataTape.length;
         System.out.println("CONTENIDO BLOQUE");
         for (int i=0;i<longo;i++){
       	  System.out.print((dataTape[i] & 0xFF) +" "); 
         }
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
         return dataTape;
	}

	private byte[] buildHeader(InputStream entrada, int lon) throws Exception {
		System.out.println("Cabecera");
		int posiTape = 0;
		int j =0;
		String S = "";
		
		byte[] dataTape = null;
		byte[] b = new byte[1];
    	
    	dataTape = new byte[lon+2];
    	dataTape[posiTape]=(byte) (lon & 0xFF); posiTape++;
    	dataTape[posiTape]=(byte) 0x00; posiTape++;
    	
    	// es una cabecera 0x00 y si son datos es 0xFF
    	dataTape[posiTape]=(byte) 0x00; posiTape++;
    	
      j = entrada.read(b, 0, 1);
      j = ((b[0] + 256) & 0xff);
      S = "Tipo:  ";
      dataTape[posiTape]=(byte) (j & 0xFF); posiTape++;
      switch (j){
        case 0:{  S = S.concat("Programa"); break;}
        case 1:{  S = S.concat("Vector Números"); break;}
        case 2:{  S = S.concat("Vector Caracteres"); break;}
        case 3:{  S = S.concat("Archivo de código"); break;}
        default:{  S = S.concat("Desconocido"); break;}
      }
      //datos.add(new DefaultMutableTreeNode(S));
      System.out.println(S);
      S = "Nombre:  \"";
      for(int i = 0; i <10; i++){
        j = entrada.read(b, 0, 1);
        dataTape[posiTape] = (byte) (b[0] & 0xFF); posiTape++;
        j = ((b[0] + 256) & 0xff);
        S = S.concat(S.valueOf((char) j));
      }
      S = S.concat("\"");
      //datos.add(new DefaultMutableTreeNode(S));
      System.out.println(S);
      S = "Longitud Cabecera:  " + Integer.toHexString(lon).toUpperCase() + "H";
      //datos.add(new DefaultMutableTreeNode(S));
      System.out.println(S);
      //entrada.skip(lon - 12);
      int longo=lon-12;
      for (int i=0;i<longo;i++){
    	  j = entrada.read(b, 0, 1);
    	  dataTape[posiTape]=(byte) (b[0] & 0xFF); posiTape++;
      }
      
      System.out.println("Cabecera posi="+posiTape);
      posiTape=0;
      longo=dataTape.length;
      System.out.println();
      for (int i=0;i<longo;i++){
    	  System.out.print((dataTape[i] & 0xFF) +" "); 
      }
      
      return dataTape;
	}

}
