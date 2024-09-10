package com.example.backend.dto;

import com.example.backend.model.CartItem;
import lombok.Data;

import java.util.List;

@Data
public class CartItemsResponse {
    private List<CartItem> items;
    private boolean hasChanged;
    private String message;
}
