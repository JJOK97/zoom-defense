package contorller;

import java.util.List;

import model.Enemy;
import model.Session;
import service.EnemyService;
import service.EnemyServiceImpl;

public class EnemyController {

	private EnemyService enemyService;

	public EnemyController() {
		this.enemyService = new EnemyServiceImpl();
	}

	public List<Enemy> getAllEnemies() {
		return enemyService.getAllEnemies();
	}
	
	
	public Enemy getEnemyById(int enemyId) {
		Enemy enemy = enemyService.getEnemyById(enemyId);
		
		return enemy;
		
	}

}
