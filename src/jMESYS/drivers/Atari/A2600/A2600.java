package jMESYS.drivers.Atari.A2600;

import java.awt.Frame;

import jMESYS.core.devices.printer.jMESYSPrinterFrame;
import jMESYS.core.sound.cards.jMESYS_SoundCard;
import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.Atari.A2600.display.A2600Display;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public class A2600 extends jMESYSComputer {
	
	// display
	A2600Display display = null;
	int width = 0;
	int height=0;

	public A2600(int mode) {
		super("Atari 2600");

		display = new A2600Display(width, height);		
	}

	@Override
	public int m1(int pc, int mr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int mem(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void mem(int addr, int v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int in(int port) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void out(int port, int v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int mem16(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void mem16(int addr, int v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh_new() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public jMESYSDisplay getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public jMESYS_SoundCard getAudioDevice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCPUtime(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCPUtime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCPUtimeLimit(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCPUtimeLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void executeCPU() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FileFormat[] getSupportedFileFormats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void end_frame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update_keyboard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public jMESYSPrinterFrame getPrinter(Frame frame) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean check_load() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean do_load(byte[] tape, boolean ready) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setModel(int iModel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadRoms() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nmi() {
		// TODO Auto-generated method stub
		
	}

}
