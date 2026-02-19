package com.studentevents.events.controller;

import com.studentevents.events.model.Event;
import com.studentevents.events.service.EventService;
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

    private String getUserEmailOrNull(HttpSession session) {
        Object v = session.getAttribute("userEmail");
        return (v instanceof String s && !s.isBlank()) ? s : null;
    }

    private EventView toView(Event e) {
        return new EventView(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getDateTime(),
                e.getLocation(),
                e.getMaxParticipants(),
                service.nbInscrits(e.getId()),
                service.placesRestantes(e.getId())
        );
    }


    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/events")
    public String list(Model model) {
        var views = service.listEvents().stream()
                .map(this::toView)
                .toList();

        model.addAttribute("events", views);
        return "events";
    }

    @GetMapping("/events/create")
    public String createForm(Model model, HttpSession session, RedirectAttributes ra) {
        String userEmail = getUserEmailOrNull(session);
        if (userEmail == null) {
            ra.addFlashAttribute("alertMsg", "Connecte-toi pour créer un événement.");
            return "redirect:/login";
        }

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
            HttpSession session,
            RedirectAttributes ra
    ) {
        String userEmail = getUserEmailOrNull(session);
        if (userEmail == null) {
            ra.addFlashAttribute("alertMsg", "Connecte-toi pour créer un événement.");
            return "redirect:/login";
        }

        service.createEvent(title, description, dateTime, location, maxParticipants, userEmail);

        ra.addFlashAttribute("alertMsg", "Événement créé avec succès");
        return "redirect:/events";
    }

    @GetMapping("/events/{id}")
    public String detail(@PathVariable String id, Model model) {
        var e = service.getOrThrow(id);
        model.addAttribute("event", toView(e));
        return "event-detail";
    }

    @GetMapping("/events/{id}/inscription")
    public String inscriptionPage(@PathVariable String id, Model model) {
        var e = service.getOrThrow(id);
        model.addAttribute("eventId", id);
        model.addAttribute("event", toView(e));
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
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("alertMsg", ex.getMessage());
        }

        return "redirect:/events/" + id;
    }

    @GetMapping("/events/{id}/desinscription-form")
    public String showForm(@PathVariable String id, Model model) {
        var e = service.getOrThrow(id);
        model.addAttribute("event", toView(e));
        return "desinscription";
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
