package com.example.backend.service;

import com.example.backend.config.Utils;
import com.example.backend.exception.ImageNotFound;
import com.example.backend.exception.QuantityProblem;
import com.example.backend.model.*;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.OrdineRepository;
import com.example.backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;

@Service
public class OrdineService {

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<Ordine> getListaOrdini(Long id){
        Optional<Utente> utenteOptional = utenteRepository.findUtenteById(id);
        if(utenteOptional.isPresent()){
            Utente utente = utenteOptional.get();
            String username = utente.getUsername();
            if (!username.equals(Utils.getUser())) {
                throw new IllegalStateException();
            }

            return ordineRepository.findOrdineByUtenteIdOrderByDataOrdineDesc(id);
        }else{
            throw new IllegalArgumentException();
        }

    }

    @Transactional
    public boolean setArrivato(Long id){
        Optional<Ordine> ordineOptional = ordineRepository.findOrdineById(id);
        if(ordineOptional.isPresent()){
            Ordine ordine = ordineOptional.get();
            String username = ordine.getUtente().getUsername();
            if (!username.equals(Utils.getUser())) {
                throw new IllegalStateException();
            }
            ordine.setArrivato(true);
            ordineRepository.save(ordine);
            return true;
        }else{
            return false;
        }
    }

    @Transactional
    public boolean deleteOrdine(Long id){
        Optional<Ordine> ordineOptional = ordineRepository.findOrdineById(id);
        if(ordineOptional.isPresent()){
            Ordine ordine = ordineOptional.get();
            String username = ordine.getUtente().getUsername();
            if (!username.equals(Utils.getUser())) {
                throw new IllegalStateException();
            }
            ordineRepository.delete(ordine);
            return true;
        }else{
            return false;
        }
    }

    @Transactional
    public Ordine checkout(List<Long> cartItemsId) throws QuantityProblem, ImageNotFound {
        List<CartItem> cartItems = new ArrayList<>();
        float prezzoTotale = 0.0f;
        List<CardSnapshot> listaSnapshot = new ArrayList<>();
        for (Long id : cartItemsId) {
            Optional<CartItem> optionalCartItem = cartItemRepository.findCartItemById(id);
            if (optionalCartItem.isPresent()) {
                CartItem cartItem = optionalCartItem.get();
                cartItems.add(cartItem);
            } else {
                throw new IllegalArgumentException("Il CartItem "+id+" non esiste.");
            }
        }
        //Per fare in modo di prendere il primo elemento della lista di cartItem e poter ottenere l'oggetto utente e il suo username
        // dato che saranno uguali per ciascun cartItem
        Optional<CartItem> optionalCartItem = cartItems.stream().findFirst();
        CartItem cartItem = optionalCartItem.orElseThrow(() -> new IllegalArgumentException("Nessun CartItem specificato."));
        Utente utente = cartItem.getUtente();
        String username = utente.getUsername();
        if (!username.equals(Utils.getUser())) {
            throw new IllegalStateException();
        }
        for(CartItem c : cartItems){
            if(!c.getUtente().getUsername().equals(username)){
                throw new IllegalArgumentException("I cartItem che hai specificato non appartengono tutti allo stesso utente.");
            }
            Card card = c.getOriginalCard();
            listaSnapshot.add(c.getCardSnapshot());

            //Calcolo del prezzo totale
            if(c.getQuantity() <= card.getQuantity()){
                prezzoTotale += c.getPrezzo() * c.getQuantity();

                int newQuantity = card.getQuantity() - c.getQuantity();

                cartItemRepository.delete(c);

                notificationService.notifySellerAboutCardPurchase(card, utente);


                if(newQuantity != 0){
                    card.setQuantity(newQuantity);
                    cardRepository.save(card);
                }else{
                    List<CartItem> carrello = cartItemRepository.findByOriginalCardId(card.getId());

                    for (CartItem item : carrello) {
                        Utente u = item.getUtente();
                        notificationService.notifyUserAboutDeletedCard(u, card);
                    }
                    cardRepository.delete(card);
                    imageService.eliminaImmagine(card.getImagePath());
                }
            }else{
                throw new QuantityProblem();
            }
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dataOrdine = now.format(formatter);

        Ordine ordine = new Ordine();
        ordine.setDataOrdine(dataOrdine);
        ordine.setPrezzoTotale(prezzoTotale);
        ordine.setCards(listaSnapshot);
        ordine.setUtente(utente);

        notificationService.notifyBuyerAboutPurchase(utente, listaSnapshot, prezzoTotale);

        return ordineRepository.save(ordine);
    }
}
