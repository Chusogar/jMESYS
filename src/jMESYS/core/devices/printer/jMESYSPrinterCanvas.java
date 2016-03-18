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
	//private Image imagePaper;
	private static int PAPER_WIDTH = 256;
	private static int PAPER_HEIGHT = 1024;
	private static int PAPER_BORDER = 10;
	private boolean borderPainted = false;
	
	private int yPos=0;
	private int contBorder=0;
	//private Color[] lineP = null;
	
	public jMESYSPrinterCanvas(int width, int height) {
		super();
		
		System.out.println(width);
		System.out.println(height);
		
		PAPER_WIDTH = width;
		PAPER_HEIGHT = height;
		
		imagePrinter=null;
		
		setPreferredSize(new Dimension(PAPER_BORDER+PAPER_WIDTH+PAPER_BORDER, PAPER_HEIGHT));
		//icon = createImage(WIDTH, HEIGHT);
		imagePrinter = createImage(PAPER_BORDER+260+PAPER_BORDER, PAPER_HEIGHT);
		//imagePaper = createImage(PAPER_BORDER+260+PAPER_BORDER, PAPER_HEIGHT);
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
		} 
		
		/*else {
			imagePrinter = createImage(WIDTH, HEIGHT);
			System.out.println("Es nulo");
		}*/
	}
	
	protected void paintComponent(Graphics g) {
		g.drawImage(imagePrinter, 0, 0, null);
	}

	public void plotLine(Color[] line, int y) {
		yPos = y;
		//lineP = line;
		
		if (imagePrinter==null){
			imagePrinter = createImage(PAPER_WIDTH, PAPER_HEIGHT);
		}
		
		Graphics gr=imagePrinter.getGraphics();
		
		if (contBorder == 8) {
			contBorder = 0;
			int posi = (y-8)/8;
			
			// left column circles
			gr.drawOval(2, 2+(10*posi), 5, 5);
								
			// left line
			gr.drawLine(9, (10*posi), 9, 3+(10*posi));
			gr.drawLine(9, 5+(10*posi), 9, 8+(10*posi));
			
			// right column circles
			gr.drawOval(PAPER_BORDER+260+10+2, 2+(10*posi), 5, 5);
								
			// right line
			gr.drawLine(PAPER_BORDER+260+10, (10*posi), PAPER_BORDER+260+10, 3+(10*posi));
			gr.drawLine(PAPER_BORDER+260+10, 5+(10*posi), PAPER_BORDER+260+10, 8+(10*posi));
		}
		
		int numPixels = line.length;
				
		for (int i=0 ; i<numPixels ; i++){
			gr.setColor(line[i]);
			gr.fillRect(i+16, yPos, 1, 1);			
		}
		
		contBorder++;
		//System.out.println();
		//repaint();
	}

}
