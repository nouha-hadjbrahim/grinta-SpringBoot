package com.esprit.backendsport.Services.Nutrition;
import org.springframework.stereotype.Service;
import java.util.List;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Repository.User.UserRepository;
import com.esprit.backendsport.Entities.Nutrition.Meal;
import com.esprit.backendsport.Repository.Nutrition.MealRepository;



@Service
public class MealService {
    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    public MealService(MealRepository mealRepository, UserRepository userRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }

    // Create
    public Meal createMeal(Meal meal) {
        User user = userRepository.findById(meal.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + meal.getUser().getId()));
        
        meal.setUser(user);
        return mealRepository.save(meal);
    }

    // Read All
    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    // Read by ID
    public Meal getMealById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + id));
    }

    // Get by User ID
    public List<Meal> getMealsByUserId(Long userId) {
        return mealRepository.findByUserId(userId);
    }

    // Update
    public Meal updateMeal(Long id, Meal meal) {
        Meal existingMeal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + id));
        
        existingMeal.setName(meal.getName());
        existingMeal.setType(meal.getType());
        existingMeal.setDate(meal.getDate());
        existingMeal.setCompleted(meal.isCompleted());
        
        return mealRepository.save(existingMeal);
    }

    // Delete
    public void deleteMeal(Long id) {
        if (!mealRepository.existsById(id)) {
            throw new RuntimeException("Meal not found with id: " + id);
        }
        mealRepository.deleteById(id);
    }
}