package org.example.gongiklifeclientbeuserservice.producer;

import dto.UserToEmail.EmailVerificationRequestDto;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationCodeProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private static final String TOPIC = "email-verification-topic";

  public CompletableFuture<Boolean> sendEmailVerificationRequest(
      EmailVerificationRequestDto request) {

    return kafkaTemplate.send(TOPIC, request)
        .thenApply(result -> true)
        .exceptionally(ex -> {
          // 에러 로깅 추가
          log.info("Error occurred while sending email verification request: " + ex.getMessage());
          return false;
        });
  }

}
