package org.example.gongiklifeclientbenotificationservice.consumer;

import dto.notification.MarkAllNotificationsAsReadRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.service.MarkAllNotificationsAsReadService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkAllNotificationsAsReadConsumer {

    private final MarkAllNotificationsAsReadService markAllNotificationsAsReadService;

    @KafkaListener(topics = KafkaTopics.MARK_ALL_NOTIFICATIONS_AS_READ_TOPIC)
    public void consume(MarkAllNotificationsAsReadRequestDto requestDto) {
        try {
            log.info("Received request to mark all notifications as read for user: {}",
                    requestDto.getUserId());

            markAllNotificationsAsReadService.markAllNotificationsAsRead(requestDto);
        } catch (Exception e) {
            log.error("Error processing mark all notifications as read message: {}",
                    requestDto.getUserId(), e);
            throw e;
        }
    }

}
