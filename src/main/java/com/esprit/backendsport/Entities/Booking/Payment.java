package com.esprit.backendsport.Entities.Booking;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String paymentId;   // ID retourné par Flouci
    private Double amount;
    private String status;      // PENDING, SUCCESS, FAILED
    private LocalDateTime createdAt;
}