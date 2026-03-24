package com.esprit.backendsport.DTOs.Booking;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStatsDTO {
    // Cards stats
    private Long totalBookingsToday;
    private Long pendingApprovals;
    private Double revenueThisMonth;
    private String mostBookedTerrain;
    private Double occupancyRate;
    
    // Charts data
    private Map<String, Long> bookingsByDay;        // 7 derniers jours
    private Map<String, Long> bookingsByTerrain;    // Par type de terrain
    private Map<String, Double> revenueByMonth;     // 6 derniers mois
    private Map<String, Long> bookingsByStatus;     // PENDING, CONFIRMED, CANCELLED
}