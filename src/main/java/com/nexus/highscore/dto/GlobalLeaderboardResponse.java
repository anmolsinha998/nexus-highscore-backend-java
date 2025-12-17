package com.nexus.highscore.dto;

import java.util.List;

public class GlobalLeaderboardResponse {
    private String gameId;
    private String levelId;
    private List<ScoreEntry> scores;

    public GlobalLeaderboardResponse(String gameId, String levelId, List<ScoreEntry> scores) {
        this.gameId = gameId;
        this.levelId = levelId;
        this.scores = scores;
    }

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getLevelId() { return levelId; }
    public void setLevelId(String levelId) { this.levelId = levelId; }

    public List<ScoreEntry> getScores() { return scores; }
    public void setScores(List<ScoreEntry> scores) { this.scores = scores; }
}
