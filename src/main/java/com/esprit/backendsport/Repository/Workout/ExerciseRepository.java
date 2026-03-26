package com.esprit.backendsport.Repository.Workout;

import com.esprit.backendsport.Entities.Workout.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, String> {

    @Query("SELECT DISTINCT e FROM Exercise e JOIN e.primaryMuscles m WHERE m IN :muscles")
    List<Exercise> findByPrimaryMusclesIn(@Param("muscles") List<String> muscles);
}
