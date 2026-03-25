package com.esprit.backendsport.Repository.community;

import com.esprit.backendsport.Entities.community.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByChallenge_ChallengeId(Long challengeId);
}
