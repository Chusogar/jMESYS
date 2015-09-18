package jMESYS.drivers.Sinclair.Spectrum;

import jMESYS.core.cpu.CPU;
import jMESYS.core.cpu.Z80;
import jMESYS.core.sound.SoundPlayer;
import jMESYS.core.sound.SoundUtil;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatSNA;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatTAP;
import jMESYS.drivers.Sinclair.Spectrum.formats.FormatZ80;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class Spectrum48k extends Z80 {
	
	int numInts = 0;
	protected int cycles = 0;
	protected boolean soundON = true;

	/*int tamMem = 65535;
	
	private byte[] mem = new byte[tamMem];*/
	
	/** Handle TAP **/
	private int ear = 0;
	
	/** Handle screen **/
	private SpectrumDisplay display;
	private int newBorder = -7;
	private int oldBorder = 0;
	private int borderWidth = 20;
	
	/** Handle Sound **/
	protected static final int CYCLES_PER_SECOND = 3500000;
	protected static final int AUDIO_TEST        = 0x40000000;
	protected SoundPlayer player = SoundUtil.getSoundPlayer(true);
	protected byte soundByte = 0;
	protected int soundUpdate = 0;
	protected int audioAdd = player.getClockAdder(AUDIO_TEST, CYCLES_PER_SECOND / 4);
	
	/** Handle Keyboard */
	private static final int b4 = 0x10;
	private static final int b3 = 0x08;
	private static final int b2 = 0x04;
	private static final int b1 = 0x02;
	private static final int b0 = 0x01;
	private int _B_SPC  = 0xff;
	private int _H_ENT  = 0xff;
	private int _Y_P    = 0xff;
	private int _6_0    = 0xff;
	private int _1_5    = 0xff;
	private int _Q_T    = 0xff;
	private int _A_G    = 0xff;
	private int _CAPS_V = 0xff;
	
	// interrupts
	private int     interruptCounter = 0;
	public  long    timeOfLastInterrupt = 0;
	public  long    timeOfLastSleep = 0;
	public long 	timeOfLastSample = 0;
	public long 	timeOfLastRefreshedScreen = 0;
	public boolean 	runAtFullSpeed = false;
	//public  double     refreshRate = 0.65;  // refresh screen every 'n' seconds
	public  int     refreshRate = 1;  // refresh screen every 'n' interrupts
	
	// file formats supported
	private FileFormat[] supportedFormats = null;
	
	public Spectrum48k(double clockFrequencyInMHz, jMESYSDisplay disp) {
		super(clockFrequencyInMHz);
		
		// inicializa la memoria
		//initMemory();
		display = (SpectrumDisplay)disp;
		
		// cargamos la ROM
		//System.out.println("MEMO: "+mem.length);
		//loadROM("D:/workspace/jMESYSalpha/bin/bios/Sinclair/Spectrum/ShadowOfTheUnicorn.rom", 0, 16384, false);
		loadROM("D:/workspace/jMESYSalpha/bin/bios/Sinclair/Spectrum/spectrum.rom", 0, 16384, false);
		/*System.out.println("MEMO: "+mem[0]);
		for (int i=0;i<10;i++){
			System.out.print(mem[i]+" ");
		}*/
		
		// iniciamos el sonido
		System.out.println("Init Sound");
		if (soundON) {
			player.play();
		}
		player.writeStereo(AUDIO_TEST, AUDIO_TEST);
				
		// reseteamos la cpu
		reset();
		
		// arrancamos la cpu
		//while (true) {
			//execute();
			
		//}
				
	}
	
	public void cycle() {
		if (timeOfLastSleep == 0)
			timeOfLastSleep=System.currentTimeMillis();
		
		/*if (System.currentTimeMillis() - timeOfLastSleep >= 2580) {
		try {
			  
			Thread.sleep(2580);
			timeOfLastSleep=System.currentTimeMillis();  
	        } catch (Exception e) { }
		}*/
		
		if (soundON) {
			//player.play();
		}
		
		if (((cycles++ & 0x04) == 0) ) {
		//if (((cycles) == 1) ) {
	      //video.cycle();
		  cycles = 0;
	      soundUpdate += audioAdd;
	      if ((soundUpdate & AUDIO_TEST) != 0) {
	    	//player.play();
	        soundUpdate -= AUDIO_TEST;
	        player.writeStereo(soundByte, soundByte);
	        
	      } 
	      //player.stop();
	    }
			
		
	  }
	
	public boolean manageKeyboard(boolean down, int keyPressed, int modifiers){
			//System.out.println("MODS: "+modifiers);
			boolean key = doKey(down, keyPressed, modifiers);
			//resetKeyboard();
			return key;
	}

	
	
	public final int interrupt() {
		//numInts++;
		//System.out.println("Interrupción!!!!!!");
		//System.out.println(mem);
		//System.out.println(mem.length);
		
		interruptCounter++;
		if (timeOfLastRefreshedScreen == 0)
			timeOfLastRefreshedScreen=System.currentTimeMillis();
		
		// temporizador
		timeOfLastInterrupt = System.currentTimeMillis();
		
		if (display != null ){
			if (display.arrayFichero != null){
		
				// Refresco de pantalla
				if ( (interruptCounter % refreshRate) == 0 ) {
				//if ( (System.currentTimeMillis()-timeOfLastRefreshedScreen) > timeOfLastRefreshedScreen ) {
					System.arraycopy(mem, 16384, display.arrayFichero, 0, display.arrayFichero.length);
					display.loadScreen(display.arrayFichero);
					timeOfLastRefreshedScreen = System.currentTimeMillis();
				}
				
				//System.out.println(mem[16384]);
				/*try { 
					Thread.sleep( 500 ); 
				} catch (Exception ignored ) {
					System.out.println(ignored);
				}*/
				
			}
		}
		//display.loadScreen("D:/workspace/jMESYS/bin/screens/WorldSeriesBaseball.scr");
		
		// sound
		//System.out.println("Antes sound");
		/*if ((cycles++ & 0x03) == 0) {
	      //video.cycle();
			//System.out.println("Probando probando...");
	      soundUpdate += audioAdd;
	      if ((soundUpdate & AUDIO_TEST) != 0) {
	    	  //System.out.println("Probando probando...");
	        soundUpdate -= AUDIO_TEST;
	        player.writeMono(soundByte);
	      }
	    }*/

		// Trying to slow to 100%, browsers resolution on the system
		// time is not accurate enough to check every interrurpt. So
		// we check every 4 interrupts.
		if ( (interruptCounter % 4) == 0 ) {
			long durOfLastInterrupt = timeOfLastInterrupt - timeOfLastSample;
			timeOfLastSample = timeOfLastInterrupt;
			if ( !runAtFullSpeed && (durOfLastInterrupt < 40) ) {
				//System.out.println("Paro pausa");
				
				//try { Thread.sleep( 135 - durOfLastInterrupt ); }
				try { Thread.sleep( 114 - durOfLastInterrupt ); }
				catch ( Exception ignored ) {}
				/*try { Thread.sleep( 10 ); }
				catch ( Exception ignored ) {}*/
			}
		}
		
		return super.interrupt();
	}
	
	
	public void setDisplay(jMESYSDisplay disp) {
		System.out.println("setDisplay");
		display = (SpectrumDisplay) disp;
	}

	/*private void initMemory() {
		for (int i = 0 ; i<tamMem ; i++ ){
			mem[i]=0x00;
		}
		
	}*/

	/**
	 * Z80 hardware interface
	 */
	public int inb( int port ) {
		int res = 0xff;
		//System.out.println("Puerto: "+port);

		if ( (port & 0x0001) == 0 ) {
			//System.out.println("Tecla");
			if ( (port & 0x8000) == 0 ) { res &= _B_SPC;}
			if ( (port & 0x4000) == 0 ) { res &= _H_ENT;}
			if ( (port & 0x2000) == 0 ) { res &= _Y_P;}
			if ( (port & 0x1000) == 0 ) { res &= _6_0;}
			if ( (port & 0x0800) == 0 ) { res &= _1_5;}
			if ( (port & 0x0400) == 0 ) { res &= _Q_T;}
			if ( (port & 0x0200) == 0 ) { res &= _A_G;}
			if ( (port & 0x0100) == 0 ) { res &= _CAPS_V;}
			//resetKeyboard();
			//ear = (la_Cinta.valor() ? 0xFF : 0xBF);
		}

		 return (res | (ear & 0x40));
	}
	
	public final void borderPaint() {
		if ( oldBorder == newBorder ) {
			return;
		}
		oldBorder = newBorder;

		if ( borderWidth == 0 ) {
			return;
		}

		/*parentGraphics.setColor( display.Paleta[ newBorder + 8 ] );
		parentGraphics.fillRect( 0, 0,
			(nPixelsWide*display.pixelScale) + borderWidth*2,
			(nPixelsHigh*display.pixelScale) + borderWidth*2 );*/
	}
	
	public void outb( int port, int outByte, int tstates ) {
		if ( (port & 0x0001) == 0 ) {
			newBorder = (outByte & 0x07);
		}
		soundByte = (outByte & 0x10) == 0 ? (byte)0x7f : (byte)0;
	}

	/** Byte access */
	public void pokeb( int addr, int newByte ){
		if ( addr >= (22528+768) ) {
			mem[ addr ] = (byte) newByte;
			return;
		}

		if ( addr < 16384 ) {
			return;
		}

		if ( mem[ addr ] != newByte ) {
			mem[ addr ] = (byte)newByte;
			
			if ((addr >=16384) && (addr <= 22527)) {
				try {
					
					//display.screenPixels[addr-16384]=(byte) newByte;
					//display.plot(addr);
				} catch (Exception e) {
					System.out.println("Error en método PLOT en dirección "+addr);
					e.printStackTrace(System.out);
				}
			}
		}	
	}

	// Word access
	public void pokew( int addr, int word ) {
		byte _mem[] = mem;

		if ( addr >= (22528+768) ) {
			_mem[ addr ] = (byte) (new Integer(word).byteValue() & 0xff);
			if ( ++addr != 65536 ) {
				_mem[ addr ] = (byte) (word >> 8);
			}
			return;
		}

		if ( addr < 16384 ) {
			return;
		}

		int        newByte0 = word & 0xff;
		if ( _mem[ addr ] != newByte0 ) {
			//plot( addr, newByte0 );
			_mem[ addr ] = (byte) newByte0;
		}

		int        newByte1 = word >> 8;
		if ( ++addr != (22528+768) ) { 
			if ( _mem[ addr ] != newByte1 ) {
				//plot( addr, newByte1 );
				_mem[ addr ] = (byte) newByte1;
			}
		}
		else {
			_mem[ addr ] = (byte) newByte1;
		}
	}

	private final void K1( boolean down ) {
		if ( down ) _1_5 &= ~b0; else _1_5 |= b0;
	}
	private final void K2( boolean down ) {
		if ( down ) _1_5 &= ~b1; else _1_5 |= b1;
	}
	private final void K3( boolean down ) {
		if ( down ) _1_5 &= ~b2; else _1_5 |= b2;
	}
	private final void K4( boolean down ) {
		if ( down ) _1_5 &= ~b3; else _1_5 |= b3;
	}
	private final void K5( boolean down ) {
		if ( down ) _1_5 &= ~b4; else _1_5 |= b4;
	}

	private final void K6( boolean down ) {
		if ( down ) _6_0 &= ~b4; else _6_0 |= b4;
	}
	private final void K7( boolean down ) {
		if ( down ) _6_0 &= ~b3; else _6_0 |= b3;
	}
	private final void K8( boolean down ) {
		if ( down ) _6_0 &= ~b2; else _6_0 |= b2;
	}
	private final void K9( boolean down ) {
		if ( down ) _6_0 &= ~b1; else _6_0 |= b1;
	}
	private final void K0( boolean down ) {
		if ( down ) _6_0 &= ~b0; else _6_0 |= b0;
	}


	private final void KQ( boolean down ) {
		if ( down ) _Q_T &= ~b0; else _Q_T |= b0;
	}
	private final void KW( boolean down ) {
		if ( down ) _Q_T &= ~b1; else _Q_T |= b1;
	}
	private final void KE( boolean down ) {
		if ( down ) _Q_T &= ~b2; else _Q_T |= b2;
	}
	private final void KR( boolean down ) {
		if ( down ) _Q_T &= ~b3; else _Q_T |= b3;
	}
	private final void KT( boolean down ) {
		if ( down ) _Q_T &= ~b4; else _Q_T |= b4;
	}

	private final void KY( boolean down ) {
		if ( down ) _Y_P &= ~b4; else _Y_P |= b4;
	}
	private final void KU( boolean down ) {
		if ( down ) _Y_P &= ~b3; else _Y_P |= b3;
	}
	private final void KI( boolean down ) {
		if ( down ) _Y_P &= ~b2; else _Y_P |= b2;
	}
	private final void KO( boolean down ) {
		if ( down ) _Y_P &= ~b1; else _Y_P |= b1;
	}
	private final void KP( boolean down ) {
		if ( down ) _Y_P &= ~b0; else _Y_P |= b0;
	}


	private final void KA( boolean down ) {
		if ( down ) _A_G &= ~b0; else _A_G |= b0;
	}
	private final void KS( boolean down ) {
		if ( down ) _A_G &= ~b1; else _A_G |= b1;
	}
	private final void KD( boolean down ) {
		if ( down ) _A_G &= ~b2; else _A_G |= b2;
	}
	private final void KF( boolean down ) {
		if ( down ) _A_G &= ~b3; else _A_G |= b3;
	}
	private final void KG( boolean down ) {
		if ( down ) _A_G &= ~b4; else _A_G |= b4;
	}

	private final void KH( boolean down ) {
		if ( down ) _H_ENT &= ~b4; else _H_ENT |= b4;
	}
	private final void KJ( boolean down ) {
		if ( down ) _H_ENT &= ~b3; else _H_ENT |= b3;
	}
	private final void KK( boolean down ) {
		if ( down ) _H_ENT &= ~b2; else _H_ENT |= b2;
	}
	private final void KL( boolean down ) {
		if ( down ) _H_ENT &= ~b1; else _H_ENT |= b1;
	}
	private final void KENT( boolean down ) {
		if ( down ) _H_ENT &= ~b0; else _H_ENT |= b0;
	}


	private final void KCAPS( boolean down ) {
		if ( down ) _CAPS_V &= ~b0; else _CAPS_V |= b0;
	}
	private final void KZ( boolean down ) {
		if ( down ) _CAPS_V &= ~b1; else _CAPS_V |= b1;
	}
	private final void KX( boolean down ) {
		if ( down ) _CAPS_V &= ~b2; else _CAPS_V |= b2;
	}
	private final void KC( boolean down ) {
		if ( down ) _CAPS_V &= ~b3; else _CAPS_V |= b3;
	}
	private final void KV( boolean down ) {
		if ( down ) _CAPS_V &= ~b4; else _CAPS_V |= b4;
	}

	private final void KB( boolean down ) {
		if ( down ) _B_SPC &= ~b4; else _B_SPC |= b4;
	}
	private final void KN( boolean down ) {
		if ( down ) _B_SPC &= ~b3; else _B_SPC |= b3;
	}
	private final void KM( boolean down ) {
		if ( down ) _B_SPC &= ~b2; else _B_SPC |= b2;
	}
	private final void KSYMB( boolean down ) {
		if ( down ) _B_SPC &= ~b1; else _B_SPC |= b1;
	}
	private final void KSPC( boolean down ) {
		if ( down ) _B_SPC &= ~b0; else _B_SPC |= b0;
	}
	
	public void resetKeyboard() {
		_B_SPC  = 0xff;
		_H_ENT  = 0xff;
		_Y_P    = 0xff;
		_6_0    = 0xff;
		_1_5    = 0xff;
		_Q_T    = 0xff;
		_A_G    = 0xff;
		_CAPS_V = 0xff;
	}

	public final boolean doKey(boolean down, int ascii, int mods) {
		//System.out.println("DoKey spectrum "+ascii);
		//boolean down = true;
		boolean    CAPS = ((mods & Event.CTRL_MASK) != 0);
		boolean    SYMB = ((mods & Event.META_MASK) != 0);
		boolean   SHIFT = ((mods & Event.SHIFT_MASK) != 0);
		/*boolean CAPS=false;
		boolean SHIFT=false;
		boolean SYMB=false;*/

		// Change control versions of keys to lower case
		if ( (ascii >= 1) && (ascii <= 0x27) && SYMB ) {
			ascii += ('a'-1);
		}

		switch ( ascii ) {
		case 'a':    KA( down );    break;
		case 'b':    KB( down );    break;
		case 'c':    KC( down );    break;
		case 'd':    KD( down );    break;
		case 'e':    KE( down );    break;
		case 'f':    KF( down );    break;
		case 'g':    KG( down );    break;
		case 'h':    KH( down );    break;
		case 'i':    KI( down );    break;
		case 'j':    KJ( down );    break;
		case 'k':    KK( down );    break;
		case 'l':    KL( down );    break;
		case 'm':    KM( down );    break;
		case 'n':    KN( down );    break;
		case 'o':    KO( down );    break;
		case 'p':    KP( down );    break;
		case 'q':    KQ( down );    break;
		case 'r':    KR( down );    break;
		case 's':    KS( down );    break;
		case 't':    KT( down );    break;
		case 'u':    KU( down );    break;
		case 'v':    KV( down );    break;
		case 'w':    KW( down );    break;
		case 'x':    KX( down );    break;
		case 'y':    KY( down );    break;
		case 'z':    KZ( down );    break;
		case '0':    K0( down );    break;
		case '1':    K1( down );    break;
		case '2':    K2( down );    break;
		case '3':    K3( down );    break;
		case '4':    K4( down );    break;
		case '5':    K5( down );    break;
		case '6':    K6( down );    break;
		case '7':    K7( down );    break;
		case '8':    K8( down );    break;
		case '9':    K9( down );    break;
		case ' ':    CAPS = SHIFT;  KSPC( down );  break;

		case 'A':    CAPS = true;   KA( down );    break;
		case 'B':    CAPS = true;   KB( down );    break;
		case 'C':    CAPS = true;   KC( down );    break;
		case 'D':    CAPS = true;   KD( down );    break;
		case 'E':    CAPS = true;   KE( down );    break;
		case 'F':    CAPS = true;   KF( down );    break;
		case 'G':    CAPS = true;   KG( down );    break;
		case 'H':    CAPS = true;   KH( down );    break;
		case 'I':    CAPS = true;   KI( down );    break;
		case 'J':    CAPS = true;   KJ( down );    break;
		case 'K':    CAPS = true;   KK( down );    break;
		case 'L':    CAPS = true;   KL( down );    break;
		case 'M':    CAPS = true;   KM( down );    break;
		case 'N':    CAPS = true;   KN( down );    break;
		case 'O':    CAPS = true;   KO( down );    break;
		case 'P':    CAPS = true;   KP( down );    break;
		case 'Q':    CAPS = true;   KQ( down );    break;
		case 'R':    CAPS = true;   KR( down );    break;
		case 'S':    CAPS = true;   KS( down );    break;
		case 'T':    CAPS = true;   KT( down );    break;
		case 'U':    CAPS = true;   KU( down );    break;
		case 'V':    CAPS = true;   KV( down );    break;
		case 'W':    CAPS = true;   KW( down );    break;
		case 'X':    CAPS = true;   KX( down );    break;
		case 'Y':    CAPS = true;   KY( down );    break;
		case 'Z':    CAPS = true;   KZ( down );    break;

		case '!':    SYMB = true;   K1( down );   break;
		case '@':    SYMB = true;   K2( down );   break;
		case '#':    SYMB = true;   K3( down );   break;
		case '$':    SYMB = true;   K4( down );   break;
		case '%':    SYMB = true;   K5( down );   break;
		case '&':    SYMB = true;   K6( down );   break;
		case '\'':   SYMB = true;   K7( down );   break;
		case '(':    SYMB = true;   K8( down );   break;
		case ')':    SYMB = true;   K9( down );   break;
		case '_':    SYMB = true;   K0( down );   break;

		case '<':    SYMB = true;   KR( down );   break;
		case '>':    SYMB = true;   KT( down );   break;
		case ';':    SYMB = true;   KO( down );   break;
		case '"':    SYMB = true;   KP( down );   break;
		case '^':    SYMB = true;   KH( down );   break;
		case '-':    SYMB = true;   KJ( down );   break;
		case '+':    SYMB = true;   KK( down );   break;
		case '=':    SYMB = true;   KL( down );   break;
		case ':':    SYMB = true;   KZ( down );   break;
		case '£':    SYMB = true;   KX( down );   break;
		case '?':    SYMB = true;   KC( down );   break;
		case '/':    SYMB = true;   KV( down );   break;
		case '*':    SYMB = true;   KB( down );   break;
		case ',':    SYMB = true;   KN( down );   break;
		case '.':    SYMB = true;   KM( down );   break;

		case '[':    SYMB = true;   KY( down );   break;
		case ']':    SYMB = true;   KU( down );   break;
		case '~':    SYMB = true;   KA( down );   break;
		case '|':    SYMB = true;   KS( down );   break;
		case '\\':   SYMB = true;   KD( down );   break;
		case '{':    SYMB = true;   KF( down );   break;
		case '}':    SYMB = true;   KF( down );   break;

		case '\n':
		case '\r':   CAPS = SHIFT; KENT( down );    break;
		case '\t':   CAPS = true; SYMB = true; break;

		case '\b':
		case 127:    CAPS = true; K0( down );    break;

		case Event.F1: CAPS = true; K1( down ); break;
		case Event.F2: CAPS = true; K2( down ); break;
		case Event.F3: CAPS = true; K3( down ); break;
		case Event.F4: CAPS = true; K4( down ); break;
		case Event.F5: CAPS = true; K5( down ); break;
		case Event.F6: CAPS = true; K6( down ); break;
		case Event.F7: CAPS = true; K7( down ); break;
		case Event.F8: CAPS = true; K8( down ); break;
		case Event.F9: CAPS = true; K9( down ); break;
		case Event.F10: CAPS = true; K0( down ); break;
		case Event.F11: CAPS = true; break;
 		case Event.F12: SYMB = true; break;

		case Event.LEFT:    CAPS = SHIFT; K5( down );    break;
		case Event.DOWN:    CAPS = SHIFT; K6( down );    break;
		case Event.UP:      CAPS = SHIFT; K7( down );    break;
		case Event.RIGHT:   CAPS = SHIFT; K8( down );    break;
					
		case Event.END: {
				if ( down ) {
					//resetAtNextInterrupt = true;
				}
				break;
			}
		case '\033': // ESC
		case Event.HOME: {
				if ( down ) {
					//pauseOrResume();
				}
				break;
			}

		default:
			return false;
		}

		KSYMB( SYMB & down );
		KCAPS( CAPS & down );

		return true;
	}

	public void load() {
		System.out.println("Estoy en load");
		//FormatSNA fSNA = new FormatSNA();
		FormatZ80 fZ80 = new FormatZ80();
		//FormatTAP fTAP = new FormatTAP();
		try {
			//String name="D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/BRUCELEE.TAP";
			String name="D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/ADVTACTF.Z80";
			//String name="D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/ShadowOfTheUnicorn.Z80";
			//fZ80.loadFormat("D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/ShadowOfTheUnicorn.Z80", new FileInputStream("D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/ShadowOfTheUnicorn.Z80"), this);
			fZ80.loadFormat(name, new FileInputStream(name), this);
			//fSNA.loadScreen("D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/MIKIE.SNA", this);
			//fSNA.loadFormat(name, new FileInputStream(name), this);
			FileInputStream fi = new FileInputStream(name);
			System.out.println("TAMAÑO="+fi.available());
			//fTAP.loadFormat(name, fi, this);
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		
		/*loadFromURLField();
		display.requestFocus();*/
	}

	private void loadFromURLField() {
		try {
			//pauseOrResume();

			//urlField.hide();
			URL	url = new URL( "file:///workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/MIKIE.SNA" );
			URLConnection snap = url.openConnection();

			InputStream input = snap.getInputStream();
			loadSnapshot( url.toString(), input, snap.getContentLength() );
			input.close();
		}
		catch ( Exception e ) {
			e.printStackTrace(System.out);
		}
	}
	
	public void loadSnapshot( String name, InputStream is, int snapshotLength ) throws Exception {
		// Linux  JDK doesn't always know the size of files
		if ( snapshotLength < 0 ) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			is = new BufferedInputStream( is, 4096 );

			int byteOrMinus1;
			int i;

			for ( i = 0; (byteOrMinus1 = is.read()) != -1; i++ ) {
				os.write( (byte) byteOrMinus1 );
			}

			is = new ByteArrayInputStream( os.toByteArray() ); 
			snapshotLength = i;
		}

		// Crude check but it'll work (SNA is a fixed size)
		if ( (snapshotLength == 49179) ) {
			loadSNA( name, is );
		}
		/*else {
			loadZ80( name, is, snapshotLength );
		}*/

		//refreshWholeScreen();
		resetKeyboard();
	}
	
	public void loadSNA( String name, InputStream is ) throws Exception {
		//startProgress( "Loading " + name, 27+49152 );
		this.haltCPU();
		//System.out.println("PC1="+PC());
		int        header[] = new int[27];

		readBytes( is, header, 0,        27 );
		readBytes( is, mem,    16384, 49152 );
    
		I( header[0] );

		HL( header[1] | (header[2]<<8) );
		DE( header[3] | (header[4]<<8) );
		BC( header[5] | (header[6]<<8) );
		AF( header[7] | (header[8]<<8) );

		exx();
		ex_af_af();

		HL( header[9]  | (header[10]<<8) );
		DE( header[11] | (header[12]<<8) );
		BC( header[13] | (header[14]<<8) );

		IY( header[15] | (header[16]<<8) );
		IX( header[17] | (header[18]<<8) );

		if ( (header[19] & 0x04)!= 0 ) {
			IFF2( true );
		}
		else {
			IFF2( false );
		}

		R( header[20] );

		AF( header[21] | (header[22]<<8) );
		SP( header[23] | (header[24]<<8) );

		switch( header[25] ) {
		case 0:
			IM( IM0 );
			break;
		case 1:
			IM( IM1 );
			break;
		default:
			IM( IM2 );
			break;
		}

		outb( 254, header[26], 0 ); // border
     
		/* Emulate RETN to start */
		IFF1( IFF2() );
		REFRESH( 2 );
		poppc();

		/*if ( urlField != null ) {
			urlField.setText( name );
		}*/
		this.resumeCPU();
		//System.out.println("PC2="+PC());
		this.execute();
	}
	
	private int readBytes(InputStream is, byte[] a, int off, int n) throws Exception {
		try {
			BufferedInputStream bis = new BufferedInputStream( is, n );

			byte buff[] = new byte[ n ];
			int toRead = n;
			while ( toRead > 0 ) {
				int	nRead = bis.read( buff, n-toRead, toRead );
				toRead -= nRead;
				//updateProgress( nRead );
			}

			for ( int i = 0; i < n; i++ ) {
				a[ i+off ] = (byte) ((buff[i]+256)&0xff);
			}

			return n;
		}
		catch ( Exception e ) {
			System.err.println( e );
			e.printStackTrace();
			//stopProgress();
			throw e;
		}
	}

	private int readBytes( InputStream is, int a[], int off, int n ) throws Exception {
		try {
			BufferedInputStream bis = new BufferedInputStream( is, n );

			byte buff[] = new byte[ n ];
			int toRead = n;
			while ( toRead > 0 ) {
				int	nRead = bis.read( buff, n-toRead, toRead );
				toRead -= nRead;
				//updateProgress( nRead );
			}

			for ( int i = 0; i < n; i++ ) {
				a[ i+off ] = (buff[i]+256)&0xff;
			}

			return n;
		}
		catch ( Exception e ) {
			System.err.println( e );
			e.printStackTrace();
			//stopProgress();
			throw e;
		}
	}
	
	public FileFormat[] getSupportedFileFormats() throws Exception {
		if (supportedFormats == null){
			supportedFormats = new FileFormat[] {
				new FormatSNA(),
				new FormatTAP(),
				new FormatZ80()
			};
		}
		
		return supportedFormats;
	}
}
