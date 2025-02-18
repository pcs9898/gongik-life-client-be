package org.example.gongiklifeclientbegraphql.producer.notification;

import dto.notification.MarkAllNotificationsAsReadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkAllNotificationsAsReadProducer {

  private static final String TOPIC = "mark-all-notifications-as-read-topic";
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendMarkAllNotificationsAsReadRequest(
      MarkAllNotificationsAsReadRequestDto requestDto) {

    kafkaTemplate.send(TOPIC, requestDto);
  }

}
