package jMESYS.drivers.Sinclair.Spectrum.formats;

import java.awt.Graphics;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
//import java.io.FileInputStream;
import java.io.InputStream;

import jMESYS.core.cpu.CPU;
import jMESYS.drivers.Sinclair.Spectrum.SpectrumDisplay;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public class FormatZ80 extends FileFormat {
	
	// extension
	private static String strExtension = ".Z80";
	
	int PC = 0;

	public String getExtension() {		
		return strExtension;
	}
	
	public byte[] loadFormatScreen(String name, InputStream is) throws Exception {
		/*int        header[] = new int[30];
		boolean    compressed = false;
				
		File fis = new File(name);
		
		int bytesLeft = (new Long(fis.length())).intValue();

		bytesLeft -= readBytes( is, header, 0, 30 );


		PC = header[6] | (header[7]<<8);*/

		byte[] b = new byte[1];
        int j = 0;
        byte[] memo = new byte[6912];
        for (int i=0 ; i<6144 ; i++){
			memo[i]=0;
		}
        
		is.skip(6);
        j = is.read(b, 0, 1);
        j = ((b[0] + 256) & 0xff);
        is.read(b, 0, 1);
        j = j | (((b[0] + 256) & 0xff) << 8);
        if(j != 0){
          is.skip(4);
          j = is.read(b, 0, 1);
          j = ((b[0] + 256) & 0xff);
          if(j == 255){
            j = 1;
          }
          is.skip(17);
          if((j & 0x20) == 0){
            for(int i = 0; i < 6912; i++){
              j = is.read(b, 0, 1);
              j = ((b[0] + 256) & 0xff);
              memo[i] = ((byte) (j & 0xff));
            }
            System.out.println("        Nombre  =>  " + name + "    Tipo  =>  .Z80");
            System.out.println("        Version  <=  1.45");
          }else{
            int i = 0;
            while(i < 6912){
              j = is.read(b, 0, 1);
              j = ((b[0] + 256) & 0xff);
              memo[i] = ((byte) (j & 0xff));
              if(j != 0xed){
            	memo[i] = ((byte) (j & 0xff));
                i++;
              }else{
                j = is.read(b, 0, 1);
                j = ((b[0] + 256) & 0xff);
                if(j != 0xed){
                	memo[i] = ((byte) (j & 0xed));
                  i++;
                  is.skip(-1);
                }else{
                  int aux2;
                  j = is.read(b, 0, 1);
                  j = ((b[0] + 256) & 0xff);
                  aux2 = j;
                  j = is.read(b, 0, 1);
                  j = ((b[0] + 256) & 0xff);
                  while((aux2--) != 0){
                	memo[i] = ((byte) (j & 0xff));
                    i++;
                  }
                }
              }
            }
            System.out.println("        Nombre  =>  " + name + "    Tipo  =>  .Z80");
            System.out.println("        Version  <=  1.45" + "    Comprimido");
          }
        }else{
          is.skip(22);
          j = is.read(b, 0, 1);
          j = ((b[0] + 256) & 0xff);
          is.read(b, 0, 1);
          j = j | (((b[0] + 256) & 0xff) << 8);
          String S = "        Nombre  =>  " + name + "    Tipo  =>  .Z80";
          System.out.println(S);
          S = "        Version  ";
          switch(j){
            case 23:{ S = S.concat("==  2.01"); break;}
            case 54:{ S = S.concat("==  3.00"); break;}
            case 58:{ S = S.concat("==  3.01"); break;}
            default:{ S = S.concat(">=  3.0x"); break;}
          }
          is.read(b, 0, 1);
          is.read(b, 0, 1);
          is.read(b, 0, 1);
          int kk = ((b[0] + 256) & 0xff);
          if(j == 23){
            switch(kk){
              case 0:{  S = S.concat("    Modelo  =>  48k");  break;}
              case 1:{  S = S.concat("    Modelo  =>  48k + If.1");  break;}
              case 2:{  S = S.concat("    Modelo  =>  SamRam");  break;}
              case 3:{  S = S.concat("    Modelo  =>  128k");  break;}
              case 4:{  S = S.concat("    Modelo  =>  128k + If.1");  break;}
              default:{  S = S.concat("    Modelo  =>  Desconocido");  break;}
            }
          }else{
            switch(kk){
              case 0:{  S = S.concat("    Modelo  =>  48k");  break;}
              case 1:{  S = S.concat("    Modelo  =>  48k + If.1");  break;}
              case 2:{  S = S.concat("    Modelo  =>  48k + M.G.T.");  break;}
              case 3:{  S = S.concat("    Modelo  =>  SamRam");  break;}
              case 4:{  S = S.concat("    Modelo  =>  128k");  break;}
              case 5:{  S = S.concat("    Modelo  =>  128k + If.1");  break;}
              case 6:{  S = S.concat("    Modelo  =>  128k + M.G.T.");  break;}
              default:{  S = S.concat("    Modelo  =>  Desconocido");  break;}
            }
          }
          System.out.println(S);
          is.skip(j - 3);
          is.read(b, 0, 1);
          int lon = ((b[0] + 256) & 0xff);
          is.read(b, 0, 1);
          lon |= (((b[0] + 256) & 0xff) << 8);
          is.read(b, 0, 1);
          kk = ((b[0] + 256) & 0xff);
          while(kk != 8){
            is.skip(lon);
            is.read(b, 0, 1);
            lon = ((b[0] + 256) & 0xff);
            is.read(b, 0, 1);
            lon |= (((b[0] + 256) & 0xff) << 8);
            is.read(b, 0, 1);
            kk = ((b[0] + 256) & 0xff);
          }
          int i = 0;
          while(i < 6912){
            j = is.read(b, 0, 1);
            j = ((b[0] + 256) & 0xff);
            memo[i] = ((byte) (j & 0xff));
            if(j != 0xed){
            	memo[i] = ((byte) (j & 0xff));
              i++;
            }else{
              j = is.read(b, 0, 1);
              j = ((b[0] + 256) & 0xff);
              if(j != 0xed){
            	memo[i] = ((byte) (j & 0xed));
                i++;
                is.skip(-1);
              }else{
                int aux2;
                j = is.read(b, 0, 1);
                j = ((b[0] + 256) & 0xff);
                aux2 = j;
                j = is.read(b, 0, 1);
                j = ((b[0] + 256) & 0xff);
                try{
                  while((aux2--) != 0){
                	  memo[i] = ((byte) (j & 0xff));
                    i++;
                  }
                }catch(Exception exc){
                  exc.printStackTrace(System.out);
                }
              }
            }
          }
        }
        
        return memo;
	}
	
	
	public void loadFormat(String name, InputStream is, CPU cpu) throws Exception {
		int        header[] = new int[30];
		boolean    compressed = false;
		
		File fis = new File(name);
		int bytesLeft = (new Long(fis.length())).intValue();
		
		/*ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] databuf = new byte[16384];

		while ((nRead = bis.read(databuf, 0, databuf.length)) != -1) {
		  buffer.write(databuf, 0, nRead);
		}

		buffer.flush();
		byte[] buf = buffer.toByteArray();
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		int bytesLeft = buf.length;*/
		//int bytesLeft = is.available();
		System.out.println(bytesLeft);

		bytesLeft -= readBytes( is, header, 0, 30 );

		cpu.setRegister("A", header[0] );
		cpu.setRegister("F", header[1] );
     
		cpu.setRegister("C", header[2] );
		cpu.setRegister("B", header[3] );
		cpu.setRegister("L", header[4] );
		cpu.setRegister("H", header[5] );

		cpu.setRegister("PC", header[6] | (header[7]<<8) );
		cpu.setRegister("SP", header[8] | (header[9]<<8) );

		cpu.setRegister("I", header[10] );
		cpu.setRegister("R", header[11] );

		int tbyte = header[12];
		if ( tbyte == 255 ) {
			tbyte = 1;
		}

		cpu.outb( 254, ((tbyte >> 1) & 0x07), 0 ); // border

		if ( (tbyte & 0x01) != 0 ) {
			cpu.setRegister("R", cpu.getRegister("R") | 0x80 );
		}
		compressed = ((tbyte & 0x20) != 0);
     
		cpu.setRegister("E", header[13] );
		cpu.setRegister("D", header[14] );

		cpu.ex_af_af();
		cpu.exx();

		cpu.setRegister("C", header[15] );
		cpu.setRegister("B", header[16] );
		cpu.setRegister("E", header[17] );
		cpu.setRegister("D", header[18] );
		cpu.setRegister("L", header[19] );
		cpu.setRegister("H", header[20] );

		cpu.setRegister("A", header[21] );
		cpu.setRegister("F", header[22] );

		cpu.ex_af_af();
		cpu.exx();

		cpu.setRegister("IY", header[23] | (header[24]<<8) );
		cpu.setRegister("IX", header[25] | (header[26]<<8) );

		cpu.interruptFF("IFF1", (header[27] != 0) );
		cpu.interruptFF("IFF2", header[28] != 0 );

		switch ( header[29] & 0x03 ) {
		case 0:
			cpu.setInterruptMode(null, 0 );
			break;
		case 1:
			cpu.setInterruptMode(null, 1 );
			break;
		default:
			cpu.setInterruptMode(null, 2 );
			break;
		}

		if ( cpu.getRegister("PC") == 0 ) {
			loadZ80_extended( is, bytesLeft, cpu );

			return;
		}

		/* Old format Z80 snapshot */
    
		if ( compressed ) {
			int data[] = new int[ bytesLeft ];
			int addr   = 16384;

			int size = readBytes( is, data, 0, bytesLeft );
			int i    = 0;

			while ( (addr < 65536) && (i < size) ) {
				tbyte = data[i++];
				if ( tbyte != 0xed ) {
					cpu.pokeb( addr, tbyte );
					addr++;
				}
				else {
					tbyte = data[i++];
					if ( tbyte != 0xed ) {
						cpu.pokeb( addr, 0xed );
						i--;
						addr++;
					}
					else {
						int        count;
						count = data[i++];
						tbyte = data[i++];
						while ( (count--) != 0 ) {
							cpu.pokeb( addr, tbyte );
							addr++;
						}
					}
				}
			}
		}
		else {
			readBytes( is, cpu.getMem(), 16384, 49152 );
		}

		
	}

	private void loadZ80_extended(InputStream is, int bytesLeft, CPU cpu) throws Exception {
		int header[] = new int[2];
		bytesLeft -= readBytes( is, header, 0, header.length );

		int type = header[0] | (header[1] << 8);

		switch( type ) {
		case 23: /* V2.01 */
			loadZ80_v201( is, bytesLeft, cpu );
			break;
		case 54: /* V3.00 */
			loadZ80_v300( is, bytesLeft, cpu );
			break;
		case 58: /* V3.01 */
			loadZ80_v301( is, bytesLeft, cpu );
			break;
		default:
			throw new Exception( "Z80 (extended): unsupported type " + type );
		}
	}

	private void loadZ80_v201(InputStream is, int bytesLeft, CPU cpu) throws Exception {
		int header[] = new int[23];
		bytesLeft -= readBytes( is, header, 0, header.length );

		cpu.setRegister("PC", header[0] | (header[1]<<8) );

		/* 0 - 48K
		 * 1 - 48K + IF1
		 * 2 - SamRam
		 * 3 - 128K
		 * 4 - 128K + IF1
		 */
		int type = header[2];
	
		if ( type > 1 ) {
			throw new Exception( "Z80 (v201): unsupported type " + type );
		}
		
		int data[] = new int[ bytesLeft ];
		readBytes( is, data, 0, bytesLeft );

		for ( int offset = 0, j = 0; j < 3; j++ ) {
			offset = loadZ80_page( data, offset, cpu );
		}
	}

	private void loadZ80_v300( InputStream is, int bytesLeft, CPU cpu ) throws Exception {
		int        header[] = new int[54];
		bytesLeft -= readBytes( is, header, 0, header.length );

		cpu.setRegister("PC", header[0] | (header[1]<<8) );

		/* 0 - 48K
		 * 1 - 48K + IF1
		 * 2 - 48K + MGT
		 * 3 - SamRam
		 * 4 - 128K
		 * 5 - 128K + IF1
		 * 6 - 128K + MGT
		 */
		int type = header[2];
		
		//System.out.println(type);
		//System.out.println(bytesLeft);
	
		if ( type > 6 ) {
			throw new Exception( "Z80 (v300): unsupported type " + type );
		}
		
		int data[] = new int[ bytesLeft ];
		readBytes( is, data, 0, bytesLeft );

		for ( int offset = 0, j = 0; j < 3; j++ ) {
			offset = loadZ80_page( data, offset, cpu );
		}
	}

	private void loadZ80_v301( InputStream is, int bytesLeft, CPU cpu ) throws Exception {
		int        header[] = new int[58];
		bytesLeft -= readBytes( is, header, 0, header.length );

		cpu.setRegister("PC", header[0] | (header[1]<<8) );

		/* 0 - 48K
		 * 1 - 48K + IF1
		 * 2 - 48K + MGT
		 * 3 - SamRam
		 * 4 - 128K
		 * 5 - 128K + IF1
		 * 6 - 128K + MGT
		 * 7 - +3
		 */
		int type = header[2];
	
		if ( type > 7 ) {
			throw new Exception( "Z80 (v301): unsupported type " + type );
		}
		
		int data[] = new int[ bytesLeft ];
		readBytes( is, data, 0, bytesLeft );

		for ( int offset = 0, j = 0; j < 3; j++ ) {
			offset = loadZ80_page( data, offset, cpu );
		}
	}

	private int loadZ80_page( int data[], int i, CPU cpu ) throws Exception {
		int blocklen;
		int page;

		blocklen  = data[i++];
		blocklen |= (data[i++]) << 8;
		page = data[i++];

		int addr;
		switch(page) {
		case 4:
			addr = 32768;
			break;
		case 5:
			addr = 49152;
			break;
		case 8:
			addr = 16384;
			break;
		default:
			throw new Exception( "Z80 (page): out of range " + page );
		}

		int        k = 0;
		while (k < blocklen) {
			int        tbyte = data[i++]; k++;
			if ( tbyte != 0xed ) {
				cpu.pokeb(addr, ~tbyte);
				cpu.pokeb(addr, tbyte);
				addr++;
			}
			else {
				tbyte = data[i++]; k++;
				if ( tbyte != 0xed ) {
					cpu.pokeb(addr, 0);
					cpu.pokeb(addr, 0xed);
					addr++;
					i--; k--;
				}
				else {
					int        count;
					count = data[i++]; k++;
					tbyte = data[i++]; k++;
					while ( count-- > 0 ) {
						cpu.pokeb(addr, ~tbyte);
						cpu.pokeb(addr, tbyte);
						addr++;
					}
				}
			}
		}

		if ((addr & 16383) != 0) {
			throw new Exception( "Z80 (page): overrun" );
		}
		
		return i;
	}

	
	
	@Override
	public String getFileName() {
	
		return "D:/workspace/jMESYSalpha/bin/games/Sinclair/Spectrum/HARDBALL.Z80";
	}

	@Override
	public Image getScreen(String name, InputStream is, jMESYSDisplay disp, Graphics g) throws Exception {
		int longo = is.available();
		byte        fichero[] = new byte[6912];
		
		System.out.println("Longo="+longo);

		byte[] arrScr = new byte[32*192];
		byte[] arrAtt = new byte[768];
		
		for (int i=0 ; i<6144 ; i++){
			arrScr[i]=0;
		}
		for (int i=0 ; i<768 ; i++){
			arrAtt[i]=0;
		}
		
		//readBytes( is, fichero, 0,        longo );
		
		fichero = loadFormatScreen(name, is);
		
		System.arraycopy(fichero, 0, arrScr, 0, (32*192));
		System.arraycopy(fichero, ((32*192)), arrAtt, 0, 768);
				
		SpectrumDisplay dsp = (SpectrumDisplay) disp;
		
		dsp.paintImageScreen(g, arrScr, arrAtt);
		
		return null;
		
	}

}
