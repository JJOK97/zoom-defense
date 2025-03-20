package service;

import dao.TowerDAO;
import model.TowerPlacement;

public class TowerPlacementServicelmpl implements TowerPlacementService {

	private TowerDAO towerDao;

	public TowerPlacementServicelmpl() {
		this.towerDao = new TowerDAO();
	}

	@Override
	public boolean placeTower(TowerPlacement tower) {

		return towerDao.placeTower(tower);
	}

	@Override
	public boolean upgradeTower(int sessionId, int x, int y) {

		return towerDao.upgradeTower(sessionId, x, y);
	}

}
