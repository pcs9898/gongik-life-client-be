package org.example.gongiklifeclientbegraphql.producer.notification;

import dto.notification.MarkNotificationAsReadRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkNotificationAsReadProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMarkNotificationAsReadRequest(MarkNotificationAsReadRequestDto requestDto) {

        kafkaTemplate.send(KafkaTopics.MARK_NOTIFICATION_AS_READ_TOPIC, requestDto);
    }

}
