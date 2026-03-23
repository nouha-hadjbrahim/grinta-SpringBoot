package com.esprit.backendsport.Controller.Nutrion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.esprit.backendsport.Entities.Nutrition.MealIngredient;
import com.esprit.backendsport.Services.Nutrition.MealIngredientService;
@RestController
@RequestMapping("/api/meal-ingredients")
@CrossOrigin(origins = "*")
public class MealIngredientController {
    private final MealIngredientService mealIngredientService;

    public MealIngredientController(MealIngredientService mealIngredientService) {
        this.mealIngredientService = mealIngredientService;
    }

    @PostMapping
    public ResponseEntity<MealIngredient> createMealIngredient(@RequestBody MealIngredient mealIngredient) {
        return new ResponseEntity<>(mealIngredientService.createMealIngredient(mealIngredient), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MealIngredient>> getAllMealIngredients() {
        return ResponseEntity.ok(mealIngredientService.getAllMealIngredients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealIngredient> getMealIngredientById(@PathVariable Long id) {
        return ResponseEntity.ok(mealIngredientService.getMealIngredientById(id));
    }

    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<MealIngredient>> getMealIngredientsByMealId(@PathVariable Long mealId) {
        return ResponseEntity.ok(mealIngredientService.getMealIngredientsByMealId(mealId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealIngredient> updateMealIngredient(@PathVariable Long id, 
                                                               @RequestBody MealIngredient mealIngredient) {
        return ResponseEntity.ok(mealIngredientService.updateMealIngredient(id, mealIngredient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealIngredient(@PathVariable Long id) {
        mealIngredientService.deleteMealIngredient(id);
        return ResponseEntity.noContent().build();
    }
}