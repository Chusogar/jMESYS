package jMESYS.core.devices;

import jMESYS.drivers.jMESYSComputer;

public interface jMESYSDevice {
	
	// device type
	public static int TYPE_JOYSTICK 	= 0;
	public static int TYPE_PRINTER 		= 1;
	public static int TYPE_INTERFACE 	= 3;
	public static int TYPE_SOUND_CARD 	= 4;
	
	// port
	public abstract int getDeviceType();
	public abstract int getPortNumber();
	
	// device name
	public abstract String getDeviceName();
	
	// open & close device
	public abstract void open() throws Exception;
	public abstract void close() throws Exception;
	
	// reset device
	public abstract void reset() throws Exception;
	
	// port access
	public abstract void out(int port, int v);
	public abstract int in(int port);
	
	// checks if the device is connected
	public abstract boolean isEnabled();
	public abstract void setEnabled(boolean enabled) throws Exception;
	
	// connect device to the system
	public abstract boolean connectDevice(jMESYSComputer computer) throws Exception;
	
	// list of compatible systems
	public abstract int[] getCompatibleSystems();
	public abstract boolean isCompatible(int sysID);
}
