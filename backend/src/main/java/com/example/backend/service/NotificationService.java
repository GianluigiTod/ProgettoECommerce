package com.example.backend.service;

import com.example.backend.model.Card;
import com.example.backend.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    // Notifica via email
    public void notifyUserAboutDeletedCard(Utente utente, Card card) {
        String subject = "Carta rimossa dal tuo carrello";
        String body = String.format("La carta %s che avevi nel carrello è stata rimossa dal venditore.", card.getName());

        sendEmail(utente.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}

