package jMESYS.core.printer;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public abstract class jMESYSPrinterFrame extends JFrame implements ActionListener {
	
	public abstract int getPaperLength();
	public abstract int getPaperWidth();
	
	public abstract String getPrinterName();
	public abstract int getPortNumber();
	
	public abstract void out(int port, int v);
	public abstract int in(int port);
	
	private int xPos=0;
	private int yPos=0;
	
	private jMESYSPrinterCanvas ivTarget=null;

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public jMESYSPrinterFrame(Frame frame, String printerName) {
		super(printerName);
		
		//ivTarget = frame;
		//JLabel label = new JLabel(new ImageIcon(getPaperImage()));
		
		/*JLabel label = new JLabel("Hola");
		label.setBackground(Color.BLACK);
		label.setBounds(0, 0, 300, 200);*/
		//ivTarget = label;
		//this.getContentPane().add(label, SwingConstants.CENTER);
	    //this.setBounds(500, 150, 300, 200);
		//ivTarget=this.getContentPane();
	    //this.getContentPane().setBackground(Color.BLACK);
	    //JPanel pan = new JPanel();
	    //pan.resize(320, 200);
	    ivTarget = new jMESYSPrinterCanvas(300, 200);
	    //this.add(new JLabel("HOLA"));
	    this.add(ivTarget);
	    //ivTarget.setBackground(Color.RED);
	    setPreferredSize(new Dimension(300, 200));
	    setBounds(500, 150, 300, 200);
	    setVisible(true);
	    //ivTargetGraphics=ivTarget.getGraphics();
	    //System.out.println(ivTarget);
	    //System.out.println(ivTargetGraphics);
	    //ivTargetGraphics.setColor(Color.BLACK);
	    //ivTargetGraphics.fillRect(0, 0, 10, 10);
	    
	    //ivTarget = this.getContentPane();
	    
        //pack();
        //setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void carriageReturn() {
		yPos++;
		//scrollPaperImage();
		//refresh();
	}

	/**
	 * Draws the printer paper on the target component.
	 */
	/*public void refresh() {
		// Draw paper bottom centered
		int sw = getPaperWidth();
		int sh = getPaperLength();
		int dw = ivTarget.getWidth();
		int dh = ivTarget.getHeight();

		int dx = (dw - sw) / 2;
		int dy = dh - sh;

		if (ivTargetGraphics != null) {
			ivTargetGraphics.drawImage(getPaperImage(), dx, dy, null);
		}
		repaint();
	}*/

	/**
	 * Scroll the off screen paper image up one line
	 */
	private void scrollPaperImage() {
		//getPaperGraphics().copyArea(0, 0, getPaperWidth(), getPaperLength(), 0, -1);
	}
	
	public void plotPrinter(int x, Color color) {
		//System.out.println("X="+x+" Color="+color);
		xPos=x;
		//System.out.println("X="+xPos+" yPos="+yPos);
		/*ivPaperImage.getGraphics().setColor(color);
		ivPaperImage.getGraphics().fillRect(xPos, yPos, 1, 1);*/
		ivTarget.plotPrinter(xPos, yPos, color);
	}
	
	public void plotLine(Color[] line) {
		int numColors=line.length;
		
		/*for (int i=0 ; i<numColors ; i++){
			ivTarget.plotPrinter(i, yPos, line[i]);
		}*/
		ivTarget.plotLine(line, yPos);
		
		//ivTarget.repaint();
		//repaint();
	}

	/*private Graphics getPaperGraphics() {
		if (ivPaperGraphics == null) {
			ivPaperGraphics = getPaperImage().getGraphics();
		}
		return ivPaperGraphics;
	}*/
	
	/*private Image getPaperImage() {
		if (ivPaperImage == null) {
			ivPaperImage = createImage(getPaperWidth(), getPaperLength());			
		}
		return ivPaperImage;
	}*/
	
	public void paint(Graphics g) {
		//System.out.println("Pinto "+g.getClass().getName());
		//super.paint(g);
		//ivTarget.repaint();
		//g.setColor(Color.BLACK);
	    //g.fillRect(0, 0, 10, 10);
		//g.drawImage(getPaperImage(), 0, 0, getPaperWidth(), getPaperLength(), null);
		//refresh();
	}
}
