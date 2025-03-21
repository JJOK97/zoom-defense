package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Session {

	private int sessionId;
	private int userId;
	private int life;
	private int money;
	private Timestamp loadTime;
	private int score;
	private int wave;
	private List<TowerPlacement> placedTowers;
	
	
	
	
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
		this.placedTowers = new ArrayList<>();
	}
	
	public Session(int userId) {
		this.userId = userId;
		this.life = 100;  // 기본값
		this.money = 30; // 기본값을 30으로 수정
		this.score = 0;   // 기본값
		this.wave = 1;    // 기본값
		this.placedTowers = new ArrayList<>();
	}
	
	public Session() {
		
	}
	
	public List<TowerPlacement> getPlacedTowers() {
		return placedTowers;
	}
	
	public void setPlacedTowers(List<TowerPlacement> placedTowers) {
		this.placedTowers = placedTowers;
	}
	
	public void addTower(TowerPlacement tower) {
		if (this.placedTowers == null) {
			this.placedTowers = new ArrayList<>();
		}
		this.placedTowers.add(tower);
	}
	
	

}
