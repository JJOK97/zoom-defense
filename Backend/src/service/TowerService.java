package service;

import java.util.ArrayList;

import model.Tower;

public interface TowerService {

	/**
	 * 1단계 타워
	 * 
	 * @param 타워 레벨별 정보
	 * @return 타워 리스트 여부
	 */
	Tower getFirstTower();

	
	/**
	 * 2단계 타워
	 * 
	 * @param 타워 레벨별 정보
	 * @return 타워 리스트 여부
	 */
	Tower getSecondTower();
	
	
	/**
	 * 2단계 타워
	 * 
	 * @param 타워 레벨별 정보
	 * @return 타워 리스트 여부
	 */
	Tower getThirdTower();
}
