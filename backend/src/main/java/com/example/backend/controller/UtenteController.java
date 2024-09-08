package com.example.backend.controller;

import com.example.backend.exception.UtenteEsistente;
import com.example.backend.exception.UtenteInesistente;
import com.example.backend.model.Utente;
import com.example.backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;


    @PutMapping("/update")
    public ResponseEntity<?> modificaUtente(@RequestBody Utente utente) {
        try{
            Utente ret = utenteService.modificaInfo(utente);
            return new ResponseEntity<>(ret, HttpStatus.OK);
        }catch(IllegalStateException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(UtenteInesistente e){
            return new ResponseEntity<>("L'utente "+utente.getId()+" non esiste.", HttpStatus.BAD_REQUEST);
        }catch (UtenteEsistente e){
            return new ResponseEntity<>("L'utente già esiste ", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registra(@RequestBody Utente u) {
        try{
            Utente ret = utenteService.registra(u);
            return new ResponseEntity<>(ret, HttpStatus.OK);
        }catch(UtenteEsistente e){
            return new ResponseEntity<>("L'utente "+u.getId()+" già esiste.", HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> cancellaUtente(@PathVariable Long id){
        try{
            boolean isDeleated = utenteService.cancellaUtente(id);
            if(isDeleated){
                return new ResponseEntity<>("Cancellazione avvenuta con successo", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("L'utente "+id+" non esiste.", HttpStatus.BAD_REQUEST);
            }
        }catch(IllegalStateException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getUtente(@RequestParam String username) {
        try{
            Utente u = utenteService.ottieniUtente(username);
            return new ResponseEntity<>(u, HttpStatus.OK);
        }catch(UtenteInesistente e){
            return new ResponseEntity<>("L'utente "+username+" non esiste.", HttpStatus.BAD_REQUEST);
        }
    }
}
