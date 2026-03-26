package com.esprit.backendsport.DTOs.Workout;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequestDTO {
    private List<String> muscles;
    private List<String> equipment;
}
