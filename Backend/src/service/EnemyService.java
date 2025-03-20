package service;

import java.util.List;

import model.Enemy;

public interface EnemyService {
	
    List<Enemy> getAllEnemies();

	Enemy getEnemyById(int enemyId);
    
}