package service;

import dao.SessionDAO;
import dao.TowerPlacementDAO;
import model.TowerPlacement;

public class TowerPlacementServicelmpl implements TowerPlacementService {
	
	private TowerPlacementDAO towerDao;
	
	public TowerPlacementServicelmpl() {
		this.towerDao = new TowerPlacementDAO();
	}

	@Override
	public boolean placeTower(TowerPlacement tower) {
		
		return towerDao.placeTower(tower);
	}
	 
}
