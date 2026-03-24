package com.esprit.backendsport.DTOs.Booking;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminBookingDTO {
    private Long id;
    private LocalDate date;
    private String time;
    private String status;
    
    // User info
    private Long userId;
    private String userName;      // ✅ On va construire ça dans le service
    private String userEmail;
    
    // Terrain info
    private Long terrainId;
    private String terrainName;
    private String terrainType;
    private String terrainLocation;
    private Double price;
    
    // Metadata
    private String createdAt;
    private String bookingReference;
}