package contorller;

import model.Tower;
import model.TowerPlacement;
import service.TowerPlacementService;
import service.TowerPlacementServiceImpl;
import service.TowerService;
import service.TowerServiceImpl;

public class TowerController {
	private TowerService towerService;
	private TowerPlacementService towerPlacementService;

	// 생성자
	public TowerController() {
		this.towerService = new TowerServiceImpl();
		this.towerPlacementService = new TowerPlacementServiceImpl();
	}
	
	public Tower getTowerById(int id) {
		return towerService.getTowerById(id);
	}

	/**
	 * 1단계 타워 목록 조회
	 * 
	 * @param 타워 레벨별 정보 목록 조회
	 * @return 랜덤으로 선택된 타워 이름
	 */
	public Tower getFirstTower() {
		return towerService.getFirstTower();
	}

	public Tower getSecondTower() {
		return towerService.getSecondTower();
	}

	public Tower getThirdTower() {
		return towerService.getThirdTower();
	}

	// 타워 배치
	public boolean placeTower(int towerId, int sessionId, int positionX, int positionY) {
		TowerPlacement tower = new TowerPlacement(towerId, sessionId, positionX, positionY);
		return towerPlacementService.placeTower(tower);
	}

	// 타워 업그레이드
	public boolean upgradeTower(int sessionId, int positionX, int positionY) {
		return towerPlacementService.upgradeTower(sessionId, positionX, positionY);
	}

}
