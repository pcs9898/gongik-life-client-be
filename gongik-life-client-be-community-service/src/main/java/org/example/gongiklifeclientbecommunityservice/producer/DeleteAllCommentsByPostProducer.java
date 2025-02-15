package org.example.gongiklifeclientbecommunityservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAllCommentsByPostProducer {

  private static final String TOPIC = "delete-all-comments-by-post-topic";
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendDeleteAllCommentsByPostRequest(String postId) {
    kafkaTemplate.send(TOPIC, postId);

  }
}
