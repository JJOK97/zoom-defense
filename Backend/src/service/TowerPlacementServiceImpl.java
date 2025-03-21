package service;

import dao.TowerDAO;
import model.TowerPlacement;

public class TowerPlacementServiceImpl implements TowerPlacementService {

	private TowerDAO towerDao;

	public TowerPlacementServiceImpl() {
		this.towerDao = new TowerDAO();
	}

	@Override
	public boolean placeTower(TowerPlacement tower) {
		System.out.println("==== 타워 배치 서비스 호출 ====");
		System.out.println("타워 ID: " + tower.getTowerId());
		System.out.println("세션 ID: " + tower.getSessionId());
		System.out.println("위치 X: " + tower.getPositionX());
		System.out.println("위치 Y: " + tower.getPositionY());
		
		// 데이터 유효성 검사
		if (tower.getTowerId() <= 0) {
			System.out.println("타워 ID가 올바르지 않습니다.");
			return false;
		}
		if (tower.getSessionId() <= 0) {
			System.out.println("세션 ID가 올바르지 않습니다.");
			return false;
		}
		
		// 타워 배치 시도
		try {
			boolean result = towerDao.placeTower(tower);
			System.out.println("타워 배치 결과: " + (result ? "성공" : "실패"));
			return result;
		} catch (Exception e) {
			System.out.println("타워 배치 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean upgradeTower(int sessionId, int x, int y) {

		return towerDao.upgradeTower(sessionId, x, y);
	}

}
