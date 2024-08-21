package com.example.backend.controller;


import com.example.backend.exception.CartaInesistente;
import com.example.backend.model.Card;
import com.example.backend.service.CardService;
import com.example.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;


    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable Long id) throws CartaInesistente {
        return ResponseEntity.ok(cardService.getCardById(id));
    }


    @GetMapping("/all")
    public Page<Card> getAllCards(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<String> direction
    ) {
        int pageNumber = page.orElse(0);
        int pageSize = size.orElse(10);
        String sortField = sortBy.orElse("name");//ho sostituito id con name, perchè altrimenti mostriamo per prime le carte più vecchie(di default)
        String sortDirection = direction.orElse("asc");
        return cardService.getAllCards(pageNumber, pageSize, sortField, sortDirection);
    }

    @GetMapping("/set/{setCode}")
    public Page<Card> getCardsBySetCode(
            @PathVariable String setCode,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size
    ) {
        int pageNumber = page.orElse(0);
        int pageSize = size.orElse(10);
        return cardService.getCardsBySetCode(setCode, pageNumber, pageSize);
    }

    @GetMapping("/seller/{username}")
    public Page<Card> getCardsBySeller(
            @PathVariable String username,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size
    ) {
        int pageNumber = page.orElse(0);
        int pageSize = size.orElse(10);
        return cardService.getCardsBySeller(username, pageNumber, pageSize);
    }

    @GetMapping("/search")
    public Page<Card> searchCardsByName(
            @RequestParam String name,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size
    ) {
        int pageNumber = page.orElse(0);
        int pageSize = size.orElse(10);
        return cardService.searchCardsByName(name, pageNumber, pageSize);
    }

    /*
    @GetMapping("/price")
    public Page<Card> getAllCardsOrderedByPrice(
            @RequestParam String direction,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size
    ) {
        int pageNumber = page.orElse(0);
        int pageSize = size.orElse(10);
        return cardService.getAllCardsOrderedByPrice(direction, pageNumber, pageSize);
    }

    @GetMapping("/rarity")
    public Page<Card> getAllCardsOrderedByRarity(
            @RequestParam String direction,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size
    ) {
        int pageNumber = page.orElse(0);
        int pageSize = size.orElse(10);
        return cardService.getAllCardsOrderedByRarity(direction, pageNumber, pageSize);
    }

     */


    @PostMapping("/create")//la post va fatta diversa, bisogna guardare il set, per via dell'immagine
    @PreAuthorize("hasRole('venditore')")
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        Card savedCard = cardService.createCard(card);
        return new ResponseEntity<>(savedCard, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/setImage")
    @PreAuthorize("hasRole('venditore')")
    public ResponseEntity<?> addImage(@RequestParam MultipartFile file, @PathVariable Long id) throws CartaInesistente, IOException {
        return cardService.setCardImage(file, id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('venditore')")
    public ResponseEntity<Card> updateCard(@RequestBody Card card) throws CartaInesistente, IOException {
        return ResponseEntity.ok(cardService.aggiornaCarta(card));
    }


    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('venditore')")
    public ResponseEntity deleteCard(@PathVariable Long id) throws CartaInesistente {
        return ResponseEntity.ok(cardService.deleteCard(id));
    }
}
