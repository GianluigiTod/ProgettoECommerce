package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String setCode;

    @Column
    private String setName;

    @Column
    private String imagePath;

    @Version
    @JsonIgnore
    private Long version;


    @OneToMany(mappedBy = "set", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Card> cards;


}
