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
import model.TowerPlacement;

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
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			// Connection은 싱글톤으로 관리하므로 여기서 닫지 않음
		} catch (SQLException e) {
			System.out.println("자원 해제 중 오류: " + e.getMessage());
		}
	}

	/**
	 * 1단계 타워 목록 조회
	 * 
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
				Tower t = new Tower(towerId, towerName, damage, range, attackSpeed, cost, upgradeCost, towerLevel);
				towerList.add(t);
			}

			// 타워 리스트가 비어 있지 않으면 랜덤으로 하나 선택
			if (!towerList.isEmpty()) {
				Random random = new Random();
				int randomIndex = random.nextInt(towerList.size()); // 랜덤 인덱스 생성
				System.out.println(towerList.size());
				System.out.println(randomIndex);
				tower = towerList.get(randomIndex); // 랜덤 인덱스를 통해 타워 선택
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

		return tower; // 랜덤으로 선택된 타워 객체 반환
	}

	/**
	 * 2단계 타워 목록 조회
	 * 
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

				Tower t = new Tower(towerId, towerName, damage, range, attackSpeed, cost, upgradeCost, towerLevel);
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
	 * 
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
				
				Tower t = new Tower(towerId, towerName, damage, range, attackSpeed, cost, upgradeCost, towerLevel);
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

	/**
	 * 게임 맵에 타워 설치
	 * 
	 * @param tower 배치할 타워 정보
	 * @return success 성공 여부
	 */
	public boolean placeTower(TowerPlacement tower) {
		boolean success = false;
		String sql = "INSERT INTO TOWER_PLACEMENTS (TOWER_ID, SESSION_ID, POSITION_X, POSITION_Y) VALUES(?, ?, ?, ?)";

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			
			// 디버깅용 출력
			System.out.println("타워 배치 시도: TowerID=" + tower.getTowerId() + 
							   ", SessionID=" + tower.getSessionId() + 
							   ", X=" + tower.getPositionX() + 
							   ", Y=" + tower.getPositionY());
			
			pstmt.setInt(1, tower.getTowerId());
			pstmt.setInt(2, tower.getSessionId());
			pstmt.setInt(3, tower.getPositionX());
			pstmt.setInt(4, tower.getPositionY());

			int result = pstmt.executeUpdate();

			if (result > 0) {
				success = true;
				System.out.println("타워 배치 성공: " + tower.getTowerId() + " at (" + 
								  tower.getPositionX() + "," + tower.getPositionY() + ")");
			} else {
				System.out.println("타워 배치 실패: 영향받은 행이 없음");
			}

		} catch (SQLException e) {
			System.out.println("타워 배치 실패 (SQL 오류): " + e.getMessage());
			e.printStackTrace();
		} finally {
			close();
		}
		return success;
	}

	/**
	 * 게임 맵에 타워 업그레이드
	 * 
	 * @param
	 * @return success 성공 여부
	 */
	public boolean upgradeTower(int sessionId, int x, int y) {
		boolean success = false;
		String sql = "";

		try {
			conn = getConnection();

			// 현재 타워 ID를 조회
			int towerId = getTowerId(sessionId, x, y);

			// 타워 ID가 1일 때
			if (towerId == 1) {
				Tower second = getSecondTower(); // 두 번째 타워를 가져오기 (타워 2로 업그레이드)
				int secondId = second.getTowerId();

				sql = "UPDATE TOWER_PLACEMENTS SET TOWER_ID = ? WHERE SESSION_ID = ? AND POSITION_X = ? AND POSITION_Y = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, secondId); // 두 번째 타워의 ID를 설정
				pstmt.setInt(2, sessionId);
				pstmt.setInt(3, x);
				pstmt.setInt(4, y);

			}
			// 타워 ID가 2일 때
			else if (towerId == 2) {
				Tower third = getThirdTower(); // 세 번째 타워를 가져오기 (타워 3으로 업그레이드)
				int thirdId = third.getTowerId();

				sql = "UPDATE TOWER_PLACEMENTS SET TOWER_ID = ? WHERE SESSION_ID = ? AND POSITION_X = ? AND POSITION_Y = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, thirdId); // 세 번째 타워의 ID를 설정
				pstmt.setInt(2, sessionId);
				pstmt.setInt(3, x);
				pstmt.setInt(4, y);

			}
			// 타워 ID가 3일 때, 업그레이드가 불가능하므로 실패 처리
			else if (towerId == 3) {
				System.out.println("타워는 더 이상 업그레이드할 수 없습니다.");
				return false; // 타워 3인 경우 업그레이드 불가
			}
			// 타워 ID가 1 또는 2가 아닌 경우 (잘못된 타워 ID)
			else {
				System.out.println("타워 레벨이 최대치입니다.");
				return false;
			}

			// SQL 실행
			int result = pstmt.executeUpdate();

			if (result > 0) {
				success = true;
				System.out.println("타워 업그레이드 성공");
			} else {
				System.out.println("타워 업그레이드 실패");
			}

		} catch (SQLException e) {
			System.out.println("타워 업그레이드 중 오류 발생: " + e.getMessage());
		} finally {
			close();
		}
		return success;
	}

	public int getTowerId(int sessionId, int x, int y) {
		int towerId = 0;
		String sql = "SELECT TOWER_ID FROM TOWER_PLACEMENTS WHERE SESSION_ID = ? AND POSITION_X = ? AND POSITION_Y = ?";

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sessionId);
			pstmt.setInt(2, x);
			pstmt.setInt(3, y);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				towerId = rs.getInt("TOWER_ID");
				System.out.println("타워 ID 조회 결과: " + towerId + " at (" + x + "," + y + ")");
			} else {
				System.out.println("타워를 찾을 수 없음: " + x + "," + y);
			}

		} catch (SQLException e) {
			System.out.println("타워 ID 조회 실패: " + e.getMessage());
			e.printStackTrace();
		} finally {
			close();
		}

		return towerId;
	}

	/**
	 * 타워 ID로 타워 정보 조회
	 * @param towerId 타워 ID
	 * @return 타워 정보
	 */
	public Tower getTowerById(int towerId) {
		Tower tower = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			String sql = "SELECT * FROM TOWERS WHERE TOWER_ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, towerId);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				tower = new Tower();
				tower.setTowerId(rs.getInt("TOWER_ID"));
				tower.setTowerName(rs.getString("TOWER_NAME"));
				tower.setTowerLevel(rs.getInt("TOWER_LEVEL"));
				tower.setDamage(rs.getInt("DAMAGE"));
				tower.setRange(rs.getInt("RANGE"));
				tower.setAttackSpeed(rs.getInt("ATTACK_SPEED"));
				tower.setCost(rs.getInt("COST"));
				tower.setUpgradeCost(rs.getInt("UPGRADE_COST"));
				
				System.out.println("타워 정보 조회됨: ID=" + tower.getTowerId() + 
								   ", 이름=" + tower.getTowerName() + 
								   ", 레벨=" + tower.getTowerLevel());
			} else {
				System.out.println("타워 ID " + towerId + "에 해당하는 타워 정보가 없음");
			}
		} catch (SQLException e) {
			System.out.println("타워 정보 조회 중 에러 발생: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return tower;
	}

	/**
	 * 세션 ID로 타워 배치 정보 목록 조회
	 * @param sessionId 세션 ID
	 * @return 타워 배치 정보 목록
	 */
	public List<TowerPlacement> getTowerPlacementsBySessionId(int sessionId) {
		List<TowerPlacement> placements = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			String sql = "SELECT * FROM TOWER_PLACEMENTS WHERE SESSION_ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sessionId);
			rs = pstmt.executeQuery();
			
			System.out.println("세션 ID " + sessionId + "에 대한 타워 배치 정보 조회 중...");
			
			while (rs.next()) {
				TowerPlacement placement = new TowerPlacement();
				placement.setTowerId(rs.getInt("TOWER_ID"));
				placement.setSessionId(rs.getInt("SESSION_ID"));
				placement.setPositionX(rs.getInt("POSITION_X"));
				placement.setPositionY(rs.getInt("POSITION_Y"));
				
				placements.add(placement);
				System.out.println("타워 배치 정보 찾음: TowerId=" + placement.getTowerId() + 
								   ", X=" + placement.getPositionX() + 
								   ", Y=" + placement.getPositionY());
			}
			
			System.out.println("총 " + placements.size() + "개의 타워 배치 정보 조회됨");
		} catch (SQLException e) {
			System.out.println("타워 배치 정보 조회 중 에러 발생: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return placements;
	}

}
