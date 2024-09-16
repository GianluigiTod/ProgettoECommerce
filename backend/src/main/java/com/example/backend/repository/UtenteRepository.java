package com.example.backend.repository;

import com.example.backend.model.Utente;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente,Long> {
    Optional<Utente> findUtenteById(Long id);

    @Query("SELECT u FROM Utente u where u.username = :username")
    Optional<Utente> findUtenteByUsername(@Param("username") String username);
}
