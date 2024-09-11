package com.example.backend.service;

import com.example.backend.exception.ImageNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${image.upload.dir}")
    private String imageUploadDir;  // Directory dove le immagini sono salvate

    public ResponseEntity<?> ottieniImmagine(String filename){
        File file = Paths.get(imageUploadDir, filename).toFile();
        if (file.exists()) {
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

    public String creaImmagine(MultipartFile file) throws IOException {
        String fileName=null;
        if (file == null || file.isEmpty()) {
            // Usa un'immagine di default se il file è vuoto
            fileName = "/immagine_mancante.jpg"; // File già esistente nella directory
        } else {
            // Genera un nome unico per il file
            fileName = "/"+ UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(imageUploadDir, fileName);
            Files.createDirectories(path.getParent());  // Crea la directory se non esiste
            Files.write(path, file.getBytes());  // Salva il file
        }
        return fileName;
    }

    public void eliminaImmagine(String filename) throws  ImageNotFound{
        File file = Paths.get(imageUploadDir, filename).toFile();
        boolean deleted;
        if (file.exists()) {
            if(!filename.equals("/immagine_mancante.jpg")){
                deleted = file.delete();
                if (!deleted) {
                    throw new RuntimeException("Failed to delete image: " + filename);
                }
            }else{
                System.out.println("L'immagine mancante non è stata eliminata");
            }
        } else {
            throw new ImageNotFound();
        }
    }

    public String aggiornaImmagine(String filename, MultipartFile file) throws IOException, ImageNotFound {
        eliminaImmagine(filename);
        return creaImmagine(file);
    }

}
