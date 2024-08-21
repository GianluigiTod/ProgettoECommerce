package com.example.backend.repository;

import com.example.backend.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findCardById(Long id);

    // Query per trovare le carte per setCode
    Page<Card> findBySetCode(String setCode, Pageable pageable);

    // Query per trovare le carte per username del venditore
    Page<Card> findByUsernameVenditore(String usernameVenditore, Pageable pageable);

    // Query per cercare le carte per nome o parte del nome
    Page<Card> findByNameContaining(String name, Pageable pageable);

    /*

    // Query per ordinare le carte per prezzo in modo crescente o decrescente
    Page<Card> findAllByOrderByPrezzoAsc(Pageable pageable);
    Page<Card> findAllByOrderByPrezzoDesc(Pageable pageable);

    // Query per ordinare le carte per rarità
    Page<Card> findAllByOrderByRarityAsc(Pageable pageable);
    Page<Card> findAllByOrderByRarityDesc(Pageable pageable);

     */

}
