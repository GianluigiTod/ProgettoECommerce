package com.example.backend.controller;


import com.example.backend.dto.CardDTO;
import com.example.backend.dto.CardPageResponse;
import com.example.backend.exception.*;
import com.example.backend.model.Card;
import com.example.backend.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/card")
public class CardController {

    @Autowired
    private CardService cardService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getCardById(@PathVariable Long id){
        try{
            Card c = cardService.getCardById(id);
            return new ResponseEntity<>(c, HttpStatus.OK);
        }catch(CartaInesistente e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/all")
    public ResponseEntity<CardPageResponse> getAllCards(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<String> direction
    ) {
        int pageNumber = page.orElse(0);
        int pageSize = size.orElse(10);
        String sortField = sortBy.orElse("name");
        String sortDirection = direction.orElse("asc");
        CardPageResponse p = cardService.getAllCards(pageNumber, pageSize, sortField, sortDirection);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

    @GetMapping("/set/{setId}")
    public ResponseEntity<?> getCardsBySetCode(
            @PathVariable Long setId,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size
    ) {
        try{
            int pageNumber = page.orElse(0);
            int pageSize = size.orElse(10);
            CardPageResponse p = cardService.getCardsBySetId(setId, pageNumber, pageSize);
            return new ResponseEntity<>(p, HttpStatus.OK);
        }catch(SetInesistente e){
            return new ResponseEntity<>("Il set "+setId+" non esiste.", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/seller/{userId}")
    public ResponseEntity<?> getCardsBySeller(
            @PathVariable Long userId,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size
    ) {
        try{
            int pageNumber = page.orElse(0);
            int pageSize = size.orElse(10);
            CardPageResponse p = cardService.getCardsBySellerId(userId, pageNumber, pageSize);
            return new ResponseEntity<>(p, HttpStatus.OK);
        }catch(UtenteInesistente e){
            return new ResponseEntity<>("L'utente "+userId+" non esiste.", HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/search")
    public ResponseEntity<CardPageResponse> searchCardsByName(
            @RequestParam String name,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size
    ) {
        int pageNumber = page.orElse(0);
        int pageSize = size.orElse(10);
        CardPageResponse p = cardService.searchCardsByName(name, pageNumber, pageSize);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('venditore') or hasRole('admin')")
    public ResponseEntity<?> createCard(@Valid @RequestBody CardDTO cardDTO) {
        try{
            Card savedCard = cardService.createCard(cardDTO);
            return new ResponseEntity<>(savedCard, HttpStatus.CREATED);
        }catch(UtenteInesistente e){
            return new ResponseEntity<>("L'utente "+cardDTO.getVenditoreId()+" non esiste.", HttpStatus.NOT_FOUND);
        }catch(IllegalStateException e){
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login.", HttpStatus.BAD_REQUEST);
        }catch(SetInesistente e){
            return new ResponseEntity<>("Il set "+cardDTO.getSetId()+" non esiste.", HttpStatus.NOT_FOUND);
        }catch(PriceProblem e){
            return new ResponseEntity<>("Il prezzo deve essere maggiore di 0.", HttpStatus.BAD_REQUEST);
        }catch(QuantityProblem e){
            return new ResponseEntity<>("La quantità deve essere maggiore di 0.", HttpStatus.BAD_REQUEST);
        }catch(IOException ioe){
            return new ResponseEntity<>("Si è verificato un problema durante la creazione dell'immagine.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/{id}/image")
    public ResponseEntity<String> getCardImage(@PathVariable Long id){
        try{
            String url = cardService.getImageUrl(id);
            return new ResponseEntity<>(url, HttpStatus.OK);
        }catch (CartaInesistente e){
            return new ResponseEntity<>("La carta "+id+" non esiste.", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/setImage")
    @PreAuthorize("hasRole('venditore') or hasRole('admin')")
    public ResponseEntity<?> addImage(@RequestParam MultipartFile file, @PathVariable Long id)  {
        try{
            Card c = cardService.setCardImage(file, id);
            return new ResponseEntity<>(c, HttpStatus.OK);
        }catch(CartaInesistente e){
            return new ResponseEntity<>("La carta "+id+" non esiste.", HttpStatus.NOT_FOUND);
        }catch(IOException e){
            return new ResponseEntity<>("Si è verificato un problema durante l'aggiornamento dell'immagine.", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(ImageNotFound e){
            return new ResponseEntity<>("L'immagine da eliminare non è stata trovata", HttpStatus.NOT_FOUND);
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('venditore') or hasRole('admin')")
    public ResponseEntity<?> updateCard(@RequestBody Card card) {
        try{
            Card c = cardService.aggiornaCarta(card);
            return new ResponseEntity<>(c, HttpStatus.OK);
        }catch(CartaInesistente e){
            return new ResponseEntity<>("La carta "+card.getId()+" non esiste.", HttpStatus.NOT_FOUND);
        }catch(IllegalStateException e){
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login.", HttpStatus.BAD_REQUEST);
        }catch(PriceProblem e){
            return new ResponseEntity<>("Il prezzo deve essere maggiore di 0", HttpStatus.BAD_REQUEST);
        }catch(QuantityProblem e){
            return new ResponseEntity<>("La quantità deve essere maggiore di 0", HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('venditore') or hasRole('admin')")
    public ResponseEntity<String> deleteCard(@PathVariable Long id)  {
        try{
            cardService.deleteCard(id);
            return new ResponseEntity<>("Cancellazione effettuata con successo", HttpStatus.OK);
        }catch(CartaInesistente e){
            return new ResponseEntity<>("La carta "+id+" non esiste.", HttpStatus.NOT_FOUND);
        }catch(IllegalStateException e){
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login.", HttpStatus.BAD_REQUEST);
        }catch(ImageNotFound e){
            return new ResponseEntity<>("L'immagine relativa alla carta non è stata trovata.", HttpStatus.NOT_FOUND);
        }
    }
}
