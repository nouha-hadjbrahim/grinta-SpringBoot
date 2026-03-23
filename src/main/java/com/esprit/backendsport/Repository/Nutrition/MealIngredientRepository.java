package com.esprit.backendsport.Repository.Nutrition;

import com.esprit.backendsport.Entities.Nutrition.MealIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MealIngredientRepository extends JpaRepository<MealIngredient, Long> {
    List<MealIngredient> findByMealId(Long mealId);
}