package com.esprit.backendsport.Controller.Booking;

import com.esprit.backendsport.Entities.Booking.Booking;
import com.esprit.backendsport.Services.Booking.BookingService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/user/{userId}")
    public List<Booking> getByUser(@PathVariable Long userId) {
        return bookingService.getByUserId(userId);
    }

    @GetMapping("/available")
    public List<String> getAvailableTimes(@RequestParam Long terrainId,
                                           @RequestParam LocalDate date) {
        return bookingService.getAvailableTimes(terrainId, date);
    }

    @GetMapping("/check")
    public List<Booking> check(@RequestParam Long terrainId,
                                @RequestParam LocalDate date) {
        return bookingService.getBookings(terrainId, date);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Booking booking) {
        try {
            return ResponseEntity.ok(bookingService.save(booking));
        } catch (RuntimeException e) {
            return switch (e.getMessage()) {
                case "ALREADY_BOOKED" -> ResponseEntity.status(409)
                    .body(Map.of("error", "Ce créneau est déjà réservé !"));
                case "NO_SLOTS" -> ResponseEntity.status(409)
                    .body(Map.of("error", "Ce terrain est complet !"));
                default -> ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
            };
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                     @RequestBody Booking booking) {
        try {
            return ResponseEntity.ok(bookingService.update(id, booking));
        } catch (RuntimeException e) {
            return switch (e.getMessage()) {
                case "ALREADY_BOOKED" -> ResponseEntity.status(409)
                    .body(Map.of("error", "Ce créneau est déjà réservé !"));
                case "PAST_BOOKING" -> ResponseEntity.status(400)
                    .body(Map.of("error", "Impossible de modifier une réservation passée !"));
                default -> ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
            };
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancel(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookingService.delete(id);
    }
}