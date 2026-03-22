package com.esprit.backendsport.Repository.community;

import com.esprit.backendsport.Entities.community.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
