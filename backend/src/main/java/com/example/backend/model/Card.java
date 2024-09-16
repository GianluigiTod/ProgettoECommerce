package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
@Entity
public class Card {

    public enum Rarity{comune, non_comune, rara, rara_mitica}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotBlank
    private float prezzo;

    @Column(name="venditore_username")
    private String usernameVenditore;

    private String manaCost;
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Rarity rarity;

    @Column(length = 1023)
    private String text;

    private int power;
    private int toughness;

    @Column(name="set_code")
    private String setCode;

    @Column
    private String imagePath;

    @Column(nullable = false)
    @NotBlank
    private int quantity;

    @Version
    @JsonIgnore
    private Long version;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "venditore_id", nullable = false)
    private Utente venditore;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "set_id", nullable = false)
    private com.example.backend.model.Set set;


    @OneToMany(mappedBy = "originalCard", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private Set<CartItem> cartItems;
}