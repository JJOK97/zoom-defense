package contorller;

import service.SessionService;
import service.SessionServiceImpl;
import model.Session;

public class SessionController {
    
    private SessionService sessionService;

    // 생성자에서 SessionService 구현체를 주입합니다.
    public SessionController() {
        this.sessionService = new SessionServiceImpl();
    }
    
    /**
     * 새 게임 시작 시 세션 생성
     * @param userId 사용자 ID
     * @return 생성된 세션 ID
     */
    public int createSession(int userId) {
        // 클라이언트로부터 받은 userId로 세션 객체 생성
        Session session = new Session();
        session.setUserId(userId);
        
        // Service 계층을 통해 세션 생성 처리 후, 생성된 SESSION_ID 반환
        return sessionService.createSession(session);
    }
    
    /**
     * 현재 게임 상태 저장
     * @param sessionId 세션 ID
     * @param currentMoney 현재 보유 자금
     * @param currentLife 현재 생명력
     * @param currentWave 현재 웨이브
     * @param score 현재 점수
     * @return 저장 성공 여부
     */
    public boolean saveGameState(int sessionId, int currentMoney, int currentLife, int currentWave, int score) {
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setMoney(currentMoney);
        session.setLife(currentLife);
        session.setWave(currentWave);
        session.setScore(score);
        
        // Service 계층을 통해 게임 상태 저장
        return sessionService.saveGameState(session);
    }
}
