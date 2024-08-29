package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class Card {

    public enum Rarity{comune, non_comune, rara, rara_mitica}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private float prezzo;
    @Column(nullable = false, name="venditore_username")
    private String usernameVenditore;

    private String manaCost;
    private String type;

    @Enumerated(EnumType.STRING)
    private Rarity rarity;

    @Column(length = 1023)
    private String text;

    private Integer power;
    private Integer toughness;

    @Column(nullable = false, name="set_code")
    private String setCode;

    @Lob
    @Column
    private String imagePath;

    @Column(nullable = false)
    private int quantita;

    @Version
    @JsonIgnore
    private Long version;

    // Associazione con Utente usando il campo username_venditore
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "venditore_username", referencedColumnName = "username", insertable = false, updatable = false)
    private Utente venditore;


    // Associazione con Set usando il campo setCode
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "set_code", referencedColumnName = "setCode", insertable = false, updatable = false)
    private com.example.backend.model.Set set;



    @OneToMany(mappedBy = "originalCard", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private Set<CartItem> cartItems;
}