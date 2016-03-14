package jMESYS.gui;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.InflaterInputStream;

import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;

import jMESYS.core.cpu.z80.Z80;
import jMESYS.core.devices.printer.jMESYSPrinterFrame;
import jMESYS.drivers.jMESYSDriver;
import jMESYS.drivers.jMESYSFamily;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.drivers.Sinclair.Spectrum.SpectrumModels;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatSNA;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatTAP;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatTXT;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatTZX;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatZ80;
import jMESYS.drivers.Sinclair.Spectrum.sites.WOSsite;
import jMESYS.files.FileFormat;
import jMESYS.files.RemoteFile;
import jMESYS.files.jMESYSLoader;
import jMESYS.gui.filters.jMESYSFilter_Blur;
import jMESYS.gui.loader.jMESYSFileLoader;
import jMESYS.gui.loader.jMESYSFileZIP;
import jMESYS.gui.loader.jMESYSRemoteFileLoader;

public class jMESYS extends Applet
	implements Runnable, KeyListener, FocusListener,
		ComponentListener, WindowListener, ActionListener
{
	public Spectrum48k spectrum;
	private jMESYSLoader loader;
	private Image img;
	private Image imgBackup;
	private Dimension size;
	private int posx, posy;
	
	//private boolean fullScreen = false;
    
	// menu
	private jMESYSMenu menubar = null;
	private static int menuOptions=0;
	//private static jMESYSDriver[] COMPUTERS = null;
	
	// custom file dialog
	private Frame    frame;
	private jMESYSFileLoader fileDialog=null;
        
	/* applet */
	public synchronized void init()
	{
		
	    
		frame = new Frame(getVersion());
		
		// splash screen
	    jMESYSSplashScreen splash = new jMESYSSplashScreen(null, "jMESYS", false);
	    //splash.display();
			    
		spectrum=new Spectrum48k(0);

		frame.add(this);
		  
		frame.addWindowListener(this);
		Insets b = frame.getInsets();
		Dimension d = this.getPreferredSize();
		frame.setSize(d.width+b.left+b.right, d.height+b.top+b.bottom);
		
		
		
		frame.show();
		
		
		
		showStatus(getAppletInfo());
		//splash.closeSplash();

		addKeyListener(this);
		addFocusListener(this);

                InputStream pin = resource("/qaop128.ini");
                System.out.println(pin==null);
                Properties prop = new Properties();
                try {
                  prop.load(pin);
                }
                catch (Exception ex) {
                  System.out.println("Can't read /qaop128.ini");
                }
                
                //splash.closeSplash();
                
                
                /* parse ini */
                String _ini_mode = (String) prop.get("mode");
                int ini_mode = (_ini_mode != null && _ini_mode.equals("48")) ? SpectrumModels.MODE_48K :
                	SpectrumModels.MODE_128K;
                boolean ini_keymatrix = ini_param( (String) prop.get("keymatrix"), true);
                String ini_arrows = (String) prop.get("arrows");
                boolean ini_ay = ini_param( (String) prop.get("ay"), true);
                boolean ini_muted = ini_param( (String) prop.get("muted"), false);
                
                

                /* Spectrum mode */
                String smode = param("mode");
                int mode = smode != null ? (smode.equals("48") ? SpectrumModels.MODE_48K : SpectrumModels.MODE_128K) :
                    ini_mode;
                mode=SpectrumModels.MODE_128K;
                System.out.println("MODE: "+mode);
                spectrum = new Spectrum48k(mode);
                
                /* keymatrix effect */
                spectrum.setKeymatrix(ini_keymatrix);

		if(param("focus", true))
			addComponentListener(this);

		String rom = param("rom");
		if(rom == null) {
			System.out.println("Leo ROM");
			//InputStream in = resource("/games/Sinclair/Spectrum/ShadowOfTheUnicorn.rom");
			InputStream in = resource("/bios/Sinclair/Spectrum/spectrum.rom");
			System.out.println(in==null);
			if(in==null || FileFormat.tomem(spectrum.rom48k, 0, 16384, in) != 0)
				System.out.println("Can't read /rom/spectrum.rom");
		}
                String rom128 = param("rom128");
                if (mode != SpectrumModels.MODE_48K && rom128 == null) {
                        InputStream in = resource("/bios/Sinclair/Spectrum/Penta_sp.rom");
                        if(in==null || FileFormat.tomem(spectrum.rom128k, 0, 16384, in) != 0)
                                showStatus("Can't read /rom/128.rom");
                }

                System.out.println(rom);
                System.out.println(rom128);
                     
		loader = new jMESYSLoader(this, rom, "file:///workspace/jMESYSalpha/src/bios/Sinclair/Spectrum/if1.rom", rom128);

		//loader.load("file:///workspace/jMESYSbeta/bin/games/Sinclair/Spectrum/SPACHARR.Z80");
		//loader.tape(param("tape"));
		//loader.tape("file:///workspace/jMESYSbeta/bin/games/Sinclair/Spectrum/SILENTSD.TAP");

		String a = param("arrows");
                if (a != null)
                  spectrum.setArrows(a);
                else
                if (ini_arrows != null)
                  spectrum.setArrows(ini_arrows);

                if (param("ay", ini_ay)){
                  //spectrum.ay(true);
                	try {
						spectrum.audioChip.setEnabled(true);
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
                }

                if (param("muted", ini_muted)){
                  //spectrum.mute(true);
                	try {
						spectrum.audioChip.setMuted(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace(System.out);
					}
                }
                
     // creo el menu
        //
        //this.setMenuBar( menubar.getMenuBar(this) );
        //menuOptions=this.getMenuBar().getMenuCount();
        createComputerList();
        creaMenu(this);
        
        
        
     // creo la lista de opciones
     		try {
     			menubar.setFormatsMenu(getComputer().getSupportedFileFormats(), this);
     		} catch (Exception e){
     			e.printStackTrace(System.out);
     		}
     		
     		
     		
     		// creo listeners del menú
     		for (int i=0 ; i<menuOptions ; i++){
     			menubar.getMenuBar(this).getMenu(i).addActionListener(this);
     			System.out.println(menubar.getMenuBar(this).getMenu(i).getActionCommand());
     		}
     		//splash.closeSplash();
        showStatus("jMESYS");
        if (true) {
        	spectrum.scale(1);
			img = createImage(spectrum.getDisplay());
			
        }
        this.focus();
        
        
        
		spectrum.start();
		
		loader.start();
	}
	
	private Spectrum48k getComputer() {
		return spectrum;
	}
	
	private void creaMenu(ActionListener t) {
		
		jMESYSFamily[] families = new jMESYSFamily[1];
		families[0] = new SpectrumModels();
		
		//menubar = (new jMESYSMenu(COMPUTERS));
		menubar = (new jMESYSMenu(families));
		
	    /*Object f = getParent ();
	    while (! (f instanceof Frame))
	      f = ((Component) f).getParent ();
	    frame = (Frame) f;*/

	    frame.setMenuBar(menubar.getMenuBar(this));
	    frame.pack();
	}

	public synchronized void destroy()
	{
		spectrum.interrupt();
		loader.interrupt();
		//spectrum.audio.close();
		try {
			spectrum.audioChip.close();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public String getVersion() {
		return jMESYSVersion.getVersion();
	}

	public String getAppletInfo() {
          String version = getVersion();
          return version;
	}

	static final String info[][] = {
		{"rom", "filename", "alternative ROM image"},
		{"if1rom", "filename", "enable Interface1; use this ROM"},
		{"tape",  "filename", "tape file"},
		{"load",  "filename", "snapshot or tape to load"},
		{"focus", "yes/no", "grab focus on start"},
		{"arrows", "keys", "define arrow keys"},
		{"ay", "yes/no", "with AY"},
		{"mute", "yes/no", "muted to start"},
                {"mode", "48/128", "spectrum 48K/128K"},
                {"rom128", "filename", "alternative 128K ROM image"},
	};

	public String[][] getParameterInfo() {
		return info;
	}

	public Dimension getPreferredSize() {
		return new Dimension(spectrum.getDisplay().getWidth(), spectrum.getDisplay().getHeight());
	}

	/* javascript interface */

	public synchronized void reset()
	{
		spectrum.reset();
	}

	public void load(String name)
	{
		if(loader!=null) loader.load(name);
	}

	public void tape(String name)
	{
		if(loader!=null) loader.tape(name);
	}

	public void focus()
	{
		requestFocus();
	}

	/* graphics */

	private void resized(Dimension d)
	{
		//System.out.println("Resized W="+d.getWidth()+" H="+d.getHeight());
		Dimension d2 = this.getSize();
		//System.out.println("Window W="+frame.getWidth()+" H="+frame.getHeight());
		size = d;
		int s = d.width>=512 && d.height>=384 ? 2 : 1;
		
		//int s = 2;
		if(spectrum.scale() != s) {
			img = null;
			spectrum.scale(s);
			img = createImage(spectrum.getDisplay());
		}


		//posx = (d.width-spectrum.getDisplay().getWidth())/2;
		//posy = (d.height-spectrum.getDisplay().getHeight())/2;
		posx=0;posy=0;
		dl_image = null;
		loader.reshape(d);
	}

	public void paint(Graphics g)
	{
		update(g);
	}

	public synchronized void update(Graphics g)
	{
		Dimension d = getSize();
		//System.out.println("WIDTH=" +d.width+" HEIGH="+d.height);
		if(!d.equals(size)) {
			resized(d);
		}
		//resized(new Dimension(640,480));

		if(loader.flength > 0) {
			paint_dl(g);
			return;
		}
		
		// filters
		try {
			
			//img = checkImageFilters(img);
			
			if (!spectrum.getDisplay().fullScreen()) {
				g.drawImage(checkImageFilters(img), 0, 0, this);
			} else {
				Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				g.drawImage(checkImageFilters(img), 0, 0, d.width, d.height, this);
				//fullScreen(checkImageFilters(img), g);
				//Image img2 = createResizedCopy((img), 1366, 768, false);
				//g.drawImage(checkImageFilters(img2), 0, 0, 1366, 768, this);
			}
			//fullScreen(checkImageFilters(img), g);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
				
		
	}
	
	BufferedImage createResizedCopy(Image originalImage, 
    		int scaledWidth, int scaledHeight, 
    		boolean preserveAlpha)
    {
    	System.out.println("resizing...");
    	int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    	BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
    	Graphics2D g = scaledBI.createGraphics();
    	if (preserveAlpha) {
    		g.setComposite(AlphaComposite.Src);
    	}
    	g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
    	g.dispose();
    	return scaledBI;
    }
	
	private Image checkImageFilters(Image imgin) throws Exception {
		
		Image imgOut = imgin;
		boolean filterApplied = false;
		
		if (getComputer().getDisplay().blur){
			//System.out.println("Aplicamos filtro blur");
			jMESYSFilter_Blur filterBlur = new jMESYSFilter_Blur();
			imgOut = filterBlur.applyFilter(imgin, getComputer().getDisplay().getWidth()*getComputer().scale(), getComputer().getDisplay().getHeight()*getComputer().scale());
			filterApplied = true;
		}
		
		/*if (filterApplied) {
			getComputer().getDisplay().force_redraw();
			getComputer().getDisplay().update_screen();
			filterApplied=false;
		}*/
		
		return imgOut;
	}
	
	

	private Image dl_image;

	private void paint_dl(Graphics g)
	{
		//System.out.println("paint_dl");
		int x = posx, y = posy;
		int sw = spectrum.getDisplay().getWidth(), sh = spectrum.getDisplay().getHeight();
		//System.out.println("sw="+sw+" sh="+sh);

		if(dl_image==null)
			dl_image = createImage(sw, sh);

		Graphics g2 = dl_image.getGraphics();
		g2.drawImage(img, 0,0, this);
		g2.translate(loader.x-x, loader.y-y);
		loader.paint(g2);
		g2.dispose();
		
		// filters
		try {
			g.drawImage(checkImageFilters(dl_image), x, y, null);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public boolean imageUpdate(Image i, int f, int x, int y, int w, int h)
	{
		//System.out.println("WW="+w+" HH="+h);
		if (spectrum.getDisplay().fullScreen()) {
			Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			w=screen.width;
			h=screen.height;
		}
		//System.out.println("WW2="+w+" HH2="+h);
		
		if((f&FRAMEBITS)!=0) {
			repaint(posx+x, posy+y, w, h);
			return true;
		}
		if((f&~SOMEBITS)==0)
			return true;
		return super.imageUpdate(i, f, x,y,w,h);
	}
	
	/* KeyListener, FocusListener */

	public void keyTyped(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {
		int c = e.getKeyCode();
		boolean m=false;
		int v;
		if(c==e.VK_DELETE && e.isControlDown()) {
			spectrum.reset();
			return;
		} else if(c==e.VK_F11) {
			//spectrum.mute(m = !spectrum.muted);
			try {
				spectrum.audioChip.muteSoundCard(m = !spectrum.audioChip.isMuted());
			} catch (Exception e1) {
				e1.printStackTrace(System.out);
			}
			//v = spectrum.volumeChg(0);
			v = spectrum.audioChip.volumeChg(0);
		} else if(c==e.VK_PAGE_UP || c==e.VK_PAGE_DOWN) {
			//m = spectrum.muted;
			m=spectrum.audioChip.isMuted();
			//v = spectrum.volumeChg(c==e.VK_PAGE_UP ? +5 : -5);
			v=spectrum.audioChip.volumeChg(c==e.VK_PAGE_UP ? +5 : -5);
		} else {
			keyEvent(e);
			return;
		}
		String s = "Volume: ";
		for(int i=0;i<v;i+=4) s += "|";
		s += " "+v+"%";
		if(m) s += " (muted)";
		showStatus(s);
	}

	public void keyReleased(KeyEvent e) {
		keyEvent(e);
	}

	void keyEvent(KeyEvent e) {
		KeyEvent[] k = spectrum.keys;
		int c = e.getKeyCode();
		int j = -1;
		synchronized(k) {
			for(int i=0; i<k.length; i++) {
				if(k[i] == null) {
					j = i;
					continue;
				}
				int d = k[i].getKeyCode();
				if(d == c) {
					j = i;
					break;
				}
			}
			if(j>=0)
				k[j] = e.getID()==KeyEvent.KEY_PRESSED ? e : null;
		}
	}

	public void focusGained(FocusEvent e) {
		//showStatus(getAppletInfo());
	}

	public void focusLost(FocusEvent e) {
		KeyEvent[] k = spectrum.keys;
		synchronized(k) {
			for(int i=0; i<k.length; i++) k[i] = null;
		}
	}

	/* ComponentListener */

	public void componentResized(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}

	public void componentShown(ComponentEvent e) {
		removeComponentListener(this);
		requestFocus();
	}

	/* parameters */

	String param(String n)
	{
		return param!=null ? (String)param.get(n) : getParameter(n);
	}

        boolean ini_param(String val, boolean dflt) {
          if (val == null || val.length() == 0)
            return dflt;
          char c = Character.toUpperCase(val.charAt(0));
          return c != 'N' && c != 'F' && c != '0';
        }
      
	boolean param(String name, boolean dflt)
	{
		String p = param(name);
		if(p==null || p.length()==0) return dflt;
		char c = Character.toUpperCase(p.charAt(0));
		return c!='N' && c!='F' && c!='0';
	}

	/* resource */

	private final InputStream resource(String name)
	{
		System.out.println(name);
		return getClass().getResourceAsStream(name);
	}

	

	/* download */

	public URL url_of_file(String f) {
		if(f != null) try {
			return new URL(getDocumentBase(), f);
		} catch(MalformedURLException e) {
			showStatus(e.toString());
			System.out.println(e);
		}
		return null;
	}

	/* snapshot & tape loading */

	private InputStream dl_input;
	private int dl_kind;
	private boolean dl_gz;

	public void do_load(InputStream in, int kind, boolean gz) {
		System.out.println("do_load");
		dl_input = in;
		dl_kind = kind;
		dl_gz = gz;
	}

	public void run() {
		InputStream in = dl_input;
		//dl_input = null;
		try {
			
			if(dl_gz) {
				/*ZipInputStream inZip = new ZipInputStream(dl_input);
				ZipEntry zEntry = inZip.getNextEntry();
				zEntry.*/
				System.out.println("ZIP");
				//in = new InflaterInputStream(dl_input);
				/*ZipInputStream inZip = new ZipInputStream(dl_input);
				ZipFile zipi = new ZipFile("a.zip");
		          in = zipi.getInputStream(inZip.getNextEntry());*/
				in = jMESYSFileZIP.checkZIP(dl_input); 
			}
			
			switch(dl_kind) {
				case jMESYSLoader.TZX:
					System.out.println("Lanzamos Play TZX "+in.available());
					load_tzx(in);
					//getComputer().play_TZX(in);
					break;
				case jMESYSLoader.TAP: load_tape(in); break;
				case jMESYSLoader.SNA: load_sna(in); break;
				case jMESYSLoader.Z80: load_z80(in); break;
				case jMESYSLoader.CART:
					spectrum.reset();
				default:
					load_rom(in, dl_kind);
			}
		} catch(Exception e) {e.printStackTrace(System.out);}
	}

	private void load_tzx(InputStream in) {
		try {
			FormatTZX fTZX = new FormatTZX();
			fTZX.loadFormat("SN", in, spectrum);
		} catch (Exception e){
			e.printStackTrace(System.out);
		}		
		
	}
	
	
	private void load_rom(InputStream in, int kind) throws IOException {
		int m[] = new int[0x8000];
		if(FileFormat.tomem(m, 0, kind&0xFFF0, in) != 0) {
			showStatus("Rom image truncated");
			return;
		}
		if(kind == jMESYSLoader.IF1ROM) {
			System.arraycopy(m,0,m,0x4000,0x4000);
			spectrum.if1rom = m;
			return;
		}
		if(kind == jMESYSLoader.ROM)
			spectrum.rom48k = m;
                if (kind == jMESYSLoader.ROM128)
                        spectrum.rom128k = m;
//		spectrum.rom = m;
	}

	private void load_tape(InputStream ins) {
		try {
			FormatTAP fTAP = new FormatTAP();
			fTAP.loadFormat("SN", ins, spectrum);
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	private void load_sna(InputStream ins) throws IOException {
		
		try {
			FormatSNA fSNA = new FormatSNA();
			fSNA.loadFormat("SN", ins, spectrum);
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	private void load_z80(InputStream ins) throws IOException {
		try {
			FormatZ80 fZ80 = new FormatZ80();
			fZ80.loadFormat("SN", ins, spectrum);
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	

	/* standalone */

	private static Hashtable param;

	public URL getDocumentBase() {
		try {
			return super.getDocumentBase();
		} catch(Exception e) {}
		try {
			return new URL("file", "", "");
		} catch(Exception e) {}
		return null;
	}

	public void showStatus(String s) {
		try {
			super.showStatus(s);
			//System.out.println(s);
		} catch(Exception e) {
			System.out.println(s);
		}
	}

	public static void main(String args[])
	{
		Hashtable p = new Hashtable();
		for(int n=0; n<args.length; n++) {
			String a = args[n];
			int i = a.indexOf('=');
			if(i>=0)
				p.put(a.substring(0,i), a.substring(i+1));
			else
				p.put("load", a);
		}
		param = p;

		
		jMESYS a = new jMESYS();
		
		a.init();
		
	}

	/* WindowListener */

	public void windowOpened(WindowEvent e) {
		//start();
	}

	public void windowClosing(WindowEvent e) {
		//stop();
		destroy();
		System.exit(0);
	}

	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	private void createComputerList() {
	   //COMPUTERS = new jMESYSDriver[1];
	   //COMPUTERS[0]=new jMESYSDriver("Spectrum48", "Sinclair ZX Spectrum 48K", "jMESYS.drivers.Spectrum48", true);
		
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		//System.out.println("Pulso "+ev.getActionCommand());
				//System.out.println(ev.getSource().getClass());
				
				try {
					//System.out.println("Desactivamos CPU");
					this.spectrum.pause(true);
					//this.spectrum.mute(true);
					this.spectrum.audioChip.muteSoundCard(true);
					
					
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
							fileDialog = new jMESYSFileLoader(276, 213, getComputer().getSupportedFileFormats(), getComputer().getDisplay());
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
					
				//} else if (ev.getActionCommand().equals("Sinclair ZX Spectrum 48K")) {
				}else if ( ev.getSource().getClass().toString().endsWith("jMESYS.gui.jMESYSMenuComputerItem") ) {
					//openLoadDialog();	
					try {
						
						System.out.println("MODE CHANGE");
						jMESYSMenuComputerItem mItem = (jMESYSMenuComputerItem) ev.getSource();
						
						int mode=mItem.getModel();
						int scale=spectrum.scale();
						boolean fullSC = spectrum.getDisplay().fullScreen();
						
						spectrum = mItem.getFamily().setModel(mode);
						
						img = createImage(spectrum.getDisplay());
						
						spectrum.start();
                        //loader.start();
						
						spectrum.reset();
						
						repaint();
						spectrum.scale(scale);
						spectrum.getDisplay().fullScreen(fullSC);
						spectrum.getDisplay().force_redraw();
						
						/*int mode=SpectrumModels.MODE_128K;
						
						spectrum.audioChip.muteSoundCard(true);
						
						spectrum = new Spectrum48k(mode);
						
						spectrum.setKeymatrix(true);
		                
						System.out.println("MODE: "+mode);
						
						InputStream in = resource("/bios/Sinclair/Spectrum/spectrum.rom");
						System.out.println(in==null);
						if(in==null || FileFormat.tomem(spectrum.rom48k, 0, 16384, in) != 0)
							System.out.println("Can't read /bios/Sinclair/Spectrum/spectrum.rom");
					
		                
						in = resource("/bios/Sinclair/Spectrum/plus2-0.rom");
                        if(in==null || FileFormat.tomem(spectrum.rom128k, 0, 16384, in) != 0)
                                showStatus("Can't read /bios/Sinclair/Spectrum/plus3-1.rom");
                        loader = new jMESYSLoader(this, null, null, null);
                        
                        showStatus("jMESYS");
                        if (true) {
                        	spectrum.scale(1);
                			img = createImage(spectrum.getDisplay());
                			
                        }
                        
                        spectrum.start();
                        loader.start();
						
						spectrum.reset();
						
						repaint();*/
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
				} else if (ev.getActionCommand().equals("Color")) {
					System.out.println("set Color Palette");
					getComputer().getDisplay().setPalette(getComputer().getDisplay().getDefaultPalette());
				} else if (ev.getActionCommand().equals("Black and White")) {
					System.out.println("set Black and White Palette");
					getComputer().getDisplay().setBWPalette();
				} else if (ev.getActionCommand().equals("Green Monochrome")) {
					System.out.println("set Green Monochrome Palette");
					getComputer().getDisplay().setGreenPalette();
				} else if (ev.getActionCommand().equals("Amber Monochrome")) {
					System.out.println("set Green Monochrome Palette");
					getComputer().getDisplay().setOrangePalette();
				}else if (ev.getActionCommand().equals("Blur Off")) {
					System.out.println("set Blur OFF");
					getComputer().getDisplay().setBlur(false);
				} else if (ev.getActionCommand().equals("Blur On")) {
					System.out.println("set Blur ON");
					getComputer().getDisplay().setBlur(true);
				} else if (ev.getActionCommand().equals("Exit")) {
					System.exit(0);
				} else if (ev.getActionCommand().equals("All Formats")) {
					openLoadDialog();
				} else if (ev.getActionCommand().equals("Reset")) {
					//getComputer().resetKeyboard();
					//getComputer().mute(true);
					try {
						getComputer().audioChip.muteSoundCard(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					getComputer().reset();
					if (getComputer().soundON) {
						//getComputer().mute(false);
						try {
							getComputer().audioChip.muteSoundCard(false);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}  else if (ev.getActionCommand().equals("Sound Off")) {
					System.out.println("Sound Off");
					//getComputer().mute(true);
					try {
						getComputer().audioChip.setMuted(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					getComputer().soundON = false;
				}  else if (ev.getActionCommand().equals("Sound On")) {
					System.out.println("Sound On");
					//getComputer().mute(false);
					try {
						getComputer().audioChip.setMuted(false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					getComputer().soundON = true;
				}  else if (ev.getActionCommand().equals("Size X 1")) {
					//getComputer().getDisplay().scale=1;
					//fullScreen = false;
					spectrum.getDisplay().fullScreen( false );
					getComputer().scale(1);
					posx=0;posy=0;
					dl_image = null;
					Dimension d = new Dimension(368,346);
					//this.resized(d);
					//this.setSize(d);
					img = createImage(getComputer().getDisplay());
					loader.reshape(d);
					frame.setSize(368, 346);
					
					
					getComputer().getDisplay().force_redraw();
					//this.resized(d);
					//this.resizeScreen();
					//this.resize(new Dimension(getComputer().getDisplay().getWidth(), getComputer().getDisplay().getHeight()));
				}  else if (ev.getActionCommand().equals("Size X 2")) {
					//fullScreen = false;
					spectrum.getDisplay().fullScreen( false );
					getComputer().scale(2);
					posx=0;posy=0;
					dl_image = null;
					Dimension d = new Dimension(720,634);
					this.resized(d);
					this.setSize(d);
					img = createImage(getComputer().getDisplay());
					frame.setSize(720, 634);
					loader.reshape(d);
					getComputer().getDisplay().force_redraw();
					
					//this.resize(new Dimension(getComputer().getDisplay().getWidth()*getComputer().getDisplay().scale, getComputer().getDisplay().getHeight()*getComputer().getDisplay().scale));
				} else if (ev.getActionCommand().equals("Poke")) {
					jMESYSPokeDialog dialog = new jMESYSPokeDialog(frame, "Poke", true);
					int option = dialog.showOpenDialog();
					System.out.println("Option="+dialog.getOption());
					if (dialog.getOption() == 1){
						getComputer().mem(Integer.parseInt(dialog.getAddress()), Integer.parseInt(dialog.getValue()));
						int posi=16384;
						for (int i=posi;i<(posi+100);i++){
							System.out.print(getComputer().mem(i)+" ");
						}
					}
					
				} else if (ev.getActionCommand().equals("Show Printer")) {
					jMESYSPrinterFrame printer = getComputer().getPrinter(frame);
					
					//int option = printer.showOpenDialog();
					//printer.setVisible(true);
					
				}  else if (ev.getActionCommand().equals("Full Screen")) {
					System.out.println("FULL SCREEN");
					getComputer().scale(1);
					//fullScreen = true;
					spectrum.getDisplay().fullScreen( true );
					
					posx=0;posy=0;
					dl_image = null;
					//Dimension d = new Dimension(720,634);
					Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
					this.resized(d);
					this.setSize(d);
					//getComputer().getDisplay().
					img = createImage(getComputer().getDisplay());
					frame.setSize(d.width, d.height);
					loader.reshape(d);
					getComputer().getDisplay().force_redraw();
					
				}  else if (ev.getActionCommand().equals("Play Tape")) {
					System.out.println("Playing Tape");
					FormatTXT fTXT = new FormatTXT();
					try {
						fTXT.loadFormat("kk", new FileInputStream(new File("/b.txt")), getComputer());
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
				}  else if (ev.getActionCommand().equals("Remote WOS")) {
					System.out.println("Remote World of Spectrum Site");
					
					try {
						jMESYSRemoteFileLoader remoteWOS = new jMESYSRemoteFileLoader(this.frame, getComputer().getSupportedFileFormats(), getComputer().getDisplay());
						int option = remoteWOS.showOpenDialog();
						
						if (option != 0){
							//File ftmp=new File();
							WOSsite wos = new WOSsite();
							FileFormat[] ff = getComputer().getSupportedFileFormats();
							int countFormats = ff.length;
							
							FileFormat xZ80 = null;
							
							RemoteFile rf = new RemoteFile();
							rf.setName( remoteWOS.getFiletoLoad() );
							
							rf.setPath("");
							//xZ80.loadFormat("/a.z80", wos.getZIPcontents(rf), getComputer());
							//xZ80.getScreen("/a.z80", wos.getZIPcontents(rf), display, panScreen.getGraphics());
							InputStream is = wos.getZIPcontents(rf);
							
							//System.out.println("Buscamos "+rf.getExtension());
							
							for (int i=0 ; i<countFormats ; i++){
								//System.out.println("Formato actual "+ff[i].getExtension().toUpperCase());
								if (("."+rf.getExtension().toUpperCase()).equals(ff[i].getExtension().toUpperCase())){
									//System.out.println("Formato Encontrado!");
									xZ80 = ff[i];
								}
							}
							
							//xZ80.loadFormat(remoteWOS.getFiletoLoad(), new FileInputStream(remoteWOS.getFiletoLoad()), getComputer());
							getComputer().reset();
							
							// removes the cache
							try {
								loader.cache.remove(new URL("file:/a."+rf.getExtension()));
							} catch (Exception e){
								System.out.println("No borrado");
							}
							
							//xZ80.loadFormat("/a."+rf.getExtension(), is, getComputer());
							loader.load("/a."+rf.getExtension());
						} 
						
					} catch (Exception e){
						e.printStackTrace(System.out);
					}
				}
				
				
				/*System.out.println("Presiono "+ev.getID());	
				System.out.println("Presiono "+ev.getActionCommand());
				System.out.println("Presiono "+ev.getSource().getClass());
				getComputer().load();*/
				
				try {
					//System.out.println("Activamos CPU");
					//this.getComputer().cpu.resumeCPU();
					this.spectrum.pause(false);
					if (getComputer().soundON) {
						//getComputer().mute(false);
						getComputer().audioChip.muteSoundCard(false);
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
				fileDialog = new jMESYSFileLoader(getComputer().getDisplay().getWidth(), getComputer().getDisplay().getHeight(), getComputer().getSupportedFileFormats(), getComputer().getDisplay());
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
			/*FileFormat fl = fileDialog.getLoaderFile(fileDialog.getSelectedFile().getAbsolutePath());
			if (fl != null) {
				String name = fileDialog.getSelectedFile().getAbsolutePath();
				String nameChecked = jMESYSFileZIP.checkZIP( name );
				//System.out.println("Voy a cargar 2 "+nameChecked);
				fl.loadFormat(nameChecked, new FileInputStream(nameChecked), getComputer());
			}*/
			String cad = fileDialog.getSelectedFile().toURI().toString();
			/*if (cad.contains("\\")) {
				cad.replace("\\", "/");
				cad = "file://" + cad;
			}*/
			
			loader.load(cad);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}

