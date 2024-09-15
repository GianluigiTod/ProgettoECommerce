package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Embeddable
@Data
public class CardSnapshot {

    @Column(nullable = false, name = "snapshot_name")
    private String name;
    @Column(nullable = false, name="venditore_username")
    private String usernameVenditore;
    @Column(nullable = false, name="set_code")
    private String setCode;
    @Column(nullable = false)
    private Long snapCardId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Card.Rarity rarity;

}
