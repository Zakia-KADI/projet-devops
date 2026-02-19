package com.studentevents.events.model;

import jakarta.persistence.*;

@Entity
@Table(
  name = "inscriptions",
  uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "email"})
)
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="event_id", nullable=false)
    private String eventId;

    @Column(nullable=false)
    private String email;

    private String prenom;
    private String nom;

    private String telephone;
    private String numeroEtudiant;

    @Column(length = 2000)
    private String commentaire;

    public Inscription() {}

    public Inscription(String eventId, String email, String prenom, String nom,
                       String telephone, String numeroEtudiant, String commentaire) {
        this.eventId = eventId;
        this.email = email.trim().toLowerCase();
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.numeroEtudiant = numeroEtudiant;
        this.commentaire = commentaire;
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public String getEmail() { return email; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
}

