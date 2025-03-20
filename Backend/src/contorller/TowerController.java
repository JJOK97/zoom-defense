package contorller;

import model.Tower;
import service.TowerService;
import service.TowerServicelmpl;

public class TowerController {
	private TowerService towerService;
	
	// 생성자
	public TowerController() {
		this.towerService = new TowerServicelmpl();
	}
	
		/**
	     * 1단계 타워 목록 조회
	     * @param 타워 레벨별 정보 목록 조회	
		 * @return 랜덤으로 선택된 타워 이름
	     */
		public Tower getFirstTower() {
			return towerService.getFirstTower();
		}
	
		public Tower getSecondTower() {
			return towerService.getFirstTower();
		}
		
		public Tower getThirdTower() {
			return towerService.getThirdTower();
		}
	
}
