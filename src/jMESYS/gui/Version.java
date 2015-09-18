package jMESYS.gui;

public class Version {

	public static final String VERSION = "1.0 Beta 4";
	public static final String RELEASE_DATE = "31-03-2015";
	
	public Version() {
		super();
	}
	
	public static String getVersion(){
		return "jMESYS v" + VERSION + " (" + RELEASE_DATE + ")";
	}
	
}
