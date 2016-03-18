package jMESYS.drivers.Sinclair.Spectrum.devices.interfaces;

import jMESYS.core.devices.jMESYSDevice;
import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.Sinclair.Spectrum.SpectrumModels;

public class MultifaceI implements jMESYSDevice {
	
	private String DEVICE_NAME = "Multiface I";
	
	public static final int[] compatibleSystems = {
			SpectrumModels.MODE_48K,
			SpectrumModels.MODE_128K,
			SpectrumModels.MODE_PLUS2,
			SpectrumModels.MODE_PLUS3,
			SpectrumModels.MODE_PENTAGON,
			SpectrumModels.MODE_PENT_SP
	};

	@Override
	public void out(int port, int v) {
		// TODO Auto-generated method stub
		
	}

	public int in(int port) {
		if ((port & 0xff) == 0x9f) {
            //memory.pageMultiface();
        }
        // Este puerto es el mismo que el Kempston. De hecho, el
        // MF1 incorporaba un puerto Kempston...
        if ((port & 0xff) == 0x1f /*&& memory.isMultifacePaged()*/) {
            //memory.unpageMultiface();
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

	@Override
	public int getPortNumber() {
		// TODO Auto-generated method stub
		return 0;
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

	public int[] getCompatibleSystems() {		
		return compatibleSystems;
	}

	public boolean isCompatible(int sysID) {
		int iNum = compatibleSystems.length;
		boolean compat = false;
		
		for (int i=0 ; i<iNum ; i++){
			if (sysID == compatibleSystems[i])
				compat = true;
		}
		
		return compat;
	}

}
