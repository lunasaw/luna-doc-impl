package io.github.lunasaw.leaderboard;

public interface LeaderBoardService {
    void updateScore(String user, double score);

    String getLeaderboard(int start, int end);
}