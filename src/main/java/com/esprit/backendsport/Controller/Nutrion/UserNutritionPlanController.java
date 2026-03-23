package com.esprit.backendsport.Controller.Nutrion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.esprit.backendsport.Entities.Nutrition.UserNutritionPlan;
import com.esprit.backendsport.Services.Nutrition.UserNutritionPlanService;
@RestController
@RequestMapping("/api/user-nutrition-plans")
@CrossOrigin(origins = "*")
public class UserNutritionPlanController {
    private final UserNutritionPlanService userNutritionPlanService;

    public UserNutritionPlanController(UserNutritionPlanService userNutritionPlanService) {
        this.userNutritionPlanService = userNutritionPlanService;
    }

    @PostMapping
    public ResponseEntity<UserNutritionPlan> createUserNutritionPlan(@RequestBody UserNutritionPlan userNutritionPlan) {
        return new ResponseEntity<>(userNutritionPlanService.createUserNutritionPlan(userNutritionPlan), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserNutritionPlan>> getAllUserNutritionPlans() {
        return ResponseEntity.ok(userNutritionPlanService.getAllUserNutritionPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserNutritionPlan> getUserNutritionPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(userNutritionPlanService.getUserNutritionPlanById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserNutritionPlan>> getUserNutritionPlansByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userNutritionPlanService.getUserNutritionPlansByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserNutritionPlan> updateUserNutritionPlan(@PathVariable Long id, 
                                                                     @RequestBody UserNutritionPlan userNutritionPlan) {
        return ResponseEntity.ok(userNutritionPlanService.updateUserNutritionPlan(id, userNutritionPlan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserNutritionPlan(@PathVariable Long id) {
        userNutritionPlanService.deleteUserNutritionPlan(id);
        return ResponseEntity.noContent().build();
    }
}
