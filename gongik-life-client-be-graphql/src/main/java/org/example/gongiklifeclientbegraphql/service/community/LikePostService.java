package org.example.gongiklifeclientbegraphql.service.community;

import dto.community.LikePostRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.community.likePost.LikePostResponseDto;
import org.example.gongiklifeclientbegraphql.exception.CommunityServiceException;
import org.example.gongiklifeclientbegraphql.producer.community.LikePostProducer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikePostService {

    private final LikePostProducer likePostProducer;

    public LikePostResponseDto likePost(LikePostRequestDto requestDto) {

        try {
            likePostProducer.sendLikePostRequest(requestDto);
            return LikePostResponseDto.builder().success(true).build();
        } catch (Exception ex) {
            log.error("Kafka 호출 중 오류 발생: ", ex);
            throw new CommunityServiceException("Kafka 호출 오류", ex);
        }
    }
}
