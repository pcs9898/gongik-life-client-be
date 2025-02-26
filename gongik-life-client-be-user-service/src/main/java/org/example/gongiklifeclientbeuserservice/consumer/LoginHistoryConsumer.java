package org.example.gongiklifeclientbeuserservice.consumer;


import dto.user.UserLoginHistoryRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeuserservice.repository.UserLoginHistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryConsumer {

    private final UserLoginHistoryRepository userLoginHistoryRepository;


    @KafkaListener(topics = KafkaTopics.LOGIN_HISTORY_TOPIC)
    @Transactional
    public void consume(UserLoginHistoryRequestDto request) {
        try {
            log.info("Received LoginHistoryRequestDto: {}", request);
            userLoginHistoryRepository.saveLoginHistory(UUID.fromString(request.getUserId()),
                    request.getIpAddress());

        } catch (Exception e) {
            log.error("Error processing message: {}", request, e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }


}
