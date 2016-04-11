package jMESYS.drivers.Coleco.ColecoVision;

import java.awt.Frame;
import java.io.InputStream;

import jMESYS.core.cpu.z80.Z80;
import jMESYS.core.devices.printer.jMESYSPrinterFrame;
import jMESYS.core.sound.cards.jMESYS_SoundCard;
import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.Coleco.ColecoVision.display.ColecoVisionDisplay;
import jMESYS.drivers.Coleco.ColecoVision.formats.FormatROM;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatSCL;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatSCR;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatSNA;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatTAP;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatTZX;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatZ80;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public class ColecoVision extends jMESYSComputer {
	
	/***************************
	 *  Coleco Vision Memory Map
	 *  
	 0000H - BIOS ROM 
	 . 
	 1FFFH 
	 2000H - Expansion port 
	 . 
	 3FFFH 
	 4000H - Expansion port 
	 . 
	 5FFFH 
	 6000H - Start of RAM (1K mapped into an 8K spot) 
	 . 
	 7FFFH 
	 8000H - Cart ROM (broken into 4 sections, each enabled seperately) 
	 . 
	 FFFFH 
	*******************************************************************/
	// mode
	private int mode = 0;
	
	// ROM 8K (0x0000)
	private int romLength = 8192;
	public int rom[] = new int[romLength];
	
	// ExpasionPort 1 (0x2000)
	private int expasionPort1[] = new int[8192];
	
	// ExpasionPort 2 (0x4000)
	private int expasionPort2[] = new int[8192];
	
	// RAM 1K (0x6000) (1K mapped into an 8K spot)
	public int ram[] = new int[8192];
	
	//	holder for ROM image (0x8000)
	private int cartridgeLength = 32*1024;
	private byte romCartridge[] = new byte[cartridgeLength];
	
	// CPU
	public final Z80 cpu = new Z80(this);
	
	// display
	public ColecoVisionDisplay display = null;
	
	// content memory
	private int ctime;
	private int NOCONT=99999;
	
	// ports
	private byte[] portsColecoVision = null;
	
	// file formats supported
	private FileFormat[] supportedFormats = null;

	public ColecoVision(int mode) {
		super("Coleco Vision");

		this.mode = mode;
	}

	public int m1(int addr, int mr) {
		int n = cpu.time - ctime;
		//if(n>0) cont(n);

		addr -= romLength;
		/*if((addr&0xC000) == 0)
			cont1(0);
		ctime = NOCONT;
		if((ir&0xC000) == 0x4000)
			ctime = cpu.time + 4;*/
		if(addr >= 0){
			//return read_ram(addr);
			return ram[addr];
		}
		/*n = rom[addr+=0x4000];
		if(if1rom!=null && (addr&0xE8F7)==0) {
			if(addr==0x0008 || addr==0x1708) {
				if(rom==rom48k) rom = if1rom;
			} else if(addr==0x0700) {
				if(rom==if1rom) rom = rom48k;
			}
		}*/
		return n;
	}

	public int mem(int addr) {
		int n = cpu.time - ctime;
		//if(n>0) cont(n);
		ctime = NOCONT;

		int iData = 0;
		
		if (addr<0x2000){ // reads ROM
			iData =(int)(rom[ addr ] & 0xFF);			
		} else if (addr<0x4000) { // reads ExpansionPort 1
			iData =(int)(expasionPort1[ addr - 0x2000 ] & 0xFF);			
		} else if (addr<0x6000) { // reads ExpansionPort 2
			iData =(int)(expasionPort2[ addr - 0x4000] & 0xFF);			
		} else if (addr<0x8000) { // reads RAM
			iData =(int)(ram[ addr - 0x6000] & 0xFF);			
		}  else if (addr<0xFFFF) { // reads cartridge
			iData =(int)(romCartridge[ addr - 0x8000 ] & 0xFF);
		}
		 
		return	iData;
	}

	public void mem(int addr, int v) {
		int n = cpu.time - ctime;
		//if(n>0) cont(n);
		ctime = NOCONT;

		if (addr<0x2000){ // write ROM
			// do nothing
			//rom[ addr ] = v & 0xFF;			
		} else if (addr<0x4000) { // write ExpansionPort 1
			expasionPort1[ addr - 0x2000 ] = v & 0xFF;			
		} else if (addr<0x6000) { // write ExpansionPort 2
			expasionPort2[ addr - 0x4000 ] = v & 0xFF;			
		} else if (addr<0x8000) { // write RAM
			// maybe I have to permit only 1K????
			ram[ addr - 0x6000] = v & 0xFF;			
		}  else if (addr<0xFFFF) { // write cartridge
			romCartridge[ addr - 0x8000 ] = (byte) (v & 0xFF);
		}
	}

	public int in(int port) {
		System.out.println("IN ColecoVision port="+port);
		/*************** ColecoVision PORTS
		 * 00-1F - No Connection 
		 * 20-3F - No Connection 
		 * 40-5F - Video 
		 * 60-7F - Video 
		 * 80-9F - No Connection 
		 * A0-BF - No Connection 
		 * C0-DF - Sound 
		 * E0-FF - Controllers; E2 is special, as wellas E0 - E0 appears to be the readback, and E2 appears to be the scan - 39
		 */
		byte bIN = 0;
		
		if (portsColecoVision == null) {
			portsColecoVision = new byte[0xFF];
			
			for (int i=0 ; i<255 ; i++) {
				portsColecoVision[i] = (byte) 0;
			}
		}
		
		if ((port >= 0x00) && (port <= 0x3F)) {
			// No Connection
		} else if ((port >= 0x40) && (port <= 0x7F)) {
			// Video
			bIN = portsColecoVision[port];
		} else if ((port >= 0x80) && (port <= 0xBF)) {
			// No Connection
		} else if ((port >= 0xC0) && (port <= 0xDF)) {
			// Sound
			bIN = portsColecoVision[port];
		} else if ((port >= 0xE0) && (port <= 0xFF)) {
			// Controllers
			bIN = portsColecoVision[port];
		}
		
		return bIN;
	}

	public void out(int port, int v) {
		System.out.println("OUT ColecoVision port="+port+" value="+v);
		if (portsColecoVision == null) {
			portsColecoVision = new byte[0xFF];
			
			for (int i=0 ; i<255 ; i++) {
				portsColecoVision[i] = (byte) 0;
			}
		}
		
		if ((port >= 0x00) && (port <= 0x3F)) {
			// No Connection
		} else if ((port >= 0x40) && (port <= 0x7F)) {
			// Video
			portsColecoVision[port] = (byte) (v & 0xFF);
		} else if ((port >= 0x80) && (port <= 0xBF)) {
			// No Connection
		} else if ((port >= 0xC0) && (port <= 0xDF)) {
			// Sound
			portsColecoVision[port] = (byte) (v & 0xFF);
		} else if ((port >= 0xE0) && (port <= 0xFF)) {
			// Controllers
			portsColecoVision[port] = (byte) (v & 0xFF);
		}
	}

	public int mem16(int addr) {
		
		int n = cpu.time - ctime;
		//if(n>0) cont(n);
		ctime = NOCONT;
		
		int wData = 0;
		
		if (addr<0x2000){ // reads ROM
			wData =(int)(rom[ addr++ ] & 0xFF);
			wData |= (int)(rom[ addr ] << 8);
		} else if (addr<0x4000) { // reads ExpansionPort 1
			addr -=0x2000;
			wData =(int)(expasionPort1[ addr++ ] & 0xFF);
			wData |= (int)(expasionPort1[ addr ] << 8);
		} else if (addr<0x6000) { // reads ExpansionPort 2
			addr -=0x4000;
			wData =(int)(expasionPort2[ addr++ ] & 0xFF);
			wData |= (int)(expasionPort2[ addr ] << 8);
		} else if (addr<0x8000) { // reads RAM
			addr -=0x6000;
			wData =(int)(ram[ addr++ ] & 0xFF);
			wData |= (int)(ram[ addr ] << 8);
		}  else if (addr<0xFFFF) { // reads cartridge
			addr -=0x8000;
			wData =(int)(romCartridge[ addr++ ] & 0xFF);
			wData |= (int)(romCartridge[ addr ] << 8);
		}
		 
		return	wData;
	}

	public void mem16(int addr, int v) {
		int n = cpu.time - ctime;
		//if(n>0) cont(n);
		ctime = NOCONT;
		
		int wData = 0;
		
		if (addr<0x2000){ // write ROM
			// do nothing
			//rom[ addr++ ] = (v & 0xFF);
			//rom[ addr ] = (v<< 8);
		} else if (addr<0x4000) { // write ExpansionPort 1
			addr -=0x2000;
			expasionPort1[ addr++ ] = (v & 0xFF);
			expasionPort1[ addr ] = (v<< 8);
		} else if (addr<0x6000) { // write ExpansionPort 2
			addr -=0x4000;
			expasionPort2[ addr++ ] = (v & 0xFF);
			expasionPort2[ addr ] = (v<< 8);
		} else if (addr<0x8000) { // write RAM
			addr -=0x6000;
			ram[ addr++ ] = (v & 0xFF);
			ram[ addr ] = (v<< 8);
		}  else if (addr<0xFFFF) { // write cartridge
			// have I to write in a cartridge????
			addr -=0x8000;
			romCartridge[ addr++ ] = (byte)(v & 0xFF);
			romCartridge[ addr ] = (byte)(v<< 8);
		}		 
		
	}

	@Override
	public void refresh_new() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public jMESYSDisplay getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public jMESYS_SoundCard getAudioDevice() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCPUtime(int t) {
		cpu.time=t;
	}

	public int getCPUtime() {
		return cpu.time;
	}

	public void setCPUtimeLimit(int t) {
		cpu.time_limit=t;
	}

	public int getCPUtimeLimit() {
		return cpu.time_limit;
	}

	public void executeCPU() throws Exception {
		cpu.interrupt(0xFF);
		cpu.execute();
	}

	public void reset() {
		stop_loading();
		cpu.reset();
		//au_reset();
		try {
			//audioChip.reset();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public FileFormat[] getSupportedFileFormats() {
		if (supportedFormats == null){
			supportedFormats = new FileFormat[] {
				new FormatROM()
			};
		}
		
		return supportedFormats;
	}

	public void loadRoms() throws Exception {
		if (mode == ColecoVisionModels.MODE_ColecoVision){
			
			InputStream in = resource("/bios/Coleco/ColecoVision/ColecoVision.rom");
			
			if(in==null || FileFormat.tomem(rom, 0, 8192, in) != 0)
				System.out.println("Can't read /bios/Coleco/ColecoVision/ColecoVision.rom");
		
		
		} else {
			System.out.println("MODE "+mode+" NOT SUPPORTED!!!!");
		}
	}

	@Override
	protected void end_frame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update_keyboard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public jMESYSPrinterFrame getPrinter(Frame frame) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean check_load() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean do_load(byte[] tape, boolean ready) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setModel(int iModel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void nmi() {
		cpu.nmi();
	}

}
