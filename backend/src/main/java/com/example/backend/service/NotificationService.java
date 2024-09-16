package com.example.backend.service;

import com.example.backend.model.Card;
import com.example.backend.model.CardSnapshot;
import com.example.backend.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    // Notifica via email la cancellazione di una carta per coloro che l'avevano nel carrello
    public void notifyUserAboutDeletedCard(Utente utente, Card card) {
        String subject = "Carta rimossa dal tuo carrello";
        String body = String.format("La carta %s che avevi nel carrello è stata rimossa dal venditore.", card.getName());

        sendEmail(utente.getEmail(), subject, body);
    }

    // Notifica via email la creazione dell'account
    public void notifyUserAboutAccountCreation(Utente utente) {
        String subject = "Benvenuto in Magic Card Emporium!";
        String body = String.format("Ciao %s,\n\nIl tuo account è stato creato con successo.\n\nBenvenuto nella nostra piattaforma!\n\nIl tuo username è: %s\n\nGrazie per esserti registrato.",
                utente.getNome(), utente.getUsername());

        sendEmail(utente.getEmail(), subject, body);
    }

    // Notifica via email la cancellazione dell'account
    public void notifyUserAboutAccountDeletion(String email, String nome) {
        String subject = "Il tuo account è stato cancellato";
        String body = String.format("Ciao %s,\n\nIl tuo account è stato cancellato con successo dal nostro sistema.\n\nCi dispiace vederti andare via. Se hai domande, non esitare a contattarci.",
                nome);

        sendEmail(email, subject, body);
    }

    // Notifica via email il venditore che una carta è stata acquistata
    public void notifySellerAboutCardPurchase(Card card, Utente utente) {
        Utente venditore = card.getVenditore();
        if (venditore != null && venditore.getEmail() != null) {
            String subject = "Una delle tue carte è stata acquistata";
            String body = String.format("La tua carta '%s' è stata acquistata dall'utente %s, la cui email è: %s.", card.getName(), utente.getUsername(), utente.getEmail());

            sendEmail(venditore.getEmail(), subject, body);
        }
    }

    // Notifica via email l'utente acquirente con la ricevuta dell'acquisto
    public void notifyBuyerAboutPurchase(Utente acquirente, List<CardSnapshot> cardSnapshots, float prezzoTotale) {
        String subject = "Ricevuta dell'acquisto";

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Hai acquistato le seguenti carte:\n\n");

        for (CardSnapshot snapshot : cardSnapshots) {
            bodyBuilder.append(snapshot.getName()).append("\n");
        }

        bodyBuilder.append("\nPrezzo totale: ").append(String.format("%.2f €", prezzoTotale));
        bodyBuilder.append("\n\nGrazie per il tuo acquisto!");

        String body = bodyBuilder.toString();

        sendEmail(acquirente.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}

