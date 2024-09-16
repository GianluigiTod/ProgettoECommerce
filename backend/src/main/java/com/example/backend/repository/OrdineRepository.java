package com.example.backend.repository;

import com.example.backend.model.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long> {
    Optional<Ordine> findOrdineById(Long id);

    List<Ordine> findOrdineByUtenteIdOrderByDataOrdineDesc(Long id);

}
