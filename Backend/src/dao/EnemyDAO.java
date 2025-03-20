package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.DBConnection;
import model.Enemy;
import model.Session;

public class EnemyDAO {
    
    // DB 연결 가져오기
    private Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    // 모든 적 정보를 가져오는 메서드
    public List<Enemy> getAllEnemies() {
        List<Enemy> enemies = new ArrayList<>();
        String sql = "SELECT * FROM enemies";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                Enemy enemy = new Enemy();  // 기본 생성자 사용
                
                // DB에서 가져온 값을 Enemy 객체에 설정
                enemy.setEnemy_id(rs.getInt("enemy_id"));
                enemy.setEnemy_name(rs.getString("enemy_name"));
                enemy.setHealth(rs.getInt("health"));
                enemy.setSpeed(rs.getInt("speed"));
                enemy.setReward(rs.getInt("reward"));
                enemy.setDamage(rs.getInt("damage"));
                
                enemies.add(enemy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("적 목록을 가져오는 중 오류 발생: " + e.getMessage());
        }

        return enemies;
    }


	public Enemy getEnemyById(int enemyId) {
		String sql = "select * from "
				+ "ENEMIES where enemy_id = ?";
		
		
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, enemyId);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) { 
	            Enemy enemy = new Enemy();
	            
	            enemy.setEnemy_id(enemyId);
	            enemy.setEnemy_name(rs.getString("enemy_name"));
	            enemy.setHealth(rs.getInt("health"));
	            enemy.setDamage(rs.getInt("damage"));
	            enemy.setReward(rs.getInt("reward"));
	            enemy.setSpeed(rs.getInt("speed"));
	            
	            
	            return enemy;
	        }
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
}
