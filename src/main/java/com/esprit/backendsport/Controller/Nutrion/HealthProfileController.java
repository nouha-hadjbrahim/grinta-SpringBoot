package com.esprit.backendsport.Controller.Nutrion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.esprit.backendsport.Entities.Nutrition.HealthProfile;
import com.esprit.backendsport.Services.Nutrition.HealthProfileService;
@RestController
@RequestMapping("/api/health-profiles")
@CrossOrigin(origins = "*")
public class HealthProfileController {
    private final HealthProfileService healthProfileService;

    public HealthProfileController(HealthProfileService healthProfileService) {
        this.healthProfileService = healthProfileService;
    }

    @PostMapping
    public ResponseEntity<HealthProfile> createHealthProfile(@RequestBody HealthProfile healthProfile) {
        return new ResponseEntity<>(healthProfileService.createHealthProfile(healthProfile), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HealthProfile>> getAllHealthProfiles() {
        return ResponseEntity.ok(healthProfileService.getAllHealthProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthProfile> getHealthProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(healthProfileService.getHealthProfileById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<HealthProfile> getHealthProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(healthProfileService.getHealthProfileByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthProfile> updateHealthProfile(@PathVariable Long id, @RequestBody HealthProfile healthProfile) {
        return ResponseEntity.ok(healthProfileService.updateHealthProfile(id, healthProfile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthProfile(@PathVariable Long id) {
        healthProfileService.deleteHealthProfile(id);
        return ResponseEntity.noContent().build();
    }
}