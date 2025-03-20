package service;

import java.util.ArrayList;

import dao.RankingDAO;
import model.Ranking;

public class RankingServiceImpl implements RankingService {

	private RankingDAO rankingDAO;

	public RankingServiceImpl() {
		this.rankingDAO = new RankingDAO();
	}

	@Override
	public boolean addRanking(Ranking ranking) {

		return rankingDAO.addRanking(ranking);
	}

	@Override
	public ArrayList<Ranking> getTopRankings() {
		return rankingDAO.getTopRankings();
	}

	@Override
	public ArrayList<Ranking> getUserRankings(int userId) {
		// TODO Auto-generated method stub
		return rankingDAO.getUserRankings(userId);
	}

}