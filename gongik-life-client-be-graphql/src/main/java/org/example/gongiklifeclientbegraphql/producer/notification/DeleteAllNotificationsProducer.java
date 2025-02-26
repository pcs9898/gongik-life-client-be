package org.example.gongiklifeclientbegraphql.producer.notification;

import dto.notification.DeleteAllNotificationsRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAllNotificationsProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendDeleteAllNotificationsRequest(
            DeleteAllNotificationsRequestDto requestDto
    ) {
        kafkaTemplate.send(KafkaTopics.DELETE_ALL_NOTIFICATION_TOPIC, requestDto);
    }

}
