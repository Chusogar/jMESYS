package jMESYS.core.cpu;

import javax.swing.SwingUtilities;

import jMESYS.core.cpu.z80core.Z80;
import jMESYS.files.FileFormat;

import jMESYS.core.cpu.z80core.Z80.IntMode;
import jMESYS.core.cpu.z80core.Z80operations;

public abstract class jMESYSZ80 extends CPU {
	
	private Z80 z80;
    protected Clock clock;
    private int counter = 32;
    
    //private byte z80Ram[] = new byte[0x10000];
    private byte z80Ports[] = new byte[0x10000];
    private boolean finish = false;
    
    public jMESYSZ80() {
    	super();
        z80 = new Z80(this);
        this.clock = Clock.getInstance();
        setMem(mem);
    }
    
    public int inb(int port) {
    	System.out.println("inPort: "+port+", value: "+(z80Ports[port] & 0xff));
        clock.addTstates(4); // 4 clocks for read byte from bus
        return z80Ports[port] & 0xff;
    }

    public void outb(int port, int value) {
    	System.out.println("outPort: "+port+", value: "+value);
        clock.addTstates(4); // 4 clocks for write byte to bus
        z80Ports[port] = (byte)value;
    }
    

	// jMESYS methods
    public byte[] getMem() {
		
		if (mem==null){
			mem = new byte[65536];
			for (int i=0 ; i<65536 ; i++){
				mem[i]=0;
			}
		}
		
		return mem;
	}
	
	public void setMem(byte[] memo) {
		mem = memo;		
	}

	public int getMemorySize() {
		return mem.length;
	}
	
	public int interrupt() {
		// If not a non-maskable interrupt
		if ( !z80.isIFF1() ) {
			return 0;
		}

		switch( z80.getIM() ) {
		case IM0:
		case IM1:
			z80.push( z80.getRegPC() ); // pushpc();
			z80.setIFF1( false );
			z80.setIFF2( false );
			z80.setRegPC( 56 );
			return 13;
		case IM2:
			z80.push( z80.getRegPC() ); // pushpc();
			z80.setIFF1( false );
			z80.setIFF2( false );
			int t = (z80.getRegI()<<8) | 0x00ff;
			z80.setRegPC( peekw(t) );
			return 19;
		}

		return 0;
	}

	public void setMemorySize(int mSize) {
		mem = new byte[mSize];		
	}

	public void pokeb(int addr, int newByte) {
		//if ((addr & 0xFFFF) == 16384) System.out.println("16384="+newByte);
		mem[ addr & 0xFFFF ] = new Integer(newByte).byteValue();		
	}

	public void pokew(int addr, int word) {
		pokeb( addr, word & 0xff );
		addr++;
		pokeb( addr & 0xffff, word >> 8 );
	}

	public int peekb(int addr) {
		int b= ((byte) mem[ (addr & 0xFFFF) ]) & 0xFF;
		return b;
	}

	public int peekw(int addr) {
		int        t = peekb( addr );
		addr++;
		return t | (peekb( addr & 0xffff ) << 8);
	}

	public void setRegister(String regName, int value) {
		if (regName.equals("I")){
			z80.setRegI(value);
		} else if(regName.equals("HL")){
			z80.setRegHL(value);
		} else if(regName.equals("DE")){
			z80.setRegDE(value);
		} else if(regName.equals("BC")){
			z80.setRegBC(value);
		} else if(regName.equals("AF")){
			z80.setRegAF(value);
		} else if(regName.equals("IX")){
			z80.setRegIX(value);
		} else if(regName.equals("IY")){
			z80.setRegIY(value);
		} else if(regName.equals("R")){
			z80.setRegR(value);
		} else if(regName.equals("SP")){
			z80.setRegSP(value);
		} else if(regName.equals("A")){
			z80.setRegA(value);
		} else if(regName.equals("F")){
			z80.setRegFx(value);
		} else if(regName.equals("C")){
			z80.setRegC(value);
		} else if(regName.equals("B")){
			z80.setRegB(value);
		} else if(regName.equals("L")){
			z80.setRegL(value);
		} else if(regName.equals("H")){
			z80.setRegH(value);
		} else if(regName.equals("PC")){
			z80.setRegPC(value);
		} else if(regName.equals("E")){
			z80.setRegE(value);
		} else if(regName.equals("D")){
			z80.setRegD(value);
		} else {
			System.out.println("Registro "+regName+" no implementado");
		}
	}

	public int getRegister(String regName) {
		int reg = 0;
		
		if (regName.equals("I")){
			reg=z80.getRegI();
		} else if(regName.equals("A")){
			reg=z80.getRegA();
		} else if(regName.equals("F")){
			reg=z80.getRegFx();
		} else if(regName.equals("HL")){
			reg=z80.getRegHL();
		} else if(regName.equals("DE")){
			reg=z80.getRegDE();
		} else if(regName.equals("BC")){
			reg=z80.getRegBC();
		} else if(regName.equals("AF")){
			reg=z80.getRegAF();
		} else if(regName.equals("IX")){
			reg=z80.getRegIX();
		} else if(regName.equals("IY")){
			reg=z80.getRegIY();
		} else if(regName.equals("R")){
			reg=z80.getRegR();
		} else if(regName.equals("SP")){
			reg=z80.getRegSP();
		} else if(regName.equals("PC")){
			reg=z80.getRegPC();
		} else {
			System.out.println("getReg "+regName+" no implementado");
		}
		
		return reg;
	}

	public void pushRegister(String regName) {
		//if (regName.equals("PC")){
			z80.push(getRegister(regName));
		/*/} else {
			System.out.println("push Reg "+regName+" no implementado");
		}
		System.out.println("push Reg "+regName+" no implementado");
		*/
	}

	public void popRegister(String regName) {
		//if (regName.equals("PC")){
			setRegister("PC", z80.pop());
		/*} else {
			System.out.println("pop Reg "+regName+" no implementado");
		}
		System.out.println("pop Reg "+regName+" no implementado");*/
	}

	public int F() {
		return z80.getRegFx();
	}

	public void F(int bite) {
		z80.setRegFx(bite);
		
	}

	public void interruptFF(String iffName, boolean value) {
		if (iffName.equals("IFF1")){
			z80.setIFF1(value);
		} else if (iffName.equals("IFF2")){
			z80.setIFF2(value);
		} else {
			System.out.println("get interruptFF "+iffName+" no implementado");
		}		
	}

	public boolean interruptFF(String iffName) {
		if (iffName.equals("IFF1")){
			return z80.isIFF1();
		} else if (iffName.equals("IFF2")){
			return z80.isIFF2();
		} else {
			System.out.println("get interruptFF "+iffName+" no implementado");
		}
		return false;
	}

	public void setInterruptMode(String interrupt, int value) {
		if (value==0) {
			z80.setIM(IntMode.IM0);
		} else if (value==1) {
			z80.setIM(IntMode.IM1);
		} else if (value==2) {
			z80.setIM(IntMode.IM2);
		}
	}

	/*@Override
	public void setInterrupt(int value) {
		System.out.println("setInterrupt jMESYSz80");
		
	}

	@Override
	public int getInterrupt() {
		System.out.println("getInterrupt jMESYSz80");
		return 0;
	}*/

	public int getInterruptMode(String iffName) {
		if (z80.getIM() == IntMode.IM0) {
			return 0;
		} else if (z80.getIM() == IntMode.IM1) {
			return 1;
		} else if (z80.getIM() == IntMode.IM2) {
			return 2;
		}
		
		return 2;
	}
	
	public void outb(int port, int bite, int tstates) {
		System.out.println("outb "+port);
	}

	public void REFRESH(int t) {
		//z80.setRegR(z80.getRegR() + t);
	}

	public void exx() {
		z80.exx();
	}

	public void ex_af_af() {
		int   t;
		t = z80.getRegAF(); z80.setRegAF( z80.getRegAFx() ); z80.setRegAFx(t);
	}

	/*@Override
	public FileFormat[] getSupportedFileFormats() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}*/
	
	public void reset() {
		System.out.println("RESET jMESYSZ80");
		clock.reset();
		z80.setPinReset();
		z80.reset();
		z80.setRegR(1);
		//z80.ac
	}
	
	public void haltCPU() {
		z80.setHalted(true);
	}
	
	public void resumeCPU() {
		z80.setHalted(false);
	}
	
	public void execute() {
		while (true){
			while (z80.isHalted()){
				try{
			          Thread.sleep(100);			         
			          	
			        }catch(Exception ex){
			          //ex.printStackTrace();
			        }
			}
			
			/*if (resetPending) {
	            doReset();
	            if (autoLoadTape) {
	                doAutoLoadTape();
	            }
	        }*/

			if (clock.getTstates() < 32) {
        		z80.setINTLine(true);
        	//z80.interruption();
        	//clock.setTstates(0);
        	}
        	z80.execute();
        	if (clock.getTstates() >= 32) {
        		z80.setINTLine(false);
        	//z80.interruption();
        	//clock.setTstates(0);
        	}
        	
        	
        //} while (!(clock.getTstates()>31));
        //z80.setINTLine(true);
        //System.out.println("2: " + clock.getTstates());
        //z80.interruption();
        
        //paintScreen();
        if (clock.getTstates()>=4000){
        	//z80.setINTLine(false);
	        clock.setTstates(0);
        }   
	        
		}
	}
	
	public String getCoreVersion() {
		return "Z80 CORE";
	}
	
	/*@Override
	public void interruption() {
		System.out.println("INTERRUPT jMESYSZ80");
		z80.interruption();
		//return 2;
	}*/
}
