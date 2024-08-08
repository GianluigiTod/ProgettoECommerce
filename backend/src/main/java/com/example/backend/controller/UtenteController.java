package com.example.backend.controller;

import com.example.backend.controller.richieste.AggiornamentoPassword;
import com.example.backend.controller.richieste.RichiestaUtente;
import com.example.backend.exception.UtenteEsistente;
import com.example.backend.exception.UtenteInesistente;
import com.example.backend.model.Utente;
import com.example.backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/utente")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;


    @PutMapping("/mod")
    public ResponseEntity modificaUtente(@RequestBody Utente utente) throws UtenteInesistente, UtenteEsistente {
        return ResponseEntity.ok(utenteService.modificaInfo(utente));
    }

    @PutMapping("/change")
    public ResponseEntity cambiaPassword(@RequestBody AggiornamentoPassword request) throws UtenteInesistente, UtenteEsistente {
        return ResponseEntity.ok(utenteService.cambiaPassword(request));

    }

    @PostMapping("/register")
    public ResponseEntity<Utente> registra(@RequestBody Utente u) throws UtenteEsistente {
        return ResponseEntity.ok(utenteService.registra(u));
    }

    @DeleteMapping("/delete")
    public void cancellaUtente(@RequestBody Utente u){
        utenteService.cancellaUtente(u);
    }

    @GetMapping("get")
    public ResponseEntity<Utente> getUtente(@RequestBody RichiestaUtente richiesta) throws UtenteInesistente {
        return ResponseEntity.ok(utenteService.ottieniUtente(richiesta));
    }
}
