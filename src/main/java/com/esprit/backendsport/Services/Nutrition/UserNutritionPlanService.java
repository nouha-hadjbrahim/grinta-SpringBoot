package com.esprit.backendsport.Services.Nutrition;

import org.springframework.stereotype.Service;
import java.util.List;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Repository.User.UserRepository;
import com.esprit.backendsport.Entities.Nutrition.UserNutritionPlan;
import com.esprit.backendsport.Repository.Nutrition.UserNutritionPlanRepository;


@Service
public class UserNutritionPlanService {
    private final UserNutritionPlanRepository userNutritionPlanRepository;
    private final UserRepository userRepository;

    public UserNutritionPlanService(UserNutritionPlanRepository userNutritionPlanRepository, 
                                   UserRepository userRepository) {
        this.userNutritionPlanRepository = userNutritionPlanRepository;
        this.userRepository = userRepository;
    }

     // Create
    public UserNutritionPlan createUserNutritionPlan(UserNutritionPlan userNutritionPlan) {
        if (userNutritionPlan.getUser() != null && userNutritionPlan.getUser().getId() != null) {
            User user = userRepository.findById(userNutritionPlan.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userNutritionPlan.getUser().getId()));
            userNutritionPlan.setUser(user);
        } else {
            throw new RuntimeException("User ID is required");
        }
        return userNutritionPlanRepository.save(userNutritionPlan);
    }

    // Read All
    public List<UserNutritionPlan> getAllUserNutritionPlans() {
        return userNutritionPlanRepository.findAll();
    }

    // Read by ID
    public UserNutritionPlan getUserNutritionPlanById(Long id) {
        return userNutritionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserNutritionPlan not found with id: " + id));
    }

    // Get by User ID
    public List<UserNutritionPlan> getUserNutritionPlansByUserId(Long userId) {
        return userNutritionPlanRepository.findByUserId(userId);
    }

    // Update
    public UserNutritionPlan updateUserNutritionPlan(Long id, UserNutritionPlan userNutritionPlan) {
        UserNutritionPlan existingPlan = userNutritionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserNutritionPlan not found with id: " + id));
        
        existingPlan.setStartDate(userNutritionPlan.getStartDate());
        existingPlan.setEndDate(userNutritionPlan.getEndDate());
        existingPlan.setActive(userNutritionPlan.isActive());
        
        return userNutritionPlanRepository.save(existingPlan);
    }

    // Delete
    public void deleteUserNutritionPlan(Long id) {
        if (!userNutritionPlanRepository.existsById(id)) {
            throw new RuntimeException("UserNutritionPlan not found with id: " + id);
        }
        userNutritionPlanRepository.deleteById(id);
    }
}