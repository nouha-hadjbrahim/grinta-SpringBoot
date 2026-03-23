package com.esprit.backendsport.Repository.Events;

import com.esprit.backendsport.Entities.Events.Event;
import com.esprit.backendsport.Entities.Events.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByLocationContainingIgnoreCase(String location);
}