package jMESYS.gui;

import jMESYS.files.FileFormat;
import jMESYS.gui.Version;
import jMESYS.drivers.jMESYSDriver;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.drivers.Sinclair.Spectrum.SpectrumDisplay;
import jMESYS.drivers.Sinclair.Spectrum.sites.WOSsite;
import jMESYS.gui.jMESYSDisplay;
import jMESYS.gui.loader.jMESYSFileLoader;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Event;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class jMESYS extends JFrame implements KeyListener, MouseListener, Runnable, ActionListener{
	
	private jMESYSDisplay display = null;
	private Spectrum48k spectrum = null;
	//public Thread thread = null;
	private static jMESYSDriver[] COMPUTERS = null;
	private static int menuOptions=0;
	private jMESYSMenu menubar = null;
	
	// custom file dialog
	private jMESYSFileLoader fileDialog=null;
    
	
	public jMESYSDisplay getDisplay() {
		return display;
	}

	public void setDisplay(jMESYSDisplay display) {
		this.display = display;
	}
	
	private void createComputerList() {
	   COMPUTERS = new jMESYSDriver[1];
	   COMPUTERS[0]=new jMESYSDriver("Spectrum48", "Sinclair ZX Spectrum 48K", "jMESYS.drivers.Spectrum48", true);
	}

	public jMESYS(){
		super();
		this.setPreferredSize(new Dimension(400, 400));
        
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // obtengo la lista de sistemas emulados
        createComputerList();
        
        // creo el menu
        menubar = (new jMESYSMenu(COMPUTERS));
        this.setMenuBar( menubar.getMenuBar(this) );
        menuOptions=this.getMenuBar().getMenuCount();
        
        // inicializo la pantalla
        display = new SpectrumDisplay();
        this.add(display);
        display.requestFocus();
        this.pack();
        this.addKeyListener(this);
        this.addMouseListener(this);
        
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        requestFocus();
        //thread = new Thread(this, "jMESYS");
        //setComputer(new TestSpectrum(3.5, display));        
        
	}
	
	 public jMESYSMenu getMenu() {
		 return menubar;
	 }

	 public static void main(String[] args) {
		jMESYS t = new jMESYS();
				
		t.setTitle( Version.getVersion() );
		System.out.println("FRAME_MARGINH="+t.getDisplay().FRAME_MARGINH);
		System.out.println("FRAME_MARGINV="+t.getDisplay().FRAME_MARGINV);
		t.setSize(t.getDisplay().FRAME_WIDTH*t.getDisplay().pixelScale+((t.getDisplay().FRAME_MARGINH)*2)+16, t.getDisplay().FRAME_HEIGHT*t.getDisplay().pixelScale+((t.getDisplay().FRAME_MARGINV)*2)+38+21);
		//System.out.println("IMAGENTV: "+t.getDisplay().imagenTV);
		t.getDisplay().imagenTV= t.createImage(t.getDisplay().FRAME_WIDTH*t.getDisplay().pixelScale, t.getDisplay().FRAME_HEIGHT*t.getDisplay().pixelScale);
		
		//t.getDisplay().loadScreen("D:/workspace/jMESYS/bin/screens/WorldSeriesBaseball.scr");
		t.getDisplay().initScreen();
		//System.out.println("IMAGENTV: "+t.getDisplay().imagenTV);
	
		/*t.getDisplay().arrayFichero = t.getDisplay().cargaPantalla("D:/workspace/jMESYS/bin/screens/WorldSeriesBaseball.scr", (t.getDisplay().FRAME_HEIGHT*t.getDisplay().columns)+768);

		t.getDisplay().screenPixels=new byte[(t.getDisplay().FRAME_WIDTH*t.getDisplay().FRAME_HEIGHT)];
		System.arraycopy(t.getDisplay().arrayFichero, 0, t.getDisplay().screenPixels, 0, (t.getDisplay().FRAME_HEIGHT*t.getDisplay().columns));
		t.getDisplay().screenAttrs=new byte[768];
		System.arraycopy(t.getDisplay().arrayFichero, (t.getDisplay().FRAME_HEIGHT*t.getDisplay().columns), t.getDisplay().screenAttrs, 0, 768);
		

		t.getDisplay().repaint();
		*/

		/*try {
			Thread.sleep(new Long(2000));
		}catch (Exception e){
			e.printStackTrace(System.out);
		}*/
		
		System.out.println("Creo Spectrum");
		Spectrum48k computer = new Spectrum48k(3.5, t.display); 
		t.setComputer( computer );
		
		
		
		// creo la lista de opciones
		try {
			t.getMenu().setFormatsMenu(t.getComputer().getSupportedFileFormats(), t);
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		
		
		
		// creo listeners del menú
		for (int i=0 ; i<menuOptions ; i++){
			t.getMenuBar().getMenu(i).addActionListener(t);
			System.out.println(t.getMenuBar().getMenu(i).getActionCommand());
		}
		
		//t.getDisplay().loadScreen("D:/workspace/jMESYS/bin/screens/WorldSeriesBasketball.scr");
		t.getComputer().execute();
		
		//t.getDisplay().loadScreen("D:/workspace/jMESYS/bin/screens/WorldSeriesBasketball.scr");
		
		//t.getDisplay().loadScreen("http://www.worldofspectrum.org/pub/sinclair/screens/load/b/scr/BruceLee.scr");
		
		/*try {
			t.getDisplay().readWosPage("http://www.worldofspectrum.org/pub/sinclair/screens/load/b/scr");
		} catch (Exception e){
			e.printStackTrace(System.out);
		}*/
		
		// listado de pantallas de WoS
		/*Vector listado = t.getDisplay().listWOS("s");
		int numScreens = listado.size();
		
		for (int i=0 ; i<numScreens ; i++){
			t.getDisplay().loadScreen( (String) listado.get(i));		
		}*/
		
		
		//spectrum.setDisplay(t.getDisplay());
		
		// cargamos en pantalla lo que hay
		/*System.out.println("CARGAMOS...");
		byte[] arrayMemo = spectrum.mem;
		System.out.println("Memo: "+spectrum.mem.length);
		System.out.println("arrayFichero: "+t.getDisplay().arrayFichero.length);
		System.out.println("longo: "+(t.getDisplay().screenPixels.length+t.getDisplay().screenAttrs.length));
		System.arraycopy(spectrum.mem, 16384, t.getDisplay().arrayFichero, 0, t.getDisplay().arrayFichero.length);
		t.getDisplay().loadScreen(t.getDisplay().arrayFichero);
		System.out.println("Cargado!");*/
	}

	private void setComputer(Spectrum48k testSpectrum) {
		//System.out.println("Hago set1 "+spectrum);
		spectrum = testSpectrum;
		//System.out.println("Hago set2 "+spectrum);
	}

	/*@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("evMouseDragged");
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("evMouseMoved");
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("evFocusGained");
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("evFocusLost");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("evActionPerformed");
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("evItemStateChanged");
	}

	*/
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Pulso ratón");
		
	}

	/*@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("evMouseEntered");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("evMouseExited");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("evMousePressed");
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("evMouseReleased");
	}*/
	
	/*public boolean handleEvent( Event e ) {
		System.out.println("Handle Event");
		switch ( e.id ) {
		case Event.MOUSE_DOWN:
			//canvas.requestFocus();
			return true;
		case Event.KEY_ACTION:
		case Event.KEY_PRESS:
			return spectrum.doKey( true, e.key, e.modifiers );
		case Event.KEY_ACTION_RELEASE:
		case Event.KEY_RELEASE:
			return spectrum.doKey( false, e.key, e.modifiers );
		case Event.GOT_FOCUS:
		case Event.LOST_FOCUS:
			//resetKeyboard();
			return true;
		}
		
		return false;
	}*/

	
	public void keyPressed(KeyEvent e) {
		//System.out.println(e.getKeyChar());
		//getComputer().manageKeyboard(e.getKeyCode());
		getComputer().manageKeyboard(true, e);
		//System.out.println("Tecla "+e.getKeyChar());
	}

	private Spectrum48k getComputer() {
		if ((spectrum == null)&&(display != null)){
			new Spectrum48k(3.5, display);
		}
		return spectrum;
	}

	/*@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("evKeyReleased");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("evKeyTyped");
	}*/

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//System.out.println("evKeyReleased");
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//System.out.println("keyreleased");
		
		getComputer().manageKeyboard(false, e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void actionPerformed(ActionEvent ev) {
		System.out.println("Pulso "+ev.getActionCommand());
		System.out.println(ev.getSource().getClass());
		
		try {
			System.out.println("Desactivamos CPU");
			this.getComputer().player.stop();
			this.getComputer().haltCPU();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		// comprobamos si es un menu de carga de ficheros
		if ( ev.getSource().getClass().toString().endsWith("jMESYS.gui.jMESYSMenuItem") ) {
			System.out.println("Dentro");
						
			jMESYSMenuItem mItem = (jMESYSMenuItem) ev.getSource();
			
			try {
				if (fileDialog == null)
					fileDialog = new jMESYSFileLoader(getDisplay().getWidth(), getDisplay().getHeight(), getComputer().getSupportedFileFormats(), getDisplay());
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
			
			FileFormat ff = mItem.getfFormat();
			
			String name=mItem.getFileName();
			System.out.println(name);
			System.out.println(ff);
			//String name="D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/ShadowOfTheUnicorn.Z80";
			//fZ80.loadFormat("D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/ShadowOfTheUnicorn.Z80", new FileInputStream("D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/ShadowOfTheUnicorn.Z80"), this);
			try {
				getComputer().reset();
				ff.loadFormat(name, new FileInputStream(name), getComputer());
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
			//getComputer().load();
			
		} else if (ev.getActionCommand().equals("Sinclair ZX Spectrum 48K")) {
			//openLoadDialog();	
			try {
				// load and unzipped remote file
				FileFormat[] ff = getComputer().getSupportedFileFormats();
				WOSsite wos = new WOSsite();
				
				FileFormat xZ80 = ff[2];
				getComputer().reset();
				xZ80.loadFormat("/a.z80", wos.getZIPcontents("http://www.worldofspectrum.org/pub/sinclair/games/m/MundialDeFutbol.z80.zip"), getComputer());
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		} else if (ev.getActionCommand().equals("Color")) {
			System.out.println("set Color Palette");
			display.setPalette(display.getDefaultPalette());
		} else if (ev.getActionCommand().equals("Black and White")) {
			System.out.println("set Black and White Palette");
			display.setBWPalette();
		} else if (ev.getActionCommand().equals("Exit")) {
			System.exit(0);
		} else if (ev.getActionCommand().equals("All Formats")) {
			openLoadDialog();
		} else if (ev.getActionCommand().equals("Reset")) {
			getComputer().resetKeyboard();
			getComputer().player.stop();
			getComputer().reset();
			if (getComputer().soundON) {
				getComputer().player.play();
			}
		}  else if (ev.getActionCommand().equals("Sound Off")) {
			System.out.println("Sound Off");
			getComputer().player.stop();
			getComputer().soundON = false;
		}  else if (ev.getActionCommand().equals("Sound On")) {
			System.out.println("Sound On");
			getComputer().player.play();
			getComputer().soundON = true;
		}
		
		/*System.out.println("Presiono "+ev.getID());	
		System.out.println("Presiono "+ev.getActionCommand());
		System.out.println("Presiono "+ev.getSource().getClass());
		getComputer().load();*/
		
		try {
			System.out.println("Activamos CPU");
			this.getComputer().resumeCPU();
			if (getComputer().soundON) {
				getComputer().player.play();
			}
			//this.getComputer().player.play();
			//this.getComputer().execute();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	private void openLoadDialog() {
		try {
			if (fileDialog == null)
				fileDialog = new jMESYSFileLoader(getDisplay().getWidth(), getDisplay().getHeight(), getComputer().getSupportedFileFormats(), display);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		fileDialog.setSelectedFile(null); 
        int option = fileDialog.showOpenDialog(this);
        if (option != jMESYSFileLoader.APPROVE_OPTION)
            return;  // User canceled or clicked the dialog's close box.
        System.out.println( fileDialog.getSelectedFile() );
        try {
			getComputer().reset();
			FileFormat fl = fileDialog.getLoaderFile(fileDialog.getSelectedFile().getAbsolutePath());
			if (fl != null) {
				String name = fileDialog.getSelectedFile().getAbsolutePath();
				fl.loadFormat(name, new FileInputStream(name), getComputer());
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
