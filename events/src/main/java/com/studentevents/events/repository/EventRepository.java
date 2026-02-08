// Stocker et récupérer les événements.
package com.studentevents.events.repository;

import com.studentevents.events.model.Event;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EventRepository {
    private final Map<String, Event> store = new ConcurrentHashMap<>();

    public List<Event> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(Event::getDateTime))
                .toList();
    }

    public Optional<Event> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Event save(Event event) {
        store.put(event.getId(), event);
        return event;
    }
}
