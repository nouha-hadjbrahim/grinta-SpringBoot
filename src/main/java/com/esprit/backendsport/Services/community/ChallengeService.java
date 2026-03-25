package com.esprit.backendsport.Services.community;

import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Entities.community.Challenge;
import com.esprit.backendsport.Entities.community.UserChallenge;
import com.esprit.backendsport.Repository.community.ChallengeRepository;
import com.esprit.backendsport.Repository.community.UserChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserChallengeRepository userChallengeRepository;

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    public Optional<Challenge> getChallengeById(Long id) {
        return challengeRepository.findById(id);
    }

    public List<User> getChallengeParticipants(Long challengeId) {
        List<UserChallenge> participations = userChallengeRepository.findByChallenge_ChallengeId(challengeId);
        return participations.stream()
                .map(UserChallenge::getUser)
                .collect(Collectors.toList());
    }

    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    public Challenge updateChallenge(Long id, Challenge challengeDetails) {
        return challengeRepository.findById(id).map(challenge -> {
            challenge.setTitle(challengeDetails.getTitle());
            challenge.setDescription(challengeDetails.getDescription());
            challenge.setType(challengeDetails.getType());
            challenge.setTargetValue(challengeDetails.getTargetValue());
            challenge.setReward(challengeDetails.getReward());
            challenge.setEndDate(challengeDetails.getEndDate());
            challenge.setTrending(challengeDetails.isTrending());
            return challengeRepository.save(challenge);
        }).orElseThrow(() -> new RuntimeException("Challenge not found with id " + id));
    }

    public void deleteChallenge(Long id) {
        challengeRepository.deleteById(id);
    }
}
