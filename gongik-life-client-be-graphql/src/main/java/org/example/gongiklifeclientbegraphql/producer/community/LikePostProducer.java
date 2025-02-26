package org.example.gongiklifeclientbegraphql.producer.community;

import dto.community.LikePostRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikePostProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLikePostRequest(LikePostRequestDto request) {

        kafkaTemplate.send(KafkaTopics.LIKE_POST_TOPIC, request);
    }

}
