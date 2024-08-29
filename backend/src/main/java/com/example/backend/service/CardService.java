package com.example.backend.service;

import com.example.backend.config.Utils;
import com.example.backend.exception.CartaInesistente;
import com.example.backend.model.Card;
import com.example.backend.model.CartItem;
import com.example.backend.model.Utente;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.CartItemRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.Set;

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

    @Transactional(readOnly = true)
    public Card getCardById(Long id) throws CartaInesistente {
        Optional<Card> card = cardRepository.findCardById(id);
        if(!card.isPresent()){
            throw new CartaInesistente("La carta con id: "+id+" non esiste");
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
    public Page<Card> getCardsBySetCode(String setCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findBySetCode(setCode, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Card> getCardsBySeller(String usernameVenditore, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findByUsernameVenditore(usernameVenditore, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Card> searchCardsByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findByNameContaining(name, pageable);
    }

    /*
    @Transactional(readOnly = true)
    public Page<Card> getAllCardsOrderedByPrice(String direction, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (direction.equalsIgnoreCase("asc")) {
            return cardRepository.findAllByOrderByPrezzoAsc(pageable);
        } else {
            return cardRepository.findAllByOrderByPrezzoDesc(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<Card> getAllCardsOrderedByRarity(String direction, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (direction.equalsIgnoreCase("asc")) {
            return cardRepository.findAllByOrderByRarityAsc(pageable);
        } else {
            return cardRepository.findAllByOrderByRarityDesc(pageable);
        }
    }

     */

    @Transactional(readOnly = false)
    public Card createCard(Card card) {

        //decidere se voglio che le carte abbiano delle colonne uniche, dato che hanno la quantià, ed eventualmente controllare che sia verficata
        if(!card.getUsernameVenditore().equals(Utils.getUser()))
            throw new IllegalStateException("L'utente che hai specificato non è lo stesso con cui hai fatto il login");

        try{
            MultipartFile file = null;
            card.setImagePath(imageService.creaImmagine(file));
            return cardRepository.save(card);
        }catch(OptimisticLockingFailureException e){
            throw new RuntimeException("Conflitto rilevato durante la creazione della carta");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> setCardImage(MultipartFile image, Long id) throws CartaInesistente, IOException {
        try{
            Optional<Card> card = cardRepository.findById(id);
            if(!card.isPresent()){
                throw new CartaInesistente("La carta non esiste");
            }else{
                try{
                    Card c = card.get();

                    if(!c.getUsernameVenditore().equals(Utils.getUser()))
                        throw new IllegalStateException("L'utente che hai specificato non è lo stesso con cui hai fatto il login");

                    if(c.getImagePath() != null){
                        imageService.eliminaImmagine(c.getImagePath());
                    }
                    c.setImagePath(imageService.creaImmagine(image));
                    return ResponseEntity.status(HttpStatus.CREATED).body(c);
                }catch(IOException e){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
                }
            }
        }catch(OptimisticLockingFailureException e){
            throw new RemoteException("Conflitto rilevato");
        }
    }

    //CORREGGERE
    @Transactional(readOnly = false)
    public Card aggiornaCarta(Card c) throws CartaInesistente {
        try{
            Optional<Card> optionalCard = cardRepository.findById(c.getId());
            if(!optionalCard.isPresent()){
                throw new CartaInesistente("La carta con id: "+c.getId()+" non esiste");
            }
            Card card = optionalCard.get();

            if(!card.getUsernameVenditore().equals(Utils.getUser()))
                throw new IllegalStateException("L'utente che hai specificato non è lo stesso con cui hai fatto il login");

            if (c.getName() != null) {
                card.setName(c.getName());
            }
            if (c.getPrezzo() != 0) {  // Usare un valore di default diverso per float o controllare con un altro metodo
                card.setPrezzo(c.getPrezzo());
            }
            if (c.getUsernameVenditore() != null) {
                card.setUsernameVenditore(c.getUsernameVenditore());
            }
            if (c.getManaCost() != null) {
                card.setManaCost(c.getManaCost());
            }
            if (c.getType() != null) {
                card.setType(c.getType());
            }
            if (c.getRarity() != null) {
                card.setRarity(c.getRarity());
            }
            if (c.getText() != null) {
                card.setText(c.getText());
            }
            if (c.getPower() != null) {
                card.setPower(c.getPower());
            }
            if (c.getToughness() != null) {
                card.setToughness(c.getToughness());
            }
            if (c.getSetCode() != null) {
                card.setSetCode(c.getSetCode());
            }
            if(c.getQuantita() != 0){
                card.setQuantita(c.getQuantita());
            }


            return cardRepository.save(card);
        }catch(OptimisticLockingFailureException e){
            throw new RuntimeException("Conflitto rilevato durante la modifica della carta");
        }
    }

    @Transactional(readOnly = false)
    public ResponseEntity deleteCard(Long id) throws CartaInesistente {
        try{
            Optional<Card> optionalCard = cardRepository.findById(id);
            if(!optionalCard.isPresent()){
                throw new CartaInesistente("La carta non esiste");
            }
            Card c = optionalCard.get();

            if(!c.getUsernameVenditore().equals(Utils.getUser()))
                throw new IllegalStateException("L'utente che hai specificato non è lo stesso con cui hai fatto il login");

            // Recupera tutti i CartItem associati alla carta
            Set<CartItem> cartItems = cartItemRepository.findByOriginalCardId(id);

            // Notifica agli utenti che hanno la carta nel carrello
            for (CartItem cartItem : cartItems) {
                Utente utente = cartItem.getUtente();
                notificationService.notifyUserAboutDeletedCard(utente, c);
            }

            cardRepository.delete(c);
            imageService.eliminaImmagine(c.getImagePath());
            return ResponseEntity.ok(HttpStatus.OK);
        }catch(OptimisticLockingFailureException e){
            throw new RuntimeException("Conflitto rilevato durante la cancellazione della carta");
        }

    }
}





