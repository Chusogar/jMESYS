package jMESYS.core.sound.cards;

import jMESYS.core.devices.jMESYSDevice;
import jMESYS.core.sound.Audio;

public abstract class jMESYS_SoundCard implements jMESYSDevice {
	
	private Audio audio;
	private int Hz=0;
	private int audioTime=0;
	
	private boolean enabled = true;
	private int volume = 50; //%
	private boolean muted = false;	
	
	// abstract methods
	public abstract void muteSoundCard(boolean m) throws Exception;
	public abstract void enableSoundCard (boolean en) throws Exception;
	public abstract void writeSoundCard(int n, int v) throws Exception;
	public abstract int getValueSoundCard() throws Exception;
	public abstract void updateSoundCard(int cpuTime) throws Exception;
	
	public jMESYS_SoundCard(int Hz){
		super();
		
		this.Hz = Hz;
	}
	
	public int getLevel() {
		return audio.level;
	}
	public void setLevel(int level) {
		audio.level = level;
	}
	
	public int getAudioTime() {
		return audioTime;
	}
	
	public void setAudioTime(int audioTime) {
		this.audioTime = audioTime;
	}
	
	public void open() throws Exception {
		//System.out.println("OPEN AUDIO");
		audio = Audio.getAudio();
		audio.open(this.Hz);
	}
	
	public void close() throws Exception {
		audio.close();
	}
	
	public Audio getAudio() {
		return audio;
	}
	
	public int getDeviceType(){
		return TYPE_SOUND_CARD;
	}
	
	public int getVolume() {
		//System.out.println("VOLUME: "+volume);
		return volume;
	}
	
	public void setVolume(int v) {
		volume = v;
	}
	
	public boolean isMuted() {
		//System.out.println("isMuted: "+muted);
		return muted;
	}

	public void setMuted(boolean m) throws Exception {
		muted = m;
		muteSoundCard(muted);
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean benabled) throws Exception {
		enabled = benabled;
		enableSoundCard(enabled);
	}

}
