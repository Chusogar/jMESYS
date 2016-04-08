package jMESYS.drivers;


public abstract class jMESYSFamily {
	
	public abstract String getModelDescription(int model);
    
    public abstract jMESYSComputer[] getModels() throws Exception;
    
    public abstract jMESYSComputer setModel(int iModel) throws Exception;
    
    public abstract String getFamilyName();
    
}
