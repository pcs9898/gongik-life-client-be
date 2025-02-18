package org.example.gongiklifeclientbegraphql.producer.notification;

import dto.notification.MarkNotificationAsReadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkNotificationAsReadProducer {

  private static final String TOPIC = "mark-notification-as-read-topic";
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendMarkNotificationAsReadRequest(MarkNotificationAsReadRequestDto requestDto) {

    kafkaTemplate.send(TOPIC, requestDto);
  }

}
