package jMESYS.drivers.Sinclair.Spectrum.devices.joysticks;

import jMESYS.core.devices.jMESYSDevice;
import jMESYS.drivers.jMESYSComputer;

public class KempstonJoystick implements jMESYSDevice {

	private static String DEVICE_NAME 	= "Joystick Kempston";
	private static int PORT_NUMBER		= 0x00E0;

	public int getDeviceType() {
		return TYPE_JOYSTICK;
	}

	public String getDeviceName() {
		return DEVICE_NAME;
	}

	@Override
	public void out(int port, int v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int in(int port) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPortNumber() {
		return PORT_NUMBER;
	}

	@Override
	public void open() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEnabled(boolean enabled) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean connectDevice(jMESYSComputer computer) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
