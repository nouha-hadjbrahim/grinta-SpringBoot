package com.esprit.backendsport.Repository.Booking;

import com.esprit.backendsport.Entities.Booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBySportsTerrainIdAndDate(Long terrainId, LocalDate date);
    // ✅ NOUVEAU
    List<Booking> findByUserId(Long userId);
}