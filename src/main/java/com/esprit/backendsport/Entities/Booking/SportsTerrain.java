package com.esprit.backendsport.Entities.Booking;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sports_terrains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportsTerrain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String location;
    private Integer capacity;
    private Double price;
}