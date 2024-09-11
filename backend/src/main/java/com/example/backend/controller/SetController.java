package com.example.backend.controller;


import com.example.backend.exception.ImageNotFound;
import com.example.backend.exception.SetEsistente;
import com.example.backend.exception.SetInesistente;
import com.example.backend.model.Set;
import com.example.backend.service.SetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/set")
public class SetController {

    @Autowired
    private SetService setService;

    @GetMapping("/all")
    public ResponseEntity<List<Set>> getSets() {
        return ResponseEntity.ok(setService.ottieniTuttiSet());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSet(@PathVariable Long id){
        try{
            Set s = setService.ottieniSet(id);
            return new ResponseEntity<>(s, HttpStatus.OK);
        }catch(SetInesistente e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/create-image")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createSet(
            @RequestParam("setCode") String setCode,
            @RequestParam("setName") String setName,
            @RequestPart("file") MultipartFile file) {
        try{
            Set s = setService.createSetWithImage(setCode, setName, file);
            return new ResponseEntity<>(s, HttpStatus.CREATED);
        }catch(IOException ioe){
            return new ResponseEntity<>("Si è verificato un problema durante la creazione dell'immagine", HttpStatus.BAD_REQUEST);
        }catch(SetEsistente e){
            return new ResponseEntity<>("Il set "+setCode+" già esiste.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createSet(
            @RequestParam("setCode") String setCode,
            @RequestParam("setName") String setName ) {
        try{
            Set s = setService.createSetWithImage(setCode, setName, null);
            return new ResponseEntity<>(s, HttpStatus.CREATED);
        }catch(IOException ioe){
            return new ResponseEntity<>("Si è verificato un problema durante la creazione dell'immagine", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(SetEsistente e){
            return new ResponseEntity<>("Il set "+setCode+" già esiste.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<String> getImageUrl(@PathVariable Long id) {
        try{
            String imageUrl = setService.getImageUrl(id);
            return new ResponseEntity<>(imageUrl, HttpStatus.OK);
        }catch(SetInesistente e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> deleteSet(@PathVariable Long id) {
        try{
            setService.eliminaSet(id);
            return new ResponseEntity<>("Cancellazione avvenuta con successo.", HttpStatus.OK);
        }catch (SetInesistente e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(ImageNotFound e){
            return new ResponseEntity<>("L'immagine relativa al set non è stata trovata.", HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/update")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateSet(@RequestBody Set set) {
        try{
            Set s = setService.aggiornaSet(set);
            return new ResponseEntity<>(s, HttpStatus.OK);
        }catch(SetEsistente e){
            return new ResponseEntity<>("Esiste già un set con setCode: "+set.getSetCode(),HttpStatus.BAD_REQUEST);
        }catch(SetInesistente e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}/image")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> updateSetImage(@RequestPart MultipartFile file, @PathVariable Long id) {
        try{
            setService.aggiornaImmagineSet(file, id);
            return new ResponseEntity<>("Aggiornamento avvenuto con successo.",HttpStatus.OK);
        }catch(SetInesistente e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(IOException ioe){
            return new ResponseEntity<>("Si è verificato un problema durante l'aggiornamento dell'immagine", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(ImageNotFound e){
            return new ResponseEntity<>("L'immagine relativa al set non è stata trovata.", HttpStatus.NOT_FOUND);
        }
    }



}
