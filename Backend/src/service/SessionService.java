package service;

import java.util.List;

import model.Session;

public interface SessionService {

	
	int createSession(Session session);
	
	
	boolean saveGameState(Session session);
	
	
	List<Session> getUserSessions(int userId);


	Session loadGameState(int sessionId);

}
