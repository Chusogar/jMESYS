package jMESYS.drivers.Sinclair.Spectrum;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JPanel;

import jMESYS.gui.jMESYSDisplay;

public class SpectrumDisplay extends jMESYSDisplay {
	
	JPanel borderPanel = new JPanel();
	boolean flashImage = true;
	private long lastFlashUpdate = 0;
	public static int columns = 32;
	public byte[] arrayFichero;
	public static byte [] screenPixels;
	public static byte [] screenAttrs;
	//public static boolean[] flashAttrs = new boolean[6144];
	public static int scr2attr[] = new int[6144];
	
	//Vector con los valores correspondientes a los colores anteriores
	  public static final int[] Paleta = {
	    0x000000, /* negro */
	    0x0000bf, /* azul */
	    0xbf0000, /* rojo */
	    0xbf00bf, /* magenta */
	    0x00bf00, /* verde */
	    0x00bfbf, /* ciano */
	    0xbfbf00, /* amarillo */
	    0xbfbfbf, /* blanco */
	    0x000000, /* negro brillante */
	    0x0000ff, /* azul brillante */
	    0xff0000, /* rojo brillante	*/
	    0xff00ff, /* magenta brillante */
	    0x00ff00, /* verde brillante */
	    0x00ffff, /* ciano brillante */
	    0xffff00, /* amarillo brillante */
	    0xffffff  /* blanco brillante */
	  };
	
	public SpectrumDisplay(){
		super();
		FRAME_WIDTH=256; 
		FRAME_HEIGHT=192;
		FRAME_MARGINH=10;
		FRAME_MARGINV=10;
	}
	
	static {
        
        for (int address = 0x4000; address < 0x5800; address++) {
            int row = ((address & 0xe0) >>> 5) | ((address & 0x1800) >>> 8);
            int col = address & 0x1f;
            int scan = (address & 0x700) >>> 8;

            scr2attr[address & 0x1fff] = 0x1800 + row * columns + col;
            //flashAttrs[address & 0x1fff] = ((scr2attr[address & 0x1fff] & 0x80) > 0);
        }
	}
	
	public int getX(int dir){
		return ((dir & 0x1f) << 3);
	}
	
	public int getY(int dir){		
        return ((dir & 0x00e0) >> 2) +
            ((dir & 0x0700) >> 8) +
            ((dir & 0x1800) >> 5);        
	}
	
	public void plot(int addr) throws Exception {
		//System.out.println("Addr: "+addr);
		int x = getX(addr);
		int y = getY(addr);
		//System.out.println("x: "+x);
		//System.out.println("y: "+y);
		int pixel = screenPixels[addr-16384];
		
		//System.out.println("Pixel: "+pixel);
		int attr = screenAttrs[(scr2attr[addr-16384])-6144];
		
		//System.out.println("attr: "+attr);
		Color tcolor=new Color(0);
		//System.out.println("a");
		int inkColor=getInkColor(attr);
		//System.out.println("b");
		int paperColor=getPaperColor(attr);
		//System.out.println("c");
		
		for (int i=0 ; i<8 ; i++){
			Byte by = (byte) ( ( ( pixel & 0x80 ) != 0 ) ? 1 : 0 );
            

            if (by==1){

            	plot(x+i, y, new Color (inkColor));
            	//System.out.println("plot x="+(x+i)+" y="+y+" Color="+inkColor);
            	
            } else {

            	plot(x+i, y, new Color (paperColor));
            	//System.out.println("plot2 x="+(x+i)+" y="+y+" Color="+paperColor);
            }
			
			// aquí meteremos una llamada a un método plot genérico
			
			
			pixel = (byte) (pixel << 1);
		}
		
	}
	
	public void doPaintWholeScreen(Graphics gi) {
		int scrPosition = 0;
	    
	    for (int i=0 ; i<FRAME_HEIGHT ; i++){
	    	
	    	for (int j=0 ; j<columns ; j++){
	    		
		    	// teniendo el valor de la memoria convertimos a binario/pixels
		        byte valorPix=screenPixels[(i*columns)+j];
		        
		       		        
		        int dir=16384+(((i*columns)+(j)));
		        
		        int atributo=screenAttrs[(scr2attr[dir-16384])-6144];
		        boolean bb=false;
		        
		        int bright=(atributo & 0x40);
		        int tinta=( (bright==0) ? (atributo & 0x7) : ((atributo & 0x7)+8));
		        int papel=( (bright==0) ? ((atributo & 0x38) /8) : (((atributo & 0x38) /8)+8));
		        					        	        
		        int colorTinta=Paleta[tinta];
		        int colorPapel=Paleta[papel];
		        //System.out.println("Dir: "+((scr2attr[dir-16384])-6144));
		        
		        if (lastFlashUpdate == 0) {
	        		lastFlashUpdate = System.currentTimeMillis();
	        	}
		        
		        boolean flash=( (atributo & 0x80) > 0);
		        if (flash && flashImage){
		        	colorTinta=Paleta[papel];
		        	colorPapel=Paleta[tinta];
		        	//flashAttrs[(scr2attr[dir-16384])-6144] = false;					        	
		        }
		        	            
		        for(int k = 0; k < 8; k++){
		            Byte by = (byte) ( ( ( valorPix & 0x80 ) != 0 ) ? 1 : 0 );
		            
		            int x = ((dir & 0x1f) << 3);
	                int y = ((dir & 0x00e0) >> 2) +
	                    ((dir & 0x0700) >> 8) +
	                    ((dir & 0x1800) >> 5);

			        
		            if (by==1){

		            	gi.setColor(new Color(colorTinta));

		            	
		            } else {

		            	gi.setColor(new Color(colorPapel));

		            }
		            
		            scrPosition++;
		            
		            gi.fillRect(((x+k))*pixelScale, (y)*pixelScale, pixelScale, pixelScale);
		            valorPix = (byte) (valorPix << 1);
		            
		          }
		        
		        
		    }

	    }			
	}
	
	public int getInkColor(int attribute) throws Exception {
		//if (true) throw new Exception ("Falta implementar getInkColor");
		int bright=(attribute & 0x40);
        int tinta=( (bright==0) ? (attribute & 0x7) : ((attribute & 0x7)+8));
        int papel=( (bright==0) ? ((attribute & 0x38) /8) : (((attribute & 0x38) /8)+8));
        					        	        
        int colorTinta=Paleta[tinta];
        int colorPapel=Paleta[papel];
        //System.out.println("Dir: "+((scr2attr[dir-16384])-6144));
        
        if (lastFlashUpdate == 0) {
    		lastFlashUpdate = System.currentTimeMillis();
    	}
        
        boolean flash=( (attribute & 0x80) > 0);
        if (flash && flashImage){
        	colorTinta=Paleta[papel];
        	colorPapel=Paleta[tinta];
        	//flashAttrs[(scr2attr[dir-16384])-6144] = false;					        	
        }
		return colorTinta;
	}
	
	public int getPaperColor(int attribute) throws Exception {
		//if (true) throw new Exception ("Falta implementar getInkColor");
		int bright=(attribute & 0x40);
        int tinta=( (bright==0) ? (attribute & 0x7) : ((attribute & 0x7)+8));
        int papel=( (bright==0) ? ((attribute & 0x38) /8) : (((attribute & 0x38) /8)+8));
        					        	        
        int colorTinta=Paleta[tinta];
        int colorPapel=Paleta[papel];
        //System.out.println("Dir: "+((scr2attr[dir-16384])-6144));
        
        if (lastFlashUpdate == 0) {
    		lastFlashUpdate = System.currentTimeMillis();
    	}
        
        boolean flash=( (attribute & 0x80) > 0);
        if (flash && flashImage){
        	colorTinta=Paleta[papel];
        	colorPapel=Paleta[tinta];
        	//flashAttrs[(scr2attr[dir-16384])-6144] = false;					        	
        }
		
		return colorPapel;
	}
	
	public void paintImageScreen(Graphics gi, byte[] screenPixels, byte[] screenAttrs) {
	    
        if (screenPixels != null){
	        int longo=screenPixels.length;
	        int scrPosition = 0;
	        
	        if (true){
	        
	        
			    
			    for (int i=0 ; i<FRAME_HEIGHT ; i++){
			    	
			    	for (int j=0 ; j<columns ; j++){
			    		
				    	// teniendo el valor de la memoria convertimos a binario/pixels
				        byte valorPix=screenPixels[(i*columns)+j];
				        
				       		        
				        int dir=16384+(((i*columns)+(j)));
				        
				        int atributo=screenAttrs[(scr2attr[dir-16384])-6144];
				        boolean bb=false;
				        
				        int bright=(atributo & 0x40);
				        int tinta=( (bright==0) ? (atributo & 0x7) : ((atributo & 0x7)+8));
				        int papel=( (bright==0) ? ((atributo & 0x38) /8) : (((atributo & 0x38) /8)+8));
				        					        	        
				        int colorTinta=Paleta[tinta];
				        int colorPapel=Paleta[papel];
				        //System.out.println("Dir: "+((scr2attr[dir-16384])-6144));
				        
				        if (lastFlashUpdate == 0) {
			        		lastFlashUpdate = System.currentTimeMillis();
			        	}
				        
				        boolean flash=( (atributo & 0x80) > 0);
				        if (flash && flashImage){
				        	colorTinta=Paleta[papel];
				        	colorPapel=Paleta[tinta];
				        	//flashAttrs[(scr2attr[dir-16384])-6144] = false;					        	
				        }
				        	            
				        for(int k = 0; k < 8; k++){
				            Byte by = (byte) ( ( ( valorPix & 0x80 ) != 0 ) ? 1 : 0 );
				            
				            int x = ((dir & 0x1f) << 3);
			                int y = ((dir & 0x00e0) >> 2) +
			                    ((dir & 0x0700) >> 8) +
			                    ((dir & 0x1800) >> 5);

					        
				            if (by==1){

				            	gi.setColor(new Color(colorTinta));

				            	
				            } else {

				            	gi.setColor(new Color(colorPapel));

				            }
				            
				            scrPosition++;
				            
				            gi.fillRect(((x+k)), (y), 1, 1);
				            valorPix = (byte) (valorPix << 1);
				            
				          }
				        
				        
				    }

			    }
			    
			    
			}
	        
	        
	}
        
    }

	
	public Image getScreenImage() {		
		return getTVImage(screenPixels, screenAttrs);
	}
	
	public Image getTVImage(byte[] screenPixels, byte[] screenAttrs) {
		if (imagenTV != null){
		//Image imagenTV = new Image(FRAME_WIDTH*pixelScale, FRAME_HEIGHT*pixelScale);
		Graphics gi = imagenTV.getGraphics();
        
        if (screenPixels != null){
	        int longo=screenPixels.length;
	        
	        
	        if (paintWholeScreen){
	        	doPaintWholeScreen(gi);
	        	//paintWholeScreen = false;
			}
	        
	        if ((System.currentTimeMillis()-lastFlashUpdate) >= 650) {
	        	//System.out.println("Hay flash");
	        	lastFlashUpdate = System.currentTimeMillis();
	        	flashImage = !flashImage;
	        }
	}}
        return imagenTV;
    }
	
	public void loadScreen(String name) {
		
		arrayFichero = cargaPantalla(name, FRAME_HEIGHT*columns+768);
		
		loadScreen(arrayFichero);
	}
	
	private byte[] cargaPantalla(String name, int size){
		System.out.println("File: " + name);
		
		byte[] buffer = new byte[size];
	    int offs = 0;
	    try {
	      InputStream stream = null;
	      try {
	    	  if (name.startsWith("http")){
	    		  stream = new URL(name).openStream();
	    	  } else {
	    		  stream = openFile(name);
	    	  }
	    	
	        while (size > 0) {
	          int read = stream.read(buffer,offs,size);
	          if (read == -1)
	            break;
	          else {
	            offs += read;
	            size -= read;
	          }
	        }
	      } finally {
	        if (stream != null)
	          stream.close();
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    if (offs < buffer.length) {
	      byte[] result = new byte[offs];
	      System.arraycopy(buffer,0,result,0,offs);
	      buffer = result;
	    }
	    return buffer;
	}
	
	public void getBlock(int address){
		
		int highByte=(address & 0xFF00);
		int lowByte=(address & 0x00FF);
		
		int hByteClean = (highByte & 0xF8FF);
		//hByteClean = (highByte << 11);
		
		for (int i=0 ; i< 8 ; i++) {
			int sumB = i;
			sumB = sumB << 8;
			System.out.println("Block "+i+":"+(hByteClean+sumB+lowByte));
		}
	}
	
	public void initScreen(){
		int tamArr = FRAME_HEIGHT*columns+768;
		arrayFichero = new byte[tamArr];
		
		for (int i=0 ; i<tamArr ; i++){
			arrayFichero[i]=0;
		}
		
		loadScreen(arrayFichero);
	}

	public void loadScreen(byte[] array) {
		
		arrayFichero = array;
		
		screenPixels=new byte[(FRAME_WIDTH*FRAME_HEIGHT)];
		System.arraycopy(arrayFichero, 0, screenPixels, 0, (FRAME_HEIGHT*columns));
		// copiamos los atributos
		screenAttrs=new byte[768];
		System.arraycopy(arrayFichero, (FRAME_HEIGHT*columns), screenAttrs, 0, 768);
		// copiamos el flash
		/*flashAttrs=new boolean[768];
		
		for (int address = 0; address < 768; address++) {	            
            flashAttrs[address & 0x1fff] = ((scr2attr[address & 0x1fff] & 0x80) > 0);
        }*/
		
		// repintamos
		repaint();
	}
}
