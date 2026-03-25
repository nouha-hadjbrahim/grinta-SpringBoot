package com.esprit.backendsport.Controller.community;

import com.esprit.backendsport.Entities.community.Challenge;
import com.esprit.backendsport.Entities.community.ChallengeDTO;
import com.esprit.backendsport.Entities.community.UserSummaryDTO;
import com.esprit.backendsport.Services.community.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/challenges")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @GetMapping
    public ResponseEntity<List<ChallengeDTO>> getAllChallenges() {
        List<ChallengeDTO> challenges = challengeService.getAllChallenges().stream()
                .map(ChallengeDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDTO> getChallengeById(@PathVariable Long id) {
        return challengeService.getChallengeById(id)
                .map(ChallengeDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<UserSummaryDTO>> getChallengeParticipants(@PathVariable Long id) {
        List<UserSummaryDTO> participants = challengeService.getChallengeParticipants(id).stream()
                .map(user -> UserSummaryDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(participants);
    }

    @PostMapping
    public ResponseEntity<ChallengeDTO> createChallenge(@RequestBody ChallengeDTO challengeDTO) {
        Challenge challenge = challengeDTO.toEntity();
        Challenge savedChallenge = challengeService.createChallenge(challenge);
        return ResponseEntity.ok(ChallengeDTO.fromEntity(savedChallenge));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChallengeDTO> updateChallenge(@PathVariable Long id, @RequestBody ChallengeDTO challengeDTODetails) {
        try {
            Challenge challengeDetails = challengeDTODetails.toEntity();
            Challenge updatedChallenge = challengeService.updateChallenge(id, challengeDetails);
            return ResponseEntity.ok(ChallengeDTO.fromEntity(updatedChallenge));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }
}
