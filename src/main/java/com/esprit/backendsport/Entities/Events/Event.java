package com.esprit.backendsport.Entities.Events;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String name;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String time;
    private String location;
    private String description;

    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.UPCOMING;

    private int maxParticipants;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;
    @Column(name = "created_by_admin", nullable = false)
    private boolean createdByAdmin = false;


    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("event-registrations")
    private List<EventRegistration> registrations;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("event-tags")
    private List<EventTag> tags;
}