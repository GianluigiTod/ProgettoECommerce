package com.example.backend.repository;

import com.example.backend.model.Card;
import com.example.backend.model.Set;
import com.example.backend.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findCardById(Long id);

    Page<Card> findBySetId(Long setId, Pageable pageable);

    Page<Card> findByVenditoreId(Long venditoreId, Pageable pageable);

    @Query("SELECT c FROM Card c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Card> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.venditore = :venditore AND c.set = :set AND c.prezzo = :prezzo AND c.rarity = :rarity")
    Optional<Card> findByData(@Param("venditore") Utente venditore, @Param("set") Set set, @Param("prezzo") float prezzo, @Param("rarity") Card.Rarity rarity);

}
