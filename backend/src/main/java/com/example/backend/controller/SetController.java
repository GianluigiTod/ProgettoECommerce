package com.example.backend.controller;


import com.example.backend.controller.richieste.RichiestaSet;
import com.example.backend.exception.SetEsistente;
import com.example.backend.exception.SetInesistente;
import com.example.backend.model.Set;
import com.example.backend.service.ImageService;
import com.example.backend.service.SetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/set")
public class SetController {

    @Autowired
    private SetService setService;


    @GetMapping("/{id}/get")//cambia nome
    public ResponseEntity<Set> getSet(@PathVariable Long id) throws SetInesistente {
        return ResponseEntity.ok(setService.ottieniSet(id));
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createSet(
            @RequestParam("setCode") String setCode,
            @RequestParam("setName") String setName,
            @RequestPart("file") MultipartFile file) throws SetEsistente, IOException {

        return setService.createSetWithImage(setCode,setName,file);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<String> getImageUrl(@PathVariable Long id) {
        String imageUrl = setService.getImageUrl(id);
        return ResponseEntity.ok(imageUrl);
    }


    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity deleteSet(@PathVariable Long id) throws SetInesistente {
        return ResponseEntity.ok(setService.eliminaSet(id));
    }


    @PutMapping("/update")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Set> updateSet(@RequestBody Set set) throws SetInesistente{
        return ResponseEntity.ok(setService.aggiornaSet(set));
    }

    @PutMapping("/update/{id}/image")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateSetImage(@RequestPart MultipartFile file, @PathVariable Long id) throws SetInesistente, IOException {
        return setService.aggiornaImmagineSet(file, id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Set>> getSets() {
        return ResponseEntity.ok(setService.ottieniTuttiSet());
    }

}
