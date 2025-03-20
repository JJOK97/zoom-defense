package service;

import model.Session;

public interface SessionService {

	
	int createSession(Session session);
	
	
	boolean saveGameState(Session session);
}
