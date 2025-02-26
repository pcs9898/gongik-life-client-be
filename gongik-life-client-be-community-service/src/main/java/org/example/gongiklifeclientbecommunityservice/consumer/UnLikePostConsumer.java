package org.example.gongiklifeclientbecommunityservice.consumer;

import dto.community.UnLikePostRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.service.post.UnLikePostService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnLikePostConsumer {

    private final UnLikePostService unLikePostService;

    @KafkaListener(topics = KafkaTopics.UNLIKE_POST_TOPIC)
    public void consume(UnLikePostRequestDto requestDto) {
        try {
            log.info("Received postId: {}, userId : {}", requestDto.getPostId(),
                    requestDto.getUserId());

            unLikePostService.unLikePost(requestDto);

        } catch (Exception e) {
            log.error("Error processing unLike post: {}", requestDto, e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }


}
