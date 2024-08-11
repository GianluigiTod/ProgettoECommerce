package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${image.upload.dir}")
    private String imageUploadDir;  // Directory dove le immagini sono salvate

    @GetMapping("/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename) {
        System.out.println(filename);
        File file = Paths.get(imageUploadDir, filename).toFile();
        if (file.exists()) {
            /*
            FileSystemResource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")  // Modifica in base al tipo di immagine
                    .body(resource);

             */
            try {
                // Determina il tipo MIME del file
                Path filePath = file.toPath();
                String contentType = Files.probeContentType(filePath);

                if (contentType == null) {
                    contentType = "application/octet-stream";  // Tipo predefinito se non trovato
                }

                FileSystemResource resource = new FileSystemResource(file);
                //inviamo l'immagine con il relativo content-type nell'header
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving file type");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
        }
    }
}
