package com.esprit.backendsport.Entities.Nutrition;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.esprit.backendsport.Entities.User.User;
@Entity
@Table(name = "nutrition_progress")
public class NutritionProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private float totalCalories;

    @Column(nullable = false)
    private float totalProtein;

    @Column(nullable = false)
    private float totalCarbs;

    @Column(nullable = false)
    private float totalFats;

    @Column(nullable = false)
    private boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_nutrition_plan_id", nullable = false)
    private UserNutritionPlan userNutritionPlan;

    // Constructors
    public NutritionProgress() {}

    public NutritionProgress(LocalDate date, float totalCalories, float totalProtein, 
                            float totalCarbs, float totalFats, boolean isCompleted, 
                            User user, UserNutritionPlan userNutritionPlan) {
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFats = totalFats;
        this.isCompleted = isCompleted;
        this.user = user;
        this.userNutritionPlan = userNutritionPlan;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(float totalCalories) {
        this.totalCalories = totalCalories;
    }

    public float getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(float totalProtein) {
        this.totalProtein = totalProtein;
    }

    public float getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(float totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public float getTotalFats() {
        return totalFats;
    }

    public void setTotalFats(float totalFats) {
        this.totalFats = totalFats;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserNutritionPlan getUserNutritionPlan() {
        return userNutritionPlan;
    }

    public void setUserNutritionPlan(UserNutritionPlan userNutritionPlan) {
        this.userNutritionPlan = userNutritionPlan;
    }
}