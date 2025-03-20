package service;

import dao.SessionDAO;
import model.Session;

public class SessionServiceImpl implements SessionService {
    
    private SessionDAO sessionDAO;
    
    public SessionServiceImpl() {
        this.sessionDAO = new SessionDAO();
    }
    
    /**
     * 새 게임 시작 시 세션 생성
     * @param session 생성할 세션 정보 (userId 필수)
     * @return 생성된 세션 ID
     */
    @Override
    public int createSession(Session session) {
        // 초기 값 설정
        session.setMoney(100);  // 초기 자금 100
        session.setLife(100);   // 초기 생명력 100
        session.setScore(0);    // 초기 점수 0
        session.setWave(1);     // 첫 번째 웨이브
        
        return sessionDAO.createSession(session);
    }

    /**
     * 현재 게임 상태 저장
     * @param session 저장할 세션 정보
     * @return 저장 성공 여부
     */
    @Override
    public boolean saveGameState(Session session) {
        return sessionDAO.updateSession(session);
    }
}
