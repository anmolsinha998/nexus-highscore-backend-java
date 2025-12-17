package com.nexus.highscore.dto;

import java.util.List;

public class FriendLeaderboardResponse {
    private String userId;
    private String gameId;
    private String levelId;
    private int limit;
    private List<ScoreEntry> friendTopScores;

    public FriendLeaderboardResponse(String userId, String gameId, String levelId, int limit, List<ScoreEntry> friendTopScores) {
        this.userId = userId;
        this.gameId = gameId;
        this.levelId = levelId;
        this.limit = limit;
        this.friendTopScores = friendTopScores;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getLevelId() { return levelId; }
    public void setLevelId(String levelId) { this.levelId = levelId; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public List<ScoreEntry> getFriendTopScores() { return friendTopScores; }
    public void setFriendTopScores(List<ScoreEntry> friendTopScores) { this.friendTopScores = friendTopScores; }
}
