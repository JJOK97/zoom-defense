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
    
    
    public int createSession(int userId, int money, int life) {
        // 클라이언트로부터 받은 정보를 바탕으로 Session 객체 생성
        Session session = new Session();
        session.setUserId(userId);
        session.setMoney(100);
        session.setLife(100);
        
        // Service 계층을 통해 세션 생성 처리 후, 생성된 SESSION_ID 반환
        int sessionId = sessionService.createSession(session);
        return sessionId;
    }
    
    
    public boolean saveGameState(int sessionId, int currentMoney, int currentLife, int currentWave, int score) {
        Session session = new Session();
        session.setSessionId(sessionId);  // SessionId 설정 (중요!)
        session.setMoney(currentMoney);
        session.setLife(currentLife);
        session.setWave(currentWave);
        session.setScore(score);
        
        // Service 계층을 통해 게임 상태 저장
        return sessionService.saveGameState(session);
    }
    
    	
  
    

}
