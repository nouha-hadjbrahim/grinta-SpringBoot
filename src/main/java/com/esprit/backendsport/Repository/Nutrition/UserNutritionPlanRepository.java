package com.esprit.backendsport.Repository.Nutrition;
import com.esprit.backendsport.Entities.Nutrition.UserNutritionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserNutritionPlanRepository extends JpaRepository<UserNutritionPlan, Long> {
    List<UserNutritionPlan> findByUserId(Long userId);
}