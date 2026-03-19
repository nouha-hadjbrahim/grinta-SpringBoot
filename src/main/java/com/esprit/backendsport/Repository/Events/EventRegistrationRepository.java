package com.esprit.backendsport.Repository.Events;

import com.esprit.backendsport.Entities.Events.EventRegistration;
import com.esprit.backendsport.Entities.Events.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEventId(Long eventId);
    List<EventRegistration> findByUserId(Long userId);
    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);
    long countByEventIdAndStatus(Long eventId, RegistrationStatus status);
}