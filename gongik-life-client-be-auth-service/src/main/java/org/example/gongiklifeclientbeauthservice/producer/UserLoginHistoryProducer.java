package org.example.gongiklifeclientbeauthservice.producer;

import dto.UserToUser.UserLoginHistoryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoginHistoryProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private static final String TOPIC = "login-history-topic";

  public void sendUserLoginHistoryRequest(UserLoginHistoryRequestDto request) {

    kafkaTemplate.send(TOPIC, request);
  }

}
