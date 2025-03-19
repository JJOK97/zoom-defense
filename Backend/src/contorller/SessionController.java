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
    

}
