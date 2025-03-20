package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import config.DBConnection;
import model.Session;

public class SessionDAO {

	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

    // DB 연결 가져오기
    private Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    // 자원 해제 메서드
    private void close() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            System.out.println("자원 해제 중 오류: " + e.getMessage());
        }
    }


    // 새로운 게임 세션 생성 (자동 증가된 SESSION_ID 사용)
    // 새로운 게임 세션 생성 (이미 설정된 시퀀스와 트리거 사용)
    public int createSession(Session session) {
        String sql = "INSERT INTO GAME_SESSIONS (USER_ID, MONEY, LIFE, SCORE, WAVE) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        String getIdSql = "SELECT SESSION_SEQ.CURRVAL FROM DUAL";
        // SESSION_SEQ : DB 스키마 참고,  CURRVAL : 현재 값, *DUAL : 시퀀스 값이 들어있는 가상테이블* -> 중요함
        
        int sessionId = 0;
        
        try {
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // 세션 생성 시 초기 값 세팅
            pstmt.setInt(1, session.getUserId()); // 사용자 ID
            pstmt.setInt(2, session.getMoney()); // 초기 자금
            pstmt.setInt(3, session.getLife()); // 초기 생명력
            pstmt.setInt(4, 0); // 기본 점수 0으로 초기화
            pstmt.setInt(5, 1); // 첫 번째 웨이브부터 시작
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                // 현재 시퀀스 값을 조회하여 생성된 SESSION_ID 가져오기
                PreparedStatement seqStmt = conn.prepareStatement(getIdSql);
                ResultSet rs = seqStmt.executeQuery();
                if (rs.next()) {
                    sessionId = rs.getInt(1);
                    System.out.println("세션 생성 성공! SESSION_ID: " + sessionId);
                }
                rs.close();
                seqStmt.close();
            }
        } catch (Exception e) {
            System.out.println("세션 생성 실패: " + e.getMessage());
        } finally {
            close(); // 자원 해제
        }
        return sessionId;
    }
    
    
    // 현 게임 상태 저장
	public boolean updateSession(Session session) {
		String sql = "UPDATE GAME_SESSIONS SET MONEY = ?, LIFE = ?, SCORE = ?, WAVE = ? WHERE SESSION_ID = ?";

		boolean success = false;

		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, session.getMoney()); // 현재 자금
			pstmt.setInt(2, session.getLife()); // 현재 생명력
			pstmt.setInt(3, session.getScore()); // 현재 점수
            pstmt.setInt(4, session.getWave()); // 현재 웨이브
            pstmt.setInt(5, session.getSessionId()); // 세션 ID
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                System.out.println("세션 업데이트 성공! SESSION_ID: " + session.getSessionId());
                success = true;
            }
        } catch (Exception e) {
            System.out.println("세션 업데이트 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close();
        }
        
        return success;
	}
	
	
	// 해당 유저의 플레이 세션 불러오기
	public List<Session> getUserSessions(int userId) {
	    List<Session> sessions = new ArrayList<>();
	    String sql = "SELECT * FROM GAME_SESSIONS WHERE USER_ID = ? ORDER BY LOAD_TIME DESC";

	    try (Connection conn = getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, userId);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Session session = new Session();
	            session.setSessionId(rs.getInt("SESSION_ID"));
	            session.setUserId(rs.getInt("USER_ID"));
	            session.setMoney(rs.getInt("MONEY"));
	            session.setLife(rs.getInt("LIFE"));
	            session.setScore(rs.getInt("SCORE"));
	            session.setWave(rs.getInt("WAVE"));
	            session.setLoadTime(rs.getTimestamp("LOAD_TIME"));
	            sessions.add(session);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return sessions;
	}

	
	// 저장된 게임 정보 로드
	public Session loadUserSessions(int sessionId) {
		
		String sql = "Select * from GAME_SESSIONS where session_id = ?";
		
		
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, sessionId);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) { 
	            Session session = new Session();
	            session.setSessionId(rs.getInt("session_id"));
	            session.setUserId(rs.getInt("user_id"));
	            session.setMoney(rs.getInt("money"));
	            session.setLife(rs.getInt("life"));
	            session.setWave(rs.getInt("wave"));
	            session.setScore(rs.getInt("score"));
	            session.setLoadTime(rs.getTimestamp("load_time"));
	            
	            return session;
	        }
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return null;
		
	}

	
	
}
