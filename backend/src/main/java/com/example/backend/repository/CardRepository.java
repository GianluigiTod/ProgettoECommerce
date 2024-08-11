package com.example.backend.repository;

import com.example.backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    //Scrivi due query, una che fa la ricerca delle carte con uno specifico nome
    //e l'altra in base al codice di un set
    //e magari anche la ricerca per id, ma a quel punto non se è una query
}
