package org.example.gongiklifeclientbenotificationservice.consumer;

import dto.notification.CreateNotificationRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateNotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.CREATE_NOTIFICATION_TOPIC)
    @Transactional
    public void consume(CreateNotificationRequestDto requestDto) {
        try {
            log.info("Received CreateNotificationRequestDto: {}", requestDto);

            notificationService.createNotification(requestDto);

            // Todo: Implement notification logic, send notification
        } catch (Exception e) {
            log.error("Error processing create notification message: {}", requestDto, e);
            throw e;
        }
    }

}
