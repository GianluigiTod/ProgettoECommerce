package com.example.backend.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long cardId;
    private Long utenteId;
    private int quantity;

}
