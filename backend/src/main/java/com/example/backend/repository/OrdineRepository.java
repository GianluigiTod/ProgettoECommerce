package com.example.backend.repository;

import com.example.backend.model.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long> {
    Optional<Ordine> findOrdineById(Long id);

    Set<Ordine> findOrdineByUtenteId(Long id);

}
