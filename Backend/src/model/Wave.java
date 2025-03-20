package model;

public class Wave {

	 private int waveId;
	 private int level;
	 
	 
	 
	public int getWaveId() {
		return waveId;
	}
	public void setWaveId(int waveId) {
		this.waveId = waveId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public Wave(int waveId, int level) {
		super();
		this.waveId = waveId;
		this.level = level;
	}
	
	 
	 
	 
	 
}
