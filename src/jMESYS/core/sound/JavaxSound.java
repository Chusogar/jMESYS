package jMESYS.core.sound;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class JavaxSound extends Audio {
	
		static final int FREQ = 22050;

		SourceDataLine line;

		JavaxSound() {
			try {
				mul = FREQ;
				AudioFormat fmt
				  = new AudioFormat(FREQ, 16, 1, true, false);
				System.out.println(fmt);
				SourceDataLine l = (SourceDataLine)AudioSystem.getLine(
					new DataLine.Info(SourceDataLine.class, fmt)
				);
				l.open(fmt, 4096);
				l.start();
				line = l;
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		public int flush(int p) {
			SourceDataLine l = line;
			if(l!=null)
				l.write(buf, 0, p);
			return 0;
		}

		public void close() {
			SourceDataLine l = line;
			if(l!=null) {
				l.stop();
				l.close();
			}
		}
	}

