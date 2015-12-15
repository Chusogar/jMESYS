package jMESYS.gui;

public class Version {

	public static final String VERSION = "1.0 Beta 5";
	public static final String RELEASE_DATE = "15-12-2015";
	
	public Version() {
		super();
	}
	
	public static String getVersion(){
		return "jMESYS v" + VERSION + " (" + RELEASE_DATE + ")";
	}
	
}
