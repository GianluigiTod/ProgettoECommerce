package com.example.backend.controller;

import com.example.backend.dto.CartItemDTO;
import com.example.backend.exception.CartaInesistente;
import com.example.backend.exception.QuantityProblem;
import com.example.backend.exception.UtenteInesistente;
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

    // GET: Recupera tutti i CartItem per uno specifico utente
    @GetMapping("/{id}")
    public ResponseEntity<?> getCartItemsByUsername(@PathVariable Long id) {
        try{
            CartItemsResponse response = cartService.getCartItemsByUser(id);
            Set<CartItem> cartItems = response.getItems();
            if (cartItems.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(IllegalStateException e){
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login",HttpStatus.BAD_REQUEST);
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>("L'utente "+id+" non esiste", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/add")
    public ResponseEntity<?> addCartItem(@RequestBody CartItemDTO dto) {
        try {
            CartItem createdCartItem = cartService.addCartItem(dto);
            return new ResponseEntity<>(createdCartItem, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login.",HttpStatus.BAD_REQUEST);
        }catch(QuantityProblem e){
            return new ResponseEntity<>("La quanità deve essere tra 0 e la quantità della carta", HttpStatus.BAD_REQUEST);
        }catch(UtenteInesistente e){
            return new ResponseEntity<>("L'utente "+dto.getUtenteId()+" non esiste.", HttpStatus.BAD_REQUEST);
        }catch(CartaInesistente e){
            return new ResponseEntity<>("Carta "+dto.getCardId()+" non trovata", HttpStatus.BAD_REQUEST);
        }
    }

    //CONTINUARE A CORREGGERE LE ECCEZIONI DA QUI

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
        try{
            boolean isDeleted = cartService.deleteCartItem(id);
            if (isDeleted) {
                return new ResponseEntity<>("Cancellazzione avvenuta con successo",HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }


}

