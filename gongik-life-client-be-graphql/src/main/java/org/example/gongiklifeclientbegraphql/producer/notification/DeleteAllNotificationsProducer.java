package org.example.gongiklifeclientbegraphql.producer.notification;

import dto.notification.DeleteAllNotificationsRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAllNotificationsProducer {

  private static final String TOPIC = "delete-all-notifications-topic";
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendDeleteAllNotificationsRequest(
      DeleteAllNotificationsRequestDto requestDto
  ) {
    kafkaTemplate.send(TOPIC, requestDto);
  }

}
