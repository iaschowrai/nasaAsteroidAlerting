package com.asteroidalerting.service;

import com.asteroidalerting.entity.Notification;
import com.asteroidalerting.repository.NotificationRepository;
import com.asteroidalerting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Value("${email.service.from.email}")
    private String fromEmail;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @Async
    public void sendAsteroidAlertEmail(){

        final String text = createEmailText();
        if(text == null){
            log.info("No asterods to send alerts for at {}", LocalDateTime.now());
            return;
        }

        final List<String> toEmails = userRepository.findAllEmailsAndNotificationEnabled();
        if(toEmails.isEmpty()){
            log.info(("No users to send email to"));
            return;
        }
        toEmails.forEach(toEmail -> sendEmail(toEmail, text));
        log.info("Email sent to : #{} users", toEmails.size());
    }

    private void sendEmail(String toEmail, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setFrom(fromEmail);
        mailMessage.setSubject("Nasa Asteroid Collision Event");
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }


    private String createEmailText(){
        // check if there are any asteroids to send alert for

        List<Notification> notificationList = notificationRepository.findByEmailSent(false);

        if(notificationList.isEmpty()){
            return null;
        }

        StringBuilder emailText = new StringBuilder();
        emailText.append("Asteroid Alert: \n");
        emailText.append("===========================================\n");

        notificationList.forEach(notification -> {
            emailText.append("Asteroid Name: ").append(notification.getAsteroidName()).append("\n");
            emailText.append("Closed Approach Date: ").append(notification.getCloseApproachDate()).append("\n");
            emailText.append("Estimated Diameter Avg Meters: ").append(notification.getEstimatedDiameterAvgMeters()).append("\n");
            emailText.append("Miss Distance Kilometers: ").append(notification.getMissDistanceKilometers()).append("\n");
            emailText.append("===========================================\n");
            notification.setEmailSent(true);
            notificationRepository.save(notification);
        });
        return emailText.toString();
    }
}
