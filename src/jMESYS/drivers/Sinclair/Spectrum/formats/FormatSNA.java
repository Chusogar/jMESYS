package jMESYS.drivers.Sinclair.Spectrum.formats;

import jMESYS.core.cpu.CPU;
import jMESYS.core.cpu.z80.Z80;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.drivers.Sinclair.Spectrum.display.SpectrumDisplay;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
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
	
	public void loadFormat( String name, InputStream is, Spectrum48k computer ) throws Exception {
		DataInputStream in = new DataInputStream(is);
		computer.reset();
		Z80 cpu = computer.cpu;

		cpu.i(get8(in));
		cpu.hl(get16(in)); cpu.de(get16(in)); cpu.bc(get16(in));
		cpu.af(get16(in));
		cpu.exx(); cpu.ex_af();
		cpu.hl(get16(in)); cpu.de(get16(in)); cpu.bc(get16(in));
		cpu.iy(get16(in)); cpu.ix(get16(in));
		cpu.ei(get8(in)!=0);
		cpu.r(get8(in));
		cpu.af(get16(in));
		cpu.sp(get16(in));
		cpu.im(get8(in));
		computer.out(254, get8(in));
                
                int[] ram49152 = new int[49152];
                tomem(ram49152, 0, 49152, in);

		try {
			cpu.pc(get16(in));
                        /* It is 128K .SNA */
                        System.out.println("It is 128K .SNA");
                        int port7ffd = get8(in);
                        int trdosrom = get8(in);
                        computer.out(32765, port7ffd);
                        poke_array(computer, ram49152, 16384, 49152);
                        for (int i = 0; i < 8; i++) {
                          if (i == 2 || i == 5 || (i == (port7ffd & 0x03)))
                            continue;
                          tomem(computer.get_rambank(i), 0, 16384, in);
                        }
                        
		} catch(EOFException e) {
                        System.out.println("It is 48K .SNA");
                        computer.rom = computer.rom48k;
                        poke_array(computer, ram49152, 16384, 49152);
			int sp = cpu.sp();
			cpu.pc(computer.mem16(sp));
			cpu.sp((char)(sp+2));
			computer.mem16(sp, 0);
		}
	}

	public String getExtension() {		
		return strExtension;
	}

	public Image getScreen(String name, InputStream is, jMESYSDisplay disp, Graphics g) throws Exception {
		System.out.println("getScreen SNA");
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
