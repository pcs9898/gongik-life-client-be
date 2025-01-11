package org.example.gongiklifeclientbeuserservice.consumer;


import dto.UserToUser.LoginHistoryRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeuserservice.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryConsumer {

  private final UserRepository userRepository;


  @KafkaListener(topics = "login-history-topic")
  public void consume(LoginHistoryRequestDto request) {
    try {
      log.info("Received LoginHistoryRequestDto: {}", request);
      userRepository.updateLastLoginAt(UUID.fromString(request.getId()));

    } catch (Exception e) {
      log.error("Error processing message: {}", request, e);
      throw e; // 트랜잭션 롤백을 위해 예외 재발생
    }
  }


}
