package org.example.gongiklifeclientbenotificationservice.consumer;

import dto.notification.MarkNotificationAsReadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkNotificationAsReadConsumer {

  private final NotificationService notificationService;

  @KafkaListener(topics = "mark-notification-as-read-topic")
  public void consume(MarkNotificationAsReadRequestDto requestDto) {
    try {
      log.info("Received notificationId for mark notification as read: {}",
          requestDto.getNotificationId());

      notificationService.markNotificationAsRead(requestDto);
    } catch (Exception e) {
      log.error("Error processing mark notification as read message: {}",
          requestDto.getNotificationId(), e);
      throw e;
    }
  }
}
