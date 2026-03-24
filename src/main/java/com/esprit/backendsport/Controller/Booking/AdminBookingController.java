package com.esprit.backendsport.Controller.Booking;

import com.esprit.backendsport.DTOs.Booking.AdminBookingDTO;
import com.esprit.backendsport.DTOs.Booking.DashboardStatsDTO;
import com.esprit.backendsport.Entities.Booking.Booking;
import com.esprit.backendsport.Services.Booking.AdminBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    /**
     * GET /api/admin/bookings - Toutes les réservations
     */
    @GetMapping
    public ResponseEntity<List<AdminBookingDTO>> getAllBookings() {
        return ResponseEntity.ok(adminBookingService.getAllBookingsAdmin());
    }

    /**
     * GET /api/admin/bookings/status/{status} - Filtrer par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AdminBookingDTO>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(adminBookingService.getBookingsByStatus(status));
    }

    /**
     * GET /api/admin/bookings/date/{date} - Filtrer par date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AdminBookingDTO>> getByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(adminBookingService.getBookingsByDate(date));
    }

    /**
     * PUT /api/admin/bookings/{id}/approve - Approuver une réservation
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveBooking(@PathVariable Long id) {
        try {
            AdminBookingDTO result = adminBookingService.approveBooking(id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return switch (e.getMessage()) {
                case "NO_SLOTS_AVAILABLE" -> ResponseEntity.status(409)
                    .body(Map.of("error", "Plus de places disponibles"));
                default -> ResponseEntity.status(400)
                    .body(Map.of("error", e.getMessage()));
            };
        }
    }

    /**
     * PUT /api/admin/bookings/{id}/reject - Rejeter une réservation
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long id) {
        try {
            AdminBookingDTO result = adminBookingService.rejectBooking(id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/bookings/{id}/cancel - Annuler une réservation confirmée
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            AdminBookingDTO result = adminBookingService.cancelConfirmedBooking(id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/bookings/{id} - Modifier une réservation
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(
        @PathVariable Long id,
        @RequestBody Booking updatedData
    ) {
        try {
            AdminBookingDTO result = adminBookingService.updateBookingAdmin(id, updatedData);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return switch (e.getMessage()) {
                case "ALREADY_BOOKED" -> ResponseEntity.status(409)
                    .body(Map.of("error", "Ce créneau est déjà réservé"));
                default -> ResponseEntity.status(400)
                    .body(Map.of("error", e.getMessage()));
            };
        }
    }

    /**
     * DELETE /api/admin/bookings/{id} - Supprimer définitivement
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            adminBookingService.deleteBookingAdmin(id);
            return ResponseEntity.ok(Map.of("message", "Booking deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/bookings/stats - Statistiques dashboard
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(adminBookingService.getDashboardStats());
    }
}