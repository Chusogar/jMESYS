package jMESYS.drivers.Sinclair.Spectrum.formats;

import java.awt.Graphics;
import java.awt.Image;
import java.io.InputStream;

import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public class FormatSCL extends FileFormat {
	
	// extension
	private static String strExtension = ".SCL";

	public String getExtension() {
		return strExtension;
	}

	public void loadFormat(String name, InputStream in, jMESYSComputer cpu) throws Exception {
		System.out.println("LOAD SCL");
		
		byte[] b = new byte[1];
		int j = 0;
				
		try {
			
			// SIGNATURE 8 bytes
			String signature = "";
			
			for(int i = 0; i < 8; i++){
				j = in.read(b, 0, 1);
				j = ((b[0] + 256) & 0xff);
				signature = signature.concat(signature.valueOf((char) j));
			}
			
			System.out.println("SIGNATURE: "+signature);
			
			// number of files 1 byte
			int numFiles = 0;
			j = in.read(b, 0, 1);
			j = ((b[0] + 256) & 0xff);
			numFiles = j;
			
			System.out.println("NUMBER OF FILES: "+numFiles);
			
			for (int i=0 ; i<numFiles ; i++) {
				byte[] directory = getDirectory(i, in);
			}
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	private byte[] getDirectory(int dirNum, InputStream in) throws Exception {
		
		byte[] dirAct = new byte[14];
		
		int j = 0;
		byte[] b = new byte[1];
		
		System.out.println("-------------------------------DIR "+dirNum);
		
		// directory name 8 bytes
		String dirName = "";
		
		for(int i = 0; i < 8; i++){
			j = in.read(b, 0, 1);
			j = ((b[0] + 256) & 0xff);
			dirName = dirName.concat(dirName.valueOf((char) j));
		}
		System.out.println("FILE NAME: "+dirName);
		if (dirName.startsWith("0")) {
			System.out.println("END OF DIRECTORY");
		} else if (dirName.startsWith("1")) {
			System.out.println("DELETED FILE");
		}
		
		// file extension 1 byte
		String fileExtension = "";
		j = in.read(b, 0, 1);
		j = ((b[0] + 256) & 0xff);
		fileExtension = fileExtension.concat(fileExtension.valueOf((char) j));
		System.out.println("FILE EXTENSION: "+fileExtension);
		/*  
		* character that decribes the file type:
 		* "B" = Basic program,
 		* "D" = DATA array (numeric or alphanumeric)
 		* "C" = CODE 
 		* "#" = Print file (may be split into several  sub-files with max. 4096 bytes each)
 		*/
		if (fileExtension.equals("B")){
			dirAct = processBasicProgram(in, dirAct);
		} else if (fileExtension.equals("D")){
			dirAct = processDataArray(in, dirAct);
		} else if (fileExtension.equals("C")){
			dirAct = processCode(in, dirAct);
		} else {
			dirAct = processPrintFile(in, dirAct);
		}
		
		// file length 1 byte
		int fileLength = 0;
		j = in.read(b, 0, 1);
		fileLength = ((b[0] + 256) & 0xff);
		System.out.println("FILE LENGTH: "+fileLength);
		
		return dirAct;
	}

	private byte[] processPrintFile(InputStream in, byte[] dirAct) throws Exception {
		int j = 0;
		byte[] b = new byte[1];
		
		// Part Number 1 byte
		int partNumber = 0;
		j = in.read(b, 0, 1);
		partNumber = ((b[0] + 256) & 0xff);
        System.out.println("PART NUMBER: "+partNumber);
        
        // UNUSED 1 byte (Always 32)
        int notUsed = 0;
        j = in.read(b, 0, 1);
        notUsed = ((b[0] + 256) & 0xff);
        System.out.println("NOT USED(always 32): "+notUsed);
		
		// print length 2 bytes
		int printLength = 0;
		j = in.read(b, 0, 1);
		printLength = ((b[0] + 256) & 0xff);
        j = in.read(b, 0, 1);
        printLength = printLength | (((b[0] + 256) & 0xff) << 8);
        System.out.println("PRINT LENGTH: "+printLength);

		return dirAct;
	}

	private byte[] processCode(InputStream in, byte[] dirAct) throws Exception {
		int j = 0;
		byte[] b = new byte[1];
		
		// Start Address 2 bytes
		int startAddress = 0;
		j = in.read(b, 0, 1);
		startAddress = ((b[0] + 256) & 0xff);
        j = in.read(b, 0, 1);
        startAddress = startAddress | (((b[0] + 256) & 0xff) << 8);
        System.out.println("START ADDRESS: "+startAddress);
		
		// length of code 2 bytes
		int codeLength = 0;
		j = in.read(b, 0, 1);
		codeLength = ((b[0] + 256) & 0xff);
        j = in.read(b, 0, 1);
        codeLength = codeLength | (((b[0] + 256) & 0xff) << 8);
        System.out.println("LONG CODE: "+codeLength);

		return dirAct;
	}

	private byte[] processDataArray(InputStream in, byte[] dirAct) throws Exception {
		
		int j = 0;
		byte[] b = new byte[1];
		
		// Param1 2 bytes (NOT USED)
		int param1 = 0;
		j = in.read(b, 0, 1);
		param1 = ((b[0] + 256) & 0xff);
        j = in.read(b, 0, 1);
        param1 = param1 | (((b[0] + 256) & 0xff) << 8);
        System.out.println("PARAM1 (Not Used): "+param1);
		
		// length of data array 2 bytes
		int longDataArray = 0;
		j = in.read(b, 0, 1);
		longDataArray = ((b[0] + 256) & 0xff);
        j = in.read(b, 0, 1);
        longDataArray = longDataArray | (((b[0] + 256) & 0xff) << 8);
        System.out.println("LONG DATA ARRAY: "+longDataArray);

		return dirAct;
	}

	private byte[] processBasicProgram(InputStream in, byte[] dirAct) throws Exception {
		int j = 0;
		byte[] b = new byte[1];
		
		// length of program + variables area 2 bytes
		int progVars = 0;
		j = in.read(b, 0, 1);
        progVars = ((b[0] + 256) & 0xff);
        j = in.read(b, 0, 1);
        progVars = progVars | (((b[0] + 256) & 0xff) << 8);
        System.out.println("PROG+VARS: "+progVars);
		
		// length of program only 2 bytes
		int prog = 0;
		j = in.read(b, 0, 1);
        prog = ((b[0] + 256) & 0xff);
        j = in.read(b, 0, 1);
        prog = prog | (((b[0] + 256) & 0xff) << 8);
        System.out.println("PROG: "+prog);

		return dirAct;
	}

	@Override
	public Image getScreen(String name, InputStream is, jMESYSDisplay display, Graphics g) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFileName() {
		return "D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/ROBIN_WC.SCL";
	}

}
