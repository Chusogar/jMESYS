package jMESYS.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class jMESYSDisplay extends JComponent {

		public static Image imagenTV;
		//private static BufferedImage imgScreen;
		//private static Graphics graphScreen = null;
		//public static JPanel borderPanel = new JPanel();
		public static JPanel screenPanel = new JPanel();
		public static boolean paintWholeScreen = true;
		
		public static int pixelScale=1;
		
		//public static int PAN_START=0;
		//public static int ATR_START=0;
		public static int FRAME_WIDTH=256; 
		public static int FRAME_HEIGHT=192;
		public static int FRAME_MARGINH=0;
		public static int FRAME_MARGINV=0;
		
		public int[] currentPalette = null;
		public boolean bwPalette = false;
				
		
		/*protected static byte[] palcolor(int m) {
			byte a[] = new byte[16];
			for(int n=0; n<8; n++) if((n&m)!=0) {
				a[n] = (byte)0xCD;
				a[n+8] = (byte)0xFF;
			}
			return a;
		}*/
		
		// abstract methods
		public abstract void doPaintWholeScreen(Graphics gi);
		public abstract Image getScreenImage();
		public abstract void initScreen();
		public abstract int[] getDefaultPalette();
		
		public int[] getPalette() {
			return currentPalette;
		}
		
		public void setPalette( int[] pal ) {
			bwPalette = false;
			currentPalette = pal;
		}
		
		public void setBWPalette() {
			bwPalette = true;
			
			currentPalette = getBWPalette();
		}
		
		public int[] getBWPalette () {
			
			int numColors = getPalette().length;
			
			int[] bwPalette = new int[numColors];
		
			for (int i=0 ; i<numColors ; i++){
				Color originalColor = new Color (getPalette()[i]);
				double meanR = originalColor.getRed()*0.3;
				double meanG = originalColor.getGreen()*0.59;
				double meanB = originalColor.getBlue()*0.11;

				int avg = ((new Double(meanR)).intValue() + (new Double(meanG)).intValue() + (new Double(meanB)).intValue());
				
				bwPalette[i] = avg<<16 | avg<<8 | avg;
			}
			
			return bwPalette;
		}
		
		public jMESYSDisplay(){
			super();
			//enableEvents(AWTEvent.KEY_EVENT_MASK);
		}
		
		/*public Image getBufferedScreenImage() {
			return imgScreen;
		}*/
		
		public void paintBuffer() {
			//canvasGraphics.drawImage( bufferImage, 0, 0, null );
	 		//borderPaint();
		}
		
		public InputStream openFile(String name) throws Exception {
		   
		    InputStream result;
		  
		      result = new FileInputStream(name);
		  
		    return result;
		  }
		
		/*public void plot(int x, int y, Color tcolor) {
			//if (graphScreen == null){
			//	System.out.println("Creamos buffer de video");
			//	imgScreen = new BufferedImage(FRAME_WIDTH*pixelScale, FRAME_HEIGHT*pixelScale,BufferedImage.TYPE_INT_RGB);
			//	graphScreen = imgScreen.getGraphics();
			//}
			Graphics gi = getScreenImage().getGraphics();
			gi.setColor(tcolor);
			gi.fillRect(x*pixelScale, y*pixelScale, pixelScale, pixelScale);
			graphScreen.setColor(tcolor);
			graphScreen.fillRect(x*pixelScale, y*pixelScale, pixelScale, pixelScale);
		}*/
		
		
	    public void paintComponent(Graphics g) {
	    	//System.out.println("paintComponent jMESYSDisplay");
	        g.drawImage(getScreenImage(), FRAME_MARGINH, FRAME_MARGINV, FRAME_WIDTH*pixelScale, FRAME_HEIGHT*pixelScale, null);
	    	//g.drawImage(imgScreen, FRAME_MARGINH, FRAME_MARGINV, FRAME_WIDTH*pixelScale, FRAME_HEIGHT*pixelScale, null);
		}
	    		
		
		
		public Vector readContents(String address) throws IOException
	    {
	        Vector v=new Vector();
	        
			StringBuilder contents = new StringBuilder(2048);
	        BufferedReader br = null;

	        try
	        {
	            URL url = new URL(address);
	            br = new BufferedReader(new InputStreamReader(url.openStream()));
	            String line = "";
	            while (line != null)
	            {
	                line = br.readLine();
	                //contents.append(line);
	                
	                // buscamos el trozo
	                String strIni = "";
	                if (line != null) {
		                if (line.indexOf("<li><a href=\"") != -1){
		                	line = line.replace("<li><a href=\"", "");
		                	if (line.indexOf("\">") != -1){
			                	line = line.substring(0, line.indexOf("\">"));
			                	if (line.contains(".scr")){
			                		System.out.println(address+"/"+line);
			                		v.addElement(address+"/"+line);
			                	}
		                	}
		                }
	                }
	            }
	        }
	        finally
	        {
	            close(br);
	        }
	        
	        System.out.println(contents.toString());

	        return v;
	    }

	    private static void close(Reader br)
	    {
	        try
	        {
	            if (br != null)
	            {
	                br.close();
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }


		public Vector listWOS(String letra) {
			Vector listado = new Vector();
			
			int numEntradas = 0;
			String URL_WoS = "http://www.worldofspectrum.org/pub/sinclair/screens/load/"+letra+"/scr";
			
			try {
				listado = readContents(URL_WoS);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
			
			return listado;
		}
		
		/*public void readWosPage (String address) throws Exception {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
			        .newInstance();
			    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			    Document document = docBuilder.parse(new URL(address).openStream());
			    //doSomething(document.getDocumentElement());
			    NodeList elem = document.getElementsByTagName("a");
			    int numElem = elem.getLength();
			    
			    for (int i=0 ; i<numElem ; i++){
			    	Element currentElem = (Element) elem.item(i);
			    	System.out.println(currentElem.getNodeValue());
			    }
		}*/
		
		/*public boolean handleEvent( Event e ) {
			System.out.println("Evento: "+e.id);
			return true;
		}*/

		
}
