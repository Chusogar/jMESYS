package jMESYS.drivers;

import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;

public abstract class jMESYSFamily {
	
	public abstract String getModelDescription(int model);
    
    public abstract Spectrum48k[] getModels() throws Exception;
    
    public abstract Spectrum48k setModel(int iModel) throws Exception;
    
    public abstract String getFamilyName();
    
}
