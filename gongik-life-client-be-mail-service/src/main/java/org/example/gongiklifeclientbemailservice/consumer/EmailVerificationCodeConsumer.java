package org.example.gongiklifeclientbemailservice.consumer;

import dto.UserToEmail.EmailVerificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationCodeConsumer {

  @KafkaListener(topics = "email-verification-topic")
  public void consume(EmailVerificationRequestDto request) {
    // 일부러 예외를 발생시키기 위해 조건 추가
    if (request == null) {
      throw new IllegalArgumentException("Request cannot be null");
    }
    throw new RuntimeException("Forced exception for testing");
//    System.out.println("Received email verification request: " + request);
    // 처리 로직
  }


}
