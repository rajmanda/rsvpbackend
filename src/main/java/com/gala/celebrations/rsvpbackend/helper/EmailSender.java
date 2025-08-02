package com.gala.celebrations.rsvpbackend.helper;

import com.gala.celebrations.rsvpbackend.dto.EmailDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String sender;

    public Mono<Void> sendSimpleMail(EmailDetails details) {
        return Mono.fromRunnable(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(sender);
                message.setTo(details.getRecipient());
                message.setSubject(details.getSubject());
                message.setText(details.getBody());
                
                // Add CC recipients
                String[] ccEmails = {"msvram@yahoo.com", "msvram@gmail.com"};
                message.setCc(ccEmails);
                
                mailSender.send(message);
                log.info("Email sent successfully to: {}", details.getRecipient());
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", details.getRecipient(), e.getMessage(), e);
                throw new RuntimeException("Failed to send email", e);
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }
}
