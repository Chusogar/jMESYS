package jMESYS.core.devices.printer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class jMESYSPrinterCanvas extends JComponent {
	
	private Image imagePrinter;
	private int PAPER_WIDTH = 256;
	private int PAPER_HEIGHT = 512;
	
	private int yPos=0;
	//private Color[] lineP = null;
	
	public jMESYSPrinterCanvas(int width, int height) {
		super();
		
		PAPER_WIDTH = width;
		PAPER_HEIGHT = height;
		
		setPreferredSize(new Dimension(PAPER_WIDTH, PAPER_HEIGHT));
		//icon = createImage(WIDTH, HEIGHT);
		imagePrinter = createImage(PAPER_WIDTH, PAPER_HEIGHT);
		//imagePrinter = new BufferedImage(PAPER_WIDTH, PAPER_HEIGHT, BufferedImage.TYPE_INT_RGB);
	}
	
	public Image getImage() {
		return imagePrinter;
	}
	
	public void setImage(Image img) {
		imagePrinter = img;
	}
	
	public void plotPrinter(int x, int y, Color color) {
		//System.out.println("X="+xPos+" yPos="+yPos+" Color="+color);
		if (imagePrinter != null){
			//yPos=y;
			imagePrinter.getGraphics().setColor(color);
			imagePrinter.getGraphics().fillRect(x, y, 1, 1);
		} /*else {
			imagePrinter = createImage(WIDTH, HEIGHT);
			System.out.println("Es nulo");
		}*/
	}
	
	protected void paintComponent(Graphics g) {
		if (imagePrinter != null){
			//System.out.println("Pinto Canvas");
			/*Graphics2D g2d = (Graphics2D) g;
			Rectangle bounds = new Rectangle(0, 0, WIDTH, HEIGHT);
			g.setColor(Color.black);
			g.drawRect(0, 0, WIDTH, HEIGHT);
			g.setColor(Color.white);
			g2d.fill(bounds);*/
			//icon.paintIcon(this, g, 1, 1);
			
			
			
			g.drawImage(imagePrinter, 0, 0, PAPER_WIDTH, PAPER_HEIGHT, null);
			/*int numPixels = lineP.length;
			
			for (int i=0 ; i<numPixels ; i++){
				g.setColor(lineP[i]);
				g.fillRect(i, yPos, 1, 1);
			}*/
			
			/*g.setColor(Color.BLUE);
			g.fillRect(0, 0, 30, 30);*/
		}
	}

	public void plotLine(Color[] line, int y) {
		yPos = y;
		//lineP = line;
		
		if (imagePrinter==null){
			imagePrinter = createImage(PAPER_WIDTH, PAPER_HEIGHT);
		}
		
		Graphics gr=imagePrinter.getGraphics();
		
		int numPixels = line.length;
		
		for (int i=0 ; i<numPixels ; i++){
			gr.setColor(line[i]);
			gr.fillRect(i, yPos, 1, 1);
		}
		
		repaint();
	}

}
