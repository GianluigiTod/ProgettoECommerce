package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean arrivato;

    @Column
    private String dataOrdine;

    @Column
    private float prezzoTotale;

    @ElementCollection
    private List<CardSnapshot> cards;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "utente_id", nullable=false)
    private Utente utente;

    @Version
    @JsonIgnore
    private Long version;

    public Ordine() {
        arrivato = false;
    }

}
