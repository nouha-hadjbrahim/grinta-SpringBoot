package com.esprit.backendsport.Entities.community;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;

    @Column(length = 150, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeType type;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isTrending;

    private Integer targetValue;

    private String reward;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int participantsCount;
}
