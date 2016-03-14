package jMESYS.gui;

import java.awt.MenuItem;

import jMESYS.drivers.jMESYSFamily;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;

public class jMESYSMenuComputerItem extends MenuItem {
	
	private int model = 0;
	private jMESYSFamily family;
	private Spectrum48k computer;
	
	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public jMESYSFamily getFamily() {
		return family;
	}

	public void setFamily(jMESYSFamily family) {
		this.family = family;
	}

	public Spectrum48k getComputer() {
		return computer;
	}

	public void setComputer(Spectrum48k computer) {
		this.computer = computer;
	}
	
	public jMESYSMenuComputerItem(String desc, int model, jMESYSFamily family) {
		super(desc);
		
		this.model=model;
		this.family=family;
		
	}
	
}
