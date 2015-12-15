package jMESYS.gui;

import jMESYS.files.FileFormat;
import jMESYS.files.RemoteFile;
import jMESYS.gui.Version;
import jMESYS.drivers.jMESYSDriver;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.drivers.Sinclair.Spectrum.SpectrumDisplay;
import jMESYS.drivers.Sinclair.Spectrum.sites.WOSsite;
import jMESYS.gui.jMESYSDisplay;
import jMESYS.gui.loader.jMESYSFileLoader;
import jMESYS.gui.loader.jMESYSFileZIP;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


public class jMESYS extends JFrame implements KeyListener, MouseListener, Runnable, ActionListener, TreeSelectionListener{
	
	private jMESYSDisplay display = null;
	private Spectrum48k spectrum = null;
	//public Thread thread = null;
	private static jMESYSDriver[] COMPUTERS = null;
	private static int menuOptions=0;
	private jMESYSMenu menubar = null;
	
	// custom file dialog
	private jMESYSFileLoader fileDialog=null;
	
	// tree
	private JTree tree;
	private JTextField input;
    //private String addrSiteWOS="http://localhost:8080/WOSserver/pub/sinclair/games/";
	private String addrSiteWOS="http://www.worldofspectrum.org/pub/sinclair/games/";
    private JPanel panScreen;
	
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
		
		t.resizeScreen();	
		//t.getDisplay().loadScreen("D:/workspace/jMESYS/bin/screens/WorldSeriesBaseball.scr");
		
		/*t.getDisplay().arrayFichero = t.getDisplay().cargaPantalla("D:/workspace/jMESYS/bin/screens/WorldSeriesBaseball.scr", (t.getDisplay().FRAME_HEIGHT*t.getDisplay().columns)+768);

		t.getDisplay().screenPixels=new byte[(t.getDisplay().FRAME_WIDTH*t.getDisplay().FRAME_HEIGHT)];
		System.arraycopy(t.getDisplay().arrayFichero, 0, t.getDisplay().screenPixels, 0, (t.getDisplay().FRAME_HEIGHT*t.getDisplay().columns));
		t.getDisplay().screenAttrs=new byte[768];
		System.arraycopy(t.getDisplay().arrayFichero, (t.getDisplay().FRAME_HEIGHT*t.getDisplay().columns), t.getDisplay().screenAttrs, 0, 768);
		

		t.getDisplay().repaint();
		*/

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
	 
	private void resizeScreen() {
		System.out.println("Resizing Screen...");
		this.setSize(getDisplay().FRAME_WIDTH*getDisplay().pixelScale+((getDisplay().FRAME_MARGINH)*2)+16, getDisplay().FRAME_HEIGHT*getDisplay().pixelScale+((getDisplay().FRAME_MARGINV)*2)+38+21);
		//System.out.println("IMAGENTV: "+t.getDisplay().imagenTV);
		int wi=getDisplay().FRAME_WIDTH*getDisplay().pixelScale;
		int he=getDisplay().FRAME_HEIGHT*getDisplay().pixelScale;
		
		getDisplay().imagenTV= createImage(wi, he);
		
		System.out.println("Width: "+wi);
		System.out.println("Height: "+he);
		//t.getDisplay().loadScreen("D:/workspace/jMESYS/bin/screens/WorldSeriesBaseball.scr");
		getDisplay().initScreen();
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
		//System.out.println("Pulso "+ev.getActionCommand());
		//System.out.println(ev.getSource().getClass());
		
		try {
			//System.out.println("Desactivamos CPU");
			this.getComputer().player.stop();
			this.getComputer().haltCPU();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		// comprobamos si es un menu de carga de ficheros
		if ( ev.getSource().getClass().toString().endsWith("jMESYS.gui.jMESYSMenuItem") ) {
			//System.out.println("Dentro");
						
			jMESYSMenuItem mItem = (jMESYSMenuItem) ev.getSource();
			
			try {
				if (fileDialog == null)
					//fileDialog = new jMESYSFileLoader(getDisplay().getWidth(), getDisplay().getHeight(), getComputer().getSupportedFileFormats(), getDisplay());
					fileDialog = new jMESYSFileLoader(276, 213, getComputer().getSupportedFileFormats(), getDisplay());
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
				String nameChecked = jMESYSFileZIP.checkZIP( name );
				System.out.println("Voy a cargar "+nameChecked);
				ff.loadFormat(nameChecked, new FileInputStream(nameChecked), getComputer());
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
				RemoteFile rf = new RemoteFile();
				rf.setName("SpiritOfNinjaThe.z80.zip");
				rf.setPath("http://www.worldofspectrum.org/pub/sinclair/games/s");
				xZ80.loadFormat("/a.z80", wos.getZIPcontents(rf), getComputer());
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
		}  else if (ev.getActionCommand().equals("Size X 1")) {
			display.pixelScale=1;
			this.resizeScreen();		
		}  else if (ev.getActionCommand().equals("Size X 2")) {
			display.pixelScale=2;
			this.resizeScreen();
		}  else if (ev.getActionCommand().equals("Size X 3")) {
			display.pixelScale=3;
			this.resizeScreen();
		}  else if (ev.getActionCommand().equals("Play Tape")) {
			System.out.println("Playing Tape");
		}  else if (ev.getActionCommand().equals("Remote WOS")) {
			System.out.println("Remote World of Spectrum Site");
			createWOSFrame();
		}
		
		
		/*System.out.println("Presiono "+ev.getID());	
		System.out.println("Presiono "+ev.getActionCommand());
		System.out.println("Presiono "+ev.getSource().getClass());
		getComputer().load();*/
		
		try {
			//System.out.println("Activamos CPU");
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

	private void createWOSFrame() {
		
		
	
	                /*JFrame frame = new JFrame("Test");
	                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	                try 
	                {
	                   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	                } catch (Exception e) {
	                   e.printStackTrace();
	                }*/
	                JPanel panel = new JPanel();
	                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	                //panel.setLayout(new BorderLayout());
	                panel.setOpaque(true);
	                /*JTextArea textArea = new JTextArea(15, 50);
	                textArea.setWrapStyleWord(true);
	                textArea.setEditable(false);
	                textArea.setFont(Font.getFont(Font.SANS_SERIF));*/
	                DefaultMutableTreeNode root = new DefaultMutableTreeNode("/");
	                tree = new JTree(root);
	                //tree.addTreeSelectionListener(this);
	                
	        		//getBranch("http://localhost:8080/WOSserver/pub/sinclair/games/", root);
	                getBranch("http://www.worldofspectrum.org/pub/sinclair/games/", root);
	        		
	        		tree.expandPath(new TreePath(root.getPath()));
	        		tree.addTreeSelectionListener(this);
	        		
	                //JScrollPane scroller = new JScrollPane(textArea);
	                JScrollPane scroller = new JScrollPane(tree);
	                scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	                scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	                JPanel inputpanel = new JPanel();
	                inputpanel.setLayout(new FlowLayout());
	                input = new JTextField(20);
	                JButton button = new JButton("Enter");
	                /*DefaultCaret caret = (DefaultCaret) textArea.getCaret();
	                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);*/
	                panel.add(scroller);
	                inputpanel.add(input);
	                inputpanel.add(button);
	                
	                panScreen = new JPanel();
	                panScreen.setPreferredSize( new Dimension(getDisplay().getWidth(), getDisplay().getHeight()) );
	                Image imgScreen = panScreen.createImage(getDisplay().getWidth(), getDisplay().getHeight());
	                panel.add(panScreen);
	                
	                panel.add(inputpanel);
	                /*frame.getContentPane().add(BorderLayout.CENTER, panel);
	                frame.pack();
	                frame.setLocationByPlatform(true);
	                frame.setVisible(true);
	                frame.setResizable(false);*/
	                input.requestFocus();
	                
	                JDialog frame = new JDialog(this, "Remote Site", true);
	                frame.getContentPane().add(panel);
	        		frame.pack();
	        		frame.setVisible(true);
	        		System.out.println("WIDTH: "+frame.getWidth());
	        		System.out.println("HEIGHT: "+frame.getHeight());
	}

	private void getBranch(String addrWOS, DefaultMutableTreeNode parentNode) {
		WOSsite site = new WOSsite();
        site.setRemoteAddress( addrWOS );
		Vector v = site.readRemotePage();
		
		int numNodes = v.size();
		for (int i=0 ; i<numNodes ; i++) {
			RemoteFile rf = (RemoteFile) v.elementAt(i);
			DefaultMutableTreeNode nodAct = new DefaultMutableTreeNode(rf.getName());
			parentNode.add(nodAct);
		}
		
		tree.expandPath(new TreePath(parentNode.getPath()));
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
				String nameChecked = jMESYSFileZIP.checkZIP( name );
				//System.out.println("Voy a cargar 2 "+nameChecked);
				fl.loadFormat(nameChecked, new FileInputStream(nameChecked), getComputer());
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public void valueChanged(TreeSelectionEvent event) {
		System.out.println("Click tree");
		tree.expandPath(event.getPath());
        input.setText(event.getPath().toString());
        
        String completePath = addrSiteWOS+event.getPath().getLastPathComponent().toString();
        String partialPath = addrSiteWOS;
        TreePath tp = event.getPath();
        String strFile = addrSiteWOS;
        
        int numPath = tp.getPathCount();
        for (int i=0 ; i<numPath ; i++){
        	strFile += (tp.getPath()[i]).toString();
        }
        
        
        System.out.println("CompletePath: "+completePath);
        System.out.println("PartialPath: "+partialPath);
        System.out.println("eventPath: "+tp.toString());
        System.out.println("strFile: "+strFile);
        
        if (event.getPath().getLastPathComponent().toString().endsWith("/")){
        	System.out.println("Directorio "+addrSiteWOS+event.getPath().getLastPathComponent().toString());
        	getBranch(completePath, 
        			(DefaultMutableTreeNode)((JTree)event.getSource()).getLastSelectedPathComponent());
        	
        } else {
        	System.out.println("Fichero");
        	
        	try {
				// load and unzipped remote file
				FileFormat[] ff = getComputer().getSupportedFileFormats();
				WOSsite wos = new WOSsite();
				
				FileFormat xZ80 = ff[2];
				getComputer().reset();
				RemoteFile rf = new RemoteFile();
				rf.setName( strFile );
				
				rf.setPath("");
				//xZ80.loadFormat("/a.z80", wos.getZIPcontents(rf), getComputer());
				xZ80.getScreen("/a.z80", wos.getZIPcontents(rf), getDisplay(), panScreen.getGraphics());
				xZ80.loadFormat("/a.z80", wos.getZIPcontents(rf), getComputer());
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
        }
	}
}
