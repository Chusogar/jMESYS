package jMESYS.gui.loader;

import java.io.File;
import java.util.HashMap;

import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class jMESYSFileLoader extends JFileChooser {
	
	// Supported Formats 
	private FileFormat[] fileFormats = null;
	private String cadExtensions = "";
	
	// screen
	private jMESYSDisplay display;
	
	
	public jMESYSFileLoader(int width, int height, FileFormat[] ff, jMESYSDisplay disp) {
		super();
		
		// supported files
		fileFormats = ff;
		
		// screen
		display = disp;
		
		int countFormats = ff.length;
				
		for (int i=0 ; i<countFormats ; i++){
			cadExtensions += ff[i].getExtension().toUpperCase();
			if (i != (countFormats-1)){
				cadExtensions += ", ";
			} else {
				cadExtensions += " files";
			}
		}
		
		this.addChoosableFileFilter(
				
				new FileFilter() {
					public boolean accept(File file){
						
						boolean formatFound = false;
						
						
						for (int i=0 ; i<countFormats ; i++){
							if (file.getName().toUpperCase().endsWith(ff[i].getExtension().toUpperCase()))
								formatFound = true;
						}
						
						return (formatFound || file.isDirectory());
					}
					
					public String getDescription(){
												
						return cadExtensions;
					}
				}
				
				
		);
				
		
		// includes Preview Panel
		setAccessory(new jMESYSImagePreview(this, width, height, fileFormats, display));
	}
	
	public FileFormat getLoaderFile(String name) throws Exception {
		int numFormats = fileFormats.length;
		int counter = 0;
		boolean foundFormat=false;
		
		String nameChecked = jMESYSFileZIP.checkZIP( name );
		
		FileFormat selectedFormat = null;
		
		while (!foundFormat && (counter < numFormats)) {
			FileFormat ff = (FileFormat) fileFormats[counter];
			if (nameChecked.toUpperCase().endsWith(ff.getExtension())){
				foundFormat = true;
				selectedFormat = ff;
			}
			counter++;
		}
		
		return selectedFormat;
	}
}
