package service;

import model.TowerPlacement;

public interface TowerPlacementService {
	
	/**
     * 새 게임 시작 시 세션 생성
     * @param session 생성할 세션 정보 (userId 필수)
     * @return 생성된 세션 ID
     */
	boolean placeTower(TowerPlacement tower);
}
