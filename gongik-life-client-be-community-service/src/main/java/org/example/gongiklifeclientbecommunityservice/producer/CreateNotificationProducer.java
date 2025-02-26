package org.example.gongiklifeclientbecommunityservice.producer;

import dto.notification.CreateNotificationRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateNotificationProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCreateNotificationRequest(CreateNotificationRequestDto requestDto) {
        kafkaTemplate.send(KafkaTopics.CREATE_NOTIFICATION_TOPIC, requestDto);
    }

}
