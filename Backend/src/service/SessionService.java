package service;

import model.Session;

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
}
