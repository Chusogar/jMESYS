package jMESYS.drivers.Sinclair.Spectrum.formats.disk;

public class jMESYSDiskHeader {
	
	public static int BASIC_PROGRAM 	= 0;
	public static int DATA_ARRAY 		= 1;
	public static int CODE		 		= 2;
	public static int PRINT_FILE 		= 3;
	
	// directory name 8 bytes
	private String dirName = "";
	// file extension 1 byte
	private String fileExtension = "";
	// file length in sectors 1 byte
	private int fileLength = 0;
	
	public String getDirName() {
		return dirName;
	}
	public void setDirName(String dirName) {
		this.dirName = dirName;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	public int getFileLength() {
		return fileLength;
	}
	public void setFileLength(int fileLength) {
		this.fileLength = fileLength;
	}
	
	
	
}
