package com.esprit.backendsport.Entities.Coaching;

import jakarta.persistence.*;
import lombok.*;
import com.esprit.backendsport.Entities.User.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String type;
    private String description;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
