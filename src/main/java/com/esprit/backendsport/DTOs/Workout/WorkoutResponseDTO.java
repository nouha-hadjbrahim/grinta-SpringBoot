package com.esprit.backendsport.DTOs.Workout;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutResponseDTO {
    private Map<String, List<ExerciseDTO>> programs;
}
