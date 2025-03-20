package service;

import model.TowerPlacement;

public interface TowerPlacementService {

	// 타워 배치
	boolean placeTower(TowerPlacement tower);

	// 타워 업그레이드
	boolean upgradeTower(int sessionId, int x, int y);
}
