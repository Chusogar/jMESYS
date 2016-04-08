package jMESYS.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.util.Vector;

public abstract class jMESYSDisplay implements ImageProducer {

	private Vector consumers = new Vector(1);
	private int width = 0;
	private int height = 0;
	
	public boolean fullScreen = false;
	
	public int scale=0;
	public int want_scale=0;
	
	public int[] currentPalette = null;
	public boolean bwPalette = false;
	public boolean blur = false;
	
	public jMESYSDisplay(int w, int h) {
		super();
		width = w;
		height = h;
		
		setPalette(getDefaultPalette());
	}
	
	// abstract methods
	public abstract void force_redraw();
	public abstract void update_screen(ImageConsumer ic);
	public abstract int[] getDefaultPalette();
	public abstract int getW();
	public abstract int getH();
	
	public static ColorModel cm = null;
	
	public void update_screen() {
		for(int i=0; i<consumers.size();) {
			ImageConsumer c = (ImageConsumer)
				consumers.elementAt(consumers.size() - ++i);
			update_screen(c);
		}
	}
	
	public boolean fullScreen() {
		return fullScreen;
	}
	
	public void fullScreen(boolean b) {
		fullScreen = b;
	}
	
	public synchronized void addConsumer(ImageConsumer ic)
	{
		try {
			ic.setDimensions(width*scale, height*scale);
			
			consumers.addElement(ic); // XXX it may have been just removed
			ic.setHints(ic.RANDOMPIXELORDER|ic.SINGLEPASS);
			if(isConsumer(ic)) ic.setColorModel(cm);
			force_redraw();
		} catch(Exception e) {
			if(isConsumer(ic))
				ic.imageComplete(ImageConsumer.IMAGEERROR);
		}
	}
	
	public boolean isConsumer(ImageConsumer ic)
	{
		return consumers.contains(ic);
	}

	public synchronized void removeConsumer(ImageConsumer ic)
	{
		consumers.removeElement(ic);
	}

	public void startProduction(ImageConsumer ic)
	{
		addConsumer(ic);
	}
	
	public void requestTopDownLeftRightResend(ImageConsumer ic) {}

	public void abort_consumers()
	{
		for(;;) {
			int s = consumers.size();
			if(s == 0) break;
			s--;
			ImageConsumer c = (ImageConsumer)consumers.elementAt(s);
			consumers.removeElementAt(s);
			c.imageComplete(ImageConsumer.IMAGEABORTED);
		}
	}

	public int getWidth() {
		//System.out.println("getWidth="+width);
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void setWidth(int w) {
		//System.out.println("getWidth="+width);
		width=w;
	}

	public void setHeight(int h) {
		height=h;
	}
	
	public int[] getPalette() {
		if (currentPalette == null) {
			currentPalette = getDefaultPalette();
		}
		return currentPalette;
	}
	
	public void setPalette( int[] pal ) {
		bwPalette = false;
		currentPalette = pal;
		
		int numColors = pal.length;
		
		byte[] colR = new byte[numColors];
		byte[] colG = new byte[numColors];
		byte[] colB = new byte[numColors];
		
		for (int i=0; i<numColors ; i++){
			Color col = new Color (pal[i]);
			
			colR[i] = (byte) (col.getRed());
			colG[i] = (byte) (col.getGreen());
			colB[i] = (byte) (col.getBlue());
		}
		
		cm = new IndexColorModel(8, 16,
				colR, colG, colB);
		
		force_redraw();
	}
	
	public void setBWPalette() {
		
		currentPalette = getBWPalette();
		bwPalette = true;
		
		force_redraw();
	}
	
	public int[] getBWPalette () {
		
		int numColors = getDefaultPalette().length;
		
		int[] bwPalette = new int[numColors];
		
		byte[] colR = new byte[numColors];
		byte[] colG = new byte[numColors];
		byte[] colB = new byte[numColors];
	
		for (int i=0 ; i<numColors ; i++){
			Color originalColor = new Color (getDefaultPalette()[i]);
			double meanR = originalColor.getRed()*0.3;
			double meanG = originalColor.getGreen()*0.59;
			double meanB = originalColor.getBlue()*0.11;

			int avg = ((new Double(meanR)).intValue() + (new Double(meanG)).intValue() + (new Double(meanB)).intValue());
			
			bwPalette[i] = avg<<16 | avg<<8 | avg;
			colR[i] = colG[i] = colB[i] = (byte) (bwPalette[i] & 0xFF);
			//System.out.println("["+(colR[i]&0xFF)+", "+colG[i]+", "+colB[i]+"]");
		}
		
		cm = new IndexColorModel(8, 16,
				colR, colG, colB);
		
		return bwPalette;
	}
	
	public void setGreenPalette() {
		
		currentPalette = getGreenPalette();
		bwPalette = false;
		
		force_redraw();
	}
	
	public int[] getGreenPalette () {
		
		int numColors = getBWPalette().length;
		
		int[] greenPalette = new int[numColors];
		
		byte[] colR = new byte[numColors];
		byte[] colG = new byte[numColors];
		byte[] colB = new byte[numColors];
		
		int[] originalMonPalette = getBWPalette();
	
		for (int i=0 ; i<numColors ; i++){
			Color originalColor = new Color (originalMonPalette[i]);
			double meanR = 0.0;
			double meanG = originalColor.getGreen();
			double meanB = 0.0;

			colR[i] = colB[i] = 0;
			colG[i] = (byte) (new Double(meanG).intValue() & 0xFF);
						
			int compR = ((colR[i])<<16) & 0xFF0000;
			int compG = ((colG[i])<<8) & 0x00FF00;
			int compB = (colB[i]) & 0xFF;
			
			int avg = ( compR | compG | compB) & 0xFFFFFF;
			
			greenPalette[i] = avg;
			//System.out.println("R="+(colR[i]&0xFF)+" G="+(colG[i]&0xFF)+" B="+(colB[i]&0xFF)+" --> "+avg);
		}
		
		cm = new IndexColorModel(8, 16,
				colR, colG, colB);
		
		return greenPalette;
	}
	
	public void setOrangePalette() {
		
		currentPalette = getOrangePalette();
		bwPalette = false;
		
		force_redraw();
	}
	
	public int[] getOrangePalette () {
		
		int numColors = getBWPalette().length;
		
		int[] orangePalette = new int[numColors];
		
		byte[] colR = new byte[numColors];
		byte[] colG = new byte[numColors];
		byte[] colB = new byte[numColors];
	
		for (int i=0 ; i<numColors ; i++){
			Color originalColor = new Color (getBWPalette()[i]);
			double meanR = originalColor.getRed();
			double meanG = originalColor.getGreen();
			double meanB = 0.0;
			
			if (i==0){
				colR[i] = colG[i] = colB[i] = 0;
			} else if (i == (numColors-1)) {
				colR[i] = (byte) 0xFF;
				colG[i] = (byte) 0xE3;
				colB[i] = (byte) 0x34;
			} else if ((meanR==meanG) &&(meanG==meanB) && (meanB==0.0)) {
				colR[i] = colG[i] = colB[i] = 0;
			} else {
			
				colR[i] = (byte) ((originalColor.getRed()+10) & 0xFF);
				colG[i] = (byte) ((originalColor.getGreen()-10) & 0xFF);
				colB[i] = 0;
			}
			int compR = ((colR[i])<<16) & 0xFF0000;
			int compG = ((colG[i])<<8) & 0x00FF00;
			int compB = (colB[i]) & 0xFF;
			
			int avg = ( compR | compG | compB) & 0xFFFFFF;
			
			//System.out.println("R="+(colR[i]&0xFF)+" G="+(colG[i]&0xFF)+" B="+(colB[i]&0xFF)+" --> "+avg);
			
			orangePalette[i] = avg;
		}
		
		cm = new IndexColorModel(8, 16,
				colR, colG, colB);
		
		return orangePalette;
	}
	
	public void setBlur(boolean b) {
		blur = b;
	}
	
	public boolean getBlur() {
		return blur;
	}	
	
}
