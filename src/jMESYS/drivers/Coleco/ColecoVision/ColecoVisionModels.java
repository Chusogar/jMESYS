package jMESYS.drivers.Coleco.ColecoVision;

import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.jMESYSFamily;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;

public class ColecoVisionModels extends jMESYSFamily {
	
	// family
	private static String FAMILY_NAME = "Coleco";
	
	// models
	public static final int MODE_ColecoVision = 0;
	
	private ColecoVision[] models = null;
		
	public String getModelDescription(int model) {
		String s = "MODEL NOT FOUND";
    	
    	switch (model) {
    		case MODE_ColecoVision:
    			s = "Coleco Vision";
    		break;
    	}
    	
    	return s;
	}

	public jMESYSComputer[] getModels() throws Exception {
		if (models == null) {
    		
    		models = new ColecoVision[1];
    		
    		models[MODE_ColecoVision] = new ColecoVision(MODE_ColecoVision);
    		
    	}
    	
    	return models;
	}

	public jMESYSComputer setModel(int iModel) throws Exception {
		ColecoVision myModel = null;
    	
    	switch (iModel) {
    		case MODE_ColecoVision:
    			myModel = new ColecoVision(MODE_ColecoVision);
    			myModel.loadRoms();
    			break;    		
    	}
    	
    	return myModel;
	}

	public String getFamilyName() {
		return FAMILY_NAME;
	}

}
