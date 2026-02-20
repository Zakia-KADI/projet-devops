package com.studentevents.events.controller;

import com.studentevents.events.model.User;
import com.studentevents.events.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserRepository userRepo;

    public AuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    private String cleanEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String name,
                               RedirectAttributes ra) {

        String cleanEmail = cleanEmail(email);

        if (cleanEmail.isBlank() || password == null || password.length() < 4) {
            ra.addFlashAttribute("msg", "Email invalide ou mot de passe trop court (min 4).");
            return "redirect:/signup";
        }

        if (userRepo.existsByEmail(cleanEmail)) {
            ra.addFlashAttribute("msg", "Un compte existe déjà avec cet email.");
            return "redirect:/signup";
        }

        userRepo.save(new User(cleanEmail, password, name));
        ra.addFlashAttribute("msg", "Compte créé ! Connecte-toi.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes ra) {

        String cleanEmail = cleanEmail(email);

        var userOpt = userRepo.findByEmail(cleanEmail);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            ra.addFlashAttribute("msg", "Email ou mot de passe incorrect.");
            return "redirect:/login";
        }

        session.setAttribute("userEmail", userOpt.get().getEmail());
        session.setAttribute("userName", userOpt.get().getName());

        ra.addFlashAttribute("msg", "Connexion réussie.");
        return "redirect:/events";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("msg", "Déconnexion OK.");
        return "redirect:/";
    }
}
