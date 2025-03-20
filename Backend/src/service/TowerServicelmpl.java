package service;

import dao.TowerDAO;
import model.Tower;

public class TowerServicelmpl implements TowerService {
	private TowerDAO towerDAO;
	
	
	public TowerServicelmpl() {
		this.towerDAO = new TowerDAO();
	}

	@Override
	public Tower getFirstTower() {
		return towerDAO.getFirstTower();
	}

	@Override
	public Tower getSecondTower() {
		return towerDAO.getSecondTower();
	}

	@Override
	public Tower getThirdTower() {
		return towerDAO.getThirdTower();
	}
}
