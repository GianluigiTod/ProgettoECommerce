package com.example.backend.exception;

public class CartaInesistente extends Exception {
    public CartaInesistente(String message) {
        super(message);
    }
}
