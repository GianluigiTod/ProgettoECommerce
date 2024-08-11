package com.example.backend.service;


import com.example.backend.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    //questi servizi saranno offerti solo a chi è venditore
    //vanno fatti i servizi per la vendita di carte
    //e in generale tutte le modifiche, miraccomando vanno permesse solo al creatore di quel prodotto da vendere

}




