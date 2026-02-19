package com.studentevents.events.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {

    @Id
    private String id;

    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDateTime dateTime;

    private String location;

    private int maxParticipants;

    @Column(nullable = true)
    private String createdByEmail;


    public Event() {
        this.id = UUID.randomUUID().toString();
    }

    public Event(String title, String description, LocalDateTime dateTime,
             String location, int maxParticipants) {
    this(title, description, dateTime, location, maxParticipants, null);
}


    public Event(String title, String description, LocalDateTime dateTime,
             String location, int maxParticipants, String createdByEmail) {

    this(); 

    this.title = title;
    this.description = description;
    this.dateTime = dateTime;
    this.location = location;
    this.maxParticipants = maxParticipants;
    this.createdByEmail = createdByEmail;
}

    public String getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public String getCreatedByEmail() { return createdByEmail; }
    public void setCreatedByEmail(String createdByEmail) { this.createdByEmail = createdByEmail; }
}
