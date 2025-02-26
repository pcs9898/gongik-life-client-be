package org.example.gongiklifeclientbenotificationservice.consumer;

import dto.notification.DeleteAllNotificationsRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.service.DeleteAllNotificationsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAllNotificationsConsumer {


    private final DeleteAllNotificationsService deleteAllNotificationsService;

    @KafkaListener(topics = KafkaTopics.DELETE_ALL_NOTIFICATION_TOPIC)
    public void consume(DeleteAllNotificationsRequestDto requestDto) {
        try {
            log.info("Received DeleteAllNotificationsRequestDto: {}", requestDto);

            deleteAllNotificationsService.deleteAllNotifications(requestDto);
        } catch (Exception e) {
            log.error("Error processing delete all notifications message: {}", requestDto, e);
            throw e;
        }
    }


}
