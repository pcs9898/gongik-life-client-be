package org.example.gongiklifeclientbeuserservice.producer;

import dto.mail.EmailVerificationRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationCodeProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendEmailVerificationRequest(EmailVerificationRequestDto request) {

    kafkaTemplate.send(KafkaTopics.EMAIL_VERIFICATION_TOPIC, request);
  }
  
}
