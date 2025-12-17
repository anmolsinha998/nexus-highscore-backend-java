package com.nexus.highscore.controller;

import com.nexus.highscore.dto.*;
import com.nexus.highscore.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/{gameId}/levels/{levelId}")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    // Requirement 5.b: Global Leaderboard
    @GetMapping("/globalLeader")
    public ResponseEntity<GlobalLeaderboardResponse> getGlobalLeaderboard(
            @PathVariable String gameId,
            @PathVariable String levelId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        GlobalLeaderboardResponse response = leaderboardService.getGlobalLeaderboard(gameId, levelId, limit, offset);
        return ResponseEntity.ok(response);
    }

    // Requirement 5.c: Friends Leaderboard
    @GetMapping("/leaderboard/friends")
    public ResponseEntity<FriendLeaderboardResponse> getFriendLeaderboard(
            @PathVariable String gameId,
            @PathVariable String levelId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit) {

        FriendLeaderboardResponse response = leaderboardService.getFriendLeaderboard(userId, gameId, levelId, limit);
        return ResponseEntity.ok(response);
    }

    // Requirement 5.a: Submit Score
    @PostMapping("/scores")
    public ResponseEntity<String> submitScore(
            @PathVariable String gameId,
            @PathVariable String levelId,
            @RequestBody ScoreSubmissionRequest request) {

        leaderboardService.submitScore(request.getUserId(), gameId, levelId, Long.parseLong(request.getScore()));
        return ResponseEntity.ok("Score submitted successfully");
    }
}
