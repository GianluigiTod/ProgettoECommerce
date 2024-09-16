package com.example.backend.dto;

import com.example.backend.model.Card;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardDTO {

    @NotBlank
    private String name;


    @NotBlank
    private float prezzo;

    private String manaCost;
    private String type;

    @NotBlank
    private Card.Rarity rarity;
    private String text;
    private int power;
    private int toughness;


    @NotBlank
    private int quantity;

    @NotBlank
    private Long venditoreId;
    @NotBlank
    private Long setId;
}
