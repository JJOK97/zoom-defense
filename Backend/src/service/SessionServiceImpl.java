package service;

import java.util.List;

import dao.SessionDAO;
import model.Session;

public class SessionServiceImpl implements SessionService {
    
    private SessionDAO sessionDAO;
    
    public SessionServiceImpl() {
        this.sessionDAO = new SessionDAO();
    }
    

    @Override
    public int createSession(Session session) {
       
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

	
}
