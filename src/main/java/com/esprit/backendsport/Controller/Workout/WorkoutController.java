package com.esprit.backendsport.Controller.Workout;

import com.esprit.backendsport.DTOs.Workout.WorkoutRequestDTO;
import com.esprit.backendsport.DTOs.Workout.WorkoutResponseDTO;
import com.esprit.backendsport.Services.Workout.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    /**
     * POST /api/programs/generate
     * Body: { "muscles": [...], "equipment": [...] }
     */
    @PostMapping("/generate")
    public ResponseEntity<WorkoutResponseDTO> generate(@RequestBody WorkoutRequestDTO request) {
        return ResponseEntity.ok(workoutService.generatePrograms(request));
    }
}
