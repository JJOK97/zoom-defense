package service;

import dao.TowerDAO;
import model.Tower;

public class TowerServicelmpl implements TowerService {
	private TowerDAO towerDAO;
	private static final int INITIAL_TOWER_COST = 30; // 초기 타워 비용
	private static final float COST_INCREASE_RATE = 1.1f; // 타워 비용 증가율 (10%)
	private static int currentTowerCost = INITIAL_TOWER_COST; // 현재 타워 비용

	public TowerServicelmpl() {
		this.towerDAO = new TowerDAO();
	}

	@Override
	public Tower getFirstTower() {
		Tower tower = towerDAO.getFirstTower();
		if (tower != null) {
			System.out.println("1단계 타워 가져오기 성공: ID=" + tower.getTowerId() + ", Name=" + tower.getTowerName());
			
			// 타워 비용 변경 (DB 값 대신 현재 계산된 비용 사용)
			tower.setCost(currentTowerCost);
			
			// 다음 타워 비용 증가는 여기서 하지 않고 실제 타워 배치 성공 시 호출되도록 변경
			System.out.println("타워 비용 설정: " + tower.getCost());
		} else {
			System.out.println("1단계 타워 가져오기 실패");
		}
		return tower;
	}
	
	/**
	 * 타워 설치 성공 시 다음 타워 비용 증가
	 */
	public static void increaseTowerCost() {
		currentTowerCost = Math.round(currentTowerCost * COST_INCREASE_RATE);
		System.out.println("다음 타워 비용 증가: " + currentTowerCost);
	}

	// 타워 비용 초기화 메소드
	public static void resetTowerCost() {
		currentTowerCost = INITIAL_TOWER_COST;
	}

	@Override
	public Tower getSecondTower() {
		Tower tower = towerDAO.getSecondTower();
		if (tower != null) {
			System.out.println("2단계 타워 가져오기 성공: ID=" + tower.getTowerId() + ", Name=" + tower.getTowerName());
		} else {
			System.out.println("2단계 타워 가져오기 실패");
		}
		return tower;
	}

	@Override
	public Tower getThirdTower() {
		Tower tower = towerDAO.getThirdTower();
		if (tower != null) {
			System.out.println("3단계 타워 가져오기 성공: ID=" + tower.getTowerId() + ", Name=" + tower.getTowerName());
		} else {
			System.out.println("3단계 타워 가져오기 실패");
		}
		return tower;
	}

	@Override
	public Tower getTowerById(int id) {
		return towerDAO.getTowerById(id);
	}
}
