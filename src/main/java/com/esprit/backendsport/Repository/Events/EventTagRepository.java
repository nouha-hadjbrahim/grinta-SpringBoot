package com.esprit.backendsport.Repository.Events;

import com.esprit.backendsport.Entities.Events.EventTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventTagRepository extends JpaRepository<EventTag, Long> {
    List<EventTag> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);
}