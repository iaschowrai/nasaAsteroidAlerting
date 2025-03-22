package com.asteroidalerting.service;


import com.asteroidalerting.entity.Notification;
import com.asteroidalerting.event.AsteroidCollisionEvent;
import com.asteroidalerting.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final EmailService emailService;

    @KafkaListener(topics = "asteroid-alert", groupId = "notification-service")
    public void alertEvent(AsteroidCollisionEvent notificationEvent){
        log.info("Received order event: {}", notificationEvent);

        // Create entity for notification
        final Notification notification = Notification.builder()
                .asteroidName(notificationEvent.getAsteroidName())
                .closeApproachDate(LocalDate.parse(notificationEvent.getCloseApproachDate()))
                .missDistanceKilometers(new BigDecimal(notificationEvent.getMissDistanceKilometers()))
                .estimatedDiameterAvgMeters(notificationEvent.getEstimateDiameterAvgMeters())
                .emailSent(false)
                .build();

        //Save notification
    final Notification savedNotification = notificationRepository.saveAndFlush(notification);
    log.info("Notification saved: {}",savedNotification);

    }
    @Scheduled(fixedRate = 10000)
    public void sendAlertingEmail(){
        log.info("Triggering scheduled job to send email alerts");
        emailService.sendAsteroidAlertEmail();
    }
}
