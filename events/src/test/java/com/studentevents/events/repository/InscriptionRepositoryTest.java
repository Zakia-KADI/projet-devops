package com.studentevents.events.repository;

import com.studentevents.events.model.Inscription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InscriptionRepositoryTest {

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Test
    void countByEventId_shouldCountInscriptions() {
        inscriptionRepository.save(new Inscription("E1", "a@mail.com", "A", "B", null, null, null));
        inscriptionRepository.save(new Inscription("E1", "c@mail.com", "C", "D", null, null, null));
        inscriptionRepository.save(new Inscription("E2", "x@mail.com", "X", "Y", null, null, null));

        long countE1 = inscriptionRepository.countByEventId("E1");

        assertEquals(2L, countE1);
    }

    @Test
    void existsByEventIdAndEmail_shouldReturnTrueIfExists() {
        inscriptionRepository.save(new Inscription("E1", "a@mail.com", "A", "B", null, null, null));

        assertTrue(inscriptionRepository.existsByEventIdAndEmail("E1", "a@mail.com"));
        assertFalse(inscriptionRepository.existsByEventIdAndEmail("E1", "nope@mail.com"));
    }

    @Test
    void deleteByEventIdAndEmail_shouldDeleteOnlyThatRow() {
        inscriptionRepository.save(new Inscription("E1", "a@mail.com", "A", "B", null, null, null));
        inscriptionRepository.save(new Inscription("E1", "b@mail.com", "B", "C", null, null, null));

        assertEquals(2L, inscriptionRepository.countByEventId("E1"));

        inscriptionRepository.deleteByEventIdAndEmail("E1", "a@mail.com");

        assertEquals(1L, inscriptionRepository.countByEventId("E1"));
        assertFalse(inscriptionRepository.existsByEventIdAndEmail("E1", "a@mail.com"));
        assertTrue(inscriptionRepository.existsByEventIdAndEmail("E1", "b@mail.com"));
    }

    @Test
    void findEventIdsByEmail_shouldReturnEventIdsOfThatEmail() {
        inscriptionRepository.save(new Inscription("E1", "a@mail.com", "A", "B", null, null, null));
        inscriptionRepository.save(new Inscription("E2", "a@mail.com", "A", "B", null, null, null));
        inscriptionRepository.save(new Inscription("E3", "o@mail.com", "X", "Y", null, null, null));

        var ids = inscriptionRepository.findEventIdsByEmail("a@mail.com");

        assertEquals(2, ids.size());
        assertTrue(ids.contains("E1"));
        assertTrue(ids.contains("E2"));
        assertFalse(ids.contains("E3"));
    }
}
