package com.example.backend.controller;

import com.example.backend.model.Utente;
import com.example.backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AutController {

    @Autowired
    private UtenteService service;

    @PostMapping("/register")
    public ResponseEntity<?> registro(@RequestBody Utente utente) {
        if (service.findByUsername(utente.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Questo username è già usato");
        }
        return ResponseEntity.ok(service.registro(utente));
    }

    @GetMapping("/currentUser")
    public ResponseEntity<?> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userDetails);
    }
}

