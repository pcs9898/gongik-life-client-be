package org.example.gongiklifeclientbenotificationservice.producer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.notification.SendNotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendNotificationProducer {

  private static final String TOPIC = "send-notification-topic";
  private final KafkaTemplate<String, String> kafkaStringTemplate;
  private final ObjectMapper objectMapper;


  public void sendNotificationRequest(SendNotificationRequestDto request) {

    objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS); // null 값도 포함

    try {
      String serializedRequestString = objectMapper.writeValueAsString(request);
      log.info("Sending SendNotificationRequestDto: {}", serializedRequestString);

      kafkaStringTemplate.send(TOPIC, serializedRequestString);
    } catch (Exception e) {
      log.error("Error serializing SendNotificationRequestDto: {}", request, e);

      throw new RuntimeException("Error serializing SendNotificationRequestDto", e);
    }


  }

}
