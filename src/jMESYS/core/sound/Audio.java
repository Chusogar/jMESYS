/*
 *	Audio.java
 *
 *	Copyright 2007 Jan Bobrowski <jb@wizard.ae.krakow.pl>
 *
 *	This program is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	version 2 as published by the Free Software Foundation.
 */
package jMESYS.core.sound;
import javax.sound.sampled.*;
import java.io.InputStream;

public abstract class Audio
{
	byte buf[] = new byte[4096];
	int bufp;

	long div;
	int idiv, mul;
	int acct;
	int accv0, accv1;
	public int level;

	public void open(int hz)
	{
		div = hz;
		acct = hz;
		idiv = (1<<30) / hz;
	}

	public void step(int t, int d)
	{
		t = acct - mul*t;

		if(t<0) {
			int p = bufp, v = accv0;
			buf[p++] = (byte)v;
			buf[p++] = (byte)(v>>8);
			v = accv1; accv1 = level;
loop:
			for(;;) {
				if(p == buf.length)
					p = flush(p);
				if((t += div)>=0)
					break;
				buf[p++] = (byte)v;
				buf[p++] = (byte)(v>>8);
				v = level;
				if(p == buf.length)
					continue;
				if((t += div)>=0)
					break;
				byte l=(byte)v;
				byte h=(byte)(v>>8);
				for(;;) {
					buf[p++] = l;
					buf[p++] = h;
					if(p == buf.length)
						continue loop;
					if((t += div)>=0)
						break loop;
				}
			}
			accv0 = v;
			bufp = p;
		}

		// 0 <= t < div

		acct = t;
		int v = level + d;
		if((short)v != v) {
			v = (short)(v>>31 ^ 0x7FFF);
			d = v - level;
		}
		level = v;

		int x = idiv*t >> 22;
		int xx = x*x >> 9;
		accv0 += d*xx >> 8;
		xx = 128 - xx + x;
		accv1 += d*xx >> 8;
	}

	public static Audio getAudio()
	{
		String ver = System.getProperty("java.version");
		int i = ver.indexOf('.',2);
		if(!ver.startsWith("1.")
		 || Integer.parseInt(ver.substring(2,i<0?ver.length():i)) >= 3)
		 	try {
				return new JavaxSound();
			} catch(Throwable e) {}
		return new SunAudio();
	}

	abstract int flush(int p);
	public abstract void close();
}

