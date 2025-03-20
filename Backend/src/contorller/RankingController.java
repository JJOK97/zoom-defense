package contorller;

import java.util.ArrayList;

import model.Ranking;
import service.RankingService;
import service.RankingServiceImpl;

public class RankingController {

	private RankingService rankingService;

	public RankingController(){
		this.rankingService = new RankingServiceImpl();}

	/**
	 * 랭킹 등록
	 * @param userId 사용자 ID
	 * @param score 점수
	 * @return 등록 성공 여부
	 */
	public boolean addRanking(int userId, int score) {

		Ranking ranking = new Ranking(userId, score);

		return rankingService.addRanking(ranking);
	}

	/**
	 * 상위 10위 랭킹 조회
	 * @return 상위 10위 랭킹 목록
	 */
	public ArrayList<Ranking> getTopRankings() {
		return rankingService.getTopRankings();
	}

	/**
	 * 특정 사용자의 랭킹 기록 조회
	 * @param userId 사용자 ID
	 * @return 사용자의 랭킹 기록 목록
	 */
	public ArrayList<Ranking> getUserRankings(int userId) {

		return rankingService.getUserRankings(userId);

	}

}