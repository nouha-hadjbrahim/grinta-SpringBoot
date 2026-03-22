package com.esprit.backendsport.Entities.Coaching;

import jakarta.persistence.*;
import lombok.*;
import com.esprit.backendsport.Entities.User.User;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate bookingDate;
    private LocalDate sessionDate;
    private String status;
    private String paymentStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;
}

