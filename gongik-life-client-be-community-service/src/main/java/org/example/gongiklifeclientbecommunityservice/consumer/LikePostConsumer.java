package org.example.gongiklifeclientbecommunityservice.consumer;

import dto.community.LikePostRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.service.post.LikePostService;
import org.example.gongiklifeclientbecommunityservice.service.post.PostService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikePostConsumer {

    private final PostService postService;
    private final LikePostService likePostService;

    @KafkaListener(topics = KafkaTopics.LIKE_POST_TOPIC)
    public void consume(LikePostRequestDto requestDto) {
        try {
            log.info("Received postId: {}, userId : {}", requestDto.getPostId(),
                    requestDto.getUserId());

            likePostService.likePost(requestDto);

        } catch (Exception e) {
            log.error("Error processing like post: {}", requestDto, e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }


}
