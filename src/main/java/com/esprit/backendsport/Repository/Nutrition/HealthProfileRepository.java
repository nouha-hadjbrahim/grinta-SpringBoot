package com.esprit.backendsport.Repository.Nutrition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.esprit.backendsport.Entities.Nutrition.HealthProfile;
@Repository
public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {
      Optional<HealthProfile> findByUserId(Long userId);
    
} 