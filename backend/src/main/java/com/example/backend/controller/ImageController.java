package com.example.backend.controller;

import com.example.backend.exception.ImageNotFound;
import com.example.backend.model.InfoImmagine;
import com.example.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename) {
        try{
            InfoImmagine info = imageService.ottieniImmagine(filename);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, info.getContentType()).body(info.getFile());
        }catch (IOException e){
            return new ResponseEntity<>("Si è verificato un problema durante l'aggiornamento dell'immagine.", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(ImageNotFound e){
            return new ResponseEntity<>("L'immagine non è stata trovata.", HttpStatus.NOT_FOUND);
        }
    }


}
