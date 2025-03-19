package service;

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
}
