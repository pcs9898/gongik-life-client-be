package org.example.gongiklifeclientbeauthservice.producer;

import dto.user.UserLoginHistoryRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoginHistoryProducer {

    private static final String TOPIC = KafkaTopics.LOGIN_HISTORY_TOPIC;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserLoginHistoryRequest(UserLoginHistoryRequestDto request) {

        kafkaTemplate.send(TOPIC, request);
    }

}
