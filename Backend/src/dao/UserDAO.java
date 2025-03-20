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
    
    /**
     * 로그인 인증 메소드
     * @param loginId 체크할 아이디
     * @param password 체크할 비밀번호
     * @return 로그인 성공 여부 (true: 성공, false: 실패)
     */
    public boolean validateLogin(String loginId, String password) {
    	boolean validate = false;
    	String sql = "SELECT * FROM USERS WHERE USER_LOGIN_ID = ? AND PASSWORD = ?";
    	
    	try {
    		conn = getConnection();
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, loginId);
    		pstmt.setString(2, password);
    		
    		rs = pstmt.executeQuery();
    		
    		if(rs.next()) {
    			validate = true;
    		} else {
    			System.out.println("다시 시도해주세요");
    		}
    		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
    	return validate;
    }
    
    /**
     * 사용자 정보 조회
     * @param getUserById 체크할 아이디
     * @return 정보 조회 성공 여부 (true: 성공, false: 실패)
     */
    public boolean getUserById(int userId, String loginId, String nickname) {
    	boolean UserBy = false;
    	String sql = "SELECT USER_ID,USER_LOGIN_ID,NICKNAME FROM USERS";
    	
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
			System.out.println("=====사용자 목록=====");
			System.out.println("UserID\tLoginID\t\tNickname");
			do {
				int userIdResult = rs.getInt("USER_ID");
				String loginIdResult = rs.getString("USER_LOGIN_ID");
		        String nicknameResult = rs.getString("NICKNAME");
		        
		        System.out.println(userIdResult + "\t" + loginIdResult + "\t" + nicknameResult);
				} while(rs.next());
				UserBy = true;
			} else {
				System.out.println("사용자 목록 출력을 실패했습니다. 다시 시도해 주세요!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
    	return UserBy;
    }
    
} 