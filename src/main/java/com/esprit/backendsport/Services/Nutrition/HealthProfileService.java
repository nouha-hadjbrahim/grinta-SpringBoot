package com.esprit.backendsport.Services.Nutrition;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Entities.Nutrition.HealthProfile;
import com.esprit.backendsport.Repository.Nutrition.HealthProfileRepository;
import com.esprit.backendsport.Repository.User.UserRepository;

@Service
public class HealthProfileService {
    private final HealthProfileRepository healthProfileRepository;
    private final UserRepository userRepository;

    public HealthProfileService(HealthProfileRepository healthProfileRepository, UserRepository userRepository) {
        this.healthProfileRepository = healthProfileRepository;
        this.userRepository = userRepository;
    }

    // Create
    public HealthProfile createHealthProfile(HealthProfile healthProfile) {
        User user = userRepository.findById(healthProfile.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + healthProfile.getUser().getId()));
        
        healthProfile.setUser(user);
        return healthProfileRepository.save(healthProfile);
    }

    // Read All
    public List<HealthProfile> getAllHealthProfiles() {
        return healthProfileRepository.findAll();
    }

    // Read by ID
    public HealthProfile getHealthProfileById(Long id) {
        return healthProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Health Profile not found with id: " + id));
    }

    // Get by User ID
    public HealthProfile getHealthProfileByUserId(Long userId) {
        return healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Health Profile not found for user id: " + userId));
    }

    // Update
    public HealthProfile updateHealthProfile(Long id, HealthProfile healthProfile) {
        HealthProfile existingProfile = healthProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Health Profile not found with id: " + id));
        
        existingProfile.setHeight(healthProfile.getHeight());
        existingProfile.setWeight(healthProfile.getWeight());
        existingProfile.setBmi(healthProfile.getBmi());
        existingProfile.setGoal(healthProfile.getGoal());
        existingProfile.setActivityLevel(healthProfile.getActivityLevel());
        
        return healthProfileRepository.save(existingProfile);
    }

    // Delete
    public void deleteHealthProfile(Long id) {
        if (!healthProfileRepository.existsById(id)) {
            throw new RuntimeException("Health Profile not found with id: " + id);
        }
        healthProfileRepository.deleteById(id);
    }
}