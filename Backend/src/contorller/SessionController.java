package contorller;

import java.util.List;

import model.Session;
import service.SessionService;
import service.SessionServiceImpl;

/**
 * 게임 세션 관련 컨트롤러 클래스
 */
public class SessionController {
    
    private SessionService sessionService;
    
    /**
     * 기본 생성자
     */
    public SessionController() {
        this.sessionService = new SessionServiceImpl();
    }
    
    /**
     * 새 게임 세션 생성
     * @param userId 사용자 ID
     * @return 생성된 세션 ID (실패 시 0 이하의 값)
     */
    public int createSession(int userId) {
        try {
            Session newSession = new Session(userId);
            return sessionService.createSession(newSession);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * 현재 게임 상태 저장
     * @param session 저장할 세션 정보
     * @return 저장 성공 여부
     */
    public boolean saveGameState(Session session) {
        try {
            return sessionService.saveGameState(session);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 사용자의 저장된 게임 세션 목록 조회
     * @param userId 사용자 ID
     * @return 게임 세션 목록
     */
    public List<Session> getUserSessions(int userId) {
        try {
            return sessionService.getUserSessions(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 저장된 게임 세션 불러오기
     * @param sessionId 세션 ID
     * @return 세션 정보
     */
    public Session loadGameState(int sessionId) {
        try {
            return sessionService.loadGameState(sessionId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
