package com.studentevents.events.repository;

import com.studentevents.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {

    List<Event> findByCreatedByEmail(String email);

}
