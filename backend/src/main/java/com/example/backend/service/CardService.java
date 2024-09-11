package com.example.backend.service;

import com.example.backend.config.Utils;
import com.example.backend.dto.CardDTO;
import com.example.backend.exception.*;
import com.example.backend.model.Card;
import com.example.backend.model.CartItem;
import com.example.backend.model.Set;
import com.example.backend.model.Utente;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.SetRepository;
import com.example.backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private UtenteRepository utenteRepository;


    @Transactional(readOnly = true)
    public Card getCardById(Long id) throws CartaInesistente {
        Optional<Card> card = cardRepository.findCardById(id);
        if(!card.isPresent()){
            throw new CartaInesistente();
        }
        Card c = card.get();
        return c;
    }

    @Transactional(readOnly = true)
    public Page<Card> getAllCards(int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        return cardRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Card> getCardsBySetId(Long setId, int page, int size) throws SetInesistente{
        if(!setRepository.findSetById(setId).isPresent()){
            throw new SetInesistente();
        }
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findBySetId(setId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Card> getCardsBySellerId(Long venditoreId, int page, int size) throws UtenteInesistente{
        if(!utenteRepository.findUtenteById(venditoreId).isPresent()){
            throw new UtenteInesistente();
        }
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findByVenditoreId(venditoreId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Card> searchCardsByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findByNameContaining(name, pageable);
    }

    @Transactional(readOnly = false)
    public Card createCard(CardDTO cardDTO) throws UtenteInesistente, SetInesistente, PriceProblem, QuantityProblem, IOException {
        Optional<Utente> utenteOptional = utenteRepository.findUtenteById(cardDTO.getVenditoreId());
        if(!utenteOptional.isPresent())
            throw new UtenteInesistente();
        Utente venditore = utenteOptional.get();
        String username = venditore.getUsername();
        if(!Utils.getUser().equals(username))
            throw new IllegalStateException();
        Optional<Set> setOptional = setRepository.findSetById(cardDTO.getSetId());
        if(!setOptional.isPresent())
            throw new SetInesistente();
        Set set = setOptional.get();
        Optional<Card> cardOptional = cardRepository.findByData(venditore, set, cardDTO.getPrezzo(), cardDTO.getRarity());
        if(cardOptional.isPresent()){
            Card c1 = cardOptional.get();
            int newQuantity = c1.getQuantity() + cardDTO.getQuantity();
            c1.setQuantity(newQuantity);
            cardRepository.save(c1);
            return c1;
        }
        if(cardDTO.getPrezzo() <= 0.f){
            throw new PriceProblem();
        }
        if(cardDTO.getQuantity() <= 0){
            throw new QuantityProblem();
        }
        Card card = new Card();
        card.setVenditore(venditore);
        card.setSet(set);
        card.setImagePath(imageService.creaImmagine(null));//Per creare una carta con l'immagine di default
        card.setUsernameVenditore(venditore.getUsername());
        card.setSetCode(set.getSetCode());
        card.setPower(cardDTO.getPower());
        card.setText(cardDTO.getText());
        card.setType(cardDTO.getType());
        card.setRarity(cardDTO.getRarity());
        card.setToughness(cardDTO.getToughness());
        card.setManaCost(cardDTO.getManaCost());
        card.setName(cardDTO.getName());
        card.setPrezzo(cardDTO.getPrezzo());
        card.setQuantity(cardDTO.getQuantity());
        return cardRepository.save(card);
    }

    @Transactional(readOnly = false)
    public Card setCardImage(MultipartFile image, Long id) throws CartaInesistente, IOException, ImageNotFound {
        Optional<Card> card = cardRepository.findById(id);
        if(!card.isPresent()){
            throw new CartaInesistente();
        }
        Card c = card.get();
        if(!c.getUsernameVenditore().equals(Utils.getUser()))
            throw new IllegalStateException();

        imageService.eliminaImmagine(c.getImagePath());
        c.setImagePath(imageService.creaImmagine(image));
        return c;
    }

    //CORREGGERE
    @Transactional(readOnly = false)
    public Card aggiornaCarta(Card c) throws CartaInesistente, PriceProblem, QuantityProblem {
        Optional<Card> optionalCard = cardRepository.findById(c.getId());
        if(!optionalCard.isPresent()){
            throw new CartaInesistente();
        }
        Card card = optionalCard.get();

        if(!card.getUsernameVenditore().equals(Utils.getUser()))
            throw new IllegalStateException();

        if (!c.getName().equals(card.getName())) {
            card.setName(c.getName());
        }
        if (c.getPrezzo() <= 0.f) {
            throw new PriceProblem();
        }
        if(c.getPrezzo() != card.getPrezzo()){
            card.setPrezzo(c.getPrezzo());
        }

        if (!c.getManaCost().equals(card.getManaCost())) {
            card.setManaCost(c.getManaCost());
        }

        if (!c.getType().equals(card.getType())) {
            card.setType(c.getType());
        }
        if (c.getRarity() != card.getRarity()) {
            card.setRarity(c.getRarity());
        }
        if (!c.getText().equals(card.getText())) {
            card.setText(c.getText());
        }
        if (c.getPower() != c.getPower()) {
            card.setPower(c.getPower());
        }
        if (c.getToughness() != c.getToughness()) {
            card.setToughness(c.getToughness());
        }
        if(c.getQuantity() <= 0){
            throw new QuantityProblem();
        }

        if(c.getQuantity() != card.getQuantity()){
            card.setQuantity(c.getQuantity());
        }

        return cardRepository.save(card);
    }

    @Transactional(readOnly = false)
    public void deleteCard(Long id) throws CartaInesistente, ImageNotFound {
        Optional<Card> optionalCard = cardRepository.findById(id);
        if(!optionalCard.isPresent()){
            throw new CartaInesistente();
        }
        Card c = optionalCard.get();

        if(!c.getUsernameVenditore().equals(Utils.getUser()))
            throw new IllegalStateException();

        // Recupera tutti i CartItem associati alla carta
        List<CartItem> cartItems = cartItemRepository.findByOriginalCardId(id);

        // Notifica agli utenti che hanno la carta nel carrello
        for (CartItem cartItem : cartItems) {
            Utente utente = cartItem.getUtente();
            notificationService.notifyUserAboutDeletedCard(utente, c);
        }

        cardRepository.delete(c);
        imageService.eliminaImmagine(c.getImagePath());
    }
}





