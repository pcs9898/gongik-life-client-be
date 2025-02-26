package org.example.gongiklifeclientbemailservice.consumer;

import dto.mail.EmailVerificationRequestDto;
import jakarta.mail.MessagingException;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.example.gongiklifeclientbemailservice.service.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationCodeConsumer {

  private final MailService mailService;

  @KafkaListener(topics = KafkaTopics.EMAIL_VERIFICATION_TOPIC)
  public void consume(EmailVerificationRequestDto request) throws MessagingException {
    mailService.sendEmail(request);
  }
}
