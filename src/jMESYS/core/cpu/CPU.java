package jMESYS.core.cpu;

import jMESYS.files.FileFormat;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class CPU {
	
	protected byte[] mem;
	
	public CPU() {
		super();
		mem = getMem();
	}
	
	// Memory
	public abstract byte[] getMem();
	public abstract void setMem(byte[] memo);
	public abstract int getMemorySize();
	public abstract void setMemorySize(int mSize);
	public abstract void pokeb(int addr, int newByte);
	public abstract void pokew( int addr, int word );
	public abstract int peekb( int addr );
	public abstract int peekw( int addr );
	
	// Registers
	public abstract void setRegister(String regName, int value);
	public abstract int getRegister(String regName);	
	public abstract void pushRegister(String regName);
	public abstract void popRegister(String regName);

	// Flags
	public abstract int F();
	public abstract void F( int bite );
	
	// Interrupts
	public abstract void interruptFF(String iffName, boolean value);
	public abstract boolean interruptFF(String iffName);	
	public abstract void setInterruptMode(String interrupt, int value);
	public abstract void setInterrupt(int value);
	public abstract int getInterrupt();
	public abstract int getInterruptMode(String iffName);
	public abstract void cycle();
	
	// ports
	public abstract void outb( int port, int bite, int tstates );
	
	// refresh
	public abstract void REFRESH( int t );
	
	// others
	public abstract void exx();
	public abstract void ex_af_af();
	
	// supported file format
	public abstract FileFormat[] getSupportedFileFormats() throws Exception;
	
	protected void loadROM(String name, int offset, int size, boolean crop) {
		try {
			loadFile(name, size, crop);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public byte[] loadFile (String name, int size, boolean crop) throws Exception {
		//byte[] buffer = new byte[size];
	    int offs = 0;
	    try {
	      InputStream stream = null;
	      try {
	        stream = openFile(name);
	        while (size > 0) {
	        	//System.out.println("Memoria: "+mem);
	          int read = stream.read(getMem(),offs,size);
	          if (read == -1)
	            break;
	          else {
	            offs += read;
	            size -= read;
	          }
	        }
	      } finally {
	        if (stream != null)
	          stream.close();
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    if (crop && offs < mem.length) {
	      byte[] result = new byte[offs];
	      System.arraycopy(mem,0,result,0,offs);
	      mem = result;
	    }
	    return mem;
	}
	
	public InputStream openFile(String name) throws Exception {
	    System.out.println("File: " + name);
	    InputStream result;
	    try {
	    	System.out.println(new URL(name));
	      result = new URL(name).openStream();
	    } catch(Exception e) {
//	      e.printStackTrace();
	      result = new FileInputStream(name);
	    }
	    if (name.toLowerCase().endsWith(".zip")) {
	      ZipInputStream str = new ZipInputStream(result);
	      ZipEntry entry = str.getNextEntry();
	      System.out.println(entry.getName());
	      result = str;
	    }
	    return result;
	  }

}
