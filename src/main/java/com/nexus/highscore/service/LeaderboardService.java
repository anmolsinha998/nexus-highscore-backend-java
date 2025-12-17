package com.nexus.highscore.service;

import com.nexus.highscore.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LeaderboardService {

    @Autowired
    private StringRedisTemplate redisTemplate; // Redis Client

    @Autowired
    private JdbcTemplate jdbcTemplate; // SQL Client

    /**
     * Retrieves the Global Leaderboard from Redis (Sorted Sets).
     * This is optimized for high-speed reads of top N scores.
     */
    public GlobalLeaderboardResponse getGlobalLeaderboard(String gameId, String levelId, int limit, int offset) {
        String key = "leaderboard:" + gameId + ":" + levelId;

        // Fetch range from Redis (Ordered High to Low)
        Set<ZSetOperations.TypedTuple<String>> range = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, offset, offset + limit - 1);

        List<ScoreEntry> entries = new ArrayList<>();
        int rank = offset + 1;

        if (range != null) {
            for (ZSetOperations.TypedTuple<String> tuple : range) {
                String userId = tuple.getValue();
                long score = tuple.getScore() != null ? tuple.getScore().longValue() : 0;
                
                // In a production app, you might fetch user metadata (name, avatar) from a cache here
                entries.add(new ScoreEntry(rank++, userId, String.valueOf(score)));
            }
        }

        return new GlobalLeaderboardResponse(gameId, levelId, entries);
    }

    /**
     * Retrieves the Friends Leaderboard from PostgreSQL.
     * This performs a relational join to filter scores by the user's social graph.
     */
    public FriendLeaderboardResponse getFriendLeaderboard(String userId, String gameId, String levelId, int limit) {
        
        // Complex Query: Get scores for user AND their friends
        String sql = """
            SELECT s.user_id, s.score
            FROM scores s
            WHERE s.game_id = ? AND s.level_id = ?
            AND (
                s.user_id = ? 
                OR s.user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)
            )
            ORDER BY s.score DESC
            LIMIT ?
        """;

        // Execute Query
        List<ScoreEntry> entries = jdbcTemplate.query(sql, (rs, rowNum) -> 
            new ScoreEntry(
                rowNum + 1, // rank
                rs.getString("user_id"),
                String.valueOf(rs.getLong("score"))
            ),
            gameId, levelId, userId, userId, limit
        );

        return new FriendLeaderboardResponse(userId, gameId, levelId, limit, entries);
    }

    /**
     * Submits a score to both systems (Hybrid Write Path).
     * 1. Redis: For real-time global ranking.
     * 2. PostgreSQL: For persistence and friend/social queries.
     */
    public void submitScore(String userId, String gameId, String levelId, long score) {
        String key = "leaderboard:" + gameId + ":" + levelId;

        // 1. Update Redis (Global Rank)
        // ZADD maintains the sorted set.
        redisTemplate.opsForZSet().add(key, userId, score);

        // 2. Update PostgreSQL (Persistence & Friends View)
        // Upsert Pattern: Insert, or Update if exists and new score is higher
        String sql = """
            INSERT INTO scores (user_id, game_id, level_id, score, updated_at)
            VALUES (?, ?, ?, ?, NOW())
            ON CONFLICT (user_id, game_id, level_id) 
            DO UPDATE SET 
                score = EXCLUDED.score, 
                updated_at = NOW()
            WHERE EXCLUDED.score > scores.score
        """;
        
        jdbcTemplate.update(sql, userId, gameId, levelId, score);
    }
}
