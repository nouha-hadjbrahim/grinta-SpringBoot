package com.esprit.backendsport.Controller.Events;

import com.esprit.backendsport.Entities.Events.*;
import com.esprit.backendsport.Services.Events.IEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class EventController {

    private final IEventService eventService;

    // ── Event CRUD ──────────────────────────────────────────
    @GetMapping
    public List<Event> getAll() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id,
                                        @RequestBody Event event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // ── Tags ────────────────────────────────────────────────
    @PostMapping("/{eventId}/tags")
    public ResponseEntity<EventTag> addTag(@PathVariable Long eventId,
                                           @RequestBody EventTag tag) {
        return ResponseEntity.ok(eventService.addTag(eventId, tag));
    }

    @GetMapping("/{eventId}/tags")
    public List<EventTag> getTags(@PathVariable Long eventId) {
        return eventService.getTagsByEvent(eventId);
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        eventService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }

    // ── Registrations ───────────────────────────────────────
    @PostMapping("/{eventId}/register")
    public ResponseEntity<EventRegistration> register(
            @PathVariable Long eventId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(eventService.registerToEvent(eventId, userId));
    }

    @GetMapping("/{eventId}/registrations")
    public List<EventRegistration> getRegistrations(@PathVariable Long eventId) {
        return eventService.getRegistrationsByEvent(eventId);
    }

    @GetMapping("/user/{userId}/registrations")
    public List<EventRegistration> getByUser(@PathVariable Long userId) {
        return eventService.getRegistrationsByUser(userId);
    }

    @PutMapping("/registrations/{registrationId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long registrationId) {
        eventService.cancelRegistration(registrationId);
        return ResponseEntity.noContent().build();
    }
}