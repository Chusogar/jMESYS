package jMESYS.core.sound.cards.AY_3_8912;

import jMESYS.core.sound.cards.jMESYS_SoundCard;
import jMESYS.drivers.jMESYSComputer;

public class AY_3_8912 extends jMESYS_SoundCard {
	
	private String devName = "General Instruments AY-3-8912";
	
	static final int CHANNEL_VOLUME = 26000;
	static final int SPEAKER_VOLUME = 50000;

	public boolean ay_enabled=true;
	public byte ay_idx;
	
	public int speaker;
	public static int sp_volt[];

	public final byte ay_reg[] = new byte[16];

	public int ay_aper, ay_bper, ay_cper, ay_nper, ay_eper;
	public int ay_acnt, ay_bcnt, ay_ccnt, ay_ncnt, ay_ecnt;
	public int ay_gen, ay_mix, ay_ech, ay_dis;
	public int ay_avol, ay_bvol, ay_cvol;
	public int ay_noise = 1;
	public int ay_ekeep; // >=0:hold, ==0:stop
	public boolean ay_div16;
	public int ay_eattack, ay_ealt, ay_estep;

	public static int ay_volt[];
	
	public int au_time;
	public int au_val, au_dt;

	public String getDeviceName() {
		return devName;
	}

	public void out(int port, int v) {}
	
	public void out(int port, int v, int cpuTime) {
		if((port&0x0001)==0) {
			//ula28 = (byte)v;
			int n = v&7;
			/*if(n != display.border) {
				refresh_border();
				display.border = (byte)n;
			}*/
			n = sp_volt[v>>3 & 3];
			if(n != speaker) {
				//au_update();
				try {
					updateSoundCard(cpuTime);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace(System.out);
				}
				speaker = n;
			}
			
			//ear = ((v & 0x10) != 0 ? 0xFF : 0xBF);
		}
		//if((port&0x8002)==0x8000 && ay_enabled) {
		if((port&0x8002)==0x8000 && isEnabled()) {
			if((port&0x4000)!=0)
				ay_idx = (byte)(v&15);
			else {
				try {
					//au_update();
					updateSoundCard(cpuTime);
					//ay_write(ay_idx, v);
					writeSoundCard(ay_idx, v);
				} catch (Exception e){
					e.printStackTrace(System.out);
				}
			}
		}
	}

	public int in(int port) {
		if(ay_idx>=14 && (ay_reg[7]>>ay_idx-8 & 1) == 0)
			return 0xFF;
		return ay_reg[ay_idx];
	}

	@Override
	public boolean connectDevice(jMESYSComputer computer) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPortNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void reset() throws Exception {
		/* XXX */
		speaker = 0;
		ay_mix = ay_gen = 0;
		ay_avol = ay_bvol = ay_cvol = 0;
		ay_ekeep = 0;
		ay_dis = 077;
	}
	
	public void muteSoundCard(boolean m) throws Exception {
		
		if (isMuted()){
			ay_ekeep = 0;
			ay_mix=0;
		}
		setvol();
	}

	public void writeSoundCard(int n, int v) throws Exception {
		//System.out.println("writeSoundCard");
		switch(n) {
		case  0: ay_aper = ay_aper&0xF00 | v; break;
		case  1: ay_aper = ay_aper&0x0FF | (v&=15)<<8; break;
		case  2: ay_bper = ay_bper&0xF00 | v; break;
		case  3: ay_bper = ay_bper&0x0FF | (v&=15)<<8; break;
		case  4: ay_cper = ay_cper&0xF00 | v; break;
		case  5: ay_cper = ay_cper&0x0FF | (v&=15)<<8; break;
		case  6: ay_nper = v&=31; break;
		case  7: ay_mix = ~(v|ay_dis); break;
		case  8:
		case  9:
		case 10:
			int a=v&=31, x=011<<(n-8);
			if(v==0) {
				ay_dis |= x;
				ay_ech &= ~x;
			} else if(v<16) {
				ay_dis &= (x = ~x);
				ay_ech &= x;
			} else {
				ay_dis &= ~x;
				ay_ech |= x;
				a = ay_estep^ay_eattack;
			}
			ay_mix = ~(ay_reg[7]|ay_dis);
			a = ay_volt[a];
			switch(n) {
			case 8: ay_avol = a; break;
			case 9: ay_bvol = a; break;
			case 10: ay_cvol = a; break;
			}
			break;
		case 11: ay_eper = ay_eper&0xFF00 | v; break;
		case 12: ay_eper = ay_eper&0xFF | v<<8; break;
		case 13: shapeSoundCard(v&=15); break;
		}
		ay_reg[n] = (byte)v;
	}

	public void shapeSoundCard(int v) throws Exception {
		if(v<8)
			v = v<4 ? 1 : 7;

		ay_ekeep = (v&1)!=0 ? 1 : -1;
		ay_ealt = (v+1&2)!=0 ? 15 : 0;
		ay_eattack = (v&4)!=0 ? 15 : 0;
		ay_estep = 15;

		ay_ecnt = -1; // ?
		changedSoundCard();
	}

	public void changedSoundCard() throws Exception {
		int v = ay_volt[ay_estep ^ ay_eattack];
		int x = ay_ech;
		if((x&1)!=0) ay_avol = v;
		if((x&2)!=0) ay_bvol = v;
		if((x&4)!=0) ay_cvol = v;
	}

	public int tickSoundCard() throws Exception {
		int x = 0;
		if((--ay_acnt & ay_aper)==0) {
			ay_acnt = -1;
			x ^= 1;
		}
		if((--ay_bcnt & ay_bper)==0) {
			ay_bcnt = -1;
			x ^= 2;
		}
		if((--ay_ccnt & ay_cper)==0) {
			ay_ccnt = -1;
			x ^= 4;
		}

		if(ay_div16 ^= true) {
			ay_gen ^= x;
			return x & ay_mix;
		}

		if((--ay_ncnt & ay_nper)==0) {
			ay_ncnt = -1;
			if((ay_noise&1)!=0) {
				x ^= 070;
				ay_noise ^= 0x28000;
			}
			ay_noise >>= 1;
		}

		if((--ay_ecnt & ay_eper)==0) {
			ay_ecnt = -1;
			if(ay_ekeep!=0) {
				if(ay_estep==0) {
					ay_eattack ^= ay_ealt;
					ay_ekeep >>= 1;
					ay_estep = 16;
				}
				ay_estep--;
				if(ay_ech!=0) {
					changedSoundCard();
					x |= 0x100;
				}
			}
		}
		ay_gen ^= x;
		return x & ay_mix;
	}

	public int getValueSoundCard() throws Exception {
		int g = ay_mix & ay_gen;
		int v = speaker;
		if((g&011)==0) v += ay_avol;
		if((g&022)==0) v += ay_bvol;
		if((g&044)==0) v += ay_cvol;
		return v;
	}
	
	public void setAudioTime(int audioTime) {
		au_time = audioTime;
	}

	public void updateSoundCard(int cpuTime) throws Exception {
		//System.out.println("updateSoundCard");
		int t = cpuTime;
		au_time += (t -= au_time);

		int dv = getValueSoundCard() - au_val;
		if(dv != 0) {
			au_val += dv;
			getAudio().step(0, dv);
		}
		int dt = au_dt;
		for(; t>=dt; dt+=16) {
			if(tickSoundCard() == 0)
				continue;
			dv = getValueSoundCard() - au_val;
			if(dv == 0)
				continue;
			au_val += dv;
			getAudio().step(dt, dv);
			t -= dt; dt = 0;
		}
		au_dt = dt - t;
		getAudio().step(t, 0);
	}

	public void enableSoundCard(boolean en) throws Exception {
		if(!en) ay_mix = 0;
		ay_enabled = en;		
	}
	
	public AY_3_8912(int Hz) {
		super(Hz);
		
		sp_volt = new int[4];
		ay_volt = new int[16];
		//setvol();
		double a = isMuted() ? 0 : 50/100.;
		a *= a;

		sp_volt[2] = (int)(SPEAKER_VOLUME*a);
		sp_volt[3] = (int)(SPEAKER_VOLUME*1.06*a);

		a *= CHANNEL_VOLUME;
		int n;
		ay_volt[n=15] = (int)a;
		do {
			ay_volt[--n] = (int)(a *= 0.7071);
		} while(n>1);
	}
	
	public void setvol()
	{
		double a = isMuted() ? 0 : getVolume()/100.;
		a *= a;

		sp_volt[2] = (int)(SPEAKER_VOLUME*a);
		sp_volt[3] = (int)(SPEAKER_VOLUME*1.06*a);

		a *= CHANNEL_VOLUME;
		int n;
		ay_volt[n=15] = (int)a;
		do {
			ay_volt[--n] = (int)(a *= 0.7071);
		} while(n>1);
	}
	
	public int volumeChg(int chg) {
		int v = getVolume() + chg;
		if(v<0) v=0; else if(v>100) v=100;
		setVolume(v);
		setvol();
		return v;
	}
}
