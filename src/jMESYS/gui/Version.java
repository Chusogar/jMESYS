/*
 * Created on 29 nov. 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jMESYS.gui;

/**
 * @author chusogar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Version {
	public static final String VERSION="1.0 Beta 6";
	public static final String RELEASE_DATE = "30-12-2015";
	
	public Version() {
		super();
	}
	
	public static String getVersion(){
		return "jMESYS v" + VERSION + " (" + RELEASE_DATE + ")";
	}
}
