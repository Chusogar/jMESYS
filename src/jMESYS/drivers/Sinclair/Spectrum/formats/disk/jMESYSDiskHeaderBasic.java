package jMESYS.drivers.Sinclair.Spectrum.formats.disk;

public class jMESYSDiskHeaderBasic {
	
	// length of program + variables area 2 bytes
	private int progVars = 0;
	// length of program only 2 bytes
	private int prog = 0;
	
	public int getProgVars() {
		return progVars;
	}
	public void setProgVars(int progVars) {
		this.progVars = progVars;
	}
	public int getProg() {
		return prog;
	}
	public void setProg(int prog) {
		this.prog = prog;
	}
	
}
