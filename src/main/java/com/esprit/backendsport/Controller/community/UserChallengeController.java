package com.esprit.backendsport.Controller.community;

import com.esprit.backendsport.Entities.community.ChallengeDTO;
import com.esprit.backendsport.Services.community.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community/challenges")
public class UserChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @GetMapping
    public ResponseEntity<List<ChallengeDTO>> getAllChallenges() {
        List<ChallengeDTO> challenges = challengeService.getAllChallenges().stream()
                .map(ChallengeDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/joined")
    public ResponseEntity<List<Long>> getJoinedChallengeIds(@RequestParam Long userId) {
        List<Long> joinedIds = challengeService.getJoinedChallengeIds(userId);
        return ResponseEntity.ok(joinedIds);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinChallenge(@PathVariable Long id, @RequestParam Long userId) {
        try {
            challengeService.joinChallenge(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveChallenge(@PathVariable Long id, @RequestParam Long userId) {
        try {
            challengeService.leaveChallenge(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
