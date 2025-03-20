package contorller;

import java.util.ArrayList;

import model.Ranking;
import service.RankingService;
import service.RankingServiceImpl;

public class RankingController {

	private RankingService rankingService;

	public RankingController(){
		this.rankingService = new RankingServiceImpl();}

	public boolean addRanking(int userId, int score) {

		Ranking ranking = new Ranking(userId, score);

		return rankingService.addRanking(ranking);
	}

	// 상위 랭킹 조회(10명)
	public ArrayList<Ranking> getTopRankings() {

		// if에 (아무 비교값이 없으면 boolean타입이다..
		// 얘는 리스트로 변경해줬기 때문에 boolean형식의 비교가 불가능
//		if(rankingService.getTopRankings()) {
//			System.out.println("getTopRankings 작동 잘됨");
//		} else {
//			System.out.println("getUserRankings안됨");
//		}
		// 위 코드를 제거하거나 아니면 List를 구별하는 코드로 변

		// 리턴이 문제랍니다.
		// 왜?
		// 왜왜왜왜왜왜?????????
		// 나는 ArrayList로 받고싶은데
		// 어레이리스트로 받고싶으니 서비스도 어레이리스트로 변경하자!!!
		return rankingService.getTopRankings();
	}

	// 특정유저의 랭킹 기록조회
	public ArrayList<Ranking> getUserRankings(int userId) {

		return rankingService.getUserRankings(userId);

	}

}
