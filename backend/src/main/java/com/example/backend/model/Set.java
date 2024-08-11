package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String setCode;

    @Column
    private String setName;

    @Lob
    @Column
    private String imagePath;
}
