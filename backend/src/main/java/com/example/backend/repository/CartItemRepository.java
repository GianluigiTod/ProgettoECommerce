package com.example.backend.repository;


import com.example.backend.model.Card;
import com.example.backend.model.CartItem;
import com.example.backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findCartItemById(Long id);

    List<CartItem> findByUtenteUsername(String username);

    // Restituisce un Optional con il CartItem esistente per lo stesso utente e carta
    @Query("SELECT ci FROM CartItem ci WHERE ci.utente = :utente AND ci.originalCard = :card")
    Optional<CartItem> findByUtenteAndCard(@Param("utente") Utente utente, @Param("card") Card card);

    List<CartItem> findByOriginalCardId(Long cardId);
}
