package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    /**
     * 새로운 게임 세션 생성
     * @param session 생성할 세션 정보 (userId 필수)
     * @return 생성된 세션 ID
     */
    public int createSession(Session session) {
        String sql = "INSERT INTO GAME_SESSIONS (USER_ID, MONEY, LIFE, SCORE, WAVE) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        String getIdSql = "SELECT SESSION_SEQ.CURRVAL FROM DUAL";
        
        int sessionId = 0;
        
        try {
            conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // 세션 생성 시 값 세팅
            pstmt.setInt(1, session.getUserId());
            pstmt.setInt(2, session.getMoney());
            pstmt.setInt(3, session.getLife());
            pstmt.setInt(4, session.getScore());
            pstmt.setInt(5, session.getWave());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                // 현재 시퀀스 값을 조회하여 생성된 SESSION_ID 가져오기
                PreparedStatement seqStmt = conn.prepareStatement(getIdSql);
                ResultSet rs = seqStmt.executeQuery();
                if (rs.next()) {
                    sessionId = rs.getInt(1);
                }
                rs.close();
                seqStmt.close();
            }
        } catch (Exception e) {
            System.out.println("세션 생성 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close();
        }
        return sessionId;
    }
    
    

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
}
