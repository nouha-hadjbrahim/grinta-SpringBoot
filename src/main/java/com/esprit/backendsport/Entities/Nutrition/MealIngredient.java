package com.esprit.backendsport.Entities.Nutrition;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_ingredients")
public class MealIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float quantity;

    @Column(nullable = false)
    private float calculatedCalories;

    @Column(nullable = false)
    private float calculatedProtein;

    @Column(nullable = false)
    private float calculatedCarbs;

    @Column(nullable = false)
    private float calculatedFats;

    @ManyToOne
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    // Constructors
    public MealIngredient() {}

    public MealIngredient(float quantity, float calculatedCalories, float calculatedProtein,
                         float calculatedCarbs, float calculatedFats, Meal meal, Ingredient ingredient) {
        this.quantity = quantity;
        this.calculatedCalories = calculatedCalories;
        this.calculatedProtein = calculatedProtein;
        this.calculatedCarbs = calculatedCarbs;
        this.calculatedFats = calculatedFats;
        this.meal = meal;
        this.ingredient = ingredient;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getCalculatedCalories() {
        return calculatedCalories;
    }

    public void setCalculatedCalories(float calculatedCalories) {
        this.calculatedCalories = calculatedCalories;
    }

    public float getCalculatedProtein() {
        return calculatedProtein;
    }

    public void setCalculatedProtein(float calculatedProtein) {
        this.calculatedProtein = calculatedProtein;
    }

    public float getCalculatedCarbs() {
        return calculatedCarbs;
    }

    public void setCalculatedCarbs(float calculatedCarbs) {
        this.calculatedCarbs = calculatedCarbs;
    }

    public float getCalculatedFats() {
        return calculatedFats;
    }

    public void setCalculatedFats(float calculatedFats) {
        this.calculatedFats = calculatedFats;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}