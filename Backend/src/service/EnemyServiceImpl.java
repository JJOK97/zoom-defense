package service;

import java.util.List;

import dao.EnemyDAO;
import model.Enemy;

public class EnemyServiceImpl implements EnemyService {

    private EnemyDAO enemyDAO = new EnemyDAO();

    @Override
    public List<Enemy> getAllEnemies() {
        return enemyDAO.getAllEnemies();
    }

	@Override
	public Enemy getEnemyById(int enemyId) {
		return enemyDAO.getEnemyById(enemyId);
	}
}