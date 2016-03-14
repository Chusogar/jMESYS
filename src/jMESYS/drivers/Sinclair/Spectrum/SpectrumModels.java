package jMESYS.drivers.Sinclair.Spectrum;

import jMESYS.drivers.jMESYSFamily;

public class SpectrumModels extends jMESYSFamily {
	
	// family
	private static String FAMILY_NAME = "ZX Spectrum";
	
	// models
	public static final int MODE_48K 		= 0;
    public static final int MODE_128K 		= 1;
    public static final int MODE_PLUS2 		= 2;
    public static final int MODE_PLUS3 		= 3;
    public static final int MODE_PENTAGON 	= 4;
    
    private Spectrum48k[] models = null;
    
    public String getModelDescription(int model) {
    	
    	String s = "MODEL NOT FOUND";
    	
    	switch (model) {
    		case MODE_48K:
    			s = "Sinclair ZX Spectrum 48k";
    		break;
    		
    		case MODE_128K:
    			s = "Sinclair ZX Spectrum 128k";
    		break;
    		
    		case MODE_PLUS2:
    			s = "Amstrad ZX Spectrum +2 128k";
    		break;
    		
    		case MODE_PLUS3:
    			s = "Amstrad ZX Spectrum +3 128k";
    		break;
    		
    		case MODE_PENTAGON:
    			s = "ZX Spectrum Pentagon 128k";
    		break;
    	}
    	
    	return s;
    }
    
    public Spectrum48k[] getModels() throws Exception {
    	
    	if (models == null) {
    		
    		models = new Spectrum48k[5];
    		
    		models[MODE_48K] 		= new Spectrum48k(MODE_48K);
    		models[MODE_128K] 		= new Spectrum48k(MODE_128K);
    		models[MODE_PLUS2] 		= new Spectrum48k(MODE_128K); 
    		models[MODE_PLUS3] 		= new Spectrum48k(MODE_128K);
    		models[MODE_PENTAGON] 	= new Spectrum48k(MODE_128K);
    		
    	}
    	
    	return models;
    }
    
    public Spectrum48k setModel(int iModel) throws Exception {
    	
    	Spectrum48k myModel = null;
    	
    	switch (iModel) {
    		case MODE_48K:
    			myModel = new Spectrum48k(MODE_48K);
    			myModel.loadRoms();
    			break;
    		case MODE_128K:
    			myModel = new Spectrum48k(MODE_128K);
    			myModel.loadRoms();
    			break;
    		case MODE_PLUS2:
    			myModel = new Spectrum48k(MODE_PLUS2);
    			myModel.loadRoms();
    			break;
    		case MODE_PLUS3:
    			myModel = new Spectrum48k(MODE_PLUS3);
    			myModel.loadRoms();
    			break;
    		case MODE_PENTAGON:
    			myModel = new Spectrum48k(MODE_PENTAGON);
    			myModel.loadRoms();
    			break;
    	}
    	
    	return myModel;
    }
    
    public String getFamilyName() {
    	return FAMILY_NAME;
    }
}
