package jMESYS.drivers.Sinclair.Spectrum.formats;

import java.awt.Graphics;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
//import java.io.FileInputStream;
import java.io.InputStream;

import jMESYS.core.cpu.CPU;
import jMESYS.core.cpu.z80.Z80;
import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.drivers.Sinclair.Spectrum.display.SpectrumDisplay;
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
	
	
	public void loadFormat(String name, InputStream is, jMESYSComputer computer) throws Exception {
		DataInputStream in = new DataInputStream(is);
		computer.reset();
		Z80 cpu = ((Spectrum48k)computer).cpu;

		cpu.a(get8(in)); cpu.f(get8(in));
		cpu.bc(get16(in));
		cpu.hl(get16(in));
		int pc = get16(in);
		cpu.pc(pc);
		cpu.sp(get16(in));
		cpu.i(get8(in));
		int f1 = get16(in);
		cpu.r(f1&0x7F | f1>>1&0x80);
		f1 >>>= 8; if(f1==0xFF) f1 = 0;
		computer.out(0xFE, f1>>1 & 7);
		cpu.de(get16(in));

		cpu.exx(); cpu.ex_af();
		cpu.bc(get16(in)); cpu.de(get16(in)); cpu.hl(get16(in));
		cpu.a(get8(in)); cpu.f(get8(in));
		cpu.exx(); cpu.ex_af();

		cpu.iy(get16(in)); cpu.ix(get16(in));
		int v = get8(in);
		cpu.iff((v==0?0:1) | (get8(in)==0?0:2));
		cpu.im(get8(in));

		if(pc != 0) {
			if((f1&0x20)!=0)
				uncompress_z80((Spectrum48k)computer, in, 16384, 49152);
			else
				poke_stream(computer, in, 16384, 49152);
			return;
		}

		int l = get16(in);
		cpu.pc(get16(in));
		int hm = get8(in);
		if(hm>1) System.out.println("Unsupported model: #"+hm);
		get8(in);
		if(get8(in)==0xFF) {
			if(((Spectrum48k)computer).if1rom != null) {
                          /** @todo if1rom */
                          //spectrum.rom = spectrum.if1rom;
                        }
		}
		//if((get8(in)&4)!=0 && computer.ay_enabled && l>=23) {
		if((get8(in)&4)!=0 && computer.getAudioDevice().isEnabled() && l>=23) {
			//computer.ay_idx = (byte)(get8(in) & 15);
			((Spectrum48k)computer).audioChip.ay_idx = (byte)(get8(in) & 15);
			for(int i=0;i<16;i++){
				//computer.ay_write(i, get8(in));
				computer.getAudioDevice().writeSoundCard(i, get8(in));
			}
			l -= 17;
		}
		in.skip(l-6);

		for(;;) {
			l = get16(in);
			int a;
			switch(get8(in)) {
				case 8: a = 0x4000; break;
				case 4: a = 0x8000; break;
				case 5: a = 0xC000; break;
				default: in.skip(l); continue;
			}
			if(l == 0xFFFF)
				poke_stream(computer, in, a, 16384);
			else
				uncompress_z80((Spectrum48k)computer, in, a, 16384);
		}
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
	
	private int uncompress_z80(Spectrum48k computer, DataInputStream in, int pos, int count)
			throws IOException
		{
			int end = pos+count;
			int n = 0;
			loop: do {
				int v = get8(in); n++;
				if(v != 0xED) {
					computer.mem(pos++, v);
					continue;
				}
				v = get8(in); n++;
				if(v != 0xED) {
					computer.mem16(pos, v<<8 | 0xED);
					pos += 2;
					continue;
				}
				int l = get8(in);
				v = get8(in); n += 2;
				while(l>0) {
					computer.mem(pos++, v);
					if(pos>=end) break loop;
					l--;
				}
			} while(pos<end);
			return n;
		}
}
