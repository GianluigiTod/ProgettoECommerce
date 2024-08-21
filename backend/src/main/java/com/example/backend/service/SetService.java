package com.example.backend.service;

import com.example.backend.controller.richieste.RichiestaSet;
import com.example.backend.exception.SetEsistente;
import com.example.backend.exception.SetInesistente;
import com.example.backend.model.Set;
import com.example.backend.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

    @Autowired
    private ImageService imageService;

    @Transactional(readOnly = false)
    public ResponseEntity<?> createSetWithImage(String setCode, String setName, MultipartFile file) {
        try {
            Set set = new Set();
            set.setSetCode(setCode);
            set.setSetName(setName);
            set.setImagePath(imageService.creaImmagine(file));

            return ResponseEntity.status(HttpStatus.CREATED).body(setRepository.save(set));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    public String getImageUrl(Long id) {
        Set set = setRepository.findById(id).orElseThrow(() -> new RuntimeException("Set not found"));
        return imageBaseUrl + set.getImagePath();
    }

    @Transactional(readOnly = true)
    public Set ottieniSet(Long id)throws SetInesistente {
        Optional<Set> set = setRepository.findSetById(id);
        if(!set.isPresent()){
            throw new SetInesistente("Il set di id " + id + " non esiste");
        }
        Set s = set.get();
        return s;
    }

    @Transactional(readOnly = false)
    public Set aggiornaSet(Set set) throws SetInesistente {
        Optional<Set> setOptional = setRepository.findSetById(set.getId());
        if(setOptional.isPresent()){
            Set s_prec= setOptional.get();
            s_prec.setSetCode(set.getSetCode());
            s_prec.setSetName(set.getSetName());
            return setRepository.save(s_prec);
        }else{
            throw new SetInesistente("Il Set " + set.getSetCode() + " non esiste");
        }
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> aggiornaImmagineSet(MultipartFile file, Long id) throws SetInesistente, IOException {
        Optional<Set> setOptional = setRepository.findSetById(id);
        if(setOptional.isPresent()){
            Set s_prec= setOptional.get();
            s_prec.setImagePath(imageService.aggiornaImmagine(s_prec.getImagePath(), file));
            return ResponseEntity.ok(setRepository.save(s_prec));
        }else{
            throw new SetInesistente("Il Set con id: " +id+ " non esiste");
        }
    }

    @Transactional(readOnly = false)
    public ResponseEntity eliminaSet(Long id) throws SetInesistente{
        Optional<Set> setOptional = setRepository.findSetById(id);
        if(!setOptional.isPresent()){
            throw new SetInesistente("Il Set " + id + " non esiste");
        }
        Set set = setOptional.get();
        setRepository.delete(set);
        imageService.eliminaImmagine(set.getImagePath());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public List<Set> ottieniTuttiSet(){
        return setRepository.findAll();
    }


}
