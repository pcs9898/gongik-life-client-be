package org.example.gongiklifeclientbenotificationservice.consumer;

import dto.notification.DeleteAllNotificationsRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAllNotificationsConsumer {

  private final NotificationService notificationService;

  @KafkaListener(topics = "delete-all-notifications-topic")
  public void consume(DeleteAllNotificationsRequestDto requestDto) {
    try {
      log.info("Received DeleteAllNotificationsRequestDto: {}", requestDto);

      notificationService.deleteAllNotifications(requestDto);
    } catch (Exception e) {
      log.error("Error processing delete all notifications message: {}", requestDto, e);
      throw e;
    }
  }


}
