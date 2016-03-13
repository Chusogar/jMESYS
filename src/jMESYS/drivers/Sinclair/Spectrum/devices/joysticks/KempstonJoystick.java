package jMESYS.drivers.Sinclair.Spectrum.devices.joysticks;

import jMESYS.core.devices.jMESYSDevice;

public class KempstonJoystick implements jMESYSDevice {

	private static String DEVICE_NAME = "Joystick Kempston";

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

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

}
