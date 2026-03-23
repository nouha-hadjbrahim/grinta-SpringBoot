package com.esprit.backendsport.Services.Events;

import com.esprit.backendsport.Entities.Events.*;
import java.util.List;

public interface IEventService {
    Event createEvent(Event event);
    List<Event> getAllEvents();
    Event getEventById(Long id);
    Event updateEvent(Long id, Event event);
    void deleteEvent(Long id);

    EventTag addTag(Long eventId, EventTag tag);
    List<EventTag> getTagsByEvent(Long eventId);
    void deleteTag(Long tagId);

    EventRegistration registerToEvent(Long eventId, Long userId);
    List<EventRegistration> getRegistrationsByEvent(Long eventId);
    List<EventRegistration> getRegistrationsByUser(Long userId);
    void cancelRegistration(Long registrationId);
}