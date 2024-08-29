package com.example.backend.controller;

import com.example.backend.dto.CartItemDTO;
import com.example.backend.model.CartItem;
import com.example.backend.dto.CartItemsResponse;
import com.example.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // GET: Recupera tutti i CartItem per uno specifico username
    @GetMapping("/{username}")
    public ResponseEntity<CartItemsResponse> getCartItemsByUsername(@PathVariable String username) {
        CartItemsResponse response = cartService.getCartItemsByUser(username);
        Set<CartItem> cartItems = response.getItems();
        if (cartItems.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<CartItem> addCartItem(@RequestBody CartItemDTO dto) {
        try {
            CartItem createdCartItem = cartService.addCartItem(dto);
            return new ResponseEntity<>(createdCartItem, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long id, @RequestParam int quantity) {
        try {
            CartItem updatedCartItem = cartService.updateCartItem(id, quantity);
            if (updatedCartItem != null) {
                return new ResponseEntity<>(updatedCartItem, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long id) {
        boolean isDeleted = cartService.deleteCartItem(id);
        if (isDeleted) {
            return new ResponseEntity<>("Cancellazzione avvenuta con successo",HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}

