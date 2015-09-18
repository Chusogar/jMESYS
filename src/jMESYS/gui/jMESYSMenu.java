package jMESYS.gui;

import jMESYS.drivers.jMESYSDriver;
import jMESYS.files.FileFormat;

import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;

public class jMESYSMenu {

	private static MenuBar menubar = null;
	private jMESYSDriver[] computers = null;
	private Menu mOpen;
	
	public jMESYSMenu(jMESYSDriver[] comps) {
		super();
		computers = comps;
	}
	
	public MenuBar getMenuBar() {
	
		if (menubar==null){
			
			menubar = new MenuBar();
			menubar.setFont(new Font("",0,11));
			
			Menu menuFile = new Menu("File");
		    Menu menuSys = new Menu("Systems");
		    Menu menuSet = new Menu("Settings");
		    //Menu crtc = new Menu("CRTC type");
		    //Menu menue4 = new Menu("Monitor");
		    //Menu menue6 = new Menu("Edit");
		    Menu menuExtra = new Menu("Extras");
		    Menu menuHelp = new Menu("Help");
		    
		    menubar.add(menuFile);
		    menubar.add(menuSys);
		    menubar.add(menuSet);
		    menubar.add(menuExtra);
		    menubar.add(menuHelp);
		    
		    mOpen = new Menu("Open");
		    MenuItem mExit = new MenuItem("Exit");
		    
		    //mOpen.addActionListener(arg0);
		    
		    menuFile.add(mOpen);
		    menuFile.add(mExit);
		    
		    // Menu Systems
		    int longo = computers.length;
		    for (int i = 0; i < longo; i++) {
		    	jMESYSDriver desc = computers[i];
	          if (desc.shown) {
	            menuSys.add(new MenuItem(desc.name));
	           
	        }
		   }
		    
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
			
		} else {
			System.out.println("+++No supported files+++");
		}
	}

	public int getMenuCount() {
		return menubar.getMenuCount();
	}
}
