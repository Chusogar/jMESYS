package jMESYS.drivers.Sinclair.Spectrum.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageConsumer;

import jMESYS.gui.jMESYSDisplay;

public class SpectrumDisplay extends jMESYSDisplay {
	
	/* screen */

	public static final int Mh = 6;
	public static final int Mv = 6;
	public static final int W = 256 + 8*Mh*2; // 352
	public static final int H = 192 + 8*Mv*2; // 288
	public int width=W;
	public int height=H;
	
	public int brdchg_ud;
	public int brdchg_l;
	public int brdchg_r;
	
	
	public byte border = (byte)7;		// border color
	public byte border_solid = -1;		// nonnegative: color of whole border
	public static final int BORDER_START = -224*8*(Mv) - 4*(Mh) + 4;
	
	public final int screen[] = new int[W/8*H];	// canonicalized scr. content
	public final int scrchg[] = new int[24];	// where the picture changed
	
	// APAÑO
	public static int scr2attr[] = new int[6144]; // 32 cols (32 bytes)*192 rows
	static {
        /*for (int i=0 ; i< 32 ; i++){
        	attr2scr[i]=-1;
        }*/
        
        for (int address = 0x4000; address < 0x5800; address++) {
            int row = ((address & 0xe0) >>> 5) | ((address & 0x1800) >>> 8);
            int col = address & 0x1f;
            int scan = (address & 0x700) >>> 8;

            scr2attr[address & 0x1fff] = 0x1800 + row * 32 + col;
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

	public SpectrumDisplay() {
		super(W, H);
	}
	
	public void force_redraw()
	{
		if (screen != null){
			for(int i=0; i<screen.length; i++)
				screen[i] = 0x1FF;	// impossible value
			border_solid = -1;
		}
	}
	
	public int[] getDefaultPalette() {
		return DefaultSP48Palette;
	}
	
	public void update_screen(ImageConsumer ic)
	{
		//System.out.println("Updating Screen");
		int m = brdchg_ud; brdchg_ud = 0;
		int bl = brdchg_l; brdchg_l = 0;
		int br = brdchg_r; brdchg_r = 0;

		boolean chg = (m | bl | br) != 0;

		byte buf[] = new byte[8*W*scale*scale];

		if(m != 0)
			for(int r=0; r<Mv; r++, m>>>=1)
				if((m&1)!=0)
					update_box(ic, r, 0, Mh+32+Mh, buf);

		for(int r=0; r<24; r++) {
			int d = scrchg[r];
			scrchg[r] = 0;
			int x = Mh, n = 0;
			if((bl&1)!=0) {
				n = x; x = 0;
			}
			for(;;) {
				while((d&1)!=0) {
					n++;
					d >>>= 1;
				}
				if(n!=0) {
					if(x+n == Mh+32 && (br&1)!=0)
						n += Mh;
					chg = true;
					update_box(ic, Mv+r, x, n, buf);
					x += n;
					if(x >= Mh+32)
						break;
				}
				if(d==0) {
					if((br&1)==0)
						break;
					x = Mh+32; n = Mh;
					continue;
				}
				do {
					x++;
					d >>>= 1;
				} while((d&1)==0);
				n = 1; d >>>= 1;
			}
			bl >>>= 1; br >>>= 1;
		}

		if(m != 0)
			for(int r=Mv+24; r<Mv+24+Mv; r++, m>>>=1)
				if((m&1)!=0)
					update_box(ic, r, 0, Mh+32+Mh, buf);

		if(chg){
			ic.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
			//System.out.println("COMPLETE IMAGE");
			
		}
	}
	
	/* image */

	public final void update_box(ImageConsumer cons,
			int y, int x, int w, byte buf[])
	{
		int si = y*W + x;
		int p = 0;

		x <<= 3; y <<= 3;
		int h, s;

		if(scale==1) {
			s = w*8;
			for(int n=0; n<8; n++) {
				for(int k=0; k<w; k++) {
					int m = screen[si++];
					byte c0 = (byte)(m>>>8 & 0xF);
					byte c1 = (byte)(m>>>12);
					m &= 0xFF;
					do
						buf[p++] = (m&1)==0 ? c0 : c1;
					while((m >>>= 1)!=0);
				}
				si += (W/8)-w;
			}
			h = 8;
		} else {
			h = scale<<3;
			s = w*h;
			for(int n=0; n<8; n++) {
				for(int k=0; k<w; k++) {
					int m = screen[si++];
					byte c0 = (byte)(m>>>8 & 0xF);
					byte c1 = (byte)(m>>>12);
					m &= 0xFF;
					do {
						buf[p] = buf[p+1]
						 = buf[p+s] = buf[p+s+1]
						 = (m&1)==0 ? c0 : c1;
						p += 2;
					} while((m >>>= 1)!=0);
				}
				p += s;
				si += (W/8)-w;
			}
			x *= scale; y *= scale;
		}
		/*System.out.println("x="+x);
		System.out.println("y="+y);
		System.out.println("s="+s);
		System.out.println("h="+h);
		System.out.println("buf="+buf.length);*/
		cons.setPixels(x, y, s, h, cm, buf, 0, s);
	}

	public void paintImageScreen(Graphics gi, byte[] screenPixels, byte[] screenAttrs) {
		boolean painted = false; 
	    System.out.println("Estoy en paintImageScreen de SpectrumDisplay");
        if (screenPixels != null){
        	//System.out.println("No es null paintImageScreen de SpectrumDisplay");
	        int longo=screenPixels.length;
	        int scrPosition = 0;
	        
	        if (true){
	        
	        
			    
			    for (int i=0 ; i<192 ; i++){
			    	
			    	for (int j=0 ; j<32 ; j++){
			    		
				    	// teniendo el valor de la memoria convertimos a binario/pixels
				        byte valorPix=screenPixels[(i*32)+j];
				        
				       		        
				        int dir=16384+(((i*32)+(j)));
				        
				        int atributo=screenAttrs[(scr2attr[dir-16384])-6144];
				        boolean bb=false;
				        
				        int bright=(atributo & 0x40);
				        int tinta=( (bright==0) ? (atributo & 0x7) : ((atributo & 0x7)+8));
				        int papel=( (bright==0) ? ((atributo & 0x38) /8) : (((atributo & 0x38) /8)+8));
				        					        	        
				        int colorTinta=getPalette()[tinta];
				        int colorPapel=getPalette()[papel];
				        //System.out.println("Dir: "+((scr2attr[dir-16384])-6144));
				        
				        /*if (lastFlashUpdate == 0) {
			        		lastFlashUpdate = System.currentTimeMillis();
			        	}
				        
				        boolean flash=( (atributo & 0x80) > 0);
				        if (flash && flashImage){
				        	colorTinta=getPalette()[papel];
				        	colorPapel=getPalette()[tinta];
				        	//flashAttrs[(scr2attr[dir-16384])-6144] = false;					        	
				        }*/
				        	            
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
				            painted=true;
				            valorPix = (byte) (valorPix << 1);
				            
				          }
				        
				        
				    }

			    }
			    
			    
			}
	        
	        
	}
        if (!painted){
        	System.out.println("NO PNTADO");
        	gi.setColor(Color.RED);
			gi.fillRect(0, 0, getWidth(), getHeight());
        }
        
        
	}
}
