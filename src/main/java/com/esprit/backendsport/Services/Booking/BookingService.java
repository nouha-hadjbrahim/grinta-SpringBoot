package com.esprit.backendsport.Services.Booking;

import com.esprit.backendsport.Entities.Booking.Booking;
import com.esprit.backendsport.Entities.Booking.TerrainSchedule;
import com.esprit.backendsport.Repository.Booking.BookingRepository;
import com.esprit.backendsport.Repository.Booking.SportsTerrainRepository;
import com.esprit.backendsport.Repository.Booking.TerrainScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SportsTerrainRepository terrainRepository;
    private final TerrainScheduleRepository scheduleRepository;

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public List<Booking> getByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Booking save(Booking booking) {
    // Vérifier double réservation
    boolean alreadyBooked = bookingRepository
        .findBySportsTerrainIdAndDate(
            booking.getSportsTerrain().getId(), booking.getDate())
        .stream()
        .anyMatch(b -> b.getTime().equals(booking.getTime())
                    && !"CANCELLED".equals(b.getStatus()));
    if (alreadyBooked) throw new RuntimeException("ALREADY_BOOKED");

    // Vérifier places disponibles
    var terrain = terrainRepository.findById(
        booking.getSportsTerrain().getId())
        .orElseThrow(() -> new RuntimeException("Terrain not found"));

    if (terrain.getAvailableSlots() != null && terrain.getAvailableSlots() <= 0) {
        throw new RuntimeException("NO_SLOTS");
    }

    // ✅ Status PENDING au lieu de CONFIRMED (paiement requis)
    booking.setStatus("PENDING");
    return bookingRepository.save(booking);
}

    public Booking update(Long id, Booking updated) {
        return bookingRepository.findById(id).map(b -> {
            // ✅ Empêcher edit si date passée
            if (b.getDate().isBefore(LocalDate.now())) {
                throw new RuntimeException("PAST_BOOKING");
            }

            boolean conflict = bookingRepository
                .findBySportsTerrainIdAndDate(
                    updated.getSportsTerrain().getId(), updated.getDate())
                .stream()
                .anyMatch(existing ->
                    !existing.getId().equals(id) &&
                    existing.getTime().equals(updated.getTime()) &&
                    !"CANCELLED".equals(existing.getStatus()));
            if (conflict) throw new RuntimeException("ALREADY_BOOKED");

            b.setDate(updated.getDate());
            b.setTime(updated.getTime());
            b.setSportsTerrain(updated.getSportsTerrain());
            return bookingRepository.save(b);
        }).orElseThrow(() -> new RuntimeException("Booking not found: " + id));
    }

    public Booking cancel(Long id) {
        return bookingRepository.findById(id).map(b -> {
            b.setStatus("CANCELLED");
            // ✅ Remettre une place disponible
            var terrain = b.getSportsTerrain();
            if (terrain.getAvailableSlots() != null
                && terrain.getAvailableSlots() < terrain.getCapacity()) {
                terrain.setAvailableSlots(terrain.getAvailableSlots() + 1);
                terrainRepository.save(terrain);
            }
            return bookingRepository.save(b);
        }).orElseThrow(() -> new RuntimeException("Booking not found: " + id));
    }

    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<Booking> getBookings(Long terrainId, LocalDate date) {
        return bookingRepository.findBySportsTerrainIdAndDate(terrainId, date);
    }

    public List<String> getAvailableTimes(Long terrainId, LocalDate date) {
        List<String> allTimes = List.of(
            "08:00","10:00","12:00","14:00","16:00","18:00","20:00","22:00"
        );

        // ✅ Créneaux déjà réservés ce jour
        List<String> bookedTimes = bookingRepository
            .findBySportsTerrainIdAndDate(terrainId, date)
            .stream()
            .filter(b -> !"CANCELLED".equals(b.getStatus()))
            .map(Booking::getTime)
            .collect(Collectors.toList());

        // ✅ Créneaux bloqués selon le jour de la semaine
        String dayOfWeek = date.getDayOfWeek().name(); // "MONDAY", "TUESDAY"...
        List<TerrainSchedule> schedules =
            scheduleRepository.findByTerrainIdAndDayOfWeek(terrainId, dayOfWeek);

        List<String> blockedTimes = schedules.stream()
            .flatMap(s -> Arrays.stream(s.getBlockedTimes().split(",")))
            .map(String::trim)
            .collect(Collectors.toList());

        return allTimes.stream()
            .filter(t -> !bookedTimes.contains(t) && !blockedTimes.contains(t))
            .collect(Collectors.toList());
    }
}