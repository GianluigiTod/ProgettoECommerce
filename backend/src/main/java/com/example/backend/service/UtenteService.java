package com.example.backend.service;

import com.example.backend.config.KeycloakConfig;
import com.example.backend.config.Utils;
import com.example.backend.exception.UtenteEsistente;
import com.example.backend.exception.UtenteInesistente;
import com.example.backend.model.Card;
import com.example.backend.model.CartItem;
import com.example.backend.model.Utente;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.UtenteRepository;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UtenteService  {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional(readOnly = false)
    public Utente registra(Utente u) throws UtenteEsistente {
        if(utenteRepository.findUtenteByUsername(u.getUsername()).isPresent())
            throw new UtenteEsistente();
        addKeyCloak(u);
        assignClientRoleToUser(u.getUsername(), u.getRuolo().name());//per assegnargli il ruolo
        //per non avere problemi con keycloack, salvo gli username sempre minuscoli
        u.setUsername(u.getUsername().toLowerCase());

        Utente ret = utenteRepository.save(u);
        notificationService.notifyUserAboutAccountCreation(ret);
        return ret;
    }

    @Transactional
    public void cancellaUtente(Long id) throws UtenteInesistente {
        Optional<Utente> utente = utenteRepository.findById(id);
        if(utente.isPresent()){
            Utente u = utente.get();
            String username=u.getUsername();
            if(!u.getUsername().equals(Utils.getUser()))
                throw new IllegalStateException("L'utente che hai specificato non è lo stesso con cui hai fatto il login");
            if(!u.getCards().isEmpty()){
                for(Card card: u.getCards()){
                    for(CartItem cartItem : cartItemRepository.findByOriginalCardId(card.getId())){
                        notificationService.notifyUserAboutDeletedCard(cartItem.getUtente(), cartItem.getOriginalCard());
                    }
                }
            }
            String email = u.getEmail();
            String nome = u.getNome();
            utenteRepository.delete(u);
            deleteKeycloak(username);
            notificationService.notifyUserAboutAccountDeletion(email, nome);
        }else{
            throw new UtenteInesistente();
        }
    }

    @Transactional
    public Utente modificaInfo(Utente u) throws UtenteInesistente, UtenteEsistente {
        Optional<Utente> utente = utenteRepository.findUtenteById(u.getId());
        if(utente.isPresent()){
            Utente utente_precedente = utente.get();
            String usernamePrecedente = utente_precedente.getUsername();
            if(!utente_precedente.getUsername().equals(Utils.getUser())) {
                throw new IllegalStateException("L'utente che hai specificato non è lo stesso con cui hai fatto il login");
            }
            boolean changes=false;
            if(!u.getUsername().equals(utente_precedente.getUsername())){
                usernamePrecedente = utente_precedente.getUsername().toLowerCase();
                utente_precedente.setUsername(u.getUsername());
                changes=true;
            }
            if(!u.getEmail().equals(utente_precedente.getEmail())){
                utente_precedente.setEmail(u.getEmail());
                changes=true;
            }
            if(!u.getNome().equals(utente_precedente.getNome())){
                utente_precedente.setNome(u.getNome());
                changes=true;
            }
            if(!u.getPassword().equals(utente_precedente.getPassword())){
                utente_precedente.setPassword(u.getPassword());
                changes=true;
            }
            if(!u.getIndirizzo().equals(utente_precedente.getIndirizzo())){
                utente_precedente.setIndirizzo(u.getIndirizzo());
            }
            if(!u.getCognome().equals(utente_precedente.getCognome())){
                utente_precedente.setCognome(u.getCognome());
                changes=true;
            }
            if(!u.getRuolo().equals(utente_precedente.getRuolo())){
                utente_precedente.setRuolo(u.getRuolo());
                changes=true;
            }
            if(changes){
                deleteKeycloak(usernamePrecedente);
                addKeyCloak(utente_precedente);
                assignClientRoleToUser(utente_precedente.getUsername(), utente_precedente.getRuolo().toString());
            }
            return utenteRepository.save(utente_precedente);
        }else{
            throw new UtenteInesistente();
        }
    }



    @Transactional(readOnly = true)
    public Utente ottieniUtente(String username) throws UtenteInesistente {
        Optional<Utente> utente = utenteRepository.findUtenteByUsername(username);
        if(!utente.isPresent()){
            throw new UtenteInesistente();
        }
        Utente u = utente.get();
        return u;
    }




    private void addKeyCloak(Utente utente) throws UtenteEsistente {
        Keycloak keycloak = KeycloakConfig.getInstance();

        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> existingUsers = usersResource.search(utente.getUsername(), true);
        if (!existingUsers.isEmpty()) {
            throw new UtenteEsistente();
        }

        //Qui definisco l'utente
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(utente.getUsername());
        user.setEmail(utente.getEmail());
        user.setFirstName(utente.getNome());
        user.setLastName(utente.getCognome());
        user.setCredentials(Collections.singletonList(createPasswordCredentials(utente.getPassword())));
        user.setEmailVerified(true);


        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(utente.getPassword());
        user.setCredentials(Collections.singletonList(credentialRepresentation));



        Response response = usersResource.create(user);
        if (response.getStatus() == 201) {
            System.out.println("Utente creato con successo.");
        } else {
            System.err.println("Utente non creato. HTTP error code: " + response.getStatus());
            System.err.println("Messaggio di errore: " + response.getStatusInfo().getReasonPhrase());
            if (response.hasEntity())
                System.err.println("Dettagli dell'errore: " + response.readEntity(String.class));
            response.close();
            throw new IllegalStateException("Creazione dell'utente su keycloack non è andata a buon fine.");
        }

    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private void deleteKeycloak(String username) throws UtenteInesistente{
        Keycloak keycloak = KeycloakConfig.getInstance();
        List<UserRepresentation> users = keycloak.realm("master").users().search(username);

        if (users.isEmpty()) {
            throw new UtenteInesistente();
        }

        UserRepresentation user = users.get(0);

        try {
            String userId = user.getId();
            keycloak.realm("master").users().get(userId).remove();
            System.out.println("Utente eliminato con successo: " + username);
        } catch (Exception e) {
            System.err.println("Errore durante l'eliminazione dell'utente: " + e.getMessage());
        }
    }

    public void assignClientRoleToUser(String username, String role) {

        Keycloak keycloak = KeycloakConfig.getInstance();
        String realm="master";

        List<UserRepresentation> users = keycloak.realm("master").users().search(username);
        UserRepresentation user = users.get(0);
        String userId = user.getId();

        UsersResource usersResource = keycloak.realm(realm).users();
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        UserResource userResource = usersResource.get(userId);


        //per ottenere il cliente
        ClientRepresentation clientRepresentation = keycloak.realm(realm).clients().findAll().stream().filter(client -> client.getClientId().equals("my-app-client")).collect(Collectors.toList()).get(0);
        System.out.println("clientRepresentation: "+ clientRepresentation);
        System.out.println("ruolo: "+role);
        System.out.println("id: "+userId);

        ClientResource clientResource = keycloak.realm(realm).clients().get(clientRepresentation.getId());

        //per ottenere il ruolo nella lista di ruoli del client
        RoleRepresentation roleRepresentation = clientResource.roles().list().stream().filter(element -> element.getName().equals(role)).collect(Collectors.toList()).get(0);
        System.out.println("roleRepresentation: "+ roleRepresentation);

        //per assegnare il ruolo all'utente
        userResource.roles().clientLevel(clientRepresentation.getId()).add(Collections.singletonList(roleRepresentation));
    }


}

