package model;

import java.util.Date;

public class Ranking {
	private int rankingId;
	private int userId;
	private int score;
	private Date recordDate;

	public Ranking() {
	}
	//랭킹 집어넣는 생성자
	public Ranking(int rankingId, int userId, int score, Date recordDate) {
		super();
		this.rankingId = rankingId;
		this.userId = userId;
		this.score = score;
		this.recordDate = recordDate;
	}
	
	//랭킹 상위 10위 생성자
	public Ranking(int userId, int score) {
		this.userId = userId;
		this.score = score;
	}
	
	//특정 유저 랭킹 생성자
	public Ranking(int userId) {
		this.userId = userId;
	}

	
	
	//getter setter 메소드
	public int getRankingId() {
		return rankingId;
	}

	public void setRankingId(int rankingId) {
		this.rankingId = rankingId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

}
