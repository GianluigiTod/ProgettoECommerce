package com.example.backend.dto;

import com.example.backend.model.Card;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class CardDTO {

    public enum Rarity{comune, non_comune, rara, rara_mitica}

    private String name;
    private float prezzo;
    private String manaCost;
    private String type;
    private Card.Rarity rarity;
    private String text;
    private int power;
    private int toughness;
    private int quantity;
    private Long venditoreId;
    private Long setId;
}
