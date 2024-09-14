package com.example.backend.dto;

import com.example.backend.model.Card;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class CardPageResponse {
    private List<Card> cards;
    private int numPagine;
    private long numTotaleCarte;

    public CardPageResponse(Page<Card> pagina) {
        numPagine = pagina.getTotalPages();
        numTotaleCarte = pagina.getTotalElements();
        cards = pagina.getContent();
    }
}
