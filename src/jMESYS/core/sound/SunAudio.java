package jMESYS.core.sound;

import java.io.InputStream;

import sun.audio.AudioPlayer;

public class SunAudio extends Audio {
		InputStream is;
		byte[] out;
		int outp, outl;

		SunAudio() {
			mul = 8000;

			is = new InputStream() {
				public int read() {
					byte b[] = new byte[1];
					int v = read(b, 0, 1);
					return v>=0 ? b[0]&0xFF : v;
				}

				public int read(byte b[], int p, int l) {
					outp = p;
					outl = l;
					out = b;
					synchronized(b) {
						while(outl > 0) try {
							if(is == null)
								return -1;
							b.wait();
						} catch(InterruptedException x) {
							Thread.currentThread().interrupt();
							break;
						}
					}
					return l-outl;
				}
			};
			// XXX deadlock
			AudioPlayer.player.start(is);
		}

		int flush(int p) {
			while(out == null)
				return p-2;

			p >>= 1;
			int n = outl;
			if(n>p) n=p;
			convert(n);
			outp += n;
			outl -= n;
			if(outl <= 0) synchronized(out) {
				out.notify();
				out = null;
			}
			p -= n;
			if(p>0)
				System.arraycopy(buf,2*n, buf,0, 2*p);
			return 2*p;
		}

		public void close() {
			is = null;
			if(out!=null) synchronized(out) {
				out.notify();
			}
		}

		private void convert(int sp) {
			int dp = outp + sp;
			sp *= 2;
			do {
				int v = buf[--sp]<<8;
				v += buf[--sp];

				int x=0x0F;
				if(v<0) {v=-v; x=0x8F;}
				v = v+0x84 >> 3;
				if(v>=256) {
					v >>= 4; x-=0x40;
					if(v>=256) v=255;
				}
				if(v>=64) {v >>= 2; x-=0x20;}
				if(v>=32) {v >>= 1; x-=0x10;}
		
				out[--dp] = (byte)(x - v);
			} while(sp>0);
		}
	}


