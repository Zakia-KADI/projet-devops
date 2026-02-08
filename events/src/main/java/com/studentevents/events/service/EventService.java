//logique metier
package com.studentevents.events.service;

import com.studentevents.events.model.Event;
import com.studentevents.events.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
        //seed();
    }

    private void seed() {
        if (!repo.findAll().isEmpty()) return;

        repo.save(new Event("Conférence IA", "Une conférence sur l'intelligence artificielle",
                LocalDateTime.now().plusDays(7).withHour(18).withMinute(0),
                "Amphithéâtre B", 30));

        repo.save(new Event("Soirée Étudiante", "Soirée au club",
                LocalDateTime.now().plusDays(10).withHour(21).withMinute(0),
                "Club XYZ", 50));

        // Pour montrer "Complet" : 0 place restante
        Event t = new Event("Tournoi de Football", "Matchs inter-facs",
                LocalDateTime.now().plusDays(14).withHour(14).withMinute(0),
                "Stade Universitaire", 1);
        t.registerOne(); // complet
        repo.save(t);
    }

    public List<Event> list() {
        return repo.findAll();
    }

    public Event getOrThrow(String id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    public Event create(String title, String description, LocalDateTime dateTime, String location, int maxParticipants) {
        Event e = new Event(title, description, dateTime, location, maxParticipants);
        return repo.save(e);
    }

    public void register(String id) {
        Event e = getOrThrow(id);
        e.registerOne();
        repo.save(e);
    }

    public void unregister(String id) {
        Event e = getOrThrow(id);
        e.unregisterOne();
        repo.save(e);
    }
}
