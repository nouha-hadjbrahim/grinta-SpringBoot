package com.esprit.backendsport.Entities.Booking;

import com.esprit.backendsport.Entities.User.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String time;
    private String status; // CONFIRMED | CANCELLED
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "sports_terrain_id")
    private SportsTerrain sportsTerrain;
}