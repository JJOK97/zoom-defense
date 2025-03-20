package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import config.DBConnection;
import model.Tower;

public class TowerDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	// DB연결 가져오기
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
     * 1단계 타워 목록 조회
     * @param 타워 레벨별 정보 목록 조회	
	 * @return 랜덤으로 선택된 타워 이름
     */
	public Tower getFirstTower() {
	    Tower tower = null;
	    List<Tower> towerList = new ArrayList<>();
	    
	    // SQL 쿼리: 레벨 1 타워 목록 가져오기
	    String sql = "SELECT * FROM TOWERS WHERE TOWER_LEVEL = 1";
	    
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        
	        // 데이터베이스에서 타워 정보를 리스트에 추가
	        while (rs.next()) {
	            int towerId = rs.getInt("TOWER_ID");
	            String towerName = rs.getString("TOWER_NAME");
	            int towerLevel = rs.getInt("TOWER_LEVEL");
	            int damage = rs.getInt("DAMAGE");
	            int range = rs.getInt("RANGE");
	            int attackSpeed = rs.getInt("ATTACK_SPEED");
	            int cost = rs.getInt("COST");
	            int upgradeCost = rs.getInt("UPGRADE_COST");
	            
	            // 타워 객체 생성 후 리스트에 추가
	            Tower t = new Tower(towerId, towerName, towerLevel, damage, range, attackSpeed, cost, upgradeCost);
	            towerList.add(t);
	        }
	        
	        // 타워 리스트가 비어 있지 않으면 랜덤으로 하나 선택
	        if (!towerList.isEmpty()) {
	            Random random = new Random();
	            int randomIndex = random.nextInt(towerList.size());  // 랜덤 인덱스 생성
	            System.out.println(towerList.size());
	            System.out.println(randomIndex);
	            tower = towerList.get(randomIndex);  				// 랜덤 인덱스를 통해 타워 선택
	            System.out.println("랜덤으로 선택된 타워: " + tower.getTowerId());
	        } else {
	            System.out.println("레벨 1 타워가 없습니다.");
	        }
	        
	    } catch (SQLException e) {
	        System.out.println("1레벨 타워 생성 실패");
	        e.printStackTrace();
	    } finally {
	        close();
	    }
	    
	    return tower;  // 랜덤으로 선택된 타워 객체 반환
	}

	
	
	/**
     * 2단계 타워 목록 조회
     * @param 타워 레벨별 정보 목록 조회	
	 * @return 랜덤으로 선택된 타워 이름
     */
	
	public Tower getSecondTower() {
	    Tower tower = null;
	    List<Tower> towerList = new ArrayList<>();
	    
	    // SQL 쿼리: 레벨 2 타워 목록 가져오기
	    String sql = "SELECT * FROM TOWERS WHERE TOWER_LEVEL = 2";
	    
	    try {
	    	conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
		while (rs.next()) {
			int towerId = rs.getInt("TOWER_ID");
		    String towerName = rs.getString("TOWER_NAME");
		    int towerLevel = rs.getInt("TOWER_LEVEL");
		    int damage = rs.getInt("DAMAGE");
		    int range = rs.getInt("RANGE");
		    int attackSpeed = rs.getInt("ATTACK_SPEED");
		    int cost = rs.getInt("COST");
		    int upgradeCost = rs.getInt("UPGRADE_COST");	
			
		    Tower t = new Tower(towerId, towerName, towerLevel, damage, range, attackSpeed, cost, upgradeCost);
            towerList.add(t);
        }
		
        if (!towerList.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(towerList.size());
            System.out.println(towerList.size());
            System.out.println(randomIndex);
            tower = towerList.get(randomIndex);
            System.out.println("랜덤으로 선택된 타워: " + tower.getTowerLevel());
        } else {
            System.out.println("레벨 2 타워가 없습니다.");
        }
			
		} catch (SQLException e) {
			System.out.println("2레벨 타워 생성 실패");
	        e.printStackTrace();
	    } finally {
	        close();
	    }
	    return tower;
	}
	
	
	/**
     * 3단계 타워 목록 조회
     * @param 타워 레벨별 정보 목록 조회	
	 * @return 랜덤으로 선택된 타워 이름
     */
	
	public Tower getThirdTower() {
	    Tower tower = null;
	    List<Tower> towerList = new ArrayList<>();
	    
	    // SQL 쿼리: 레벨 3 타워 목록 가져오기
	    String sql = "SELECT * FROM TOWERS WHERE TOWER_LEVEL = 3";
	    
	    try {
	    	conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
		while (rs.next()) {
			int towerId = rs.getInt("TOWER_ID");
		    String towerName = rs.getString("TOWER_NAME");
		    int towerLevel = rs.getInt("TOWER_LEVEL");
		    int damage = rs.getInt("DAMAGE");
		    int range = rs.getInt("RANGE");
		    int attackSpeed = rs.getInt("ATTACK_SPEED");
		    int cost = rs.getInt("COST");
		    int upgradeCost = rs.getInt("UPGRADE_COST");	
			
		    Tower t = new Tower(towerId, towerName, towerLevel, damage, range, attackSpeed, cost, upgradeCost);
            towerList.add(t);
        }
		
        if (!towerList.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(towerList.size());
            System.out.println(towerList.size());
            System.out.println(randomIndex);
            tower = towerList.get(randomIndex);
            System.out.println("랜덤으로 선택된 타워: " + tower.getTowerLevel());
        } else {
            System.out.println("레벨 3 타워가 없습니다.");
        }
			
		} catch (SQLException e) {
			System.out.println("3레벨 타워 생성 실패");
	        e.printStackTrace();
	    } finally {
	        close();
	    }
	    return tower;
	}
}
