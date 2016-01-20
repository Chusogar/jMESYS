package jMESYS.drivers.Sinclair.Spectrum;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.JPanel;

import jMESYS.gui.jMESYSDisplay;

public class SpectrumDisplay extends jMESYSDisplay {
	
	// border emulation
	public JPanel borderPanel = new JPanel();
	public static Image imageBorder;
	public int newBorder = 7;
	public int oldBorder = -1;
	public int borderWidth = 0;
	
	private int totalWidth=0;
	private int totalHeight=0;
	
	//public JPanel innerPanel = new JPanel();
	
	boolean flashImage = true;
	private long lastFlashUpdate = 0;
	private long lastScreenUpdate = 0;
	public static int columns = 32;
	public byte[] arrayFichero;
	public static byte [] screenPixels;
	public static byte [] screenAttrs;
	//public static boolean[] flashAttrs = new boolean[6144];
	public static int scr2attr[] = new int[6144]; // 32 cols (32 bytes)*192 rows
	//public static int attr2scr[] = new int[768];
	
	// Original Spectrum Palette
	  public static final int[] DefaultSP48Palette = {
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
		
		setPalette(DefaultSP48Palette);
	}
	
	static {
        /*for (int i=0 ; i< 32 ; i++){
        	attr2scr[i]=-1;
        }*/
        
        for (int address = 0x4000; address < 0x5800; address++) {
            int row = ((address & 0xe0) >>> 5) | ((address & 0x1800) >>> 8);
            int col = address & 0x1f;
            int scan = (address & 0x700) >>> 8;

            scr2attr[address & 0x1fff] = 0x1800 + row * columns + col;
            /*if (attr2scr[(row * columns + col)] == -1){
            	attr2scr[(row * columns + col)] = (address);
            }*/
            //System.out.println((row * columns + col)+"="+attr2scr[(row * columns + col)]);
            //flashAttrs[address & 0x1fff] = ((scr2attr[address & 0x1fff] & 0x80) > 0);
        }       
        
        /*for (int i=0 ; i< 768 ; i++){
        	System.out.println(i+"="+attr2scr[i]);
        }*/
	}
	
	public int getX(int dir){
		return ((dir & 0x1f) << 3);
	}
	
	public int getY(int dir){		
        return ((dir & 0x00e0) >> 2) +
            ((dir & 0x0700) >> 8) +
            ((dir & 0x1800) >> 5);        
	}
	
	/*public void plot(int addr, byte[] mem) throws Exception {
		int x = getX(addr);
		int y = getY(addr);
		int pixel = mem[addr];
		
		int attr = mem[16384+(scr2attr[addr-16384])];
		Color tcolor=new Color(0);

		int inkColor=getInkColor(attr);

		int paperColor=getPaperColor(attr);

		
		for (int i=0 ; i<8 ; i++){
			Byte by = (byte) ( ( ( pixel & 0x80 ) != 0 ) ? 1 : 0 );
            

            if (by==1){

            	plot(x+i, y, new Color (inkColor));
            	
            } else {

            	plot(x+i, y, new Color (paperColor));

            }
			
			pixel = (byte) (pixel << 1);
		}
		
	}*/
	
	public void doPaintWholeScreen(Graphics gi) {
		//System.out.println("Pinto todo");
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
		        					        	        
		        int colorTinta=getPalette()[tinta];
		        int colorPapel=getPalette()[papel];
		        //System.out.println("Dir: "+((scr2attr[dir-16384])-6144));
		        
		        if (lastFlashUpdate == 0) {
	        		lastFlashUpdate = System.currentTimeMillis();
	        	}
		        
		        boolean flash=( (atributo & 0x80) > 0);
		        if (flash && flashImage){
		        	colorTinta=getPalette()[papel];
		        	colorPapel=getPalette()[tinta];
		        	//flashAttrs[(scr2attr[dir-16384])-6144] = false;					        	
		        }
		        
		        int x = ((dir & 0x1f) << 3);
                int y = ((dir & 0x00e0) >> 2) +
                    ((dir & 0x0700) >> 8) +
                    ((dir & 0x1800) >> 5);
		        	            
		        for(int k = 0; k < 8; k++){
		            Byte by = (byte) ( ( ( valorPix & 0x80 ) != 0 ) ? 1 : 0 );
			        
		            if (by==1){
		            	// paint all	
		            	gi.setColor(new Color(colorTinta));
		            	//plot(x+k,y, new Color(colorTinta));
		            } else {
		            	// paint all
		            	gi.setColor(new Color(colorPapel));
		            	//plot(x+k,y, new Color(colorPapel));
		            }
		            
		            scrPosition++;
		            // paint all
		            //gi.fillRect((((x+k))*pixelScale)+FRAME_MARGINH, ((y*pixelScale))+FRAME_MARGINV, pixelScale, pixelScale);
		            gi.fillRect((((x+k))*pixelScale), ((y*pixelScale)), pixelScale, pixelScale);
		            
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
        					        	        
        int colorTinta=getPalette()[tinta];
        int colorPapel=getPalette()[papel];
        //System.out.println("Dir: "+((scr2attr[dir-16384])-6144));
        
        if (lastFlashUpdate == 0) {
    		lastFlashUpdate = System.currentTimeMillis();
    	}
        
        boolean flash=( (attribute & 0x80) > 0);
        if (flash && flashImage){
        	colorTinta=getPalette()[papel];
        	colorPapel=getPalette()[tinta];
        	//flashAttrs[(scr2attr[dir-16384])-6144] = false;					        	
        }
		return colorTinta;
	}
	
	public int getPaperColor(int attribute) throws Exception {
		//if (true) throw new Exception ("Falta implementar getInkColor");
		int bright=(attribute & 0x40);
        int tinta=( (bright==0) ? (attribute & 0x7) : ((attribute & 0x7)+8));
        int papel=( (bright==0) ? ((attribute & 0x38) /8) : (((attribute & 0x38) /8)+8));
        					        	        
        int colorTinta=getPalette()[tinta];
        int colorPapel=getPalette()[papel];
        //System.out.println("Dir: "+((scr2attr[dir-16384])-6144));
        
        if (lastFlashUpdate == 0) {
    		lastFlashUpdate = System.currentTimeMillis();
    	}
        
        boolean flash=( (attribute & 0x80) > 0);
        if (flash && flashImage){
        	colorTinta=getPalette()[papel];
        	colorPapel=getPalette()[tinta];
        	//flashAttrs[(scr2attr[dir-16384])-6144] = false;					        	
        }
		
		return colorPapel;
	}
	
	public void paintImageScreen(Graphics gi, byte[] screenPixels, byte[] screenAttrs) {
	    System.out.println("Estoy en paintImageScreen de SpectrumDisplay");
        if (screenPixels != null){
        	//System.out.println("No es null paintImageScreen de SpectrumDisplay");
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
				        					        	        
				        int colorTinta=getPalette()[tinta];
				        int colorPapel=getPalette()[papel];
				        //System.out.println("Dir: "+((scr2attr[dir-16384])-6144));
				        
				        if (lastFlashUpdate == 0) {
			        		lastFlashUpdate = System.currentTimeMillis();
			        	}
				        
				        boolean flash=( (atributo & 0x80) > 0);
				        if (flash && flashImage){
				        	colorTinta=getPalette()[papel];
				        	colorPapel=getPalette()[tinta];
				        	//flashAttrs[(scr2attr[dir-16384])-6144] = false;					        	
				        }
				        	            
				        for(int k = 0; k < 8; k++){
				            Byte by = (byte) ( ( ( valorPix & 0x80 ) != 0 ) ? 1 : 0 );
				            
				            int x = ((dir & 0x1f) << 3);
			                int y = ((dir & 0x00e0) >> 2) +
			                    ((dir & 0x0700) >> 8) +
			                    ((dir & 0x1800) >> 5);

					        
				            if (by==1){
				            	// paint all
				            	gi.setColor(new Color(colorTinta));
				            	//plot(x+k, y, new Color(colorTinta));
				            	
				            } else {
				            	// paint all
				            	gi.setColor(new Color(colorPapel));
				            	//plot(x+k, y, new Color(colorPapel));
				            }
				            
				            scrPosition++;
				            // paint all
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
	
	public final void borderPaint() {
		//System.out.println("Border Paint");
		/*if ( oldBorder == newBorder ) {
			return;
		}*/
		oldBorder = newBorder;

		/*if ( borderWidth == 0 ) {
			return;
		}*/
		
		if (imageBorder==null){
			imageBorder=createImage((FRAME_WIDTH*pixelScale)+(FRAME_MARGINH*2), (FRAME_HEIGHT*pixelScale)+(FRAME_MARGINV*2));
		}
		
		Graphics parentGraphics = imageBorder.getGraphics();
		//System.out.println("Color Borde="+getPalette()[ newBorder + 8 ]);
		//Graphics parentGraphics = imagenTV.getGraphics();
		parentGraphics.setColor( new Color(getPalette()[ newBorder + 8 ]) );
		parentGraphics.fillRect( 0, 0,
			(FRAME_WIDTH*pixelScale) + FRAME_MARGINH*2,
			(FRAME_HEIGHT*pixelScale) + FRAME_MARGINV*2 );
	}
	
	public Image getTVImage(byte[] screenPixels, byte[] screenAttrs) {
		//System.out.println("getTVImage "+imagenTV);
		if (imagenTV != null){
		//Image imagenTV = new Image(FRAME_WIDTH*pixelScale, FRAME_HEIGHT*pixelScale);
		
		//Graphics gi = imagenTV.getGraphics();
		/*if (imageBorder == null) {
			System.out.println("Es nulo");
			System.out.println(borderPanel);
			imageBorder = borderPanel.createImage((FRAME_WIDTH*pixelScale)+(FRAME_MARGINH*2), (FRAME_HEIGHT*pixelScale)+(FRAME_MARGINV*2));
			System.out.println(imageBorder);
		}*/
		
		if (lastScreenUpdate == 0) {
			lastScreenUpdate = System.currentTimeMillis();
    	}
		//Graphics gi = imageBorder.getGraphics();
		Graphics gi = imagenTV.getGraphics();
        
        if (screenPixels != null){
	        int longo=screenPixels.length;
	        
	        
	        if (paintWholeScreen){
	        //if ((System.currentTimeMillis()-lastScreenUpdate) >= 400) {
	        	borderPaint();
	        	doPaintWholeScreen(gi);
	        	//paintWholeScreen = false;
	        	lastScreenUpdate = System.currentTimeMillis();
	        	
			}
	        
	        //gi.drawImage(getBufferedScreenImage(), FRAME_MARGINH, FRAME_MARGINV, FRAME_WIDTH*pixelScale, FRAME_HEIGHT*pixelScale, null);
	        
	        if ((System.currentTimeMillis()-lastFlashUpdate) >= 650) {
	        	//System.out.println("Hay flash");
	        	lastFlashUpdate = System.currentTimeMillis();
	        	flashImage = !flashImage;
	        }
	}
        }
        return imagenTV;
		//return imageBorder;
        //return getBufferedScreenImage();
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
	
	public int[] getBlock(int address){
		
		int[] colorBlock = new int[8];
		
		int highByte=(address & 0xFF00);
		int lowByte=(address & 0x00FF);
		
		int hByteClean = (highByte & 0xF8FF);
		//hByteClean = (highByte << 11);
		
		for (int i=0 ; i< 8 ; i++) {
			int sumB = i;
			sumB = sumB << 8;
			//System.out.println("Block "+i+":"+(hByteClean+sumB+lowByte));
			colorBlock[i] = (hByteClean+sumB+lowByte);
		}
		
		return colorBlock;
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

	
	public int[] getDefaultPalette() {
		return DefaultSP48Palette;
	}

	public int getTotalWidth() {
		if (totalWidth==0) totalWidth = (FRAME_WIDTH*pixelScale) + borderWidth*2;
		return totalWidth;
	}

	public int getTotalHeight() {
		if (totalHeight==0) totalHeight = (FRAME_HEIGHT*pixelScale) + borderWidth*2;
		return totalHeight;
	}

	public Image getBorderImage() {
		return imageBorder;
	}

	/*public void paintBlockBack(int addr, byte[] mem) throws Exception {

		int firstAddrScreen = (((addr & 0x300) << 3) + (addr & 0xff))+16384;
		
		int[] blckCol = getBlock( (firstAddrScreen) );
		
		for (int i=0 ; i<8 ; i++) {
			plot(blckCol[i], mem);
		}
		
	}*/
}
