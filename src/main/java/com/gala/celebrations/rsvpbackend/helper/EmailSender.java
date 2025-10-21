package com.gala.celebrations.rsvpbackend.helper;

import com.gala.celebrations.rsvpbackend.dto.EmailDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private final JavaMailSender javaMailSender;
    private final String sender;
    private final String[] ccRecipients; // Field to hold the injected CC list

    @Autowired
    public EmailSender(JavaMailSender javaMailSender,
                       @Value("${spring.mail.username}") String sender,
                       @Value("${app.mail.cc-recipients}") String[] ccRecipients) { // Inject the array
        this.javaMailSender = javaMailSender;
        this.sender = sender;
        this.ccRecipients = ccRecipients;
    }

    public void sendSimpleMail(EmailDetails details) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getBody());
            mailMessage.setSubject(details.getSubject());

            // Use the injected array from your configuration file
            // Add a check to ensure it's not null or empty before setting
            if (this.ccRecipients != null && this.ccRecipients.length > 0) {
                mailMessage.setCc(this.ccRecipients);
            }

            javaMailSender.send(mailMessage);
            logger.info("Email sent successfully to: {}", details.getRecipient());
        } catch (Exception e) {
            logger.error("Error while sending mail to {}:", details.getRecipient(), e);
        }
    }
}