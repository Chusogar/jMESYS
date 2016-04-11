package jMESYS.drivers.Atari.A2600;

import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.jMESYSFamily;


public class Atari2600Models extends jMESYSFamily {
	
	// family
	private static String FAMILY_NAME = "Atari 2600";
	
	// models
	public static final int MODE_A2600 		= 0;
	
	private jMESYSComputer[] models = null;

	public String getModelDescription(int model) {
		String s = "MODEL NOT FOUND";
    	
    	switch (model) {
    		case MODE_A2600:
    			s = "Atari 2600";
    		break;
    	}
    	
    	return s;
	}

	public jMESYSComputer[] getModels() throws Exception {
		
		if (models == null) {
    		
    		models = new jMESYSComputer[1];
    		
    		models[MODE_A2600] = new A2600(MODE_A2600);
		}
		
		return models;
	}

	public jMESYSComputer setModel(int iModel) throws Exception {
		A2600 myModel = null;
    	
    	switch (iModel) {
    		case MODE_A2600:
    			myModel = new A2600(MODE_A2600);
    			myModel.loadRoms();
    			break;
    	}
    	
		return myModel;
	}

	public String getFamilyName() {
		return FAMILY_NAME;
	}

}
