package com.example.backend.repository;

import com.example.backend.model.Card;
import com.example.backend.model.Set;
import com.example.backend.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findCardById(Long id);

    // Query per trovare le carte per setCode
    Page<Card> findBySetId(Long setId, Pageable pageable);

    // Query per trovare le carte per username del venditore
    Page<Card> findByVenditoreId(Long venditoreId, Pageable pageable);

    // Query per cercare le carte per nome o parte del nome
    Page<Card> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.venditore = :venditore AND c.set = :set AND c.prezzo = :prezzo AND c.rarity = :rarity")
    Optional<Card> findByData(@Param("venditore") Utente venditore, @Param("set") Set set, @Param("prezzo") float prezzo, @Param("rarity") Card.Rarity rarity);

}
