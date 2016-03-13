package jMESYS.drivers.Sinclair.Spectrum.devices.interfaces;

import jMESYS.core.devices.jMESYSDevice;

public class MultifaceI implements jMESYSDevice {
	
	private String DEVICE_NAME = "Multiface I";

	@Override
	public void out(int port, int v) {
		// TODO Auto-generated method stub
		
	}

	public int in(int port) {
		if ((port & 0xff) == 0x9f) {
            memory.pageMultiface();
        }
        // Este puerto es el mismo que el Kempston. De hecho, el
        // MF1 incorporaba un puerto Kempston...
        if ((port & 0xff) == 0x1f && memory.isMultifacePaged()) {
            memory.unpageMultiface();
        }
		return 0xFF;
	}

	public boolean isConnected() {
		return true;
	}

	public int getDeviceType() {
		return TYPE_INTERFACE;
	}

	public String getDeviceName() {
		return DEVICE_NAME;
	}

}
