package com.example.backend.model;

import lombok.Data;
import org.springframework.core.io.FileSystemResource;

@Data
public class InfoImmagine {
    private FileSystemResource file;
    private String contentType;

    public InfoImmagine(FileSystemResource file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }
}
