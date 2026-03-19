package com.esprit.backendsport.Services.Events;

import com.esprit.backendsport.Entities.Events.*;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Repository.Events.EventRegistrationRepository;
import com.esprit.backendsport.Repository.Events.EventRepository;
import com.esprit.backendsport.Repository.Events.EventTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final EventTagRepository tagRepository;
    private final EventRegistrationRepository registrationRepository;

    // ── Event CRUD ──────────────────────────────────────────
    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    @Override
    public Event updateEvent(Long id, Event updated) {
        Event existing = getEventById(id);
        existing.setTitle(updated.getTitle());
        existing.setName(updated.getName());
        existing.setDate(updated.getDate());
        existing.setTime(updated.getTime());
        existing.setLocation(updated.getLocation());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setMaxParticipants(updated.getMaxParticipants());
        return eventRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // ── Tags ────────────────────────────────────────────────
    @Override
    public EventTag addTag(Long eventId, EventTag tag) {
        Event event = getEventById(eventId);
        tag.setEvent(event);
        return tagRepository.save(tag);
    }

    @Override
    public List<EventTag> getTagsByEvent(Long eventId) {
        return tagRepository.findByEventId(eventId);
    }

    @Override
    public void deleteTag(Long tagId) {
        tagRepository.deleteById(tagId);
    }

    // ── Registrations ───────────────────────────────────────
    @Override
    public EventRegistration registerToEvent(Long eventId, Long userId) {
        // Vérifier si déjà inscrit
        registrationRepository.findByEventIdAndUserId(eventId, userId)
                .ifPresent(r -> { throw new RuntimeException("User already registered to this event"); });

        Event event = getEventById(eventId);

        // Vérifier la capacité max
        long confirmed = registrationRepository
                .countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED);

        RegistrationStatus status = RegistrationStatus.CONFIRMED;
        if (event.getMaxParticipants() > 0 && confirmed >= event.getMaxParticipants()) {
            status = RegistrationStatus.WAITLISTED;
        }

        User user = new User();
        user.setId(userId);

        EventRegistration reg = EventRegistration.builder()
                .event(event)
                .user(user)
                .registrationDate(new Date())
                .status(status)
                .build();

        return registrationRepository.save(reg);
    }

    @Override
    public List<EventRegistration> getRegistrationsByEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    @Override
    public List<EventRegistration> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void cancelRegistration(Long registrationId) {
        EventRegistration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        reg.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(reg);
    }
}