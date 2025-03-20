package model;

public class Tower {
	private int towerId;
	private String towerName;
	private int damage;
	private int range;
	private int attackSpeed;
	private int cost;
	private int upgradeCost;
	private int towerLevel;

	public Tower(int towerId, String towername, int damage, int range, int attackspeed, int cost, int upgradecost,
			int towerlevel) {
		super();
		this.towerId = towerId;
		this.towerName = towername;
		this.damage = damage;
		this.range = range;
		this.attackSpeed = attackspeed;
		this.cost = cost;
		this.upgradeCost = upgradecost;
		this.towerLevel = towerlevel;
	}

	public Tower() {
	}

	public int getTowerId() {
		return towerId;
	}

	public void setTowerId(int towerId) {
		this.towerId = towerId;
	}

	public String getTowerName() {
		return towerName;
	}

	public void setTowerName(String towerName) {
		this.towerName = towerName;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}

	public void setAttackSpeed(int attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getUpgradeCost() {
		return upgradeCost;
	}

	public void setUpgradeCost(int upgradeCost) {
		this.upgradeCost = upgradeCost;
	}

	public int getTowerLevel() {
		return towerLevel;
	}

	public void setTowerLevel(int towerLevel) {
		this.towerLevel = towerLevel;
	}

}
