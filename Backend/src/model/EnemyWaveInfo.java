package model;

public class EnemyWaveInfo {
    private int enemyId;
    private String enemyName;
    private int health;
    private int speed;
    private int reward;
    private int damage;
    private int quantity;


    public int getEnemyId() {
        return enemyId;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public int getReward() {
        return reward;
    }

    public int getDamage() {
        return damage;
    }

    public int getQuantity() {
        return quantity;
    }
    
    
    public EnemyWaveInfo(int enemyId, String enemyName, int health, int speed, int reward, int damage, int quantity) {
        this.enemyId = enemyId;
        this.enemyName = enemyName;
        this.health = health;
        this.speed = speed;
        this.reward = reward;
        this.damage = damage;
        this.quantity = quantity;
    }
    
}
