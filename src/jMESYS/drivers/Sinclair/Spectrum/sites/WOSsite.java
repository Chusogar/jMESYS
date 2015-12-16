package jMESYS.drivers.Sinclair.Spectrum.sites;

import java.util.Vector;

import jMESYS.gui.loader.jMESYSRemoteSite;
import jMESYS.files.RemoteFile;

public class WOSsite extends jMESYSRemoteSite {

	private String WOSaddress = "http://www.worldofspectrum.org/pub/sinclair/games/s";
	private String initag = "<li><a href=\"";
	private String endtag = "\">";
	
	public String getRemoteAddress() {
		return WOSaddress;
	}
		
	public static void main(String[] args) {
		
		WOSsite site = new WOSsite();
		Vector v = site.readRemotePage();
		
		try {
			site.getZIPcontents((RemoteFile) v.elementAt(1));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public String getIniTag() {
		return initag;
	}
	
	public String getEndTag() {
		return endtag;
	}

	public void setRemoteAddress(String addr) {
		WOSaddress = addr;
	}

}
