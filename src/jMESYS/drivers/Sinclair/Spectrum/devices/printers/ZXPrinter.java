package jMESYS.drivers.Sinclair.Spectrum.devices.printers;

import java.awt.Color;
import java.awt.Frame;

import jMESYS.core.devices.printer.jMESYSPrinterFrame;
import jMESYS.drivers.jMESYSComputer;

public class ZXPrinter extends jMESYSPrinterFrame {
	
	private int printerByte=0;
	private int ivX = 0;
	
	private Color colorPaper=Color.WHITE;
	private Color colorInk=Color.BLACK;
	
	private Color[] line;
	
	private static String PRINTER_NAME = "ZX Printer";
	public int portNumber = 0xFB;
	
	// paper size (in pixels)
	public int PAPER_LENGTH = 100;
	public int PAPER_WIDTH = 256;
	
	
	public ZXPrinter(Frame frame) {
		super(frame, PRINTER_NAME);
		line = new Color[PAPER_WIDTH];
	}
	
	public int getPortNumber(){
		return portNumber;
	}

	public void out(int port, int v) {
		//System.out.println("PRINTER OUT "+v);
		
		printerByte = v;
		
		if ((v & 0x04) == 0) {
			// Motor is on
			if (ivX < PAPER_WIDTH) {
				// Pixelposition within line < 256
				if ((v & 0x80) != 0) {
					line[ivX] = colorInk;
					//System.out.print("1 ");
				} else {
					line[ivX] = colorPaper;
					//System.out.print("0 ");
				}
				
			}
		}
	}

	public int in(int port) {
		int v=0x3C;
		
		if ((port & 0x04) == 0x00) {
			
			ivX++;
			
			// Check if end of line is reached
			if (ivX >= 260) {
				// perform carriage return
				ivX = 0;

				plotLine(line);
				carriageReturn();
				
				//System.out.println("");
				
			}

			// Check if end of printable area is reached
			if (ivX >= PAPER_WIDTH) {
				// End reached, signal stylus
				v |= 0x80;					
			} else {
				// Within printable area, signal sync
				v |= 0x01;					
			}
			
			
			
		} else {
			v = printerByte;
		}
		
		//System.out.println("PRINTER IN-->"+v);
		
		return v;
	}

	public int getPaperLength() {
		return PAPER_LENGTH;
	}

	public int getPaperWidth() {
		return PAPER_WIDTH;
	}

	public String getPrinterName() {
		return PRINTER_NAME;
	}

	@Override
	public boolean connectDevice(jMESYSComputer computer) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

		
}
