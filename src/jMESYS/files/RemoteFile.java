package jMESYS.files;

import java.io.Serializable;

public class RemoteFile implements Serializable {
	
	private String name;
	private int length = 0;
	private String path;
	private boolean directory = false;
	private String extension;
		
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isDirectory() {
		return directory;
	}
	
	public void isDirectory(boolean b) {
		directory = b;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

}
