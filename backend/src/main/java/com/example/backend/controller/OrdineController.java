package com.example.backend.controller;

import com.example.backend.exception.ImageNotFound;
import com.example.backend.exception.QuantityProblem;
import com.example.backend.model.Ordine;
import com.example.backend.service.OrdineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrdineController {

    @Autowired
    private OrdineService ordineService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getListaOrdini(@PathVariable Long id) {
        try{
            List<Ordine> ordini = ordineService.getListaOrdini(id);
            if(ordini.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(ordini, HttpStatus.OK);
        }catch (IllegalStateException e) {
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("L'utente "+id+" non esiste.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> addOrdine(@RequestBody List<Long> cartItemsId){
        try{
            Ordine ordine = ordineService.checkout(cartItemsId);
            return new ResponseEntity<>(ordine, HttpStatus.CREATED);
        }catch (IllegalStateException e) {
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("C'è un problema con gli elementi del carrello.",HttpStatus.BAD_REQUEST);
        }catch(QuantityProblem e){
            return new ResponseEntity<>("La quantità di un cartItem non può essere più grande di quella della carta a cui è legato.",HttpStatus.BAD_REQUEST);
        }catch(ImageNotFound e){
            return new ResponseEntity<>("L'immagine relativa a una delle carte che hai acquistato non è stata trovata", HttpStatus.NOT_FOUND);
        }
    }

    //imposta a prescindere il valore arrivato a true
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateOrdine(@PathVariable Long id){
        try {
            boolean arrivato = ordineService.setArrivato(id);
            if(arrivato) {
                return new ResponseEntity<>("Conferma arrivo ordine avvenuta con successo", HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch (IllegalStateException e){
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login",HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrdine(@PathVariable Long id){
        try{
            boolean isDeleted = ordineService.deleteOrdine(id);
            if(isDeleted){
                return new ResponseEntity<>("Cancellazzione avvenuta con successo", HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch(IllegalStateException e){
            return new ResponseEntity<>("L'utente che hai specificato non è lo stesso con cui hai fatto il login",HttpStatus.BAD_REQUEST);
        }
    }


}
