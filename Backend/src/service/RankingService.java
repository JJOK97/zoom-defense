package service;

import java.util.ArrayList;

import model.Ranking;



public interface RankingService {

	boolean addRanking(Ranking ranking);

	ArrayList<Ranking> getTopRankings();

	ArrayList<Ranking> getUserRankings(int userId);

}