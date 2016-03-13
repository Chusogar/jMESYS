package jMESYS.drivers;

import java.util.Vector;

import jMESYS.core.devices.jMESYSDevice;
import jMESYS.core.sound.cards.jMESYS_SoundCard;
import jMESYS.gui.jMESYSDisplay;

public abstract class jMESYSComputer {
	
	// devices
	private Vector devices = new Vector();
	
	// display
	private jMESYSDisplay display;
	
	// sound
	private boolean soundON = true;
	private jMESYS_SoundCard audioChip;
	
	public abstract jMESYS_SoundCard getAudioDevice();
	
	public jMESYSComputer() {
		super();
	}
	
	public jMESYSDisplay getDisplay() {
		return display;
	}
	
	public void setDisplay(jMESYSDisplay disp) {
		System.out.println("DISPLAY="+display);
		display = disp;
	}
	
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
	
	// SCALE????

}
