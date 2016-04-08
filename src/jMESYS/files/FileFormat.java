package jMESYS.files;

import jMESYS.core.cpu.CPU;
import jMESYS.drivers.jMESYSComputer;
import jMESYS.gui.jMESYSDisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class FileFormat {
	
	private String extension = null;
	
	public FileFormat(){
		super();
	}
	
	public boolean resetBeforeLoad() {
		return true;
	}
	
	public abstract String getExtension();
	public abstract void loadFormat( String name, InputStream is, jMESYSComputer cpu ) throws Exception;
	public abstract Image getScreen( String name, InputStream is, jMESYSDisplay display, Graphics g ) throws Exception;
	
	public static int get8(DataInputStream i) throws IOException {
		return i.readUnsignedByte();
	}

	public static int get16(DataInputStream i) throws IOException {
		int b = i.readUnsignedByte();
		return b | i.readUnsignedByte()<<8;
	}
	
	/**
     * write int array to memory
     * @param a int[]
     * @param pos int
     * @param len int
     */
    public void poke_array(jMESYSComputer computer, int[] a, int pos, int len) {
      for (int i = 0; i < len; i++) {
        computer.mem(pos + i, a[i]);
      }
    }
    
    public byte[] getArrayFile( InputStream is ) throws Exception {
		/*BufferedInputStream tapeFile = new BufferedInputStream(is);
		int fileSize = tapeFile.available();
		System.out.println("File Size="+fileSize);
		byte tapeBuffer[] = new byte[fileSize];
		tapeFile.read(tapeBuffer);
		tapeFile.close();
		
		byte tapeBufferOut[] = new byte[fileSize];
		System.arraycopy(tapeBuffer, 0, tapeBufferOut, 0, fileSize-2);*/
		int size = new BufferedInputStream(is).available();
		byte tapeBuffer[] = new byte[size];
		readBytes(is, tapeBuffer, 0, size);
		
		return tapeBuffer;
	}
    
    public void poke_stream(jMESYSComputer computer, DataInputStream in, int pos, int len) throws IOException {
		do {
			computer.mem(pos++, FileFormat.get8(in));
		} while(--len>0);
	}
	
	public static int tomem(int[] m, int p, int l, InputStream in)
	{
		do try {
			int v = in.read();
			if(v<0) break;
			m[p++] = v;
		} catch(IOException ignored) {
			break;
		} while(--l>0);
		return l;
	}
	
	public void load(String name, jMESYSComputer cpu) throws Exception {
		checkExtension(name);		
		InputStream stream = getFileStream(name);
		loadFormat(name, stream, cpu);
	}
	
	public Image loadScreen(String name, jMESYSDisplay disp, Graphics g) throws Exception {
		Image imTV = null;
		boolean loadBlackScreen = false;
		
		checkExtension(name);		
		InputStream stream = getFileStream(name);
		try {
			System.out.println("GETSCREEN");
			imTV = getScreen(name, stream, disp, g);
		} catch (Exception e) {
			loadBlackScreen = true;
			e.printStackTrace(System.out);
		}
		
		if ( (imTV == null) || (loadBlackScreen) ) {
			System.out.println("Pinto pantalla nula");
			g.setColor(Color.RED);
			g.fillRect(0, 0, disp.getWidth(), disp.getHeight());
			//disp.paintImageScreen(g, arrScr, arrAtt);
		}
		
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
