package com.esprit.backendsport.Entities.Nutrition;


import com.esprit.backendsport.Entities.User.User;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user_nutrition_plans")
public class UserNutritionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "userNutritionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NutritionProgress> nutritionProgress;

    // Constructors
    public UserNutritionPlan() {}

    public UserNutritionPlan(LocalDate startDate, LocalDate endDate, boolean isActive, User user) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public User getUser() {
    return user;
}

public void setUser(User user) {
    this.user = user;
}

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<NutritionProgress> getNutritionProgress() {
        return nutritionProgress;
    }

    public void setNutritionProgress(List<NutritionProgress> nutritionProgress) {
        this.nutritionProgress = nutritionProgress;
    }

}