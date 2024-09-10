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
import java.util.List;
import java.util.Optional;

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
    public Set createSetWithImage(String setCode, String setName, MultipartFile file) throws SetEsistente, IOException {
        Optional<Set> optionalSet = setRepository.findSetByCode(setCode);
        if(optionalSet.isPresent()) {
            throw new SetEsistente();
        }else{
            Set set = new Set();
            set.setSetCode(setCode);
            set.setSetName(setName);
            set.setImagePath(imageService.creaImmagine(file));
            setRepository.save(set);
            return set;
        }
    }

    public String getImageUrl(Long id) throws SetInesistente {
        Optional<Set> optionalSet = setRepository.findSetById(id);
        if(optionalSet.isPresent()) {
            Set set = optionalSet.get();
            return imageBaseUrl + set.getImagePath();
        }else{
            throw new SetInesistente();
        }
    }

    @Transactional(readOnly = true)
    public Set ottieniSet(Long id)throws SetInesistente {
        Optional<Set> set = setRepository.findSetById(id);
        if(!set.isPresent()){
            throw new SetInesistente();
        }
        Set s = set.get();
        return s;
    }

    @Transactional(readOnly = false)
    public Set aggiornaSet(Set set) throws SetInesistente, SetEsistente {
        Optional<Set> setOptional = setRepository.findSetById(set.getId());
        if(setOptional.isPresent()){
            Set s_prec= setOptional.get();
            if(!s_prec.getSetCode().equals(set.getSetCode())){
                if(setRepository.findSetByCode(set.getSetCode()).isPresent()){
                    throw new SetEsistente();
                }
                s_prec.setSetCode(set.getSetCode());
            }
            if(!s_prec.getSetName().equals(set.getSetName())){
                s_prec.setSetName(set.getSetName());
            }
            return setRepository.save(s_prec);
        }else{
            throw new SetInesistente();
        }
    }

    @Transactional(readOnly = false)
    public void aggiornaImmagineSet(MultipartFile file, Long id) throws SetInesistente, IOException {
        Optional<Set> setOptional = setRepository.findSetById(id);
        if(setOptional.isPresent()){
            Set s_prec= setOptional.get();
            s_prec.setImagePath(imageService.aggiornaImmagine(s_prec.getImagePath(), file));
            setRepository.save(s_prec);
        }else{
            throw new SetInesistente();
        }
    }

    @Transactional(readOnly = false)
    public void eliminaSet(Long id) throws SetInesistente{
        Optional<Set> setOptional = setRepository.findSetById(id);
        if(!setOptional.isPresent()){
            throw new SetInesistente();
        }
        Set set = setOptional.get();
        setRepository.delete(set);
        imageService.eliminaImmagine(set.getImagePath());
    }

    @Transactional(readOnly = true)
    public List<Set> ottieniTuttiSet(){
        return setRepository.findAll();
    }


}
