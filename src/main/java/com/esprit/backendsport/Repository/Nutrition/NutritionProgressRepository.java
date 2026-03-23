package com.esprit.backendsport.Repository.Nutrition;
import com.esprit.backendsport.Entities.Nutrition.NutritionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface NutritionProgressRepository extends JpaRepository<NutritionProgress, Long> {
    List<NutritionProgress> findByUserId(Long userId);
}