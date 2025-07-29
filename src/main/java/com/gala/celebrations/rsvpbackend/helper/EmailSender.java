package com.gala.celebrations.rsvpbackend.helper;


import com.gala.celebrations.rsvpbackend.dto.EmailDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private final JavaMailSender javaMailSender;
    private final String sender;

    @Autowired
    public EmailSender(JavaMailSender javaMailSender, @Value("${spring.mail.username}") String sender) {
        this.javaMailSender = javaMailSender;
        this.sender = sender;
    }

    public void sendSimpleMail(EmailDetails details) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            String[] ccEmails = {"msvram@yahoo.com", "msvram@gmail.com"};
            mailMessage.setCc(ccEmails);
            mailMessage.setText(details.getBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
            logger.info("Email sent successfully to: {}", details.getRecipient());
        } catch (Exception e) {
            // Log the full exception to get details on why it failed
            logger.error("Error while sending mail to {}:", details.getRecipient(), e);
        }
    }
}
