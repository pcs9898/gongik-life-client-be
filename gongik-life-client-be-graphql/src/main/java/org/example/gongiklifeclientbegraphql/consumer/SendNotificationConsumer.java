package org.example.gongiklifeclientbegraphql.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.notification.SendNotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.service.notification.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendNotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "send-notification-topic",
            containerFactory = "kafkaListenerContainerStringFactory")
    public void consume(String serializedRequestString) {
        try {
            log.info("Consuming send-notification-topic: {}", serializedRequestString);

            SendNotificationRequestDto requestDto = objectMapper.readValue(serializedRequestString,
                    SendNotificationRequestDto.class);

            notificationService.sendNotification(requestDto);
        } catch (Exception e) {
            log.error("Error deserializing SendNotificationRequestDto: {}", serializedRequestString, e);
            throw new RuntimeException("Error deserializing SendNotificationRequestDto", e);
        }
    }

}
