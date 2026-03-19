package com.esprit.backendsport.Entities.Events;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;   // ex: #outdoor, #gratuit
    private String color;   // ex: #FF5733

    @ManyToOne
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference("event-tags")
    private Event event;
}