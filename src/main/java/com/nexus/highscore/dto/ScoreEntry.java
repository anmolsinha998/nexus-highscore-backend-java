package com.nexus.highscore.dto;

public class ScoreEntry {
    private int rank;
    private String userId;
    private String score;

    public ScoreEntry(int rank, String userId, String score) {
        this.rank = rank;
        this.userId = userId;
        this.score = score;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }
}
