package com.esprit.backendsport.Repository.Booking;

import com.esprit.backendsport.Entities.Booking.TerrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TerrainScheduleRepository extends JpaRepository<TerrainSchedule, Long> {
    List<TerrainSchedule> findByTerrainIdAndDayOfWeek(Long terrainId, String dayOfWeek);
}