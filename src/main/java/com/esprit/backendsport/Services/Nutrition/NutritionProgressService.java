package com.esprit.backendsport.Services.Nutrition;
import org.springframework.stereotype.Service;
import java.util.List;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Repository.User.UserRepository;
import com.esprit.backendsport.Entities.Nutrition.NutritionProgress;
import com.esprit.backendsport.Repository.Nutrition.NutritionProgressRepository;
import com.esprit.backendsport.Entities.Nutrition.UserNutritionPlan;
import com.esprit.backendsport.Repository.Nutrition.UserNutritionPlanRepository;

@Service
public class NutritionProgressService {
    private final NutritionProgressRepository nutritionProgressRepository;
    private final UserRepository userRepository;
    private final UserNutritionPlanRepository userNutritionPlanRepository;

    public NutritionProgressService(NutritionProgressRepository nutritionProgressRepository,
                                   UserRepository userRepository,
                                   UserNutritionPlanRepository userNutritionPlanRepository) {
        this.nutritionProgressRepository = nutritionProgressRepository;
        this.userRepository = userRepository;
        this.userNutritionPlanRepository = userNutritionPlanRepository;
    }

    // Create
    public NutritionProgress createNutritionProgress(NutritionProgress nutritionProgress) {
        User user = userRepository.findById(nutritionProgress.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + nutritionProgress.getUser().getId()));
        
        UserNutritionPlan plan = userNutritionPlanRepository.findById(nutritionProgress.getUserNutritionPlan().getId())
                .orElseThrow(() -> new RuntimeException("UserNutritionPlan not found with id: " + nutritionProgress.getUserNutritionPlan().getId()));
        
        nutritionProgress.setUser(user);
        nutritionProgress.setUserNutritionPlan(plan);
        return nutritionProgressRepository.save(nutritionProgress);
    }

    // Read All
    public List<NutritionProgress> getAllNutritionProgress() {
        return nutritionProgressRepository.findAll();
    }

    // Read by ID
    public NutritionProgress getNutritionProgressById(Long id) {
        return nutritionProgressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NutritionProgress not found with id: " + id));
    }

    // Get by User ID
    public List<NutritionProgress> getNutritionProgressByUserId(Long userId) {
        return nutritionProgressRepository.findByUserId(userId);
    }

    // Update
    public NutritionProgress updateNutritionProgress(Long id, NutritionProgress nutritionProgress) {
        NutritionProgress existingProgress = nutritionProgressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NutritionProgress not found with id: " + id));
        
        existingProgress.setDate(nutritionProgress.getDate());
        existingProgress.setTotalCalories(nutritionProgress.getTotalCalories());
        existingProgress.setTotalProtein(nutritionProgress.getTotalProtein());
        existingProgress.setTotalCarbs(nutritionProgress.getTotalCarbs());
        existingProgress.setTotalFats(nutritionProgress.getTotalFats());
        existingProgress.setCompleted(nutritionProgress.isCompleted());
        
        return nutritionProgressRepository.save(existingProgress);
    }

    // Delete
    public void deleteNutritionProgress(Long id) {
        if (!nutritionProgressRepository.existsById(id)) {
            throw new RuntimeException("NutritionProgress not found with id: " + id);
        }
        nutritionProgressRepository.deleteById(id);
    }
}