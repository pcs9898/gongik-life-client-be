package org.example.gongiklifeclientbeuserservice.producer;

import dto.UserToUser.LoginHistoryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private static final String TOPIC = "login-history-topic";

  public void sendLoginHistoryRequest(LoginHistoryRequestDto request) {

    kafkaTemplate.send(TOPIC, request);
  }

}
