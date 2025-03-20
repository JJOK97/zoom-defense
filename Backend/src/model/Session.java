package model;

import java.sql.Timestamp;

public class Session {

	private int sessionId;
	private int userId;
	private int life;
	private int money;
	private Timestamp loadTime;
	private int score;
	private int wave;
	
	
	
	
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getLife() {
		return life;
	}
	public void setLife(int life) {
		this.life = life;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public Timestamp getLoadTime() {
		return loadTime;
	}
	public void setLoadTime(Timestamp loadTime) {
		this.loadTime = loadTime;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getWave() {
		return wave;
	}
	public void setWave(int wave) {
		this.wave = wave;
	}
	
	public Session(int sessionId, int userId, int life, int money, Timestamp loadTime, int score, int wave) {
		super();
		this.sessionId = sessionId;
		this.userId = userId;
		this.life = life;
		this.money = money;
		this.loadTime = loadTime;
		this.score = score;
		this.wave = wave;
	}
	public Session() {
		
	}
	
	

}
