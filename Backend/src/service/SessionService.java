package service;

import java.util.List;

import model.Session;
import model.TowerPlacement;

public interface SessionService {

	/**
	 * 새 게임 시작 시 세션 생성
	 * @param session 생성할 세션 정보 (userId 필수)
	 * @return 생성된 세션 ID
	 */
	int createSession(Session session);
	
	/**
	 * 현재 게임 상태 저장
	 * @param session 저장할 세션 정보
	 * @return 저장 성공 여부
	 */
	boolean saveGameState(Session session);
	
	
	List<Session> getUserSessions(int userId);


	Session loadGameState(int sessionId);

	/**
	 * 타워 배치 정보 저장
	 * @param sessionId 세션 ID
	 * @param towerPlacement 타워 배치 정보
	 * @return 저장 성공 여부
	 */
	boolean saveTowerPlacement(int sessionId, TowerPlacement towerPlacement);

	/**
	 * 세션의 타워 배치 정보 로드
	 * @param sessionId 세션 ID
	 * @return 타워 배치 목록
	 */
	List<TowerPlacement> loadTowerPlacements(int sessionId);

}
