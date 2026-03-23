package com.esprit.backendsport.Entities.Nutrition;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private float caloriesPerUnit;

    @Column(nullable = false)
    private float proteinPerUnit;

    @Column(nullable = false)
    private float carbsPerUnit;

    @Column(nullable = false)
    private float fatsPerUnit;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealIngredient> mealIngredients;

    // Constructors
    public Ingredient() {}

    public Ingredient(String name, String unit, float caloriesPerUnit, float proteinPerUnit, 
                     float carbsPerUnit, float fatsPerUnit) {
        this.name = name;
        this.unit = unit;
        this.caloriesPerUnit = caloriesPerUnit;
        this.proteinPerUnit = proteinPerUnit;
        this.carbsPerUnit = carbsPerUnit;
        this.fatsPerUnit = fatsPerUnit;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getCaloriesPerUnit() {
        return caloriesPerUnit;
    }

    public void setCaloriesPerUnit(float caloriesPerUnit) {
        this.caloriesPerUnit = caloriesPerUnit;
    }

    public float getProteinPerUnit() {
        return proteinPerUnit;
    }

    public void setProteinPerUnit(float proteinPerUnit) {
        this.proteinPerUnit = proteinPerUnit;
    }

    public float getCarbsPerUnit() {
        return carbsPerUnit;
    }

    public void setCarbsPerUnit(float carbsPerUnit) {
        this.carbsPerUnit = carbsPerUnit;
    }

    public float getFatsPerUnit() {
        return fatsPerUnit;
    }

    public void setFatsPerUnit(float fatsPerUnit) {
        this.fatsPerUnit = fatsPerUnit;
    }

    public List<MealIngredient> getMealIngredients() {
        return mealIngredients;
    }

    public void setMealIngredients(List<MealIngredient> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }
}