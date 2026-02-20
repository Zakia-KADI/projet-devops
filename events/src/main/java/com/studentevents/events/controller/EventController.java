package com.studentevents.events.controller;

import com.studentevents.events.service.EventService;
import com.studentevents.events.model.Event;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        var views = service.listEvents().stream()
                .map(e -> new EventView(
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getDateTime(),
                        e.getLocation(),
                        e.getMaxParticipants(),
                        service.nbInscrits(e.getId()),
                        service.placesRestantes(e.getId())
                ))
                .toList();

        model.addAttribute("events", views);
        return "events";
    }

    @GetMapping("/events/create")
    public String createForm(Model model) {
        model.addAttribute("event", new Event());
        return "create";
    }

    @PostMapping("/events")
    public String createSubmit(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTime,
            @RequestParam String location,
            @RequestParam int maxParticipants,
            HttpSession session
    ) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";

        service.createEvent(title, description, dateTime, location, maxParticipants, userEmail);
        return "redirect:/events";
    }

    @GetMapping("/events/{id}")
    public String detail(@PathVariable String id, Model model,
                         @RequestParam(required = false) String msg) {

        var e = service.getOrThrow(id);

        var view = new EventView(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getDateTime(),
                e.getLocation(),
                e.getMaxParticipants(),
                service.nbInscrits(e.getId()),
                service.placesRestantes(e.getId())
        );

        model.addAttribute("event", view);
        model.addAttribute("msg", msg);
        return "event-detail";
    }

    @GetMapping("/events/{id}/inscription")
    public String inscriptionPage(@PathVariable String id, Model model) {

        var e = service.getOrThrow(id);

        var view = new EventView(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getDateTime(),
                e.getLocation(),
                e.getMaxParticipants(),
                service.nbInscrits(e.getId()),
                service.placesRestantes(e.getId())
        );

        model.addAttribute("eventId", id);
        model.addAttribute("event", view);
        return "inscription";
    }

    @PostMapping("/events/{id}/inscription")
    public String inscrire(@PathVariable String id,
                           @RequestParam String email,
                           @RequestParam String prenom,
                           @RequestParam String nom,
                           @RequestParam(required = false) String telephone,
                           @RequestParam(required = false) String numeroEtudiant,
                           @RequestParam(required = false) String commentaire,
                           RedirectAttributes ra) {

        try {
            service.inscrire(id, email, prenom, nom, telephone, numeroEtudiant, commentaire);
            ra.addFlashAttribute("alertMsg", "Inscription réussie");
        } catch (Exception e) {
            ra.addFlashAttribute("alertMsg", e.getMessage());
        }

        return "redirect:/events/" + id;
    }

    @PostMapping("/events/{id}/desinscription")
    public String desinscrire(@PathVariable String id,
                             @RequestParam String email,
                             RedirectAttributes ra) {

        boolean ok = service.desinscrire(id, email);

        ra.addFlashAttribute("alertMsg",
                ok ? "Désinscription réussie" : "Utilisateur non inscrit");

        return "redirect:/events/" + id;
    }

    @GetMapping("/events/{id}/desinscription-form")
    public String showForm(@PathVariable String id, Model model) {
        var e = service.getOrThrow(id);

        var view = new EventView(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getDateTime(),
                e.getLocation(),
                e.getMaxParticipants(),
                service.nbInscrits(e.getId()),
                service.placesRestantes(e.getId())
        );

        model.addAttribute("event", view);
        return "desinscription";
    }

    @GetMapping("/my-events")
    public String myEvents(HttpSession session, Model model) {

        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";

        var created = service.listEventsCreatedBy(userEmail).stream()
                .map(e -> new EventView(
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getDateTime(),
                        e.getLocation(),
                        e.getMaxParticipants(),
                        service.nbInscrits(e.getId()),
                        service.placesRestantes(e.getId())
                ))
                .toList();

        var joined = service.listEventsWhereUserRegistered(userEmail).stream()
                .map(e -> new EventView(
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getDateTime(),
                        e.getLocation(),
                        e.getMaxParticipants(),
                        service.nbInscrits(e.getId()),
                        service.placesRestantes(e.getId())
                ))
                .toList();

        model.addAttribute("createdEvents", created);
        model.addAttribute("joinedEvents", joined);
        return "my-events";
    }

    public static record EventView(
            String id,
            String title,
            String description,
            LocalDateTime dateTime,
            String location,
            int maxParticipants,
            long nbInscrits,
            long placesRestantes
    ) {}
}

