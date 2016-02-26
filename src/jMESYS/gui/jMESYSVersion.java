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
public class jMESYSVersion {
	public static final String VERSION="2.0 Beta 10";
	public static final String RELEASE_DATE = "15-02-2016";
	
	public jMESYSVersion() {
		super();
	}
	
	public static String getVersion(){
		return "jMESYS v" + VERSION + " (" + RELEASE_DATE + ")";
	}
}
