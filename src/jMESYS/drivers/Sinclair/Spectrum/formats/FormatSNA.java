package jMESYS.drivers.Sinclair.Spectrum.formats;

import jMESYS.core.cpu.CPU;
import jMESYS.drivers.Sinclair.Spectrum.SpectrumDisplay;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FormatSNA extends FileFormat {
	/*
	Offset   Size   Description
   ------------------------------------------------------------------------
   0        1      byte   I
   1        8      word   HL',DE',BC',AF'
   9        10     word   HL,DE,BC,IY,IX
   19       1      byte   Interrupt (bit 2 contains IFF2, 1=EI/0=DI)
   20       1      byte   R
   21       4      words  AF,SP
   25       1      byte   IntMode (0=IM0/1=IM1/2=IM2)
   26       1      byte   BorderColor (0..7, not used by Spectrum 1.7)
   27       49152  bytes  RAM dump 16384..65535
   ------------------------------------------------------------------------
   Total: 49179 bytes
	 */
	// extension
	private static String strExtension = ".SNA";
	
	// SNA format structure
	private byte regI = 0;	
	private int regHLp = 0;
	private int regDEp = 0;
	private int regBCp = 0;
	private int regAFp = 0;
	private int regHL = 0;
	private int regDE = 0;
	private int regBC = 0;
	private int regIY = 0;
	private int regIX = 0;
	private byte interrupt = 0;
	private byte regR = 0;
	private int regAF = 0;
	private int regSP = 0;
	private byte IntMode = 0;
	private byte borderColor = 0;
	private byte[] RAM = new byte[49152];
	
	public FormatSNA(){
		super();
	}
	
	/*public void loadFile(String name, CPU cpu) throws Exception {
		checkExtension(name);		
		InputStream stream = getFileStream(name);
		loadSNA(name, stream, cpu);		
	}*/
	
	public void loadFormat( String name, InputStream is, CPU cpu ) throws Exception {
		int        header[] = new int[27];

		readBytes( is, header, 0,        27 );
		readBytes( is, cpu.getMem(),    16384, 49152 );
    
		cpu.setRegister("I", header[0]);

		cpu.setRegister("HL", ( header[1] | (header[2]<<8) ));
		cpu.setRegister("DE", ( header[3] | (header[4]<<8) ));
		cpu.setRegister("BC", ( header[5] | (header[6]<<8) ));
		cpu.setRegister("AF", ( header[7] | (header[8]<<8) ));

		cpu.exx();
		cpu.ex_af_af();

		cpu.setRegister("HL", ( header[9]  | (header[10]<<8) ));
		cpu.setRegister("DE", ( header[11] | (header[12]<<8) ));
		cpu.setRegister("BC", ( header[13] | (header[14]<<8) ));

		cpu.setRegister("IY", ( header[15] | (header[16]<<8) ));
		cpu.setRegister("IX", ( header[17] | (header[18]<<8) ));

		if ( (header[19] & 0x04)!= 0 ) {
			cpu.interruptFF("IFF2", true);
		}
		else {
			cpu.interruptFF("IFF2", false);
		}

		cpu.setRegister("R", ( header[20] ));

		cpu.setRegister("AF", ( header[21] | (header[22]<<8) ));
		cpu.setRegister("SP", ( header[23] | (header[24]<<8) ));

		switch( header[25] ) {
		case 0:
			cpu.setInterruptMode(null, 0);
			break;
		case 1:
			cpu.setInterruptMode(null, 1);
			break;
		default:
			cpu.setInterruptMode(null, 2);
			break;
		}

		cpu.outb( 254, header[26], 0 ); // border
     
		/* Emulate RETN to start */
		cpu.interruptFF("IFF1", cpu.interruptFF("IFF2") );
		cpu.REFRESH( 2 );
		cpu.popRegister("PC");

		/*if ( urlField != null ) {
			urlField.setText( name );
		}*/
	}

	public String getExtension() {		
		return strExtension;
	}

	public Image getScreen(String name, InputStream is, jMESYSDisplay disp, Graphics g) throws Exception {
		int        header[] = new int[27];
		byte[] arrScr = new byte[256*192];
		byte[] arrAtt = new byte[768];
		
		readBytes( is, header, 0,        27 );
		readBytes( is, arrScr,    0, (32*192));
		readBytes( is, arrAtt, 0, 768);
		
		SpectrumDisplay dsp = (SpectrumDisplay) disp;
		
		dsp.paintImageScreen(g, arrScr, arrAtt);
		
		return null;
	}

	public String getFileName() {
		return "D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/MIKIE.SNA";
	}

		
}
