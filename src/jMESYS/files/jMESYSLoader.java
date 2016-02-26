package jMESYS.files;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.gui.jMESYS;

public class jMESYSLoader extends Thread {

		private jMESYS qaop;

		public jMESYSLoader(jMESYS qaop, String rom, String if1rom, String rom128) {
			this.rom = rom; this.if1rom = if1rom;
			this.qaop = qaop;
	        this.rom128 = rom128;
		}

		public static final int ROM=0x4000;

		public static final int IF1ROM=0x2001;

		public static final int CART=0x4002;

		public static final int ROM128=0x4003;

		public static final int TAP=3;

		public static final int SNA=4;

		public static final int Z80=5;
		
		public static final int TZX=6;

		private String rom, rom128, if1rom, run, tape;

		public synchronized void load(String name) {
			System.out.println("LOAD "+name);
			run = name;
			notify();
		}

		public synchronized void tape(String name) {
			tape = name;
			notify();
		}

		public int flength;

		protected int floaded;
		private String text;
		static final Font font = new Font("SansSerif", 0, 14);
		static final Color fg = Color.white;
		static Color shadow = Color.black, bg;
		static {
			try {
				bg = new Color(0x66999999, true);
				shadow = new Color(0x80111111, true);
			} catch(Throwable t) {
			}
		}
		public int x;

		public int y;
		static final int w = 160, h = 45;

		public void reshape(Dimension d) {
			x = (d.width-w)/2;
			y = (d.height-h)/2;
		}

		public void paint(Graphics g) {
			if(bg != null) {
				g.setColor(bg);
				g.fillRoundRect(0, 0, w, h, 6, 6);
			}

			double perc = (double)floaded/flength;
			if(perc>1) perc=1;
			String t = text;
			int tx, ty;

			tx = ty = 0;
			if(t != null) {
				g.setFont(font);
				FontMetrics m = g.getFontMetrics();
				tx = (w - m.stringWidth(t))/2;
				ty = 20 - m.getDescent();
			}

			g.setColor(shadow);
			g.translate(1,1);
			for(boolean f=false;;f=true) {
				g.drawRect(6, 21, w-13, 17);
				g.fillRect(8, 21+2, (int)(perc*(w-16)), 17-3);
				if(t!=null && tx>=0)
					g.drawString(t, tx, ty);
				if(f)
					return;
				g.setColor(fg);
				g.translate(-1,-1);
			}
		}

		public static Hashtable cache = new Hashtable();

		public void run()
		{
			try {
				if(rom != null) {
					URL url = qaop.url_of_file(rom);
					download(url, ROM);
				}
				if(if1rom != null) {
					URL url = qaop.url_of_file(if1rom);
					download(url, IF1ROM);
				}
	                        if(rom128 != null) {
	                                URL url = qaop.url_of_file(rom128);
	                                download(url, ROM128);
	                        }
				for(;;) {
					URL url;
					synchronized(this) {
						url = qaop.url_of_file(run);
						run = null;
					}
					if(url != null) {
						qaop.spectrum.stop_loading();
						download(url, 0);
					}
					qaop.spectrum.pause(false);
					synchronized(this) {
						url = qaop.url_of_file(tape);
						tape = null;
					}
					if(url != null) {
						qaop.spectrum.tape(null, false);
						download(url, TAP);
					}

					synchronized(this) {
						if(run==null && tape==null)
							wait();
					}
				}
			} catch(InterruptedException e) {}
		}

		private void download(URL url, int kind) throws InterruptedException
		{
			String f = url.getFile();
			int i = f.lastIndexOf('/');
			if(i>=0) f = f.substring(i+1);
			text = f;
			f = f.toUpperCase();
			boolean gz = f.endsWith(".GZ") || f.endsWith(".ZIP");
			if(gz) f = f.substring(0, f.length()-3);

			if(kind == 0) {
				if(f.endsWith(".SNA")) kind = SNA;
				else if(f.endsWith(".Z80")) kind = Z80;
				else if(f.endsWith(".TAP")) kind = TAP;
				else if(f.endsWith(".TZX")) kind = TZX;
				else if(f.endsWith(".ROM")) kind = CART;
				else {
					qaop.showStatus("Unknown format: "+text);
					return;
				}
				Spectrum48k s = qaop.spectrum;
				s.pause(true);
				if((kind == TAP) || (kind == TZX)) {
					s.tape(null, false);
					s.autoload();
					s.pause(false);
				} 
			}	

			Thread t =  new Thread(qaop);
			byte data[] = (byte[])cache.get(url);
			if(data!=null) try {
				qaop.do_load(new ByteArrayInputStream(data), kind, gz);
				t.setPriority(Thread.NORM_PRIORITY-1);
				t.start();
			} catch(Exception e) {
				cache.remove(url);
				data = null;
			}

			if(data==null) {
				floaded = 0;
				flength = 1;
				PipedOutputStream pipe = new PipedOutputStream();
				try {
					qaop.do_load(new PipedInputStream(pipe), kind, gz);
					t.start();
					real_download(url, pipe);
				} catch(FileNotFoundException e) {
					String m = "File not found: "+url;
					qaop.showStatus(m);
					System.out.println(m);
				} catch(InterruptedException e) {
					throw e;
				} catch(Exception e) {
					qaop.showStatus(e.toString());
				} finally {
					try {pipe.close();} catch(IOException e) {}
				}
			}
			t.join();
			flength = 0;
			text = null;
			qaop.repaint();
			System.gc();
		}

		private void real_download(URL url, OutputStream pipe)
			throws Exception
		{
			ByteArrayOutputStream d = new ByteArrayOutputStream();
			InputStream s = null;
			qaop.repaint(x, y, w, h);
			try {
				URLConnection con = url.openConnection();
				int l = con.getContentLength();
				flength = l>0 ? l : 1<<16;
				s = con.getInputStream();

				if(con instanceof java.net.HttpURLConnection) {
					int c = ((java.net.HttpURLConnection)con).getResponseCode();
					if(c < 200 || c > 299)
						throw new FileNotFoundException();
				}

				byte buf[] = new byte[4096];
				for(;;) {
					if(interrupted())
						throw new InterruptedException();
					int n = s.available();
					if(n<1) n=1; else if(n>buf.length) n=buf.length;
					n = s.read(buf, 0, n);
					if(n<=0) break;
					d.write(buf, 0, n);
					pipe.write(buf, 0, n);
					floaded += n;
					qaop.repaint(x, y, w, h);
				}
				cache.put(url, d.toByteArray());
				//System.out.println("CACHE: "+url.getClass().getName());
			} finally {
				if(s != null)
					try {s.close();} catch(IOException e) {}
			}
		}

		
	}


