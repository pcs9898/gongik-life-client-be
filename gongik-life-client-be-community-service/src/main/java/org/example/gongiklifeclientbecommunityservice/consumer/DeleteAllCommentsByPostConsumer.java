package org.example.gongiklifeclientbecommunityservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.service.CommentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAllCommentsByPostConsumer {

  private final CommentService commentService;

  @KafkaListener(topics = "delete-all-comments-by-post-topic")
  public void consume(String postId) {
    try {
      log.info("Received postId: {}", postId);
      commentService.deleteAllCommentsByPost(postId);

    } catch (Exception e) {
      log.error("Error processing message: {}", postId, e);
      throw e; // 트랜잭션 롤백을 위해 예외 재발생
    }
  }

}
