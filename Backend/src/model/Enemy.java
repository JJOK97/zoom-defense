package model;

public class Enemy {
	
	int enemyId;
	String enemyName;
	int health;
	int speed;
	int reward;
	int damage;
	
	public int getEnemyId() {
		return enemyId;
	}
	public void setEnemy_id(int enemyId) {
		this.enemyId = enemyId;
	}
	public String getEnemyName() {
		return enemyName;
	}
	public void setEnemy_name(String enemyName) {
		this.enemyName = enemyName;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getReward() {
		return reward;
	}
	public void setReward(int reward) {
		this.reward = reward;
	}
	public int getDamage() {
		return damage;
	}
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	public Enemy(int enemy_id, String enemy_name, int health, int speed, int reward, int damage) {
		super();
		this.enemyId = enemyId;
		this.enemyName = enemyName;
		this.health = health;
		this.speed = speed;
		this.reward = reward;
		this.damage = damage;
	}
	public Enemy() {
		
	}
	
	

}
