package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.DBConnection;
import model.Session;
import model.TowerPlacement;

/**
 * 게임 세션 데이터 접근 객체
 */
public class SessionDAO {
    
    // 세션 데이터 저장을 위한 임시 Map (DB 조회 캐시 역할)
    private static Map<Integer, Session> sessionMap = new HashMap<>();
    
    // 사용자별 세션 목록 (DB 조회 캐시 역할)
    private static Map<Integer, List<Integer>> userSessionMap = new HashMap<>();
    
    // DB 연결 관련 객체들
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    // DB 연결 가져오기
    private Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
    
    // 자원 해제 메소드
    private void close() {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            // Connection은 싱글톤으로 관리하므로 여기서 닫지 않음
        } catch (SQLException e) {
            System.out.println("자원 해제 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 새 게임 세션 생성
     * @param session 생성할 세션 정보
     * @return 생성된 세션 ID
     */
    public int createSession(Session session) {
        int sessionId = -1;
        DBConnection dbConnection = DBConnection.getInstance();
        
        try {
            conn = dbConnection.getConnection();
            
            // 디버그용: DB 연결 상태 확인
            if (conn == null || conn.isClosed()) {
                System.out.println("DB 연결 실패: 연결이 null이거나 닫혀 있습니다");
                return -1;
            }
            System.out.println("DB 연결 성공: 세션 생성 시작");
            
            // Oracle 트리거 & 시퀀스를 통해 세션 ID 생성
            String sql = "INSERT INTO GAME_SESSIONS(USER_ID, MONEY, LIFE, SCORE, WAVE) VALUES(?, ?, ?, ?, ?)";
            
            // Statement.RETURN_GENERATED_KEYS 설정 대신 Oracle 방식으로 변경
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, session.getUserId());
            pstmt.setInt(2, session.getMoney());
            pstmt.setInt(3, session.getLife());
            pstmt.setInt(4, session.getScore());
            pstmt.setInt(5, session.getWave());
            
            int result = pstmt.executeUpdate();
            System.out.println("INSERT 실행 결과: " + result);
            
            if (result > 0) {
                // Oracle에서는 RETURNING 절을 사용하거나 생성 후 별도 쿼리로 ID 조회
                // 방금 추가한 세션의 ID 조회
                String getIdSql = "SELECT SESSION_SEQ.CURRVAL FROM DUAL";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(getIdSql)) {
                    
                    if (rs.next()) {
                        sessionId = rs.getInt(1);
                        System.out.println("Oracle 시퀀스에서 생성된 SESSION_ID: " + sessionId);
                        
                        // 세션 ID 설정
                        session.setSessionId(sessionId);
                        
                        // 생성 시간 기록
                        session.setLoadTime(new Timestamp(System.currentTimeMillis()));
                        
                        // 세션 캐시에 저장
                        sessionMap.put(sessionId, session);
                        
                        // 사용자 세션 목록에 추가
                        int userId = session.getUserId();
                        userSessionMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(sessionId);
                        
                        // 트랜잭션 커밋
                        dbConnection.commit();
                        System.out.println("세션 생성 완료 및 커밋: " + sessionId);
                    } else {
                        System.out.println("생성된 세션 ID를 가져오지 못했습니다.");
                        dbConnection.rollback();
                    }
                }
            } else {
                System.out.println("세션 생성 실패: INSERT 문이 실행되지 않았습니다.");
                dbConnection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("세션 생성 중 SQL 오류: " + e.getMessage());
            e.printStackTrace();
            
            // 에러 발생 시 롤백
            try {
                if (dbConnection != null) {
                    dbConnection.rollback();
                    System.out.println("세션 생성 롤백 완료");
                }
            } catch (Exception ex) {
                System.out.println("롤백 중 오류: " + ex.getMessage());
                ex.printStackTrace();
            }
        } finally {
            close();
        }
        
        return sessionId;
    }
    
    /**
     * 세션 정보 업데이트 (타워 정보 포함)
     * @param session 업데이트할 세션 정보
     * @return 업데이트 성공 여부
     */
    public boolean updateSession(Session session) {
        int sessionId = session.getSessionId();
        boolean success = false;
        DBConnection dbConnection = DBConnection.getInstance();
        
        try {
            conn = dbConnection.getConnection();
            
            // 디버그용: DB 연결 상태 확인
            if (conn == null || conn.isClosed()) {
                System.out.println("DB 연결 실패: 연결이 null이거나 닫혀 있습니다");
                return false;
            }
            System.out.println("세션 업데이트 시작: ID = " + sessionId);
            
            // 세션 정보 업데이트
            String sql = "UPDATE GAME_SESSIONS SET LOAD_TIME = SYSTIMESTAMP, SCORE = ?, WAVE = ?, MONEY = ?, LIFE = ? WHERE SESSION_ID = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, session.getScore());
            pstmt.setInt(2, session.getWave());
            pstmt.setInt(3, session.getMoney());
            pstmt.setInt(4, session.getLife());
            pstmt.setInt(5, sessionId);
            
            int result = pstmt.executeUpdate();
            System.out.println("UPDATE 실행 결과: " + result);
            
            if (result > 0) {
                // 세션 업데이트 시간 갱신
                session.setLoadTime(new Timestamp(System.currentTimeMillis()));
                
                // 세션 캐시 업데이트
                sessionMap.put(sessionId, session);
                
                // 타워 배치 정보도 함께 저장
                saveTowerPlacements(session);
                
                // 트랜잭션 커밋
                dbConnection.commit();
                System.out.println("세션 업데이트 완료 및 커밋: " + sessionId);
                
                success = true;
            } else {
                System.out.println("세션 업데이트 실패: 일치하는 세션을 찾을 수 없음: " + sessionId);
                dbConnection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("세션 업데이트 중 SQL 오류: " + e.getMessage());
            e.printStackTrace();
            
            // 에러 발생 시 롤백
            try {
                if (dbConnection != null) {
                    dbConnection.rollback();
                    System.out.println("세션 업데이트 롤백 완료");
                }
            } catch (Exception ex) {
                System.out.println("롤백 중 오류: " + ex.getMessage());
                ex.printStackTrace();
            }
        } finally {
            close();
        }
        
        return success;
    }
    
    /**
     * 타워 배치 정보 저장
     */
    private void saveTowerPlacements(Session session) {
        // DB에 타워 배치 정보 저장 로직 구현
        if (session.getPlacedTowers() != null && !session.getPlacedTowers().isEmpty()) {
            System.out.println("타워 배치 정보 저장됨: " + session.getSessionId() + 
                            ", 타워 수: " + session.getPlacedTowers().size());
        }
    }
    
    /**
     * 세션에 연결된 타워 정보 로드
     * @param sessionId 세션 ID
     * @return 타워 배치 목록
     */
    public List<TowerPlacement> loadTowerPlacements(int sessionId) {
        // 캐시에 있으면 캐시에서 가져옴
        Session session = sessionMap.get(sessionId);
        if (session != null && session.getPlacedTowers() != null && !session.getPlacedTowers().isEmpty()) {
            System.out.println("타워 배치 정보 캐시에서 로드: 세션 ID = " + sessionId);
            return session.getPlacedTowers();
        }
        
        // DB에서 타워 배치 정보 로드
        System.out.println("타워 배치 정보 DB에서 로드 시작: 세션 ID = " + sessionId);
        TowerDAO towerDAO = new TowerDAO();
        List<TowerPlacement> towers = towerDAO.getTowerPlacementsBySessionId(sessionId);
        
        if (towers != null && !towers.isEmpty()) {
            System.out.println("타워 배치 정보 " + towers.size() + "개 로드 완료: 세션 ID = " + sessionId);
            // 캐시 업데이트
            if (session != null) {
                session.setPlacedTowers(towers);
                sessionMap.put(sessionId, session);
            }
        } else {
            System.out.println("타워 배치 정보 없음: 세션 ID = " + sessionId);
            towers = new ArrayList<>();
        }
        
        return towers;
    }
    
    /**
     * 사용자의 세션 목록 조회
     * @param userId 사용자 ID
     * @return 사용자의 세션 목록
     */
    public List<Session> getUserSessions(int userId) {
        List<Session> result = new ArrayList<>();
        DBConnection dbConnection = DBConnection.getInstance();
        
        try {
            conn = dbConnection.getConnection();
            
            // 디버그용: DB 연결 상태 확인
            if (conn == null || conn.isClosed()) {
                System.out.println("DB 연결 실패: 연결이 null이거나 닫혀 있습니다");
                return result;
            }
            System.out.println("사용자 세션 목록 조회 시작: 사용자 ID = " + userId);
            
            // 사용자의 세션 목록 조회
            String sql = "SELECT * FROM GAME_SESSIONS WHERE USER_ID = ? ORDER BY LOAD_TIME DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int sessionId = rs.getInt("SESSION_ID");
                int life = rs.getInt("LIFE");
                int money = rs.getInt("MONEY");
                Timestamp loadTime = rs.getTimestamp("LOAD_TIME");
                int score = rs.getInt("SCORE");
                int wave = rs.getInt("WAVE");
                
                // 세션 객체 생성
                Session session = new Session(sessionId, userId, life, money, loadTime, score, wave);
                
                // 결과 목록에 추가
                result.add(session);
                System.out.println("세션 찾음: ID = " + sessionId + ", 생명력 = " + life + ", 자금 = " + money);
                
                // 캐시 업데이트
                sessionMap.put(sessionId, session);
            }
            
            // 사용자 세션 목록 캐시 업데이트
            List<Integer> sessionIds = new ArrayList<>();
            for (Session session : result) {
                sessionIds.add(session.getSessionId());
            }
            userSessionMap.put(userId, sessionIds);
            
            System.out.println("사용자 세션 목록 조회 완료: " + result.size() + "개 세션 찾음");
            
        } catch (SQLException e) {
            System.out.println("세션 목록 조회 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close();
        }
        
        return result;
    }
    
    /**
     * 특정 세션 정보 조회
     * @param sessionId 세션 ID
     * @return 세션 정보
     */
    public Session loadUserSessions(int sessionId) {
        // 캐시에 있으면 캐시에서 가져옴
        if (sessionMap.containsKey(sessionId)) {
            System.out.println("세션 캐시에서 로드: ID = " + sessionId);
            return sessionMap.get(sessionId);
        }
        
        Session session = null;
        DBConnection dbConnection = DBConnection.getInstance();
        
        try {
            conn = dbConnection.getConnection();
            
            // 디버그용: DB 연결 상태 확인
            if (conn == null || conn.isClosed()) {
                System.out.println("DB 연결 실패: 연결이 null이거나 닫혀 있습니다");
                return null;
            }
            System.out.println("특정 세션 조회 시작: 세션 ID = " + sessionId);
            
            // 세션 정보 조회
            String sql = "SELECT * FROM GAME_SESSIONS WHERE SESSION_ID = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sessionId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("USER_ID");
                int life = rs.getInt("LIFE");
                int money = rs.getInt("MONEY");
                Timestamp loadTime = rs.getTimestamp("LOAD_TIME");
                int score = rs.getInt("SCORE");
                int wave = rs.getInt("WAVE");
                
                // 세션 객체 생성
                session = new Session(sessionId, userId, life, money, loadTime, score, wave);
                System.out.println("세션 데이터 로드: ID = " + sessionId + ", 생명력 = " + life + ", 자금 = " + money);
                
                // 타워 배치 정보 로드
                List<TowerPlacement> towers = loadTowerPlacements(sessionId);
                session.setPlacedTowers(towers);
                
                // 캐시 업데이트
                sessionMap.put(sessionId, session);
            } else {
                System.out.println("세션을 찾을 수 없음: ID = " + sessionId);
            }
        } catch (SQLException e) {
            System.out.println("세션 조회 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close();
        }
        
        return session;
    }
}
