package jMESYS.gui;

import java.awt.MenuItem;

import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.jMESYSFamily;

public class jMESYSMenuComputerItem extends MenuItem {
	
	private int model = 0;
	private jMESYSFamily family;
	private jMESYSComputer computer;
	
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

	public jMESYSComputer getComputer() {
		return computer;
	}

	public void setComputer(jMESYSComputer computer) {
		this.computer = computer;
	}
	
	public jMESYSMenuComputerItem(String desc, int model, jMESYSFamily family) {
		super(desc);
		
		this.model=model;
		this.family=family;
		
	}
	
}
