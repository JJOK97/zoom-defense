package dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import model.Session;
import model.TowerPlacement;

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
     * 세션 정보 업데이트 (타워 정보 포함)
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
        
        // 타워 배치 정보도 함께 저장 - 실제 DB에서는 별도 테이블에 저장하게 됨
        saveTowerPlacements(session);
        
        return true;
    }
    
    /**
     * 타워 배치 정보 저장 (가상의 메서드, 실제로는 DB 작업 필요)
     */
    private void saveTowerPlacements(Session session) {
        // 실제 DB 환경에서는 세션 ID와 연결된 타워 배치 정보를 저장
        // 여기서는 이미 Session 객체 내에 저장되므로 별도 동작 필요 없음
        System.out.println("타워 배치 정보 저장됨: " + session.getSessionId() + 
                          ", 타워 수: " + (session.getPlacedTowers() != null ? session.getPlacedTowers().size() : 0));
    }
    
    /**
     * 세션에 연결된 타워 정보 로드
     * @param sessionId 세션 ID
     * @return 타워 배치 목록
     */
    public List<TowerPlacement> loadTowerPlacements(int sessionId) {
        Session session = sessionMap.get(sessionId);
        if (session != null && session.getPlacedTowers() != null) {
            return session.getPlacedTowers();
        }
        return new ArrayList<>();
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
