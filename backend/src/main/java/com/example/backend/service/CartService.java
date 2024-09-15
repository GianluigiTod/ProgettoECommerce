package com.example.backend.service;

import com.example.backend.config.Utils;
import com.example.backend.dto.CartItemDTO;
import com.example.backend.dto.CartItemsResponse;
import com.example.backend.exception.CartaInesistente;
import com.example.backend.exception.QuantityProblem;
import com.example.backend.exception.UtenteInesistente;
import com.example.backend.model.*;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UtenteRepository utenteRepository;

    @Transactional
    public CartItemsResponse getCartItemsByUser(Long id) {
        Optional<Utente> utenteOptional = utenteRepository.findById(id);
        if (utenteOptional.isPresent()) {
            Utente utente = utenteOptional.get();
            String username = utente.getUsername();
            if (!username.equals(Utils.getUser())) {
                throw new IllegalStateException();
            }

            List<CartItem> cartItems = cartItemRepository.findByUtenteUsername(username);
            boolean hasChanges = false;
            StringBuilder notificationMessage = new StringBuilder("Le seguenti modifiche sono state rilevate nel tuo carrello:\n");

            for (CartItem cartItem : cartItems) {
                HashMap<String, Boolean> verifiche = new HashMap<>();
                verifiche.put("changes", false);
                verifiche.put("quantityChanges", false);
                if (checkForCardChanges(cartItem, verifiche)) {
                    hasChanges = true;
                    if(verifiche.get("changes")){
                        notificationMessage.append(String.format("La carta %s è stata modificata.\n", cartItem.getCardSnapshot().getName()));

                        // Aggiorna il cardSnapshot nel cartItem
                        updateCardSnapshot(cartItem, cartItem.getOriginalCard());
                    }

                    if(verifiche.get("quantityChanges")){
                        notificationMessage.append(String.format("La quantità della carta %s non è più sufficiente", cartItem.getCardSnapshot().getName()));

                        //Aggiorna la quantità del cartItem
                        cartItem.setQuantity(cartItem.getOriginalCard().getQuantity());
                    }

                    cartItemRepository.save(cartItem);
                }
            }

            CartItemsResponse response = new CartItemsResponse();
            response.setItems(cartItems);
            response.setHasChanged(hasChanges);
            if (hasChanges) {
                response.setMessage(notificationMessage.toString());
            }
            return response;
        }else{
            throw new IllegalArgumentException();
        }
    }

    @Transactional(readOnly = false)
    public void updateCardSnapshot(CartItem cartItem, Card originalCard) {
        CardSnapshot snapshot = cartItem.getCardSnapshot();
        snapshot.setName(originalCard.getName());
        snapshot.setSetCode(originalCard.getSetCode());
        snapshot.setUsernameVenditore(originalCard.getUsernameVenditore());
        snapshot.setRarity(originalCard.getRarity());
        //L'id non lo aggiorno perché rimane sempre lo stesso
    }

    //L'unico modo per poter cambiare il prezzo del cartItem è cancellare l'articolo nel carrello
    public boolean checkForCardChanges(CartItem cartItem, HashMap<String, Boolean> verifiche) {
        Card originalCard = cartItem.getOriginalCard();

        CardSnapshot cardSnapshot = cartItem.getCardSnapshot();

        //Da notare che qui non confronto il prezzo
        boolean changes = !originalCard.getName().equals(cardSnapshot.getName()) ||
                !originalCard.getSetCode().equals(cardSnapshot.getSetCode()) ||
                !originalCard.getRarity().equals(cardSnapshot.getRarity());

        //Per verificare che la quantità sia ancora sufficiente
        boolean quantityChanges = cartItem.getQuantity() > originalCard.getQuantity();

        verifiche.put("changes", changes);
        verifiche.put("quantityChanges", quantityChanges);

        return changes || quantityChanges;
    }



        @Transactional(readOnly = false)
        public CartItem addCartItem(CartItemDTO dto) throws QuantityProblem, UtenteInesistente, CartaInesistente {
            Optional<Card> cardOptional = cardRepository.findById(dto.getCardId());
            if (cardOptional.isPresent()) {
                Card card = cardOptional.get();

                Optional<Utente> utenteOptional = utenteRepository.findById(dto.getUtenteId());
                if (utenteOptional.isPresent()) {
                    Utente utente = utenteOptional.get();

                    if(!utente.getUsername().equals(Utils.getUser()))
                        throw new IllegalStateException();

                    if (dto.getQuantity() > card.getQuantity() || dto.getQuantity() <= 0) {
                        throw new QuantityProblem();
                    }


                    // Crea un CardSnapshot basato sulla carta corrente
                    CardSnapshot cardSnapshot = new CardSnapshot();
                    cardSnapshot.setName(card.getName());
                    cardSnapshot.setSetCode(card.getSetCode());
                    cardSnapshot.setUsernameVenditore(card.getUsernameVenditore());
                    cardSnapshot.setSnapCardId(card.getId());
                    cardSnapshot.setRarity(card.getRarity());


                    Optional<CartItem> existingCartItem = cartItemRepository.findByUtenteAndCard(utente, card);
                    if (existingCartItem.isPresent()) {
                        CartItem item = existingCartItem.get();
                        if (dto.getQuantity() + item.getQuantity() > card.getQuantity()) {
                            throw new IllegalArgumentException("La quantità supera l'ammontare totale di carte.");
                        }
                        item.setQuantity(item.getQuantity() + dto.getQuantity());
                        return cartItemRepository.save(item);
                    }else{
                        CartItem cartItem = new CartItem();
                        cartItem.setUtente(utente);
                        cartItem.setOriginalCard(card);
                        cartItem.setCardSnapshot(cardSnapshot);
                        cartItem.setQuantity(dto.getQuantity());
                        cartItem.setPrezzo(card.getPrezzo());
                        return cartItemRepository.save(cartItem);
                    }
                }else{
                    throw new UtenteInesistente();
                }
            } else {
                throw new CartaInesistente();
            }
        }

    @Transactional(readOnly = false)
    public CartItem updateCartItem(Long id, int quantity) throws QuantityProblem{
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            String username = cartItem.getUtente().getUsername();
            if(!username.equals(Utils.getUser()))
                throw new IllegalStateException();

            Card card = cartItem.getOriginalCard();
            if (quantity <= card.getQuantity() && quantity > 0) {
                cartItem.setQuantity(quantity);
                return cartItemRepository.save(cartItem);
            } else {
                throw new QuantityProblem();
            }
        }
        return null;
    }

    @Transactional(readOnly = false)
    public boolean deleteCartItem(Long id) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            String username = cartItem.getUtente().getUsername();
            if(!username.equals(Utils.getUser()))
                throw new IllegalStateException();

            cartItemRepository.delete(cartItem);
            return true;
        }
        return false;
    }
}

