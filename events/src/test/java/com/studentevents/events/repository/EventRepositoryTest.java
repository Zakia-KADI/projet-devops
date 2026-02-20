package com.studentevents.events.repository;

import com.studentevents.events.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void findByCreatedByEmail_shouldReturnOnlyEventsOfThatUser() {
        Event e1 = new Event();
        e1.setTitle("Event 1");
        e1.setDescription("ABC");
        e1.setDateTime(LocalDateTime.now().plusDays(1));
        e1.setLocation("Marseille");
        e1.setMaxParticipants(10);
        e1.setCreatedByEmail("user1@mail.com");

        Event e2 = new Event();
        e2.setTitle("Event 2");
        e2.setDescription("ABC");
        e2.setDateTime(LocalDateTime.now().plusDays(2));
        e2.setLocation("Aix");
        e2.setMaxParticipants(20);
        e2.setCreatedByEmail("user2@mail.com");

        eventRepository.save(e1);
        eventRepository.save(e2);

        var res = eventRepository.findByCreatedByEmail("user1@mail.com");

        assertEquals(1, res.size());
        assertEquals("Event 1", res.get(0).getTitle());
        assertEquals("user1@mail.com", res.get(0).getCreatedByEmail());
    }
}
