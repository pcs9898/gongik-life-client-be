package org.example.gongiklifeclientbegraphql.producer.user;

import dto.user.UserLoginHistoryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoginHistoryProducer {

  private static final String TOPIC = "login-history-topic";
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendUserLoginHistoryRequest(UserLoginHistoryRequestDto request) {

    kafkaTemplate.send(TOPIC, request);
  }

}
