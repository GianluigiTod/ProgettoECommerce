package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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


    @OneToMany(mappedBy = "venditore", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rarity DESC")
    private List<Card> cards;


    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ordine> listaOrdini;

    @Version
    @JsonIgnore
    private Long version;
}
