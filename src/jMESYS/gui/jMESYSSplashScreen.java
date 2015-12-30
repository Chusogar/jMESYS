package jMESYS.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class jMESYSSplashScreen extends JDialog {
	
	private Image img;
	
	public jMESYSSplashScreen (JFrame parent, String title, boolean modal) {
		super(parent, title, modal);
		
		setUndecorated(true);
        buildImage();
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(img, 0);
        setSize(img.getWidth(this), img.getHeight(this));
        center();
        toFront();
        //setVisible(true);
    }
	
	private void buildImage() {
        URL imgURL = getClass().getResource("/jMESYSResources/images/jMESYS.jpg");
        img = new ImageIcon(imgURL).getImage();
    }
	
	public void closeSplash() {
		try {			  
			Thread.sleep(3000);
		} catch (Exception e){
			
		}
		
        img.flush();
        dispose();
    }
	
	/**
     * Displays this screen in a thread-clean way ?
     *
     */
    public void display() {
        Runnable r= new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            public void run() {
                setVisible(true);
            }
        };
            SwingUtilities.invokeLater(r);
    
                
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Container#update(java.awt.Graphics)
     */
    public void update(Graphics g) {
        paint(g);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Container#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    }
    
    private void center() {
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle frame = getBounds();
        //setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
        setLocation((65), (63 + frame.height));
    }
}
