package com.studentevents.events.service;

import com.studentevents.events.model.Event;
import com.studentevents.events.model.Inscription;
import com.studentevents.events.repository.EventRepository;
import com.studentevents.events.repository.InscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventSrvice {

    private final EventRepository events;
    private final InscriptionRepository inscriptions;

    public EventService(EventRepository events, InscriptionRepository inscriptions) {
        this.events = events;
        this.inscriptions = inscriptions;
    }

    public List<Event> listEvents() {
        return events.findAll();
    }

    public Event getOrThrow(String id) {
        return events.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    public void createEvent(String title, String description, LocalDateTime dateTime,
                            String location, int maxParticipants, String userEmail) {

        Event event = new Event(
                title,
                description,
                dateTime,
                location,
                maxParticipants
        );

        event.setCreatedByEmail(userEmail);

        events.save(event);
    }

    public List<Event> listEventsCreatedBy(String email) {
        return events.findByCreatedByEmail(email);
    }

    public long nbInscrits(String eventId) {
        return inscriptions.countByEventId(eventId);
    }

    public long placesRestantes(String eventId) {
        Event e = getOrThrow(eventId);
        return Math.max(0, e.getMaxParticipants() - nbInscrits(eventId));
    }

    @Transactional
    public void inscrire(String eventId, String email, String prenom, String nom,
                         String telephone, String numeroEtudiant, String commentaire) {

        String norm = email.trim().toLowerCase();

        if (inscriptions.existsByEventIdAndEmail(eventId, norm)) {
            throw new IllegalStateException("Déjà inscrit");
        }
        if (placesRestantes(eventId) == 0) {
            throw new IllegalStateException("Complet");
        }

        inscriptions.save(new Inscription(eventId, norm, prenom, nom, telephone, numeroEtudiant, commentaire));
    }

    public List<Event> listEventsWhereUserRegistered(String email) {
        String norm = email.trim().toLowerCase();

        List<String> eventIds = inscriptions.findEventIdsByEmail(norm);
        if (eventIds.isEmpty()) return List.of();

        return events.findAllById(eventIds);
    }

    @Transactional
    public boolean desinscrire(String eventId, String email) {
        String norm = email.trim().toLowerCase();

        if (!inscriptions.existsByEventIdAndEmail(eventId, norm)) {
            return false;
        }

        inscriptions.deleteByEventIdAndEmail(eventId, norm);
        return true;
    }
}
