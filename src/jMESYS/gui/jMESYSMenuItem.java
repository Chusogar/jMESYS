package jMESYS.gui;

import jMESYS.files.FileFormat;

import java.awt.MenuItem;

public class jMESYSMenuItem extends MenuItem {
	
	private FileFormat fFormat;
	private String label;
	private String fileName;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public jMESYSMenuItem(String sLabel, FileFormat format){
		super(sLabel);
		label = sLabel;
		fFormat = format;
	}

	public FileFormat getfFormat() {
		return fFormat;
	}

	public void setfFormat(FileFormat fFormat) {
		this.fFormat = fFormat;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getExtension() {
		return fFormat.getExtension();
	}

	public String getFileName() {		
		return fileName;
	}	
	
}
