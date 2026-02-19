package com.studentevents.events.repository;

import com.studentevents.events.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    long countByEventId(String eventId);

    boolean existsByEventIdAndEmail(String eventId, String email);

    void deleteByEventIdAndEmail(String eventId, String email);

    @Query("select i.eventId from Inscription i where i.email = :email")
    List<String> findEventIdsByEmail(@Param("email") String email);
}


