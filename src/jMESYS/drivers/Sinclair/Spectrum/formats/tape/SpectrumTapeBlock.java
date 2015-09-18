package jMESYS.drivers.Sinclair.Spectrum.formats.tape;

import jMESYS.files.tape.TapeBlock;

public class SpectrumTapeBlock extends TapeBlock {
	
	// block types
	public final int _HEADER_ 	= 0;
	public final int _CODE_ 	= 1;
	
	// header types
	public final int _HEADER_PROGRAM_ 		= 0;
	public final int _HEADER_NUMBER_ARRAY_ 	= 1;
	public final int _HEADER_CHAR_ARRAY 	= 2;
	public final int _HEADER_CODE_ 			= 3;
	
	// parameters (2 bytes)
	private int parameter1 = 0;
	private int parameter2 = 0;
}
