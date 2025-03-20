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
		Tower tower = towerDAO.getFirstTower();
		if (tower != null) {
			System.out.println("1단계 타워 가져오기 성공: ID=" + tower.getTowerId() + 
							  ", Name=" + tower.getTowerName());
		} else {
			System.out.println("1단계 타워 가져오기 실패");
		}
		return tower;
	}

	@Override
	public Tower getSecondTower() {
		Tower tower = towerDAO.getSecondTower();
		if (tower != null) {
			System.out.println("2단계 타워 가져오기 성공: ID=" + tower.getTowerId() + 
							  ", Name=" + tower.getTowerName());
		} else {
			System.out.println("2단계 타워 가져오기 실패");
		}
		return tower;
	}

	@Override
	public Tower getThirdTower() {
		Tower tower = towerDAO.getThirdTower();
		if (tower != null) {
			System.out.println("3단계 타워 가져오기 성공: ID=" + tower.getTowerId() + 
							  ", Name=" + tower.getTowerName());
		} else {
			System.out.println("3단계 타워 가져오기 실패");
		}
		return tower;
	}
}
