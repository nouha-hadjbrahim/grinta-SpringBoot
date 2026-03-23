package com.esprit.backendsport.Entities.Booking;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terrain_schedules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TerrainSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "terrain_id")
    private SportsTerrain terrain;

    private String dayOfWeek;    // "MONDAY", "SUNDAY"...
    private String blockedTimes; // "08:00,10:00,12:00"
}