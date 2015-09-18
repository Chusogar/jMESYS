package jMESYS.files;

import jMESYS.core.cpu.CPU;
import jMESYS.gui.jMESYSDisplay;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class FileFormat {
	
	private String extension = null;
	
	public FileFormat(){
		super();
	}
	
	public abstract String getExtension();
	public abstract void loadFormat( String name, InputStream is, CPU cpu ) throws Exception;
	public abstract Image getScreen( String name, InputStream is, jMESYSDisplay display, Graphics g ) throws Exception;
	
	public void load(String name, CPU cpu) throws Exception {
		checkExtension(name);		
		InputStream stream = getFileStream(name);
		loadFormat(name, stream, cpu);
	}
	
	public Image loadScreen(String name, jMESYSDisplay disp, Graphics g) throws Exception {
		checkExtension(name);		
		InputStream stream = getFileStream(name);
		Image imTV = getScreen(name, stream, disp, g);
		
		return imTV;
	}
	
	protected int readInt(InputStream is, int start, int len) throws Exception {
        int res = 0;
        byte buffer[] = new byte[len];
        
        readBytes(is, buffer, start, len);

        for (int idx = 0; idx < len; idx++) {
            res |= ((buffer[start + idx] << (idx * 8)) & (0xff << idx * 8));
        }
        
        return res;
    }
	
	protected int readInt(byte buffer[], int start, int len) throws Exception {
        int res = 0;
        //byte buffer[] = new byte[len];
        
        //readBytes(is, buffer, start, len);

        for (int idx = 0; idx < len; idx++) {
            res |= ((buffer[start + idx] << (idx * 8)) & (0xff << idx * 8));
        }
        
        return res;
    }
	
	protected int readBytes(InputStream is, byte[] a, int off, int n) throws Exception {
		try {
			BufferedInputStream bis = new BufferedInputStream( is, n );

			byte buff[] = new byte[ n ];
			int toRead = n;
			while ( toRead > 0 ) {
				
				int	nRead = bis.read( buff, n-toRead, toRead );
				toRead -= nRead;				
			}

			for ( int i = 0; i < n; i++ ) {
				a[ i+off ] = (byte) ((buff[i]+256)&0xff);
			}

			return n;
		}
		catch ( Exception e ) {
			System.err.println( e );
			e.printStackTrace();
			throw e;
		}
	}
	
	public InputStream getFileStream(String name) throws Exception {
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
	
	protected int readBytes( InputStream is, int a[], int off, int n ) throws Exception {
		try {
			BufferedInputStream bis = new BufferedInputStream( is, n );

			byte buff[] = new byte[ n ];
			int toRead = n;
			while ( toRead > 0 ) {
				int	nRead = bis.read( buff, n-toRead, toRead );
				toRead -= nRead;
			}

			for ( int i = 0; i < n; i++ ) {
				a[ i+off ] = (buff[i]+256)&0xff;
			}

			return n;
		}
		catch ( Exception e ) {
			System.err.println( e );
			e.printStackTrace();
			throw e;
		}
	}
	
	protected void checkExtension(String name) throws Exception {
		if (!name.endsWith(getExtension())){
			throw new Exception("Not a "+getExtension()+" file! "+name);
		}
	}

	public abstract String getFileName();
}
