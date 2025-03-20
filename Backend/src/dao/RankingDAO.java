package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DBConnection;
import model.Ranking;

public class RankingDAO {
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
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			// Connection은 싱글톤으로 관리하므로 여기서 닫지 않음
		} catch (SQLException e) {
			System.out.println("자원 해제 중 오류: " + e.getMessage());
		}
	}
	//랭킹 등록
	
	public boolean addRanking(Ranking ranking) {
		boolean success = false;

		String sql = "INSERT INTO RANKINGS(USER_ID, SCORE) VALUES(?, ?)";

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, ranking.getUserId());
			pstmt.setInt(2, ranking.getScore());

			int result = pstmt.executeUpdate();
			if (result > 0) {
				success = true;
				System.out.println("랭킹등록 성공!");
			}
		} catch (SQLException e) {
			System.out.println("랭킹등록 실패: ");
		} finally {
			close();
		}
		return success;

	}
	
	// 상위 랭킹 조회(10명)

	public ArrayList<Ranking> getTopRankings() {
		ArrayList<Ranking> resultList = new ArrayList<>();
		String sql = "SELECT *"
				+ "FROM (SELECT USER_ID, SCORE FROM RANKINGS ORDER BY SCORE DESC)"
				+ "WHERE ROWNUM <= 10";

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				int userId = rs.getInt("USER_ID");
				int score = rs.getInt("SCORE");
				resultList.add(new Ranking(userId, score));

			}

		} catch (SQLException e) {
			System.out.println("상위 랭킹 조회 실패: ");
		} finally {
			close();
		}
		return resultList;

	}

	// 특정유저의 랭킹 기록조회

	public ArrayList<Ranking> getUserRankings(int userId) {
		ArrayList<Ranking> resultList = new ArrayList<>();

		String sql = "SELECT * FROM RANKINGS WHERE USER_ID=? ";
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			rs = pstmt.executeQuery();
			
			
			
			while (rs.next()) {
				Date recordDate = rs.getDate("RECORD_DATE");
				int rankingId = rs.getInt("RANKING_ID");
				int score = rs.getInt("SCORE");
				resultList.add(new Ranking(rankingId,userId,score,recordDate));
				}
			
			
		} catch (SQLException e) {
			System.out.println("랭킹 기록 조회 실패: ");
		} finally {
			close();
		}
		return resultList;
	}


}
