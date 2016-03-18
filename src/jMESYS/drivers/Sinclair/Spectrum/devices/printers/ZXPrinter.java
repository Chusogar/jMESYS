package jMESYS.drivers.Sinclair.Spectrum.devices.printers;

import java.awt.Color;
import java.awt.Frame;

import jMESYS.core.devices.printer.jMESYSPrinterFrame;
import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.Sinclair.Spectrum.SpectrumModels;

public class ZXPrinter extends jMESYSPrinterFrame {
	
	//private int printerByte=0;
	private int ivX = 0;
	private int cont = 0;
	
	private Color colorPaper=Color.WHITE;
	private Color colorInk=Color.BLACK;
	
	private Color[] line;
	
	private static String PRINTER_NAME = "ZX Printer";
	public int portNumber = 0xFB;
	
	// paper size (in pixels)
	public int PAPER_LENGTH = 100;
	public int PAPER_WIDTH = 256;
	
	public static final int[] compatibleSystems = {
			SpectrumModels.MODE_48K,
			SpectrumModels.MODE_128K,
			SpectrumModels.MODE_PLUS2,
			SpectrumModels.MODE_PLUS3,
			SpectrumModels.MODE_PENTAGON,
			SpectrumModels.MODE_PENT_SP
	};
	
	
	public ZXPrinter(Frame frame) {
		super(frame, PRINTER_NAME);
		line = new Color[PAPER_WIDTH];
	}
	
	public int getPortNumber(){
		return portNumber;
	}

	public void out(int port, int v) {
		//System.out.println("PRINTER OUT "+v);
		
		//printerByte = v;
		
		if ((v & 0x04) == 0) {
			// Motor is on
			if (ivX < 256) {
				//System.out.println("CONT="+cont);
				// Pixelposition within line < 256
				if ((v & 0x80) != 0) {
					line[cont] = colorInk;
					//System.out.print(Character.toString ((char) 254));
				} else {
					line[cont] = colorPaper;
					//System.out.print(" ");
				}
				cont++;
				if (cont>=256){
					cont=0;
				}
			}
		}
	}

	public int in(int port) {
		int v=0x3C;
		
		//if ((port & 0x04) == 0x00) {
			
			ivX++;
			
			// Check if end of line is reached
			if (ivX >= 260) {
				// perform carriage return
				//System.out.println("X "+ivX);
				ivX = 0;
				
				/*int tamLine=line.length;
				for(int i=0;i<tamLine;i++){
					if (line[i]==Color.BLACK){
						System.out.print("X");
					} else {
						System.out.print(" ");
					}
				}
				System.out.println();*/
				cont=0;
				plotLine(line);
				carriageReturn();
				
				
				
			}

			// Check if end of printable area is reached
			if (ivX >= 256) {
				// End reached, signal stylus
				v |= 0x80;
				
			} else {
				// Within printable area, signal sync
				v |= 0x01;					
			}
			
			
			
		//} 
		
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

	public int[] getCompatibleSystems() {		
		return compatibleSystems;
	}

	public boolean isCompatible(int sysID) {
		int iNum = compatibleSystems.length;
		boolean compat = false;
		
		for (int i=0 ; i<iNum ; i++){
			if (sysID == compatibleSystems[i])
				compat = true;
		}
		
		return compat;
	}	
}
