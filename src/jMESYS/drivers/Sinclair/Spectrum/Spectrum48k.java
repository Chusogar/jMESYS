/*
 *	Spectrum.java
 *
 *	Copyright 2004-2007 Jan Bobrowski <jb@wizard.ae.krakow.pl>
 *      Extended to 128k by Andrey Radziwill 2008 <iamradziwill@gmail.com>
 *	This program is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	version 2 as published by the Free Software Foundation.
 */
package jMESYS.drivers.Sinclair.Spectrum;
import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.FileInputStream;
import java.io.InputStream;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipFile;

import com.sun.media.jfxmedia.AudioClip;

import jMESYS.core.cpu.CPU;
import jMESYS.core.cpu.z80.Z80;
import jMESYS.core.devices.jMESYSDevice;
import jMESYS.core.devices.printer.jMESYSPrinterFrame;
//import jMESYS.core.sound.Audio;
import jMESYS.core.sound.cards.AY_3_8912.AY_3_8912;
//import jMESYS.core.sound.Audio;
import jMESYS.drivers.Sinclair.Spectrum.devices.printers.ZXPrinter;
import jMESYS.drivers.Sinclair.Spectrum.display.SpectrumDisplay;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatSNA;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatTAP;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatTZX;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatZ80;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public class Spectrum48k extends Thread implements CPU
{

	public final Z80 cpu = new Z80(this);
	public SpectrumDisplay display = null;
	public jMESYSDevice[] devices = null;
	public Vector vDevices = new Vector();
	
	//public static final int MODE_48K = 0;
    //public static final int MODE_128K = 1;

	public int rom48k[] = new int[16384];
    public int rom128k[] = new int[16384];
    private final int ram48k[][] = new int[3][];
    private final int whole_ram[][] = new int[8][];
    private final int ram0[] = new int[16384];
    private final int ram1[] = new int[16384];
    private final int ram2[] = new int[16384];
    private final int ram3[] = new int[16384];
    private final int ram4[] = new int[16384];
    private final int ram5[] = new int[16384];
    private final int ram6[] = new int[16384];
    private final int ram7[] = new int[16384];
    public int rom[];
    int vram[];
    private boolean lock48k = false;
    private int mode;
    private boolean keymatrix = false;
        
	public int if1rom[];

	//public final Audio audio;
	public AY_3_8912 audioChip;
	
	public boolean soundON = true;
	
	// ZX printer
	ZXPrinter zxPrinter = null;
	
	// tape Bit
	private static int ear = 0xbf;
	private boolean tapeValue = false;
	
	// tape variables
	private int lon = 0;
	private int pas = 0;
	private int leader = 0;
	private int pulse_leader = 0;
	private int pulse_sync_1 = 0;
	private int pulse_sync_2 = 0;
	private int pulse_data_0 = 0;
	private int pulse_data_1 = 0;
	private int bits_ult = 0;
	//pause in mSeconds between blocks, default 1 second
	private int pausa = 1000;
	// T-states for reading a tape data
	private int tapein = 0;
	//Auxiliar integer for reading the tape
	private int code = 0;
	//Number & code of the last played block
	private int block = 0;
	private int code_block = 0;
	//for Zipped tapes
	private int count_ZIP = 0;
	
	private final InputStream resource(String name)
	{
		System.out.println(name);
		return getClass().getResourceAsStream(name);
	}
	
	public void addDevice(jMESYSDevice dev) {
		vDevices.addElement( dev );
	}
	
	public jMESYSDevice[] getDevices() {
		
		int numDevices = vDevices.size();
		devices = new jMESYSDevice[ numDevices ];
		
		for (int i=0 ; i<numDevices ; i++) {
			devices[i] = (jMESYSDevice) vDevices.elementAt(i);
		}
		
		return devices;
	}
	  
	// Reads one single byte of the tape
	public int readTape(InputStream is) throws Exception{
	    int j = 0;
	    try{
	      
	      byte[] b = new byte[1];
	      j = is.read(b, 0, 1);
	      j = ((b[0] + 256) & 0xff);
	    }catch(Exception e){
	      e.printStackTrace(System.out);
	      throw new Exception("Tape Error");
	    }
	    return j;
	  }
	
	//Skips n tape bytes
	public void skip(int n, InputStream is) throws Exception {
		is.skip(n);	  
	}
	
	public void stopTape(){
		System.out.println("Stopping the tape");
	    //playin(false);
	    tapeValue = false;
	    try{
	      //continuar = false;
	      //contador = 0;
	      pausa = 1000;
	      //monitor.aviso();
	      //play.stop();
	    }catch(Exception ex){
	      ex.printStackTrace(System.out);
	    }
	  }
	
	//Rewind the tape to the first block
	  public void rewindTape(InputStream is){
		System.out.println("Rewind Tape");
	    try{
	      
	      is.close();
	      //is = new FileInputStream(F);
	      block = 0;
	      code_block = 0;
	    }catch(Exception ex){
	      ex.printStackTrace();
	    }
	  }
	  
	//how many bytes are available in the tape (to be readed)
	  public int available(InputStream is){
	    try{
	      int aux = is.available();
	      
	      return aux;
	    }catch(Exception ex){
	      ex.printStackTrace();
	      return 0;
	    }
	  }
	  
	//Salta un bloque en una cinta TZX
	  public void next_block_TZX(InputStream is){
	    try{
	      code = readTape(is);
	      lon = 0;
	      block++;
	      code_block = code;
	      switch (code){
	        case 0x10:{
	          skip(2, is);
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          break;
	        }
	        case 0x11:{
	          skip(15, is);
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          lon |= (readTape(is) << 16);
	          break;
	        }
	        case 0x12:
	        case 0x2A:{
	          lon = 4;
	          break;
	        }
	        case 0x13:{
	          lon = readTape(is) * 2;
	          break;
	        }
	        case 0x14:{
	          skip(7, is);
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          lon |= (readTape(is) << 16);
	          break;
	        }
	        case 0x15:{
	          skip(5, is);
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          lon |= (readTape(is) << 16);
	          break;
	        }
	        case 0x16:
	        case 0x17:{
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          lon |= (readTape(is) << 16);
	          lon |= (readTape(is) << 24);
	          lon -= 4;
	          break;
	        }
	        case 0x20:
	        case 0x23:
	        case 0x24:{
	          lon = 2;
	          break;
	        }
	        case 0x21:
	        case 0x30:{
	          lon = readTape(is);
	          break;
	        }
	        case 0x22:
	        case 0x25:{
	          break;
	        }
	        case 0x26:{
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          lon = lon * 2;
	          break;
	        }
	        case 0x28:
	        case 0x32:{
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          break;
	        }
	        case 0x31:{
	          skip(1, is);
	          lon = readTape(is);
	          break;
	        }
	        case 0x33:{
	          lon = readTape(is) * 3;
	          break;
	        }
	        case 0x34:{
	          lon = 8;
	          break;
	        }
	        case 0x35:{
	          skip(10, is);
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          lon |= (readTape(is) << 16);
	          lon |= (readTape(is) << 24);
	          break;
	        }
	        case 0x40:{       /*SNAPSHOT*/
	          skip(1, is);
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          lon |= (readTape(is) << 16);
	          break;
	        }
	        case 0x5A:{
	          lon = 9;
	          break;
	        }
	        default:{
	          lon = readTape(is);
	          lon |= (readTape(is) << 8);
	          lon |= (readTape(is) << 16);
	          lon |= (readTape(is) << 24);
	          break;
	        }
	      }
	      skip(lon, is);
	    }catch(Exception ex){
	      ex.printStackTrace();
	    }
	  }
	
	  public void play_TZX(InputStream is){
		  
		    try{
		    	System.out.println("Play TZX "+is.available());
		    	
		      tapein = (pausa * 69888) / 20;

		      if(tapein > 0){
		        tapeValue = false;
		        cont(tapein);
		        pausa = 0;
		      }
		      
		      lon = 0;
		      pas = 0;
		      leader = 0;
		      pulse_leader = 0;
		      pulse_sync_1 = 0;
		      pulse_sync_2 = 0;
		      pulse_data_0 = 0;
		      pulse_data_1 = 0;
		      bits_ult = 0;

		      code = readTape(is);
		      block++;
		      code_block = code;

		      //System.out.println(code + " FIN PAUSA");

		      switch(code){
		        case 0x10:{
		        	System.out.println(code + " 0x10");
		          pas = readTape(is);
		          pas = pas | (readTape(is) << 8);
		          lon = readTape(is);
		          lon = lon | (readTape(is) << 8);
		          int aux = readTape(is);
		          if((lon == 0x13) && (aux == 0)){
		            leader = 8064;
		          }else{
		            leader = 3220;
		          }
		          skip(-1, is);
		          pulse_leader = 2168;
		          pulse_sync_1 = 667;
		          pulse_sync_2 = 735;
		          pulse_data_0 = 855;
		          pulse_data_1 = 1710;
		          bits_ult = 8;
		          break;
		        }
		        case 0x11:{
		        	System.out.println(code + " 0x11");
		          pulse_leader = readTape(is);
		          pulse_leader |= (readTape(is) << 8);
		          pulse_sync_1 = readTape(is);
		          pulse_sync_1 |= (readTape(is) << 8);
		          pulse_sync_2 = readTape(is);
		          pulse_sync_2 |= (readTape(is) << 8);
		          pulse_data_0 = readTape(is);
		          pulse_data_0 |= (readTape(is) << 8);
		          pulse_data_1 = readTape(is);
		          pulse_data_1 |= (readTape(is) << 8);
		          leader = readTape(is);
		          leader |= (readTape(is) << 8);
		          bits_ult = readTape(is);
		          pas = readTape(is);
		          pas |= (readTape(is) << 8);
		          lon = readTape(is);
		          lon |= (readTape(is) << 8);
		          lon |= (readTape(is) << 16);
		          break;
		        }
		        case 0x12:{
		        	System.out.println(code + " 0x12");
		          pulse_leader = readTape(is);
		          pulse_leader |= (readTape(is) << 8);
		          leader = readTape(is);
		          leader |= (readTape(is) << 8);
		          //valor = true;
		          for(int i = 0; i < leader; i++){
		            tapeValue = !tapeValue;
		            cont(pulse_leader);
		          }
		          return;
		        }
		        case 0x13:{
		        	System.out.println(code + " 0x13");
		          leader = readTape(is);
		          tapeValue = true;
		          for(int i = 0; i < leader; i++){
		            pulse_leader = readTape(is);
		            pulse_leader |= (readTape(is) << 8);
		            tapeValue = !tapeValue;
		            cont(pulse_leader);
		          }
		          return;
		        }
		        case 0x14:{
		        	System.out.println(code + " 0x14");
		          pulse_data_0 = readTape(is);
		          pulse_data_0 |= (readTape(is) << 8);
		          pulse_data_1 = readTape(is);
		          pulse_data_1 |= (readTape(is) << 8);
		          bits_ult = readTape(is);
		          pas = readTape(is);
		          pas |= (readTape(is) << 8);
		          lon = readTape(is);
		          lon |= (readTape(is) << 8);
		          lon |= (readTape(is) << 16);
		          break;
		        }
		        case 0x15:{
		        	System.out.println(code + " 0x15");
		          skip(5, is);
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          num |= (readTape(is) << 16);
		          skip(num, is);
		          return;
		        }
		        case 0x16:
		        	
		        case 0x17:{
		        	System.out.println(code + " 0x17");
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          num |= (readTape(is) << 16);
		          num |= (readTape(is) << 24);
		          skip(num - 4, is);
		          return;
		        }
		        case 0x20:{
		        	System.out.println(code + " 0x20");
		          pas = readTape(is);
		          pas |= (readTape(is) << 8);
		          pausa = pas;
		          if(pas == 0){
		            stopTape();
		          }
		          return;
		        }
		        case 0x21:{
		        	System.out.println(code + " 0x21");
		          int num = readTape(is);
		          skip(num, is);
		          num = readTape(is);
		          while(num != 0x22){
		            skip(-1, is);
		            play_TZX(is);
		            num = readTape(is);
		          }
		          return;
		        }
		        case 0x22:{
		        	System.out.println(code + " 0x22");
		          return;
		        }
		        case 0x23:{       /*SALTAR*/
		        	System.out.println(code + " 0x23");
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          if(num >= 0){
		            for(int i = 0; i < num; i++){
		              next_block_TZX(is);
		            }
		          }
		          return;
		        }
		        case 0x24:{
		        	System.out.println(code + " 0x24");
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          if(num < 1){
		            return;
		          }
		          int marca = available(is);
		          int aux2 = block;
			        for(int i = 0; i < num; i++){
		            rewindTape(is);
		            block = aux2;
		            skip(available(is) - marca, is);
				    int aux = readTape(is);
		            while(aux != 0x25){
				          skip(-1, is);
		              play_TZX(is);
		              aux = readTape(is);
		            }
		          }
		          return;
		        }
		        case 0x25:
		        case 0x27:{
		        	System.out.println(code + " 0x27");
		          return;
		        }
		        case 0x26:{
		        	System.out.println(code + " 0x26");
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          skip(num * 2, is);
		          return;
		        }
		        case 0x28:{
		        	System.out.println(code + " 0x28");
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          skip(num, is);
		          return;
		        }
		        case 0x2A:{
		        	System.out.println(code + " 0x2A");
		          skip(4, is);
		          stopTape();
		          return;
		        }
		        case 0x30:{
		        	System.out.println(code + " 0x30");
		          int num = readTape(is);
		          skip(num, is);
		          return;
		        }
		        case 0x31:{
		        	System.out.println(code + " 0x31");
		          skip(1, is);
		          int num = readTape(is);
		          skip(num, is);
		          return;
		        }
		        case 0x32:{
		        	System.out.println(code + " 0x32");
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          skip(num, is);
		          return;
		        }
		        case 0x33:{
		        	System.out.println(code + " 0x33");
		          int num = readTape(is);
		          skip(num * 3, is);
		          return;
		        }
		        case 0x34:{
		        	System.out.println(code + " 0x34");
		          skip(8, is);
		          return;
		        }
		        case 0x35:{
		        	System.out.println(code + " 0x35");
		          skip(10, is);
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          num |= (readTape(is) << 16);
		          num |= (readTape(is) << 24);
		          skip(num, is);
		          return;
		        }
		        case 0x40:{
		        	System.out.println(code + " 0x40");
		          skip(1, is);
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          num |= (readTape(is) << 16);
		          skip(num, is);
		          return;
		        }
		        case 0x5A:{
		        	System.out.println(code + " 0x5A "+is.available());
		          skip(9, is);
		          System.out.println(code + " 0x5A FIN "+is.available());
		          return;
		        }
		        default:{
		        	System.out.println(code + " default");
		          int num = readTape(is);
		          num |= (readTape(is) << 8);
		          num |= (readTape(is) << 16);
		          num |= (readTape(is) << 24);
		          skip(num, is);
		          stopTape();
		          return;
		        }
		      }
		      System.out.println("Paso1");
		      if(leader != 0){
		        tapeValue = true;
		        for(int i = 0; i < leader; i++){
		          tapeValue = !tapeValue;
		          cont(pulse_leader);
		        }
		        if(tapeValue == false){
		          tapeValue = true;
		          cont(pulse_leader);
		        }
		      }
		      System.out.println("Paso2");
		      if(pulse_sync_1 != 0){
		        tapeValue = false;
		        cont(pulse_sync_1);
		        tapeValue = true;
		        cont(pulse_sync_2);
		      }
		      System.out.println("Paso3");
		      int aux = 0;
		      int temp = 0;
		      for(int i = 0; i < (lon - 1); i++){
		    	  System.out.println("Paso4");
		        aux = readTape(is);
		        for(int j = 0; j < 8; j++){
		        	System.out.println("Paso5");
		          temp = (((aux & 0x80) != 0) ? pulse_data_1 : pulse_data_0);
		          tapeValue = false;
		          cont(temp);
		          tapeValue = true;
		          cont(temp);
		          aux <<= 1;
		        }
		      }
		      System.out.println("Paso6");
		      aux = readTape(is);
		      for(int j = 0; j < bits_ult; j++){
		        temp = (((aux & 0x80) != 0) ? pulse_data_1 : pulse_data_0);
		        tapeValue = false;
		        cont(temp);
		        tapeValue = true;
		        cont(temp);
		        aux <<= 1;
		      }
		      pausa = pas;
		      System.out.println("Paso7");
		    }catch(Exception ex){
		      ex.printStackTrace(System.out);
		    }
		  }


	
	// file formats supported
	private FileFormat[] supportedFormats = null;

        public int[] get_rambank(int i) {
          return whole_ram[i];
        }

        private void write_ram(int addr, int v) {
          ram48k[ (addr & 0xc000) >> 0xe][addr & 0x3fff] = v;
        }

        /**
         * read from video ram
         * @param addr int
         * @return int
         */
        private int read_vram(int addr) {
          return vram[addr];
        }
        
        private int read_ram(int addr) {
          return ram48k[ (addr & 0xc000) >> 0xe][addr & 0x3fff];
        }

        public void setKeymatrix(boolean _keymatrix) {
          keymatrix = _keymatrix;
        }

	public Spectrum48k(int mode)
	{
		super("Spectrum");
                this.mode = mode;
                keymatrix = true;
                rom = mode == SpectrumModels.MODE_48K ? rom48k : rom128k;
                for (int i = 0; i < 8; i++)
                  keyboard[i] = 0xFF;
                whole_ram[0] = ram0;
                whole_ram[1] = ram1;
                whole_ram[2] = ram2;
                whole_ram[3] = ram3;
                whole_ram[4] = ram4;
                whole_ram[5] = ram5;
                whole_ram[6] = ram6;
                whole_ram[7] = ram7;
                ram48k[0] = whole_ram[5];
                ram48k[1] = whole_ram[2];
                ram48k[2] = whole_ram[0];
                vram = whole_ram[5];
                
                display=new SpectrumDisplay();
		for(int i=6144;i<6912;i++) write_ram(i, 070); // white

		try {
			//audio = Audio.getAudio();
			audioChip = new AY_3_8912(3500000);
			//audio.open(3500000);
			audioChip.open();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public void run()
	{
		try {
			frames();
			audioChip.close();
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}		
	}

	private void end_frame() {
		
		try {
			//au_update();
			audioChip.updateSoundCard(cpu.time);
			audioChip.au_time -= 69888;
			//audioChip.setAudioTime( audioChip.getAudioTime() - 69888);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		refresh_screen();
		if(display.border != display.border_solid) {
			int t = refrb_t;
			refresh_border();
			if(t == display.BORDER_START)
				display.border_solid = display.border;
		}

		display.update_screen();

		cpu.time -= 69888;
		if(--flash_count <= 0) {
			flash ^= 0xFF;
			flash_count = 16;
		}
		//audio.level -= audio.level>>8;
		audioChip.setLevel( audioChip.getLevel() - (audioChip.getLevel() >> 8) );
	}

	private long time;
	private int timet;

	private void frames() throws InterruptedException
	{
		time = System.currentTimeMillis();
		cpu.time = -14335;
		cpu.time_limit = 55553;
		//au_time = cpu.time;
		audioChip.setAudioTime(cpu.time);
		
		tapeValue = false;
		
		for(;;) {
			byte[] tap = null;
			boolean tend = false;
			synchronized(this) {
				if(display.want_scale != display.scale) {
					display.scale = display.want_scale;
					if (display.scale == 3) { // Full Screen
						Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
						
						display.width=d.width; 
						display.height=d.height;
					
					} else { // scale 1 or 2
						display.width=display.scale*display.W; 
						display.height=display.scale*display.H;
					}
					
					notifyAll();
					display.abort_consumers();
				}
				if(want_pause != paused) {
					paused = want_pause;
					notifyAll();
				}
				if(stop_loading) {
					System.out.println("stop_loading");
					loading = stop_loading = false;
					notifyAll();
				}
				if(!paused) {
					tap = tape;
					tend = tape_ready;
					if(!loading && tap!=null){
						loading = check_load();						
					}
						
				}
			}

			update_keyboard();
			refresh_new();
			//System.out.println("Loading..."+loading);
			if(paused) {
				cpu.time = cpu.time_limit;
			} else if(loading) {
				loading = do_load(tap, tend);
				cpu.time = cpu.time_limit;
			} else {
				cpu.interrupt(0xFF);
				cpu.execute();
			}
			end_frame();

			/* sync */

			timet += 121;
			if(timet >= 125) {timet -= 125; time++;}
			time += 19;

			long t = System.currentTimeMillis();
			if(t < time) {
				t = time-t;
				sleep(t);
			} else {
				yield();
				if(interrupted())
					break;
				t -= 100;
				if(t > time)
					time = t;
			}
		}
	}

	boolean paused = true;
	boolean want_pause = true;

	public synchronized void pause(boolean y) throws InterruptedException
	{
		want_pause = y;
		while(paused != want_pause)
			wait();
	}

	public synchronized void reset()
	{
		stop_loading();
		cpu.reset();
		//au_reset();
		try {
			audioChip.reset();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
        
		rom = mode == SpectrumModels.MODE_48K ? rom48k : rom128k;
        lock48k = false;
        vram = whole_ram[5];
	}

	/* Z80.Env */

	public final int m1(int addr, int ir) {
		int n = cpu.time - ctime;
		if(n>0) cont(n);

		addr -= 0x4000;
		if((addr&0xC000) == 0)
			cont1(0);
		ctime = NOCONT;
		if((ir&0xC000) == 0x4000)
			ctime = cpu.time + 4;
		if(addr >= 0)
			return read_ram(addr);
		n = rom[addr+=0x4000];
		if(if1rom!=null && (addr&0xE8F7)==0) {
			if(addr==0x0008 || addr==0x1708) {
				if(rom==rom48k) rom = if1rom;
			} else if(addr==0x0700) {
				if(rom==if1rom) rom = rom48k;
			}
		}
		return n;
	}

	public final int mem(int addr) {
		int n = cpu.time - ctime;
		if(n>0) cont(n);
		ctime = NOCONT;

		addr -= 0x4000;
		if(addr>=0) {
			if(addr<0x4000) {
				cont1(0);
				ctime = cpu.time + 3;
			}
			return read_ram(addr);
		}
		return rom[addr+0x4000];
	}

	public final int mem16(int addr) {
		int n = cpu.time - ctime;
		if(n>0) cont(n);
		ctime = NOCONT;

		int addr1 = addr-0x3FFF;
		if((addr1&0x3FFF)!=0) {
			if(addr1<0)
				return rom[addr] | rom[addr1+0x4000]<<8;
			if(addr1<0x4000) {
				cont1(0); cont1(3);
				ctime = cpu.time + 6;
			}
			return read_ram(addr-0x4000) | read_ram(addr1)<<8;
		}
		switch(addr1>>>14) {
		case 0:
			cont1(3);
			ctime = cpu.time + 6;
			return rom[addr] | read_ram(0)<<8;
		case 1:
			cont1(0);
		case 2:
			return read_ram(addr-0x4000) | read_ram(addr1)<<8;
		default:
			return read_ram(0xBFFF) | rom[0]<<8;
		}
	}

	public final void mem(int addr, int v) {
		int n = cpu.time - ctime;
		if(n>0) cont(n);
		ctime = NOCONT;

		addr -= 0x4000;
		if(addr < 0x4000) {
			if(addr < 0) return;
			cont1(0);
			ctime = cpu.time + 3;
			if(addr<6912 && read_ram(addr)!=v)
				refresh_screen();
		}
                write_ram(addr, v);
	}

	public final void mem16(int addr, int v) {

		int addr1 = addr-0x3FFF;
		if((addr1&0x3FFF)!=0) {
			int n = cpu.time - ctime;
			if(n>0) cont(n);
			ctime = NOCONT;

			if(addr1<0) return;
			if(addr1>=0x4000) {
				write_ram(addr1-1, v&0xFF);
				write_ram(addr1, v>>>8);
				return;
			}
		}
		mem(addr, v&0xFF);
		cpu.time += 3;
		mem(addr+1, v>>>8);
		cpu.time -= 3;
	}

	private byte ula28;

        /**
         * 128k port
         * @param v int
         */
        private void out7ffd(int v) {
          if (mode == SpectrumModels.MODE_48K || lock48k)
            return;
          /* D0..D2 ram bank */
          int ram_bank = v & 0x7;
          ram48k[2] = whole_ram[ram_bank];
          /* D3 videoram */
          if ( (v & 0x08) == 0x08) {
            /* ram7 */
            vram = whole_ram[7];
          }
          else {
            /* standart (ram5) */
            vram = whole_ram[5];
          }
          /* D4 rom */
          if ( (v & 0x10) == 0x10) {
            /* rom 48 */
            rom = rom48k;
          }
          else {
            /* rom 128 */
            rom = rom128k;
          }
          /* D5 lock this port */
          if ( (v & 0x20) == 0x20) {
            lock48k = true;
          }
        }

	public void out(int port, int v)
	{
		//System.out.println("OUT port="+port+" value="+v);
		cont_port(port);
		
		if ((zxPrinter != null)&&((port&0xFB)==0xFB)) { // ZX Printer
			zxPrinter.out(port, v);
		}

		if((port&0x0001)==0) {
			ula28 = (byte)v;
			int n = v&7;
			if(n != display.border) {
				refresh_border();
				display.border = (byte)n;
			}
			
			audioChip.out(port, v, cpu.time);
			
			ear = ((v & 0x10) != 0 ? 0xFF : 0xBF);
		}
		//if((port&0x8002)==0x8000 && ay_enabled) {
		if((port&0x8002)==0x8000 && audioChip.isEnabled()) {
			
			audioChip.out(port, v, cpu.time);
		}
		
		/* 128k port */
        if (port == 0x7ffd) {
          out7ffd(v);
        } 
	}

	public int in(int port)
	{
		//System.out.println("IN port="+port +" "+((port&0xFB)==0xFB));
		cont_port(port);
		int v = 0xFF;
		
		/* kempston */
		if((port&0x00E0)==0)
			return kempston;
        
		/* AY */
		if((port&0xC002)==0xC000 && audioChip.isEnabled()) {
			/*if(audioChip.ay_idx>=14 && (audioChip.ay_reg[7]>>audioChip.ay_idx-8 & 1) == 0)
				return 0xFF;
			return audioChip.ay_reg[audioChip.ay_idx];*/
			return audioChip.in(port);
		}
		
		/* keyboard port FE */
		if((port&0x0001)==0) {
			for(int i=0; i<8; i++)
				if((port&0x100<<i) == 0)
					v &= keyboard[i];
			v &= ula28<<2 | 0xBF;
			
			// tape
			ear = (tapeValue ? 0xFF : 0xBF);

		    return (v | (ear & 0x40));
			//return v;
		} else if(cpu.time>=0) {
			int t = cpu.time;
			int y = t/224;
			t %= 224;
			if(y<192 && t<124 && (t&4)==0) {
				int x = t>>1 & 1 | t>>2;
				if((t&1)==0)
					x += y & 0x1800 | y<<2 & 0xE0 | y<<8 & 0x700;
				else
					x += 6144 | y<<2 & 0x3E0;
				v = read_ram(x);
				return v;
			}
		}
		
		// ZX Printer
		if ( ((port&0xFB)==0xFB) ) {	
			if (zxPrinter != null){
				return zxPrinter.in(port);
			}
		}
		
		return v;
	}

	/* contention */
	// according to scratchpad.wikia.com/wiki/Contended_memory

	static final int NOCONT = 99999;
	int ctime;

	private final void cont1(int t) {
		t += cpu.time;
		if(t<0 || t>=191*224+126) return;
		if((t&7) >= 6) return;
		if(t%224 < 126)
			cpu.time += 6 - (t&7);
	}

	private final void cont(int n) {
		int s, k;
		int t = ctime;
		if(t+n <= 0) return;
		s = 191*224+126 - t;
		if(s < 0) return;
		s %= 224;
		if(s > 126) {
			n -= s-126;
			if(n <= 0) return;
			t = 6; k = 15;
		} else {
			k = s>>>3;
			s &= 7;
			if(s == 7) {
				s--;
				if(--n == 0) return;
			}
			t = s;
		}
		n = n-1 >> 1;
		if(k<n) n = k;
		cpu.time += t + 6*n;
	}

	private void cont_port(int port)
	{
		int n = cpu.time - ctime;
		if(n>0) cont(n);

		if((port&0xC000) != 0x4000) {
			if((port&0x0001)==0)
				cont1(1);
			ctime = NOCONT;
		} else {
			ctime = cpu.time;
			cont(2 + ((port&1)<<1));
			ctime = cpu.time+4;
		}
	}

	

	

	public synchronized void scale(int m)
	{
		display.want_scale = m;
		display.scale=m;
		System.out.println("SCALE="+display.scale);
		try {
			while(display.scale != m) wait();
		} catch(InterruptedException e) {
			currentThread().interrupt();
		}
	}

	public int scale() {
		//System.out.println("SCALE="+display.scale);
		return display.scale;
	}


	int flash_count = 16;
	int flash = 0x8000;

	/* screen refresh */

	private int refresh_t, refresh_a, refresh_b, refresh_s;

	private final void refresh_new() {
		refresh_t = refresh_b = 0;
		refresh_s = display.Mv*display.W + display.Mh;
		refresh_a = 0x1800;

		refrb_p = 0;
		refrb_t = display.BORDER_START;
		refrb_x = -display.Mh;
		refrb_y = -8*display.Mv;
		refrb_r = 1;
	}

	private final void refresh_screen() {
		int ft = cpu.time;
		if(ft < refresh_t)
			return;
		final int flash = this.flash;
		int a = refresh_a, b = refresh_b;
		int t = refresh_t, s = refresh_s;
		do {
			int sch = 0;

			int v = read_vram(a)<<8 | read_vram(b++);
			if(v>=0x8000) v ^= flash;
			v = canonic[v];
			if(v!=display.screen[s]) {
				display.screen[s] = v;
				sch = 1;
			}

			v = read_vram(a+1)<<8 | read_vram(b++);
			if(v>=0x8000) v ^= flash;
			v = canonic[v];
			if(v!=display.screen[++s]) {
				display.screen[s] = v;
				sch += 2;
			}

			if(sch!=0)
				display.scrchg[a-0x1800>>5] |= sch<<(a&31);

			a+=2; t+=8; s++;
			if((a&31)!=0) continue;
			// next line
			t+=96; s+=2*display.Mh;
			a-=32; b+=256-32;
			if((b&0x700)!=0) continue;
			// next row
			a+=32; b+=32-0x800;
			if((b&0xE0)!=0) continue;
			// next part
			b+=0x800-256;
			if(b>=6144) {
				t = 99999; // just a big value
				break;
			}
		} while(ft >= t);
		refresh_a = a; refresh_b = b;
		refresh_t = t; refresh_s = s;
	}

	/* border refresh */

	

	private int refrb_p, refrb_t;
	private int refrb_x, refrb_y, refrb_r;

	

	public void refresh_border()
	{
		int ft = cpu.time;
		if(ft < refrb_t)
			return;
		display.border_solid = -1;

		int t = refrb_t;
		int b = canonic[display.border<<11];
		int p = refrb_p;
		int x = refrb_x;
		int r = refrb_r;
loop:
		do {
			if(refrb_y<0 || refrb_y>=192) {
				do {
					if(display.screen[p] != b) {
						display.screen[p] = b;
						display.brdchg_ud |= r;
					}
					p++; t+=4;
					if(++x < 32+display.Mh)
						continue;
					// next line
					x = -display.Mh;
					t += 224 - 4*(display.Mh+32+display.Mh);
					if((++refrb_y & 7) != 0)
						continue;
					// next row
					if(refrb_y == 0) {
						r = 1;
						// go to screen part
						continue loop;
					} else if(refrb_y == 192+8*display.Mv) {
						// finish
						t = 99999;
						break loop;
					}
					r <<= 1;
				} while(ft >= t);
				break;
			}
			for(;;) {
				if(x<0) {
					// left margin
					for(;;) {
						if(display.screen[p] != b) {
							display.screen[p] = b;
							display.brdchg_l |= r;
						}
						p++; t+=4;
						if(++x == 0)
							break;
						if(ft < t)
							break loop;
					}
					// skip screen
					x = 32; p += 32;
					t += 4*32;
					if(ft < t) break loop;
				}
				// right margin
				for(;;) {
					if(display.screen[p] != b) {
						display.screen[p] = b;
						display.brdchg_r |= r;
					}
					p++; t+=4;
					if(++x == 32+display.Mh)
						break;
					if(ft < t) break loop;
				}
				// next line
				x = -display.Mh;
				t += 224 - 4*(display.Mh+32+display.Mh);
				if((++refrb_y & 7) == 0) {
					if(refrb_y == 192) {
						r = 1<<display.Mv;
						// go to bottom border
						continue loop;
					}
					r <<= 1;
				}
				if(ft < t) break loop;
			}
		} while(ft >= t);

		refrb_r = r; refrb_x = x;
		refrb_p = p; refrb_t = t;
	}

	

	

	static final int canonic[] = new int[32768];
	static {
		// .bpppiii 76543210 -> bppp biii 01234567
		for(int a=0; a<0x8000; a+=0x100) {
			int b = a>>3 & 0x0800;
			int p = a>>3 & 0x0700;
			int i = a & 0x0700;
			if(p!=0) p |= b;
			if(i!=0) i |= b;
			canonic[a] = p<<4 | 0xFF;
			canonic[a|0xFF] = i<<4 | 0xFF;
			for(int m=1; m<255; m+=2) {
				if(i!=p) {
					int xm = m>>>4 | m<<4;
					xm = xm>>>2&0x33 | xm<<2&0xCC;
					xm = xm>>>1&0x55 | xm<<1&0xAA;
					canonic[a|m] = i<<4 | p | xm;
					canonic[a|m^0xFF] =  p<<4 | i | xm;
				} else
					canonic[a|m] = canonic[a|m^0xFF]
						= p<<4 | 0xFF;
			}
		}
	}

	
	/* keyboard & joystick */

	public final int keyboard[] = new int[8];
	public int kempston = 0;
	public final KeyEvent keys[] = new KeyEvent[8];
	static final int arrowsDefault[] = {0143, 0124, 0134, 0144};
	int arrows[] = arrowsDefault;

	void update_keyboard() {
		for(int i=0; i<8; i++) keyboard[i] = 0xFF;
		kempston = 0;

		int m[] = new int[] {-1,-1,-1,-1,-1};
		int s = 0;
		synchronized(keys) {
			for(int i=0; i<keys.length; i++) if(keys[i]!=null) {
				int k = key(keys[i]);
				if(k<0) continue;
				// .......xxx row
				// ....xxx... column
				// ...x...... caps shift
				// ..x....... symbol shift
				// .x........ caps shift alone
				// x......... symbol shift alone
				s |= k;
				if(k<01000)
					pressed(k,m);
			}
		}
		if((s&0300)==0) s |= s>>>3 & 0300;
		if((s&0100)!=0) pressed(000,m);
		if((s&0200)!=0) pressed(017,m);
	}

	private final void pressed(int k, int m[])
	{
		int a = k&7, b = k>>>3 & 7;
		int v = keyboard[a] & ~(1<<b);
		keyboard[a] = v;
                if (keymatrix) {
                  int n = m[b];
                  m[b] = a;
                  if (n >= 0)
                    v |= keyboard[n];
                  for (n = 0; n < 8; n++)
                    if ( (keyboard[n] | v) != 0xFF)
                      keyboard[n] = v;
                }
	}

	private int key(KeyEvent e)
	{
		int c = e.getKeyCode();
		int a = e.getKeyChar();
		int i = "[AQ10P\n ZSW29OL]XDE38IKMCFR47UJNVGT56YHB".indexOf((char)c);
		if(i>=0) simple: {
			int s = 0;
			if(c>=KeyEvent.VK_0 && c<=KeyEvent.VK_9) {
				if(c!=(int)a) break simple;
				if(e.isAltDown()) s = 0100;
			}
			return i | s;
		}
		if(a != '\0') {
			i = "\t\0\0!_\"\0\0:\0\0@);=\0\0\0\0#(\0+.?\0<$'\0-,/\0>%&\0^*".indexOf(a);
			if(i>=0)
				return i | 0200;
		}
		switch(c) {
			case KeyEvent.VK_INSERT:
			case KeyEvent.VK_ESCAPE: return 0103;
			case KeyEvent.VK_KP_LEFT:
			case KeyEvent.VK_LEFT: i=0; break;
			case KeyEvent.VK_KP_DOWN:
			case KeyEvent.VK_DOWN: i=3; break;
			case KeyEvent.VK_KP_UP:
			case KeyEvent.VK_UP: i=2; break;
			case KeyEvent.VK_KP_RIGHT:
			case KeyEvent.VK_RIGHT: i=1; break;
			case KeyEvent.VK_BACK_SPACE: return 0104;
			case KeyEvent.VK_SHIFT: return 01000;
			case KeyEvent.VK_CONTROL: kempston |= 0x10; /* fall */
			case KeyEvent.VK_ALT: return 02000;
			default: return -1;
		}
		kempston |= 1<<(i^1);
                return e.isAltDown() ? arrowsDefault[i] :
                    (arrows != null ? arrows[i] : -1);
	}

	public void setArrows(String s) {
                if (s.substring(0, 2).equals("NO")) {
                        arrows = null;
                        return;
                }
		arrows = new int[4];
		for(int i=0; i<4; i++) {
			int c = -1;
			if(i<s.length())
				c = "Caq10pE_zsw29olSxde38ikmcfr47ujnvgt56yhb"
					.indexOf(s.charAt(i));
			if(c<0) c = arrowsDefault[i];
			arrows[i] = c;
		}
	}

	/* tape */
	private boolean check_load()
	{
		//System.out.println("check_load/tape_blk="+tape_blk+" length="+tape.length);
		int pc = cpu.pc();
		//System.out.println("check_load/tape_blk="+tape_blk+" length="+tape.length);
		//System.out.println("A="+cpu.a()+" IX="+cpu.ix()+" DE="+cpu.de()+" PC="+cpu.pc());
		//if ((cpu.ix() == 16384)&&(cpu.de()==6912)) {
		//if ((cpu.a() == 95)&&(pc>=65518)) {			
		/*if ((cpu.ix() == 16384)) {
			System.out.println("ÑAPAAAAAAA!!!!!");
			pc=1528;
		}*/
		if(cpu.ei() || pc<0x56B || pc>0x604){
		//if(cpu.ei() || pc<0x56B ){
			//System.out.println("Ret 1 "+pc+" a="+cpu.a());
			/*System.out.println("cpu.ei() "+cpu.ei());
			System.out.println("pc menor 0x56B "+(pc<0x56B));
			System.out.println("pc mayor 0x604 "+(pc>0x604));*/
			return false;
		}
			
		int sp = cpu.sp();
		if(pc>=0x5E3) {
			pc = mem16(sp); sp=(char)(sp+2);
			if(pc == 0x5E6) {
				pc = mem16(sp); sp=(char)(sp+2);
			}
		}
		if(pc<0x56B || pc>0x58E){
		//if(pc<0x56B){
			//System.out.println("Ret 2; PC="+pc);
			return false;
		}
			
		cpu.sp(sp);
		cpu.ex_af();

		if(tape_changed || tape_ready && tape.length <= tape_blk) {
			tape_changed = false;
			tape_blk = 0;
		}
		tape_pos = tape_blk;
		return true;
	}

	private boolean loading, stop_loading;
	private byte[] tape;
	private int tape_blk;
	private int tape_pos;
	private boolean tape_changed = false;
	private boolean tape_ready = false;

	public synchronized void stop_loading()
	{
		stop_loading = true;
		try {
			while(loading) wait();
		} catch(InterruptedException e) {
			currentThread().interrupt();
		}
	}

	public synchronized void tape(byte[] tape, boolean end)
	{
		if(tape==null)
			tape_changed = true;
		tape_ready = end;
		this.tape = tape;
	}

	private final boolean do_load(byte[] tape, boolean ready)
	{
		System.out.println("do_load/block="+tape_blk);
		if(tape_changed || (keyboard[7]&1)==0) {
			cpu.f(0);
			System.out.println("RETORNO1: "+false);
			return false;
		}

		int p = tape_pos;

		int ix = cpu.ix();
		int de = cpu.de();
		int h, l = cpu.hl(); h = l>>8 & 0xFF; l &= 0xFF;
		int a = cpu.a();
		int f = cpu.f();
		int rf = -1;

		if(p == tape_blk) {
			p += 2;
			if(tape.length < p) {
				if(ready) {
					cpu.pc(cpu.pop());
					cpu.f(cpu.FZ);
				}
				System.out.println("RETORNO2: "+(rf<0));
				return !ready;
			}
			tape_blk = p + (tape[p-2]&0xFF | tape[p-1]<<8&0xFF00);
			h = 0;
		}

		for(;;) {
			if(p == tape_blk) {
				rf = cpu.FZ;
				break;
			}
			if(p == tape.length) {
				if(ready)
					rf = cpu.FZ;
				break;
			}
			l = tape[p++]&0xFF;
			h ^= l;
			if(de == 0) {
				a = h;
				rf = 0;
				if(a<1)
					rf = cpu.FC;
				break;
			}
			if((f&cpu.FZ)==0) {
				a ^= l;
				if(a != 0) {
					rf = 0;
					break;
				}
				f |= cpu.FZ;
				continue;
			}
			if((f&cpu.FC)!=0)
				mem(ix, l);
			else {
				a = mem(ix) ^ l;
				if(a != 0) {
					rf = 0;
					break;
				}
			}
			ix = (char)(ix+1);
			de--;
		}

		cpu.ix(ix);
		cpu.de(de);
		cpu.hl(h<<8|l);
		cpu.a(a);
		if(rf>=0) {
			f = rf;
			cpu.pc(cpu.pop());
		}
		cpu.f(f);
		tape_pos = p;
		System.out.println("RETORNO3: "+(rf<0));
		return rf<0;
	}

	/* LOAD "" */

	public final void autoload()
	{
		rom = rom48k;
                
		cpu.i(0x3F);
		int p=16384;
		do mem(p++, 0); while(p<22528);
		do mem(p++, 070); while(p<23296);
		do mem(p++, 0); while(p<65536);
		mem16(23732, --p); // P-RAMT
		p -= 0xA7;
                System.arraycopy(rom48k, 0x3E08, ram48k[2], p-49152, 0xA8);
//		System.arraycopy(rom48k, 0x3E08, ram, p-16384, 0xA8);
		mem16(23675, p--); // UDG
		mem(23608, 0x40); // RASP
		mem16(23730, p); // RAMTOP
		mem16(23606, 0x3C00); // CHARS
		mem(p--, 0x3E);
		cpu.sp(p);
		mem16(23613, p-2); // ERR-SP
		cpu.iy(0x5C3A);
		cpu.im(1);
		cpu.ei(true);

		mem16(23631, 0x5CB6); // CHANS
                System.arraycopy(rom48k, 0x15AF, ram48k[0], 0x1CB6, 0x15);
//		System.arraycopy(rom48k, 0x15AF, ram, 0x1CB6, 0x15);
		p = 0x5CB6+0x14;
		mem16(23639, p++); // DATAADD
		mem16(23635, p); // PROG
		mem16(23627, p); // VARS
		mem(p++, 0x80);
		mem16(23641, p); // E-LINE
		mem16(p, 0x22EF); mem16(p+2, 0x0D22); // LOAD ""
		mem(p+4, 0x80); p += 5;
		mem16(23649, p); // WORKSP
		mem16(23651, p); // STKBOT
		mem16(23653, p); // STKEND

		mem(23693, 070); mem(23695, 070); mem(23624, 070);
		mem16(23561, 0x0523);

		mem(23552, 0xFF); mem(23556, 0xFF); // KSTATE

                System.arraycopy(rom48k, 0x15C6, ram48k[0], 0x1C10, 14);
//		System.arraycopy(rom48k, 0x15C6, ram, 0x1C10, 14);

		mem16(23688, 0x1821); // S-POSN
		mem(23659, 2); // DF-SZ
		mem16(23656, 0x5C92); // MEM
                mem(23611, 0x0C); // FLAGS

		cpu.pc(4788);
		//au_reset();
		try {
			audioChip.reset();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
		}
	}

	public jMESYSDisplay getDisplay() {
		//System.out.println("DISPLAY="+display);
		return display;
	}
	
	public FileFormat[] getSupportedFileFormats() throws Exception {
		if (supportedFormats == null){
			supportedFormats = new FileFormat[] {
				new FormatSNA(),
				new FormatTAP(),
				new FormatTZX(),
				new FormatZ80()
			};
		}
		
		return supportedFormats;
	}

	public jMESYSPrinterFrame getPrinter(Frame frame) {
		if (zxPrinter == null) {
			zxPrinter = new ZXPrinter(frame);
		}
		
		return zxPrinter;
	}

	public void loadRoms() {
		if (mode == SpectrumModels.MODE_48K){
			
			InputStream in = resource("/bios/Sinclair/Spectrum/spectrum.rom");
			
			if(in==null || FileFormat.tomem(rom48k, 0, 16384, in) != 0)
				System.out.println("Can't read /bios/Sinclair/Spectrum/spectrum.rom");
		
		} else if (mode == SpectrumModels.MODE_128K){
			
			InputStream in = resource("/bios/Sinclair/Spectrum/spectrum.rom");
			
			if(in==null || FileFormat.tomem(rom48k, 0, 16384, in) != 0)
				System.out.println("Can't read /bios/Sinclair/Spectrum/spectrum.rom");
		
            
			in = resource("/bios/Sinclair/Spectrum/zx128_0.rom");
            if(in==null || FileFormat.tomem(rom128k, 0, 16384, in) != 0)
                    System.out.println("Can't read /bios/Sinclair/Spectrum/zx128_0.rom");
		
		} else if (mode == SpectrumModels.MODE_PLUS2){
			
			InputStream in = resource("/bios/Sinclair/Spectrum/spectrum.rom");
			
			if(in==null || FileFormat.tomem(rom48k, 0, 16384, in) != 0)
				System.out.println("Can't read /bios/Sinclair/Spectrum/spectrum.rom");
		
            
			in = resource("/bios/Sinclair/Spectrum/plus2-0.rom");
            if(in==null || FileFormat.tomem(rom128k, 0, 16384, in) != 0)
                    System.out.println("Can't read /bios/Sinclair/Spectrum/plus2-0.rom");
		
		} else if (mode == SpectrumModels.MODE_PLUS3){
			
			InputStream in = resource("/bios/Sinclair/Spectrum/spectrum.rom");
			
			if(in==null || FileFormat.tomem(rom48k, 0, 16384, in) != 0)
				System.out.println("Can't read /bios/Sinclair/Spectrum/spectrum.rom");
		
            
			in = resource("/bios/Sinclair/Spectrum/plus3-0.rom");
            if(in==null || FileFormat.tomem(rom128k, 0, 16384, in) != 0)
                    System.out.println("Can't read /bios/Sinclair/Spectrum/plus3-0.rom");
		
		}else if (mode == SpectrumModels.MODE_PENTAGON){
			System.out.println("PENTAGON");
			InputStream in = resource("/bios/Sinclair/Spectrum/spectrum.rom");
			
			if(in==null || FileFormat.tomem(rom48k, 0, 16384, in) != 0)
				System.out.println("Can't read /bios/Sinclair/Spectrum/spectrum.rom");
		
            
			in = resource("/bios/Sinclair/Spectrum/Pentagon.rom");
            if(in==null || FileFormat.tomem(rom128k, 0, 16384, in) != 0)
                    System.out.println("Can't read /bios/Sinclair/Spectrum/Pentagon.rom");
		
		} else {
			System.out.println("MODE "+mode+" NOT SUPPORTED!!!!");
		}
	}
}
