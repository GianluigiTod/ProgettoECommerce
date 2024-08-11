package com.example.backend.service;

import com.example.backend.controller.richieste.RichiestaSet;
import com.example.backend.exception.SetEsistente;
import com.example.backend.exception.SetInesistente;
import com.example.backend.model.Set;
import com.example.backend.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class SetService {

    @Value("${image.upload.dir}")
    private String imageUploadDir;  // Directory per caricare le immagini

    @Value("${image.base.url}")
    private String imageBaseUrl;  // URL base per le immagini

    @Autowired
    private SetRepository setRepository;

    public Set createSetWithImage(Set set, MultipartFile file) throws IOException {
        String fileName=null;
        if (file == null || file.isEmpty()) {
            // Usa un'immagine di default se il file è vuoto
            fileName = "/immagine_mancante.jpg"; // File già esistente nella directory
        } else {
            // Genera un nome unico per il file
            fileName = "/"+UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(imageUploadDir, fileName);
            Files.createDirectories(path.getParent());  // Crea la directory se non esiste
            Files.write(path, file.getBytes());  // Salva il file
        }


        // Imposta il percorso dell'immagine nell'entità Set
        set.setImagePath(fileName);
        return setRepository.save(set);
    }

    public String getImageUrl(Long id) {
        Set set = setRepository.findById(id).orElseThrow(() -> new RuntimeException("Set not found"));
        return imageBaseUrl + set.getImagePath();
    }

    @Transactional(readOnly = true)
    public Set ottieniSet(RichiestaSet richiesta)throws SetInesistente {
        String codiceSet = richiesta.getSetCode();
        Optional<Set> set = setRepository.findSetByCode(codiceSet);
        if(!set.isPresent()){
            throw new SetInesistente("Il set con il codice " + codiceSet + " non esiste");
        }
        Set s = set.get();
        return s;
    }
    /*

    @Transactional(readOnly = false)
    public String creaSet(Set set) throws SetEsistente {
        if(setRepository.findSetByCode(set.getSetCode()).isPresent()){
            throw new SetEsistente("Set " + set.getSetCode() + " esiste già");
        }
        return setRepository.save(set);
    }


     */
    @Transactional(readOnly = false)
    public Set aggiornaSet(Set set) throws SetInesistente {
        Optional<Set> setOptional = setRepository.findSetById(set.getId());
        if(setOptional.isPresent()){
            Set s_prec= setOptional.get();
            s_prec.setSetCode(set.getSetCode());
            s_prec.setSetName(set.getSetName());
            //s_prec.setImage(set.getImage());
            return setRepository.save(s_prec);
        }else{
            throw new SetInesistente("Il Set " + set.getSetCode() + " non esiste");
        }
    }

    @Transactional(readOnly = false)
    public void eliminaSet(Set set) throws SetInesistente{
        if(!setRepository.findSetByCode(set.getSetCode()).isPresent()){
            throw new SetInesistente("Il Set " + set.getSetCode() + " non esiste");
        }
        setRepository.delete(set);
    }


}
