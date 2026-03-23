package com.esprit.backendsport.Entities.Coaching;

import com.esprit.backendsport.Entities.User.User;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "coaches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coach extends User {
    private String specialization;
    private int experience;

    @OneToMany(mappedBy = "coach")
    private List<TrainingProgram> createdPrograms;

    @OneToMany(mappedBy = "coach")
    private List<SessionBooking> coachedSessions;
}
