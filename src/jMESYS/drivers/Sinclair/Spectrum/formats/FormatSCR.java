package jMESYS.drivers.Sinclair.Spectrum.formats;

import java.awt.Graphics;
import java.awt.Image;
import java.io.InputStream;

import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.drivers.Sinclair.Spectrum.display.SpectrumDisplay;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public class FormatSCR extends FileFormat {
	
	// extension
	private static String strExtension = ".SCR";

	public String getExtension() {
		return strExtension;
	}
	
	public boolean resetBeforeLoad() {
		return false;
	}

	public void loadFormat(String name, InputStream is, jMESYSComputer computer) throws Exception {


		int[] ram = new int[6912];
        tomem(ram, 0, 6912, is);
        
        poke_array(computer, ram, 16384, 6912);
	}

	public Image getScreen(String name, InputStream is, jMESYSDisplay display, Graphics g) throws Exception {
		System.out.println("getScreen SCR");

		byte[] arrScr = new byte[6144];
		byte[] arrAtt = new byte[768];
		
		readBytes( is, arrScr,    0, (32*192));
		readBytes( is, arrAtt, 0, 768);
		
		SpectrumDisplay dsp = (SpectrumDisplay) display;
		
		dsp.paintImageScreen(g, arrScr, arrAtt);
		
		return null;
	}

	public String getFileName() {
		return "D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/WorldSeriesBaseball.scr";
	}

}
