package jMESYS.drivers;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.Vector;

import jMESYS.core.cpu.CPU;
import jMESYS.core.devices.jMESYSDevice;
import jMESYS.core.devices.printer.jMESYSPrinterFrame;
import jMESYS.core.sound.cards.jMESYS_SoundCard;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public abstract class jMESYSComputer extends Thread implements CPU {
	
	// devices
	private Vector devices = new Vector();
	
	// computer name
	private String computerName = null;
	
	// display
	//private jMESYSDisplay display;
	public abstract void refresh_new();
	public abstract jMESYSDisplay getDisplay();
	
	// sound
	public boolean soundON = true;
	
	public abstract jMESYS_SoundCard getAudioDevice();
	
	// pause
	boolean paused = true;
	boolean want_pause = true;
	
	private long time;
	private int timet;
	
	public abstract void setCPUtime(int t);
	public abstract int getCPUtime();
	public abstract void setCPUtimeLimit(int t);
	public abstract int getCPUtimeLimit();
	public abstract void executeCPU() throws Exception;
	public abstract void reset();
	
	// supported file formats
	private FileFormat[] supportedFormats = null;
	public abstract FileFormat[] getSupportedFileFormats();
	
	// frames
	protected abstract void end_frame();
	
	// keyboard
	public abstract void update_keyboard();
	public KeyEvent keys[] = new KeyEvent[8];
	
	// printer
	public abstract jMESYSPrinterFrame getPrinter(Frame frame);
	
	// tape
	public boolean loading, stop_loading, tapeValue;
	public byte[] tape;
	public int tape_blk;
	public int tape_pos;
	public boolean tape_changed = false;
	public boolean tape_ready = false;
	//pause in mSeconds between blocks, default 1 second
	private int pausa = 1000;
	// T-states for reading a tape data
	private int tapein = 0;
	//Auxiliar integer for reading the tape
	private int code = 0;
	//Number & code of the last played block
	private int block = 0;
	private int code_block = 0;
	
	public abstract boolean check_load();
	public abstract boolean do_load(byte[] tape, boolean ready);

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
	
	public final InputStream resource(String name)
	{
		System.out.println(name);
		return getClass().getResourceAsStream(name);
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
	  
	public jMESYSComputer(String cName) {
		super();
		computerName = cName;
	}
	
	/*public jMESYSDisplay getDisplay() throws Exception {
		if (display==null)
			throw new Exception("setDisplay needs to be set!");
		return display;
	}*/
	
	/*public void setDisplay(jMESYSDisplay disp) {
		System.out.println("DISPLAY="+display);
		display = disp;
	}*/
	
	public void addDevice(jMESYSDevice device) throws Exception {
		System.out.println("adding device:"+device.getDeviceName());
		devices.add(device);
	}
	
	public abstract void setModel(int iModel) throws Exception;
	
	public jMESYSDevice[] getDevices() {
		int numDevices = devices.size();
		
		jMESYSDevice[] arrDevices = new jMESYSDevice[numDevices];
		
		for (int i=0 ; i<numDevices ; i++){
			arrDevices[i] = (jMESYSDevice) devices.elementAt(i);
		}
		
		return arrDevices;
	}
	
	public boolean isMuted() {
		return !soundON;
	}
	
	public void setMuted(boolean muted) throws Exception {
		soundON = !muted;		
		getAudioDevice().muteSoundCard(muted);		
	}
	
	
	// main thread method
	public void run()
	{
		try {
			frames();
			getAudioDevice().close();
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}		
	}
	
	public synchronized void pause(boolean y) throws InterruptedException
	{
		want_pause = y;
		while(paused != want_pause)
			wait();
	}
	
	public void frames() throws Exception
	{
		time = System.currentTimeMillis();
		//cpu.time = -14335;
		setCPUtime(-14335);
		//cpu.time_limit = 55553;
		setCPUtimeLimit(55553);
		//au_time = cpu.time;
		//getAudioDevice().setAudioTime(cpu.time);
		getAudioDevice().setAudioTime(getCPUtime());
		
		tapeValue = false;
		
		for(;;) {
			byte[] tap = null;
			boolean tend = false;
			synchronized(this) {
				if(getDisplay().want_scale != getDisplay().scale) {
					getDisplay().scale = getDisplay().want_scale;
					if (getDisplay().scale == 3) { // Full Screen
						Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
						
						getDisplay().setWidth(d.width); 
						getDisplay().setHeight(d.height);
					
					} else { // scale 1 or 2
						getDisplay().setWidth(getDisplay().scale*getDisplay().getW()); 
						getDisplay().setHeight(getDisplay().scale*getDisplay().getH());
					}
					
					notifyAll();
					getDisplay().abort_consumers();
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
				//cpu.time = cpu.time_limit;
				setCPUtime(getCPUtimeLimit());
			} else if(loading) {
				loading = do_load(tap, tend);
				//cpu.time = cpu.time_limit;
				setCPUtime(getCPUtimeLimit());
			} else {
				executeCPU();
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
	
	public synchronized void scale(int m) 
	{
		if (getDisplay()!=null) {
			getDisplay().want_scale = m;
			getDisplay().scale=m;
			System.out.println("SCALE="+getDisplay().scale);
			try {
				while(getDisplay().scale != m) wait();
			} catch(InterruptedException e) {
				currentThread().interrupt();
			}
		}
	}

	public int scale() {
		System.out.println("Display="+getDisplay());
		//System.out.println("SCALE="+display.scale);
		if (getDisplay()!=null) return 1;
		
		return getDisplay().scale;
	}
}
