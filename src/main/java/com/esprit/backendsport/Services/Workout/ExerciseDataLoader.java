package com.esprit.backendsport.Services.Workout;

import com.esprit.backendsport.Entities.Workout.Exercise;
import com.esprit.backendsport.Repository.Workout.ExerciseRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExerciseDataLoader implements ApplicationRunner {

    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (exerciseRepository.count() > 0) {
            log.info("Exercises already loaded ({} records), skipping seed.", exerciseRepository.count());
            return;
        }

        ClassPathResource resource = new ClassPathResource("data/exercises.json");
        if (!resource.exists()) {
            log.warn("exercises.json not found at classpath:data/exercises.json — skipping exercise seed.");
            return;
        }

        try (InputStream is = resource.getInputStream()) {
            List<ExerciseJson> rawList = objectMapper.readValue(is, new TypeReference<>() {});
            log.info("Seeding {} exercises from JSON...", rawList.size());

            List<Exercise> exercises = rawList.stream()
                    .map(this::toEntity)
                    .toList();

            exerciseRepository.saveAll(exercises);
            log.info("Exercise seed complete — {} exercises saved.", exercises.size());
        } catch (Exception e) {
            log.error("Failed to seed exercises: {}", e.getMessage(), e);
        }
    }

    private Exercise toEntity(ExerciseJson json) {
        return Exercise.builder()
                .id(json.getId())
                .name(json.getName())
                .level(json.getLevel())
                .force(json.getForce())
                .mechanic(json.getMechanic())
                .equipment(json.getEquipment())
                .category(json.getCategory())
                .primaryMuscles(json.getPrimaryMuscles())
                .instructions(json.getInstructions())
                .images(json.getImages())
                .build();
    }

    /** Internal DTO for deserializing exercises.json — secondaryMuscles is intentionally ignored. */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ExerciseJson {
        private String id;
        private String name;
        private String level;
        private String force;
        private String mechanic;
        private String equipment;
        private String category;
        private List<String> primaryMuscles;
        private List<String> instructions;
        private List<String> images;
    }
}
