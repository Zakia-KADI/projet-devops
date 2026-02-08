package com.studentevents.events.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {
    private final String id;

    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private int maxParticipants;
    private int registeredCount;

    public Event() {
        this.id = UUID.randomUUID().toString();
    }

    public Event(String title, String description, LocalDateTime dateTime, String location, int maxParticipants) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.registeredCount = 0;
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

    public int getRegisteredCount() { return registeredCount; }

    public int getRemainingSeats() {
        return Math.max(0, maxParticipants - registeredCount);
    }

    public boolean isFull() {
        return getRemainingSeats() == 0;
    }

    public void registerOne() {
        if (isFull()) throw new IllegalStateException("Complet");
        registeredCount++;
    }

    public void unregisterOne() {
        if (registeredCount <= 0) throw new IllegalStateException("Aucune inscription");
        registeredCount--;
    }
}
