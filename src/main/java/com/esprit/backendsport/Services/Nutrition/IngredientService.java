package com.esprit.backendsport.Services.Nutrition;
import com.esprit.backendsport.Entities.Nutrition.Ingredient;
import com.esprit.backendsport.Repository.Nutrition.IngredientRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    // Create
    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    // Read All
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    // Read by ID
    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));
    }

    // Update
    public Ingredient updateIngredient(Long id, Ingredient ingredient) {
        Ingredient existingIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));
        
        existingIngredient.setName(ingredient.getName());
        existingIngredient.setUnit(ingredient.getUnit());
        existingIngredient.setCaloriesPerUnit(ingredient.getCaloriesPerUnit());
        existingIngredient.setProteinPerUnit(ingredient.getProteinPerUnit());
        existingIngredient.setCarbsPerUnit(ingredient.getCarbsPerUnit());
        existingIngredient.setFatsPerUnit(ingredient.getFatsPerUnit());
        
        return ingredientRepository.save(existingIngredient);
    }

    // Delete
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new RuntimeException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }
}