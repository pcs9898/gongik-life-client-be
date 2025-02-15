package org.example.gongiklifeclientbecommunityservice.consumer;

import dto.community.LikePostRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.service.PostService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikePostConsumer {

  private final PostService postService;

  @KafkaListener(topics = "like-post-topic")
  public void consume(LikePostRequestDto requestDto) {
    try {
      log.info("Received postId: {}, userId : {}", requestDto.getPostId(),
          requestDto.getUserId());

      postService.likePost(requestDto);

    } catch (Exception e) {
      log.error("Error processing like post: {}", requestDto, e);
      throw e; // 트랜잭션 롤백을 위해 예외 재발생
    }
  }


}
