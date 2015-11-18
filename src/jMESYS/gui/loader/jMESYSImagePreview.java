package jMESYS.gui.loader;

import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

public class jMESYSImagePreview extends JComponent implements PropertyChangeListener {
	
	private Image icon;
	private int WIDTH = 150;
	private int HEIGHT = 100;
	private FileFormat[] fileFormats = null;
	private jMESYSDisplay disp = null;
	private jMESYSFileLoader fc = null;
	
	public jMESYSImagePreview(jMESYSFileLoader fcx, int width, int height, FileFormat[] ff, jMESYSDisplay display){
		fileFormats = ff;
		fcx.addPropertyChangeListener(this);
		WIDTH = width;
		HEIGHT = height;
		disp = display;
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}
	
	protected void paintComponent(Graphics g) {
		if (icon != null){
			
			Graphics2D g2d = (Graphics2D) g;
			Rectangle bounds = new Rectangle(0, 0, WIDTH, HEIGHT);
			g.setColor(Color.black);
			g.drawRect(0, 0, WIDTH, HEIGHT);
			g.setColor(Color.white);
			g2d.fill(bounds);
			//icon.paintIcon(this, g, 1, 1);
			g.drawImage(icon, 0, 0, WIDTH, HEIGHT, null);
			
		}
	}

	public void propertyChange(PropertyChangeEvent ev) {
		String propName = ev.getPropertyName();
		
		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propName)){
			icon = null;
			repaint();
			return;
		}
		
		if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propName)){
			File file = (File) ev.getNewValue();
			
			if (file == null){
				icon = null;
				repaint();
				return;
			}
			
			//icon = new ImageIcon(file.getPath());
			icon = createImage(WIDTH, HEIGHT);
			
			if (icon != null){
				icon.flush();

				/*FormatSNA fSNA = new FormatSNA();
				try{
					String cad=((File)ev.getNewValue()).getAbsolutePath();
					System.out.println(cad);
					fSNA.getScreen(cad, (new FileInputStream(cad)), disp, icon.getGraphics());
				} catch (Exception e) {
					e.printStackTrace(System.out);
				}*/
				
				//String cad=((File)ev.getNewValue()).getAbsolutePath();
				
				String cad = jMESYSFileZIP.checkZIP( ((File)ev.getNewValue()).getAbsolutePath() );
				
				try {
					//String cad=((File)ev.getNewValue()).getAbsolutePath();
					int countFormats=fileFormats.length;
					int counter = 0;
					FileFormat currentFF = null;
					boolean foundFileFormat = false;
					
					while (counter < countFormats && !foundFileFormat){
						currentFF = fileFormats[counter];
						if (cad.toUpperCase().endsWith(currentFF.getExtension().toUpperCase())) {
							foundFileFormat = true; 
						}
						counter++;
					}
					
					if (foundFileFormat) {
						currentFF.getScreen(cad, (new FileInputStream(cad)), disp, icon.getGraphics());
					}
				} catch (Exception e) {
					e.printStackTrace(System.out);
				}
			}
			
			/*if (icon.getIconWidth() > WIDTH){
				icon = new ImageIcon(icon.getImage().getScaledInstance(WIDTH-1, -1, Image.SCALE_DEFAULT));
				FormatSNA fSNA = new FormatSNA();
				try{
					fSNA.getScreen((String)ev.getNewValue(), (new FileInputStream((String)ev.getNewValue())), disp, icon.getImage().getGraphics());
				} catch (Exception e) {
					e.printStackTrace(System.out);
				}
			}*/
			
			repaint();
		}
		
		
	}

}
