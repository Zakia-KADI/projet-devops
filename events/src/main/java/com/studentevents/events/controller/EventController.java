package com.studentevents.events.controller;

import com.studentevents.events.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/events")
    public String list(Model model) {
        model.addAttribute("events", service.list());
        return "events";
    }

    @GetMapping("/events/create")
    public String createForm() {
        return "create";
    }

    @PostMapping("/events/create")
    public String createSubmit(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam String location,
            @RequestParam int maxParticipants
    ) {
        service.create(title, description, dateTime, location, maxParticipants);
        return "redirect:/events";
    }

}