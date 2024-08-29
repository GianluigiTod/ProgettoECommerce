package com.example.backend.dto;

import com.example.backend.model.CartItem;
import lombok.Data;
import java.util.Set;

@Data
public class CartItemsResponse {
    private Set<CartItem> items;
    private boolean hasChanged;
    private String message;
}
