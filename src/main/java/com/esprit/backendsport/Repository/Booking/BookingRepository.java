package com.esprit.backendsport.Repository.Booking;


import com.esprit.backendsport.Entities.Booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsBySportsTerrainIdAndDateAndTime(Long terrainId, LocalDate date, String time);
}