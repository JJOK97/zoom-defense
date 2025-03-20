package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.DBConnection;
import model.TowerPlacement;
import model.User;
import service.TowerPlacementServicelmpl;

public class TowerPlacementDAO {
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
     * 게임 맵에 타워 설치
     * @param 
     * @return success 성공 여부
     */
    public boolean placeTower(TowerPlacement tower) {
    	boolean success = false;
    	String sql = "INSERT INTO TOWER_PLACEMENTS VALUES(?,?,?,?) ";
    	
    	try {
    		conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, tower.getPlacementId());
            pstmt.setInt(2, tower.getSessionId());
            pstmt.setInt(3, tower.getPositionX());
            pstmt.setInt(4, tower.getPositionY());
            
            int result = pstmt.executeUpdate();
            
            if(result > 0) {
            	success = true;
            	System.out.println("타워 배치 성공");
            } else {
            	System.out.println("타워 배치 실패");
            }
			
		} catch (SQLException e) {
			System.out.println("타워 배치 실패: " + e.getMessage());
        } finally {
            close();
        }
        return success;
        
    }
}
