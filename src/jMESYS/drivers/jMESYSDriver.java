package jMESYS.drivers;

public class jMESYSDriver {
		  
	  public String key, name, className;
	  public boolean shown;
	  
	  public jMESYSDriver(String key, String name, String className, boolean shown) {
	    this.key = key;
	    this.name = name;
	    this.className = className;
	    this.shown = shown;
	  }
	  
	  public String toString() {
	    return name;
	  }
		  
}
