package com.esprit.backendsport.Entities.community;

import com.esprit.backendsport.Entities.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "user_challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participationId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "challengeId", nullable = false)
    private Challenge challenge;

    @Temporal(TemporalType.DATE)
    private Date joinDate;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int progress;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int pointsEarned;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean completed;
}
