package com.esprit.backendsport.Controller.Booking;

import com.esprit.backendsport.Entities.Booking.Booking;
import com.esprit.backendsport.Services.Booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<Booking> getAll() {
        return bookingService.getAll();
    }

    @PostMapping
    public Booking create(
            @RequestParam Long userId,
            @RequestParam Long terrainId,
            @RequestParam String date,
            @RequestParam String time
    ) {
        return bookingService.createBooking(
                userId,
                terrainId,
                LocalDate.parse(date),
                time
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookingService.delete(id);
    }
}