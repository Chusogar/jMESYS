package jMESYS.drivers.Coleco.ColecoVision.formats;

import java.awt.Graphics;
import java.awt.Image;
import java.io.InputStream;

import jMESYS.drivers.jMESYSComputer;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public class FormatROM extends FileFormat {
	
	// extension
	private static String strExtension = ".ROM";

	public String getExtension() {
		return strExtension;
	}

	@Override
	public void loadFormat(String name, InputStream is, jMESYSComputer cpu) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getScreen(String name, InputStream is, jMESYSDisplay display, Graphics g) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFileName() {
		return "D:/workspace/jMESYSalpha/bin/games/Coleco/ColecoVision/Star Wars - The Arcade Game (1984).rom";
	}

}
