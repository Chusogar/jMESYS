package jMESYS.gui;

import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.jMESYSDriver;
import jMESYS.drivers.jMESYSFamily;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.files.FileFormat;

import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;

public class jMESYSMenu {

	private static MenuBar menubar = null;
	//private jMESYSDriver[] computers = null;
	private jMESYSFamily[] computers = null;
	private Menu mOpen;
	
	/*public jMESYSMenu(jMESYSDriver[] comps) {
		super();
		computers = comps;
	}*/
	
	public jMESYSMenu(jMESYSFamily[] comps) {
		super();
		computers = comps;
	}
	
	public MenuBar getMenuBar(jMESYS t) {
	
		if (menubar==null){
			
			menubar = new MenuBar();
			menubar.setFont(new Font("",0,11));
			
			Menu menuFile = new Menu("File");
			Menu menuSys = new Menu("Systems");
			Menu menuSet = new Menu("Settings");
		    
			Menu mTape = new Menu("Tape");
		    MenuItem mPlayTape = new MenuItem("Play Tape");
		    mPlayTape.addActionListener(t);
		    mTape.add(mPlayTape);
		    
		    //Menu menue4 = new Menu("Monitor");
		    //Menu menue6 = new Menu("Edit");
		    Menu menuExtra = new Menu("Extras");
		    Menu menuHelp = new Menu("Help");
		    
		    menubar.add(menuFile);
		    menubar.add(menuSys);
		    menubar.add(menuSet);
		    menubar.add(mTape);
		    menubar.add(menuExtra);
		    menubar.add(menuHelp);
		    
		    mOpen = new Menu("Open");
		    
		    MenuItem mExit = new MenuItem("Exit");
		    mExit.addActionListener(t);
		    
		    //mOpen.addActionListener(arg0);
		    
		    menuFile.add(mOpen);
		    menuFile.add(mExit);
		    
		    // Menu Systems
		    int longo = computers.length;
		    /*for (int i = 0; i < longo; i++) {
		    	jMESYSDriver desc = computers[i];
	          if (desc.shown) {
	        	  MenuItem mIt = new MenuItem(desc.name);
	        	  mIt.addActionListener(t);
	        	  
	        	  menuSys.add(mIt);	           
	        }*/
		    
		    for (int i = 0; i < longo; i++) {
		    	jMESYSFamily fam = computers[i];
		    	Menu mFam = new Menu(fam.getFamilyName());
		    	menuSys.add(mFam);
	        	  
	        	  
				try {
					jMESYSComputer[] comps;
					comps = fam.getModels();
					
					int numModels = comps.length;
		        	  
		        	  for (int iFam=0 ; iFam<numModels ; iFam++){
		        		  jMESYSMenuComputerItem mComp = new jMESYSMenuComputerItem( fam.getModelDescription(iFam), iFam, fam );
		        		  mComp.addActionListener(t);
		        		  mFam.add(mComp);
		        	  }
		        	  
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	  
		   }
		    
		    // Menu Extras
		    MenuItem menuPoke = new MenuItem("Poke");
		    menuPoke.addActionListener(t);
		    MenuItem menuPrinter = new MenuItem("Show Printer");
		    menuPrinter.addActionListener(t);
		    menuExtra.add(menuPoke);
		    menuExtra.add(menuPrinter);
		   
		    // Menu Settings
		    Menu mDisplay = new Menu("Display");
		    MenuItem crtcColor = new MenuItem("Color");
		    crtcColor.addActionListener(t);
		    MenuItem crtcBW = new MenuItem("Black and White");
		    crtcBW.addActionListener(t);
		    MenuItem crtcGreen = new MenuItem("Green Monochrome");
		    crtcGreen.addActionListener(t);
		    MenuItem crtcAmber = new MenuItem("Amber Monochrome");
		    crtcAmber.addActionListener(t);
		    
		    MenuItem ctrcBlurOFF  = new MenuItem("Blur Off");
		    ctrcBlurOFF.addActionListener(t);
		    MenuItem ctrcBlurON  = new MenuItem("Blur On");
		    ctrcBlurON.addActionListener(t);
		    
		    mDisplay.add(crtcColor);
		    mDisplay.add(crtcBW);
		    mDisplay.add(crtcGreen);
		    mDisplay.add(crtcAmber);
		    mDisplay.addSeparator();
		    mDisplay.add(ctrcBlurOFF);
		    mDisplay.add(ctrcBlurON);
		    menuSet.add(mDisplay);
		    
		    mDisplay.addSeparator();
		    
		    MenuItem mX1 = new MenuItem("Size X 1");
		    mX1.addActionListener(t);
		    mDisplay.add(mX1);
		    
		    MenuItem mX2 = new MenuItem("Size X 2");
		    mX2.addActionListener(t);
		    mDisplay.add(mX2);
		    
		    MenuItem mX3 = new MenuItem("Full Screen");
		    mX3.addActionListener(t);
		    mDisplay.add(mX3);
		    menuSet.addSeparator();
		    MenuItem mReset = new MenuItem("Reset");
		    mReset.addActionListener(t);
		    menuSet.add(mReset);
		    
		    menuSet.addSeparator();
		    MenuItem mSoundOff = new MenuItem("Sound Off");
		    mSoundOff.addActionListener(t);
		    menuSet.add(mSoundOff);
		    
		    MenuItem mSoundOn = new MenuItem("Sound On");
		    mSoundOn.addActionListener(t);
		    menuSet.add(mSoundOn);
		}
		
		return menubar;
			
	}
	
	public void setFormatsMenu(FileFormat[] supportedFormats, jMESYS t) throws Exception {
		
		if (supportedFormats != null) {
			
			int lenFormats = supportedFormats.length;
			System.out.println("Leo Formatos...");
			for (int i=0 ; i<lenFormats ; i++) {
				jMESYSMenuItem mItem = new jMESYSMenuItem( supportedFormats[i].getExtension(), supportedFormats[i] );
				System.out.println(supportedFormats[i].getFileName());
				mItem.setFileName( supportedFormats[i].getFileName() );
				mItem.addActionListener(t);
				mOpen.add( mItem );
			}
			
			mOpen.addSeparator();
			MenuItem allFormats = new MenuItem("All Formats");
			allFormats.addActionListener(t);
			mOpen.add(allFormats);
			
			mOpen.addSeparator();
			MenuItem mWOS = new MenuItem("Remote WOS");
			mWOS.addActionListener(t);
			mOpen.add(mWOS);
			
		} else {
			System.out.println("+++No supported files+++");
		}
	}

	public int getMenuCount() {
		return menubar.getMenuCount();
	}
}
