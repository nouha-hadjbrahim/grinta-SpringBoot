package com.esprit.backendsport.Services.Nutrition;
import org.springframework.stereotype.Service;
import java.util.List;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Repository.User.UserRepository;
import com.esprit.backendsport.Entities.Nutrition.Meal;
import com.esprit.backendsport.Repository.Nutrition.MealRepository;
import com.esprit.backendsport.Entities.Nutrition.Ingredient;
import com.esprit.backendsport.Repository.Nutrition.IngredientRepository;
import com.esprit.backendsport.Entities.Nutrition.MealIngredient;
import com.esprit.backendsport.Repository.Nutrition.MealIngredientRepository;

@Service
public class MealIngredientService {
    private final MealIngredientRepository mealIngredientRepository;
    private final MealRepository mealRepository;
    private final IngredientRepository ingredientRepository;

    public MealIngredientService(MealIngredientRepository mealIngredientRepository,
                                MealRepository mealRepository,
                                IngredientRepository ingredientRepository) {
        this.mealIngredientRepository = mealIngredientRepository;
        this.mealRepository = mealRepository;
        this.ingredientRepository = ingredientRepository;
    }

    // Create
    public MealIngredient createMealIngredient(MealIngredient mealIngredient) {
        Meal meal = mealRepository.findById(mealIngredient.getMeal().getId())
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + mealIngredient.getMeal().getId()));
        
        Ingredient ingredient = ingredientRepository.findById(mealIngredient.getIngredient().getId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + mealIngredient.getIngredient().getId()));
        
        mealIngredient.setMeal(meal);
        mealIngredient.setIngredient(ingredient);
        
        // Calculate nutritional values based on quantity
        calculateNutritionalValues(mealIngredient);
        
        return mealIngredientRepository.save(mealIngredient);
    }

    // Read All
    public List<MealIngredient> getAllMealIngredients() {
        return mealIngredientRepository.findAll();
    }

    // Read by ID
    public MealIngredient getMealIngredientById(Long id) {
        return mealIngredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MealIngredient not found with id: " + id));
    }

    // Get by Meal ID
    public List<MealIngredient> getMealIngredientsByMealId(Long mealId) {
        return mealIngredientRepository.findByMealId(mealId);
    }

    // Update
    public MealIngredient updateMealIngredient(Long id, MealIngredient mealIngredient) {
        MealIngredient existingMealIngredient = mealIngredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MealIngredient not found with id: " + id));
        
        existingMealIngredient.setQuantity(mealIngredient.getQuantity());
        
        // Recalculate nutritional values based on new quantity
        calculateNutritionalValues(existingMealIngredient);
        
        return mealIngredientRepository.save(existingMealIngredient);
    }

    // Delete
    public void deleteMealIngredient(Long id) {
        if (!mealIngredientRepository.existsById(id)) {
            throw new RuntimeException("MealIngredient not found with id: " + id);
        }
        mealIngredientRepository.deleteById(id);
    }

    // Helper method to calculate nutritional values based on quantity
    private void calculateNutritionalValues(MealIngredient mealIngredient) {
        Ingredient ingredient = mealIngredient.getIngredient();
        float quantity = mealIngredient.getQuantity();
        
        mealIngredient.setCalculatedCalories(ingredient.getCaloriesPerUnit() * quantity);
        mealIngredient.setCalculatedProtein(ingredient.getProteinPerUnit() * quantity);
        mealIngredient.setCalculatedCarbs(ingredient.getCarbsPerUnit() * quantity);
        mealIngredient.setCalculatedFats(ingredient.getFatsPerUnit() * quantity);
    }
}
