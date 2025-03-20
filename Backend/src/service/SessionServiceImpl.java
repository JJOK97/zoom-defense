package service;

import java.util.List;

import dao.SessionDAO;
import model.Session;
import model.TowerPlacement;

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

	@Override
	public boolean saveGameState(Session session) {
		
		return sessionDAO.updateSession(session);
	}
	
	@Override
	public List<Session> getUserSessions(int userId) {
	    return sessionDAO.getUserSessions(userId);
	}


	@Override
	public Session loadGameState(int sessionId) {
		
		return sessionDAO.loadUserSessions(sessionId);
	}

	@Override
	public boolean saveTowerPlacement(int sessionId, TowerPlacement towerPlacement) {
		Session session = sessionDAO.loadUserSessions(sessionId);
		if (session != null) {
			session.addTower(towerPlacement);
			return sessionDAO.updateSession(session);
		}
		return false;
	}

	@Override
	public List<TowerPlacement> loadTowerPlacements(int sessionId) {
		return sessionDAO.loadTowerPlacements(sessionId);
	}

}
