package org.example.gongiklifeclientbegraphql.producer.community;

import dto.community.UnLikePostRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnLikePostProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUnLikePostRequest(UnLikePostRequestDto request) {

        kafkaTemplate.send(KafkaTopics.UNLIKE_POST_TOPIC, request);
    }

}
