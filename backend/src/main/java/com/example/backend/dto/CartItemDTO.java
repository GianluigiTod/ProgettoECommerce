package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartItemDTO {
    @NotBlank
    private Long cardId;
    @NotBlank
    private Long utenteId;
    @NotBlank
    private int quantity;

}
