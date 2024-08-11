package com.example.backend.controller;


import com.example.backend.controller.richieste.RichiestaSet;
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

@RestController
@RequestMapping("api/set")
public class SetController {

    @Autowired
    private SetService setService;



    @GetMapping("/get")//cambia nome
    public ResponseEntity<Set> getSet(@RequestBody RichiestaSet richiesta) throws SetInesistente {
        return ResponseEntity.ok(setService.ottieniSet(richiesta));
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createSet(
            @RequestParam("setCode") String setCode,
            @RequestParam("setName") String setName,
            @RequestPart("file") MultipartFile file) throws SetEsistente, IOException {

        try {
            Set set = new Set();
            set.setSetCode(setCode);
            set.setSetName(setName);

            Set createdSet = setService.createSetWithImage(set, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSet);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<String> getImageUrl(@PathVariable Long id) {
        String imageUrl = setService.getImageUrl(id);
        return ResponseEntity.ok(imageUrl);
    }


    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('admin')")
    public void deleteSet(@RequestBody Set set) throws SetInesistente {
        setService.eliminaSet(set);
    }


    @PutMapping("/update")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Set> updateSet(@RequestBody Set set) throws SetInesistente{
        return ResponseEntity.ok(setService.aggiornaSet(set));
    }

}
