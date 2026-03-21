package com.esprit.backendsport.Services.Booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.esprit.backendsport.Entities.Booking.Booking;
import com.esprit.backendsport.Entities.Booking.SportsTerrain;
import com.esprit.backendsport.Entities.User.User;

import com.esprit.backendsport.Repository.Booking.BookingRepository;
import com.esprit.backendsport.Repository.Booking.SportsTerrainRepository;
import com.esprit.backendsport.Repository.User.UserRepository;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SportsTerrainRepository terrainRepository;
    private final UserRepository userRepository;

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public Booking createBooking(Long userId, Long terrainId, LocalDate date, String time) {

        if (bookingRepository.existsBySportsTerrainIdAndDateAndTime(terrainId, date, time)) {
            throw new RuntimeException("Ce créneau est déjà réservé !");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SportsTerrain terrain = terrainRepository.findById(terrainId)
                .orElseThrow(() -> new RuntimeException("Terrain not found"));

        Booking booking = Booking.builder()
                .date(date)
                .time(time)
                .status("CONFIRMED")
                .user(user)
                .sportsTerrain(terrain)
                .build();

        return bookingRepository.save(booking);
    }

    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }
}