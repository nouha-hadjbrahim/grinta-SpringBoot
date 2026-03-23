package com.esprit.backendsport.Controller.Nutrion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.esprit.backendsport.Entities.Nutrition.NutritionProgress;
import com.esprit.backendsport.Services.Nutrition.NutritionProgressService;

@RestController
@RequestMapping("/api/nutrition-progress")
@CrossOrigin(origins = "*")
public class NutritionProgressController {
    private final NutritionProgressService nutritionProgressService;

    public NutritionProgressController(NutritionProgressService nutritionProgressService) {
        this.nutritionProgressService = nutritionProgressService;
    }

    @PostMapping
    public ResponseEntity<NutritionProgress> createNutritionProgress(@RequestBody NutritionProgress nutritionProgress) {
        return new ResponseEntity<>(nutritionProgressService.createNutritionProgress(nutritionProgress), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NutritionProgress>> getAllNutritionProgress() {
        return ResponseEntity.ok(nutritionProgressService.getAllNutritionProgress());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NutritionProgress> getNutritionProgressById(@PathVariable Long id) {
        return ResponseEntity.ok(nutritionProgressService.getNutritionProgressById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NutritionProgress>> getNutritionProgressByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(nutritionProgressService.getNutritionProgressByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NutritionProgress> updateNutritionProgress(@PathVariable Long id, 
                                                                     @RequestBody NutritionProgress nutritionProgress) {
        return ResponseEntity.ok(nutritionProgressService.updateNutritionProgress(id, nutritionProgress));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNutritionProgress(@PathVariable Long id) {
        nutritionProgressService.deleteNutritionProgress(id);
        return ResponseEntity.noContent().build();
    }
}