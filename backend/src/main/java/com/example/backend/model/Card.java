package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private float prezzo;
    @Column(nullable = false)
    private String username_venditore;

    private String manaCost;
    private String type;
    private String rarity;
    private String text;
    private Integer power;
    private Integer toughness;

    @Column(nullable = false)
    private String setCode;

    private String setName;
    @Lob
    @Column(name = "image", columnDefinition = "BYTEA")
    private String image;

    //vanno definiti meglio questi campi, cioè se devono essere unici oppure non nulli oppure enum etc..
}