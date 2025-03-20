package dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import model.Session;

/**
 * 게임 세션 데이터 접근 객체 (임시 메모리 저장 방식)
 */
public class SessionDAO {
    
    // 세션 ID 자동 증가 변수
    private static AtomicInteger sessionIdCounter = new AtomicInteger(1);
    
    // 세션 데이터 저장을 위한 임시 Map (실제로는 DB 사용)
    private static Map<Integer, Session> sessionMap = new HashMap<>();
    
    // 사용자별 세션 목록 (실제로는 DB의 인덱스 역할)
    private static Map<Integer, List<Integer>> userSessionMap = new HashMap<>();
    
    /**
     * 새 게임 세션 생성
     * @param session 생성할 세션 정보
     * @return 생성된 세션 ID
     */
    public int createSession(Session session) {
        // 세션 ID 생성
        int sessionId = sessionIdCounter.getAndIncrement();
        
        // 현재 시간 설정
        session.setLoadTime(new Timestamp(System.currentTimeMillis()));
        
        // 세션 ID 설정
        session.setSessionId(sessionId);
        
        // 세션 데이터 저장
        sessionMap.put(sessionId, session);
        
        // 사용자 세션 목록에 추가
        int userId = session.getUserId();
        userSessionMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(sessionId);
        
        return sessionId;
    }
    
    /**
     * 세션 정보 업데이트
     * @param session 업데이트할 세션 정보
     * @return 업데이트 성공 여부
     */
    public boolean updateSession(Session session) {
        int sessionId = session.getSessionId();
        
        // 세션 존재 확인
        if (!sessionMap.containsKey(sessionId)) {
            return false;
        }
        
        // 세션 업데이트 시간 갱신
        session.setLoadTime(new Timestamp(System.currentTimeMillis()));
        
        // 세션 데이터 업데이트
        sessionMap.put(sessionId, session);
        
        return true;
    }
    
    /**
     * 사용자의 세션 목록 조회
     * @param userId 사용자 ID
     * @return 사용자의 세션 목록
     */
    public List<Session> getUserSessions(int userId) {
        List<Session> result = new ArrayList<>();
        
        // 사용자의 세션 ID 목록 조회
        List<Integer> sessionIds = userSessionMap.get(userId);
        
        if (sessionIds != null) {
            // 최근 세션부터 조회
            for (int i = sessionIds.size() - 1; i >= 0; i--) {
                int sessionId = sessionIds.get(i);
                Session session = sessionMap.get(sessionId);
                if (session != null) {
                    result.add(session);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 특정 세션 정보 조회
     * @param sessionId 세션 ID
     * @return 세션 정보
     */
    public Session loadUserSessions(int sessionId) {
        return sessionMap.get(sessionId);
    }
}
