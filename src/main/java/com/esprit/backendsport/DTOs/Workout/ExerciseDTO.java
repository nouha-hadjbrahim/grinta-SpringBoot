package com.esprit.backendsport.DTOs.Workout;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDTO {
    private String name;
    private String level;
    private String equipment;
    private List<String> primaryMuscles;
    private String firstImage;
    private List<String> instructions;
    private String videoUrl;
}
