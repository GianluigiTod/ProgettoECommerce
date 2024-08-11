package com.example.backend.repository;

import com.example.backend.model.Set;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface SetRepository extends JpaRepository<Set, Long> {
    Optional<Set> findSetById(Long id);

    @Query("SELECT s FROM Set s where s.setCode = :setCode")
    Optional<Set> findSetByCode(@Param("setCode") String setCode);
}
