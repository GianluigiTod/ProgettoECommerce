package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Data
public class Utente {
    public enum Ruolo{cliente, admin, venditore}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;


    @Column(length=255)
    private String nome;

    @Column(length = 255)
    private String cognome;

    @Column(length = 255)
    private String indirizzo;


    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;


    // Relazione con l'entità Card
    @OneToMany(mappedBy = "venditore", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Card> cards;


    // Relazione con CartItem
    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<CartItem> cartItems;

    //Relazione con Ordine
    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Ordine> listaOrdini;
}
