package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.DBConnection;
import model.User;

/**
 * 사용자 정보를 데이터베이스에 저장하고 조회하는 DAO 클래스
 */
public class UserDAO {
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
     * 회원가입 메소드
     * @param user 등록할 사용자 정보
     * @return 등록 성공 여부
     */
    public boolean registerUser(User user) {
        boolean success = false;
        String sql = "INSERT INTO USERS(USER_LOGIN_ID, PASSWORD, NICKNAME) VALUES(?, ?, ?)";
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getLoginId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getNickname());
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                success = true;
                System.out.println("회원가입 성공!");
            }
        } catch (SQLException e) {
            System.out.println("회원가입 실패: " + e.getMessage());
        } finally {
            close();
        }
        return success;
    }
    
    /**
     * 아이디 중복 체크 메소드
     * @param loginId 체크할 아이디
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
    public boolean checkDuplicateId(String loginId) {
        boolean isDuplicate = false;
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_LOGIN_ID = ?";
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loginId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                isDuplicate = true;
            }
        } catch (SQLException e) {
            System.out.println("아이디 중복 체크 실패: " + e.getMessage());
        } finally {
            close();
        }
        return isDuplicate;
    }
} 