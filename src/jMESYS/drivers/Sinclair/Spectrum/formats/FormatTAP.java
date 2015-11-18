package jMESYS.drivers.Sinclair.Spectrum.formats;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.InputStream;

import jMESYS.core.cpu.CPU;
import jMESYS.drivers.Sinclair.Spectrum.SpectrumDisplay;
import jMESYS.files.FileFormat;
import jMESYS.files.tape.BasicTape;
import jMESYS.files.tape.TapeBlock;
import jMESYS.gui.jMESYSDisplay;

public class FormatTAP extends FileFormat {

	// extension
	private static String strExtension = ".TAP";
	
	// file
	//private BufferedInputStream tapeFile;
	
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
	


	@Override
	public void loadFormat(String name, InputStream is, CPU cpu) throws Exception {
		
		// new Method
		BasicTape tape = new BasicTape();
		tape.setName(name);
		
		byte tapeBuffer[] = getArrayFile(is);
		tape.setTapeArray(tapeBuffer);
		tape.setSize(tapeBuffer.length);
		
		getAllBlocks( tapeBuffer, cpu, tape );
		
	}
	
	private void getAllBlocks(byte[] tapeBuffer, CPU cpu, BasicTape tape) throws Exception {
		
		// offset
		int idx = 0;
		int size = tapeBuffer.length;
		int blockNum = 1;
		
		while (idx < size) {
			idx = getIndividualBlock(blockNum, idx, tapeBuffer, cpu, tape);
			blockNum++;
		}
		
		System.out.println(tape.toString());
		
	}
	
	public void loadBlock1(CPU cpu) throws Exception {
		//cpu.inb();
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
		
		if ((bytesCabecera >= 6914)&&(bytesCabecera<7000)){
			byte[] arrScr = new byte[32*192];
			byte[] arrAtt = new byte[768];
			
			System.arraycopy(header, 1, arrScr, 0, (32*192));
			System.arraycopy(header, 1+(32*192), arrAtt, 0, 768);
			
			SpectrumDisplay dsp = (SpectrumDisplay) disp;
			
			dsp.paintImageScreen(g, arrScr, arrAtt);
		}
		
		return idx;
	}

	private int getIndividualBlock(int blockNum, int idx, byte[] tapeBuffer, CPU cpu, BasicTape tape) throws Exception {
		//System.out.println("Bloque "+blockNum);
		//System.out.println("Offset "+idx);
		
		
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
		
		//System.out.println(tb);
		if (blockNum==5){
			System.out.println(bytesCabecera);
			int param1 = (readInt(header, (bytesCabecera-4), 2) & 0xFFFF);
			System.out.println("Par1: "+param1);
			int param2 = (readInt(header, (bytesCabecera-2), 2) & 0xFFFF);
			System.out.println("Par2: "+param2);
		}
		
		if ((bytesCabecera >= 6914)&&(bytesCabecera<7000)){
			System.arraycopy(header, 2, cpu.getMem(), 16384, bytesCabecera-2);
		}
		
		return idx;
	}

	private byte[] getArrayFile( InputStream is ) throws Exception {
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
	
	/*private void getBlocks(byte[] tapeBuffer, CPU cpu, BasicTape tape) throws Exception {
		
		int idx = 2;
		
		System.out.println("Cabecera="+(new String(tapeBuffer)));
		
		// cabecera
		int bytesCabecera=readInt(tapeBuffer, 0, 2);
		System.out.println("Longitud Cabecera="+bytesCabecera);
		byte        header[] = new byte[bytesCabecera];
		System.arraycopy(tapeBuffer, 2, header, 0, bytesCabecera);
		int iType = (header[0] & 0xff);
		int iCod1 = (header[1] & 0xff);
				
		
		//readBytes(is, header, 0, bytesCabecera);
		System.out.println("Cabecera="+(new String(header)));		
		System.out.println("Type="+iType);
		System.out.println("Cod1="+iCod1);
		System.out.println("Dato="+getCleanMsg(2, 10, header));
		System.out.println("Dato screen: "+header[0]);
		System.out.println(getHeader(iType, iCod1, header));
		
		idx += bytesCabecera;
		System.out.println("IDX="+idx);
		
		// creates block object
		TapeBlock tb = new TapeBlock();
		tb.setBlockNumber(1);
		tb.setContent(header);
		tb.setSize(bytesCabecera);
		tb.setToStr(getCleanMsg(2, 10, header));
		tb.setTypeBlock(iType);
		
		tape.addBlock(tb);
		
		// bloque 2
		int bytesBlk2=readInt(tapeBuffer, 0+idx, 2);
		idx += 2;
		byte        headerBlk2[] = new byte[bytesBlk2];
		System .out.println("Longitud Blok2="+bytesBlk2);
		//readBytes(is, headerBlk2, 0, bytesBlk2);
		System.arraycopy(tapeBuffer, 0+idx, headerBlk2, 0, bytesBlk2);
		//System.out.println("Cabecera="+(new String(headerBlk2)));
		System.out.println("Blok2="+(new String(headerBlk2)));
		iType = (headerBlk2[0] & 0xff);
		iCod1 = (headerBlk2[1] & 0xff);
		//System.out.println("Type="+iType);
		//System.out.println("Cod1="+iCod1);
		//System.out.println("Dato="+getCleanMsg(2, 10, headerBlk2));
		System.out.println("Dato screen: "+headerBlk2[0]);
		System.out.println(getHeader(iType, iCod1, headerBlk2));
		idx += bytesBlk2;
		System.out.println("IDX="+idx);
		
		// creates block object
		tb = new TapeBlock();
		tb.setBlockNumber(2);
		tb.setContent(headerBlk2);
		tb.setSize(bytesBlk2);
		tb.setToStr(getCleanMsg(2, 10, headerBlk2));
		tb.setTypeBlock(iType);
		
		tape.addBlock(tb);
		
		// bloque 3
		int bytesBlk3=readInt(tapeBuffer, 0+idx, 2);
		idx += 2;
		byte        headerBlk3[] = new byte[bytesBlk3];
		System .out.println("Longitud Blok3="+bytesBlk3);
		System.arraycopy(tapeBuffer, 0+idx, headerBlk3, 0, bytesBlk3);
		System.out.println("Cabecera="+(new String(headerBlk3)));
		iType = (headerBlk3[0] & 0xff);
		iCod1 = (headerBlk3[1] & 0xff);
		//System.out.println("Type="+iType);
		//System.out.println("Cod1="+iCod1);
		//System.out.println("Dato="+getCleanMsg(2, 10, headerBlk3));
		System.out.println("Dato screen: "+headerBlk3[0]);
		System.out.println(getHeader(iType, iCod1, headerBlk3));
		idx += bytesBlk3;
		System.out.println("IDX="+idx);
		
		// creates block object
		tb = new TapeBlock();
		tb.setBlockNumber(3);
		tb.setContent(headerBlk3);
		tb.setSize(bytesBlk3);
		tb.setToStr(getCleanMsg(2, 10, headerBlk3));
		tb.setTypeBlock(iType);
		
		tape.addBlock(tb);
		
		// bloque 4 - screen
		int bytesBlk4=readInt(tapeBuffer, 0+idx, 2);
		idx += 2;
		byte        headerBlk4[] = new byte[bytesBlk4];
		System .out.println("Longitud Blok4="+bytesBlk4);
		//readBytes(is, headerBlk4, 0, bytesBlk4);
		System.arraycopy(tapeBuffer, 0+idx, headerBlk4, 0, bytesBlk4);
		//System.out.println("Blok4="+(new String(headerBlk4)));
		iType = (headerBlk4[0] & 0xff);
		iCod1 = (headerBlk4[1] & 0xff);
		System.out.println("Type="+iType);
		System.out.println("Cod1="+iCod1);
		//System.out.println("Dato="+getCleanMsg(2, 10, headerBlk4));
		System.out.println(getHeader(iType, iCod1, headerBlk4));
		System.out.println("Dato screen: "+headerBlk4[0]);
		System.arraycopy(headerBlk4, 1, cpu.getMem(),    16384, bytesBlk4-1);
		idx += bytesBlk4;
		
		// creates block object
		tb = new TapeBlock();
		tb.setBlockNumber(4);
		tb.setContent(headerBlk4);
		tb.setSize(bytesBlk4);
		tb.setToStr(getCleanMsg(2, 10, headerBlk4));
		tb.setTypeBlock(iType);
		
		tape.addBlock(tb);
						
		// bloque 5
		int bytesBlk5=readInt(tapeBuffer, 0+idx, 2);
		idx += 2;
		byte        headerBlk5[] = new byte[bytesBlk5];
		System .out.println("Longitud Blok5="+bytesBlk5);
		//readBytes(is, headerBlk5, 0, bytesBlk5);
		System.arraycopy(tapeBuffer, 0+idx, headerBlk5, 0, bytesBlk5);
		//System.out.println("Blok5="+(new String(headerBlk5)));
		iType = (headerBlk5[0] & 0xff);
		iCod1 = (headerBlk5[1] & 0xff);
		//System.out.println("Type="+iType);
		//System.out.println("Cod1="+iCod1);
		//System.out.println("Dato="+getCleanMsg(2, 10, headerBlk5));
		System.out.println(getHeader(iType, iCod1, headerBlk5));
		idx += bytesBlk5;
		
		// creates block object
		tb = new TapeBlock();
		tb.setBlockNumber(5);
		tb.setContent(headerBlk5);
		tb.setSize(bytesBlk5);
		tb.setToStr(getCleanMsg(2, 10, headerBlk5));
		tb.setTypeBlock(iType);
		
		tape.addBlock(tb);
		
		System.out.println("IDX="+idx);
		System.out.println("TAM="+tapeBuffer.length);
		
		
		// Final Fichero
		System .out.println("FIN");
		
		System.out.println("OBJETO");
		System.out.println( tape.toString() );
		
	}*/

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
		
		return null;
	}

	private void getOnlyScreenBlock(byte[] tapeBuffer, jMESYSDisplay disp, BasicTape tape, Graphics g) throws Exception {
		
		// offset
		int idx = 0;
		int size = tapeBuffer.length;
		int blockNum = 1;
		
		while (idx < size) {
			idx = getIndividualScreenBlock(blockNum, idx, tapeBuffer, disp, tape, g);
			blockNum++;
		}
		
		System.out.println("Finaliza");
		System.out.println(tape.toString());
	}

}
