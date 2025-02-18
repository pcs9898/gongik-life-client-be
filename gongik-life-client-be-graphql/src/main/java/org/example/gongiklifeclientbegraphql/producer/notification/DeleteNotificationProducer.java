package org.example.gongiklifeclientbegraphql.producer.notification;

import dto.notification.DeleteNotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteNotificationProducer {

  private static final String TOPIC = "delete-notification-topic";
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendDeleteNotificationRequest(
      DeleteNotificationRequestDto requestDto) {
    
    kafkaTemplate.send(TOPIC, requestDto);
  }

}
