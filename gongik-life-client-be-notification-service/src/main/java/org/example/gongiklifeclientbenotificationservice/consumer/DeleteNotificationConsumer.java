package org.example.gongiklifeclientbenotificationservice.consumer;

import dto.notification.DeleteNotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteNotificationConsumer {

  private final NotificationService notificationService;

  @KafkaListener(topics = "delete-notification-topic")
  public void consume(DeleteNotificationRequestDto requestDto) {
    try {
      log.info("Received DeleteNotificationRequestDto: {}", requestDto);

      notificationService.deleteNotification(requestDto);
    } catch (Exception e) {
      log.error("Error processing delete notification message: {}", requestDto, e);
      throw e;
    }
  }

}
