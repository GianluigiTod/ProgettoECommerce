package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String setCode;

    @Column
    private String setName;

    @Lob
    @Column
    private String imagePath;


    // Relazione con l'entità Card
    @OneToMany(mappedBy = "set", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Card> cards;


}
