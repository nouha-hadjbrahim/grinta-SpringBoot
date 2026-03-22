package com.esprit.backendsport.Repository.community;

import com.esprit.backendsport.Entities.community.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
}
