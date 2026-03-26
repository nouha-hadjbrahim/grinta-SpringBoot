package com.esprit.backendsport.Entities.Workout;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    private String id;

    private String name;
    private String level;

    @Column(name = "force_type")
    private String force;

    private String mechanic;
    private String equipment;
    private String category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exercise_primary_muscles", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle")
    private List<String> primaryMuscles;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exercise_instructions", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "instruction", columnDefinition = "TEXT")
    private List<String> instructions;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exercise_images", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "image")
    private List<String> images;
}
