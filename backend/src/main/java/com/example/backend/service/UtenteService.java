package com.example.backend.service;

import com.example.backend.config.KeycloakConfig;
import com.example.backend.config.Utils;
import com.example.backend.exception.UtenteEsistente;
import com.example.backend.exception.UtenteInesistente;
import com.example.backend.model.Utente;
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

    @Transactional(readOnly = false)
    public Utente registra(Utente u) throws UtenteEsistente {
        if(utenteRepository.findUtenteByUsername(u.getUsername()).isPresent())
            throw new UtenteEsistente();
        addKeyCloak(u);
        assignClientRoleToUser(u.getUsername(), u.getRuolo().name());//per assegnargli il ruolo
        u.setUsername(u.getUsername().toLowerCase());
        return utenteRepository.save(u);
    }

    @Transactional(readOnly = false)
    public boolean cancellaUtente(Long id){
        Optional<Utente> utente = utenteRepository.findById(id);
        if(utente.isPresent()){
            Utente u = utente.get();
            if(!u.getUsername().equals(Utils.getUser()))
                throw new IllegalStateException("L'utente che hai specificato non è lo stesso con cui hai fatto il login");
            utenteRepository.delete(u);
            deleteKeycloak(u);
            return true;
        }else{
            return false;
        }
    }

    @Transactional(readOnly=false)
    public Utente modificaInfo(Utente u) throws UtenteInesistente, UtenteEsistente {
        if(!u.getUsername().equals(Utils.getUser()))
            throw new IllegalStateException("L'utente che hai specificato non è lo stesso con cui hai fatto il login");
        Optional<Utente> utente = utenteRepository.findUtenteById(u.getId());//l'id dell'utente che creerò nel frontend dovrà essere uguale all'id di un utente già esistente
        if(utente.isPresent()){
            Utente utente_precedente = utente.get();
            if(u.getUsername() != null){
                utente_precedente.setUsername(u.getUsername());
            }
            if(u.getEmail() != null){
                utente_precedente.setEmail(u.getEmail());
            }
            if(u.getNome() != null){
                utente_precedente.setNome(u.getNome());
            }
            if(u.getPassword() != null && !utente_precedente.getPassword().equals(u.getPassword())){
                utente_precedente.setPassword(u.getPassword());
            }else if (!utente_precedente.getPassword().equals(u.getPassword())){
                throw new IllegalArgumentException("La password è uguale a quella precedente.");
            }
            if(u.getIndirizzo() != null){
                utente_precedente.setIndirizzo(u.getIndirizzo());
            }
            if(u.getCognome() != null){
                utente_precedente.setCognome(u.getCognome());
            }
            if(u.getRuolo() != null){
                utente_precedente.setRuolo(u.getRuolo());
            }
            deleteKeycloak(utente_precedente);
            addKeyCloak(utente_precedente);//verifica se la modifica funziona, ho appena cambiato u con utente_precedente qui
            return utenteRepository.save(utente_precedente);//e qui
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

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(utente.getUsername());
        user.setEmail(utente.getEmail());
        user.setFirstName(utente.getNome());
        user.setLastName(utente.getCognome());
        user.setCredentials(Collections.singletonList(createPasswordCredentials(utente.getPassword())));
        user.setEmailVerified(true);



        //Get realm
        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);
        UsersResource usersResource = realmResource.users();


        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(utente.getPassword());
        user.setCredentials(Collections.singletonList(credentialRepresentation));



        Response response = usersResource.create(user);
        if (response.getStatus() == 201) {
            System.out.println("Utente creato con successo.");
        } else {
            System.err.println("Failed to create user. HTTP error code: " + response.getStatus());
            System.err.println("Error message: " + response.getStatusInfo().getReasonPhrase());
            if (response.hasEntity())
                System.err.println("Error details: " + response.readEntity(String.class));

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

    private void deleteKeycloak(Utente u) {
        Keycloak keycloak = KeycloakConfig.getInstance();
        List<UserRepresentation> users = keycloak.realm("master").users().search(u.getUsername());

        if (users.isEmpty()) {
            System.out.println("Nessun utente trovato con username: " + u.getUsername());
            return; // O gestisci il caso come appropriato
        }

        // Supponiamo che ci sia solo un utente con il username fornito
        // In caso contrario, potresti voler gestire una lista di utenti
        UserRepresentation user = users.get(0); // Prendi il primo utente

        try {
            String userId = user.getId();
            keycloak.realm("master").users().get(userId).remove();
            System.out.println("Utente eliminato con successo: " + u.getUsername());
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


        //getting client
        ClientRepresentation clientRepresentation = keycloak.realm(realm).clients().findAll().stream().filter(client -> client.getClientId().equals("my-app-client")).collect(Collectors.toList()).get(0);
        System.out.println("clientRepresentation: "+ clientRepresentation);
        System.out.println("ruolo: "+role);
        System.out.println("id: "+userId);

        ClientResource clientResource = keycloak.realm(realm).clients().get(clientRepresentation.getId());
        //getting role
        RoleRepresentation roleRepresentation = clientResource.roles().list().stream().filter(element -> element.getName().equals(role)).collect(Collectors.toList()).get(0);
        System.out.println("roleRepresentation: "+ roleRepresentation);
        //assigning to user
        userResource.roles().clientLevel(clientRepresentation.getId()).add(Collections.singletonList(roleRepresentation));


    }


}

