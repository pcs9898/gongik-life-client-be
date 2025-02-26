package org.example.gongiklifeclientbecommunityservice.producer;

import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAllCommentsByPostProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendDeleteAllCommentsByPostRequest(String postId) {
        kafkaTemplate.send(KafkaTopics.DELETE_ALL_COMMENTS_BY_POST_TOPIC, postId);

    }
}
