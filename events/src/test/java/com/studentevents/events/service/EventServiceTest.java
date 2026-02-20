package com.studentevents.events.service;

import com.studentevents.events.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class EventServiceTest {

    @Autowired
    private EventService service;

    private Event createEventAndGetLast(String title, int max, String createdByEmail) {
        service.createEvent(
                title,
                "ABC",
                LocalDateTime.now().plusDays(1),
                "Salle A",
                max,
                createdByEmail
        );
        var events = service.listEvents();
        return events.get(events.size() - 1);
    }

    @Test
    void createEvent_shouldIncreaseListSize() {
        int before = service.listEvents().size();

        service.createEvent(
                "Conf DevOps",
                "tests",
                LocalDateTime.now().plusDays(1),
                "Amphi A",
                10,
                "creator@mail.com"
        );

        int after = service.listEvents().size();
        assertThat(after).isEqualTo(before + 1);
    }

    @Test
    void inscrire_shouldIncreaseNbInscrits_andDecreaseRemainingSeats() {
        Event e = createEventAndGetLast("ABC", 2, "creator@mail.com");

        long nb0 = service.nbInscrits(e.getId());
        long rest0 = service.placesRestantes(e.getId());

        service.inscrire(
                e.getId(),
                "a@mail.com",
                "A",
                "B",
                null,
                null,
                "hello"
        );

        long nb1 = service.nbInscrits(e.getId());
        long rest1 = service.placesRestantes(e.getId());

        assertThat(nb1).isEqualTo(nb0 + 1);
        assertThat(rest1).isEqualTo(rest0 - 1);
    }

    @Test
    void inscrire_sameEmailTwice_shouldThrow() {
        Event e = createEventAndGetLast("Atelier", 5, "creator@mail.com");

        service.inscrire(e.getId(), "d@mail.com", "A", "B", null, null, null);

        assertThatThrownBy(() ->
                service.inscrire(e.getId(), "d@mail.com", "A", "B", null, null, null)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void inscrire_whenFull_shouldThrow() {
        Event e = createEventAndGetLast("Mini event", 1, "creator@mail.com");

        service.inscrire(e.getId(), "a@mail.com", "A", "B", null, null, null);
        assertThat(service.placesRestantes(e.getId())).isEqualTo(0);

        assertThatThrownBy(() ->
                service.inscrire(e.getId(), "b@mail.com", "C", "D", null, null, null)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void desinscrire_shouldIncreaseRemainingSeats_andReturnTrue() {
        Event e = createEventAndGetLast("Conf", 2, "creator@mail.com");

        service.inscrire(e.getId(), "x@mail.com", "X", "Y", null, null, null);

        long restBefore = service.placesRestantes(e.getId());

        boolean ok = service.desinscrire(e.getId(), "x@mail.com");

        long restAfter = service.placesRestantes(e.getId());

        assertThat(ok).isTrue();
        assertThat(restAfter).isEqualTo(restBefore + 1);
    }

    @Test
    void desinscrire_whenNotRegistered_shouldReturnFalse() {
        Event e = createEventAndGetLast("Conf", 2, "creator@mail.com");

        boolean ok = service.desinscrire(e.getId(), "unknown@mail.com");

        assertThat(ok).isFalse();
    }
}
