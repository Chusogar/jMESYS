package jMESYS.drivers.Sinclair.Spectrum.formats;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import jMESYS.core.cpu.CPU;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.drivers.Sinclair.Spectrum.display.SpectrumDisplay;
import jMESYS.files.FileFormat;
import jMESYS.files.tape.BasicTape;
import jMESYS.files.tape.TapeBlock;
import jMESYS.gui.jMESYSDisplay;

public class FormatTAP extends FileFormat {

	// extension
	private static String strExtension = ".TAP";
	
	// file
	//private BufferedInputStream tapeFile;
	
	private boolean loading, stop_loading;
	private byte[] tape;
	private int tape_blk;
	private int tape_pos;
	private boolean tape_changed = false;
	private boolean tape_ready = false;
	
	private boolean painted = false;
	
	public String getExtension() {
		return strExtension;
	}
	
	private String getCleanMsg(int offset, int len, byte tapeBuffer[]) {
        byte msg[] = new byte[len];

        if (tapeBuffer.length > len) {
	        // Hay que quitar los caracteres especiales
	        for (int car = 0; car < len; car++) {
	            if ((tapeBuffer[offset + car] & 0xff) > 31 && (tapeBuffer[offset + car] & 0xff) < 128) {
	                msg[car] = tapeBuffer[offset + car];
	            } else {
	                msg[car] = '?'; // sustituir el carácter no imprimible
	            }
	        }
        }

        return new String(msg);
    }
	
	public void loadFormat(String name, InputStream is, Spectrum48k computer) throws Exception {
		System.out.println("Load TAP");
		
		byte data[] = null;
		int pos = 0;
		for(;;) try {
			byte buf[] = new byte[pos+512];
			int n = is.read(buf, pos, 512);
			if(n<512) {
				if(n<=0) break;
				byte buf2[] = new byte[pos+n];
				System.arraycopy(buf, pos, buf2, pos, n);
				buf = buf2;
			}
			if(data!=null)
				System.arraycopy(data, 0, buf, 0, pos);
			data = buf; pos += n;
			computer.tape(data, false);
			Thread.yield();
		} catch(IOException e) {
			break;
		}
		if(data != null)
			computer.tape(data, true);
	}
	
	private void load_tape(InputStream in) {
		byte data[] = null;
		int pos = 0;
		for(;;) try {
			byte buf[] = new byte[pos+512];
			int n = in.read(buf, pos, 512);
			if(n<512) {
				if(n<=0) break;
				byte buf2[] = new byte[pos+n];
				System.arraycopy(buf, pos, buf2, pos, n);
				buf = buf2;
			}
			if(data!=null)
				System.arraycopy(data, 0, buf, 0, pos);
			data = buf; pos += n;
			//spectrum.tape(data, false);
			//Thread.yield();
		} catch(IOException e) {
			break;
		}
		if(data != null){
			//spectrum.tape(data, true);
			tape = data;
			System.out.println("DATA: "+tape);
		}
			
	}
	
	private boolean do_load(byte[] tape, boolean ready, CPU cpu) {
		System.out.println("--Do Load--");
		return false;
	}
	
	
	

	
	public String getFileName() {
		
		return "D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/BACKTOSK.TAP";
	}

	@Override
	public Image getScreen(String name, InputStream is, jMESYSDisplay disp, Graphics g) throws Exception {
		// new Method
		BasicTape tape = new BasicTape();
		tape.setName(name);
		
		byte tapeBuffer[] = getArrayFile(is);
		tape.setTapeArray(tapeBuffer);
		tape.setSize(tapeBuffer.length);
		
		getOnlyScreenBlock( tapeBuffer, disp, tape, g );
		
		/*int        header[] = new int[27];
		byte[] arrScr = new byte[256*192];
		byte[] arrAtt = new byte[768];
		
		readBytes( is, header, 0,        27 );
		readBytes( is, arrScr,    0, (32*192));
		readBytes( is, arrAtt, 0, 768);
		
		disp.paintImageScreen(g, arrScr, arrAtt);*/
		
		if (!painted){
        	System.out.println("TAP NO PINTADO");
        	g.setColor(Color.RED);
			g.fillRect(0, 0, disp.getWidth(), disp.getHeight());
        }
		painted=false;
		
		return null;
	}

	private void getOnlyScreenBlock(byte[] tapeBuffer, jMESYSDisplay disp, BasicTape tape, Graphics g) throws Exception {
		
		// offset
		int idx = 0;
		int size = tapeBuffer.length;
		int blockNum = 1;
		
		while ((idx < size) && (!painted)) {
			idx = getIndividualScreenBlock(blockNum, idx, tapeBuffer, disp, tape, g);
			blockNum++;
		}
		
		System.out.println("Finaliza");
		System.out.println(tape.toString());
	}	
	
private int getIndividualScreenBlock(int blockNum, int idx, byte[] tapeBuffer, jMESYSDisplay disp, BasicTape tape, Graphics g) throws Exception {
		
		// cabecera		
		int bytesCabecera=readInt(tapeBuffer, idx, 2);
		idx += 2;
		
		byte        header[] = new byte[bytesCabecera];
		System.arraycopy(tapeBuffer, idx, header, 0, bytesCabecera);
		int iType = (header[0] & 0xff);
		int iCod1 = (header[1] & 0xff);
		
		//System.out.println("iType "+iType);
		//System.out.println("iCod1 "+iCod1);
				
		idx += bytesCabecera;
		
		// creates block object
		TapeBlock tb = new TapeBlock();
		tb.setBlockNumber(blockNum);
		tb.setContent(header);
		// (size-2) --> removing param1 and param2 ( 
		tb.setSize(bytesCabecera - 2);
		//tb.setToStr(getCleanMsg(2, 10, header));
		tb.setToStr(getHeader(iType, iCod1, header));
		tb.setTypeBlock(iType);
		
		tape.addBlock(tb);
		/*System.out.println("--------------------");
		System.out.println(tb.toString());
		System.out.println("--------------------");*/
		
		if ((bytesCabecera >= 6914)&&(bytesCabecera<7000)){
			byte[] arrScr = new byte[32*192];
			byte[] arrAtt = new byte[768];
			
			System.arraycopy(header, 1, arrScr, 0, (32*192));
			System.arraycopy(header, 1+(32*192), arrAtt, 0, 768);
			
			SpectrumDisplay dsp = (SpectrumDisplay) disp;
			
			dsp.paintImageScreen(g, arrScr, arrAtt);
			painted = true;
		}
		
		return idx;
	}

private String getHeader(int type, int cod1, byte[] block) throws Exception {
	String msg = "";
	
	switch (type) {
		case 0:
			if (cod1==0) msg="Program: ";
			else if (cod1 == 1) msg = "Number Array: ";
			else if (cod1 == 2) msg = "Char Array: ";
			else if (cod1 == 3) msg = "Bytes: ";
			
			msg += getCleanMsg(2, 10, block);
		break;
	}
	
	
	
	return msg;
}
}
