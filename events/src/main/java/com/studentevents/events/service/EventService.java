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
public class EventService {

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
            maxParticipants,
            userEmail
    );

    events.save(event); 
}

    
    
}
