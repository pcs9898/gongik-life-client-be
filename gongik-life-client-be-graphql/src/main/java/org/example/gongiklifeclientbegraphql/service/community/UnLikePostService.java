package org.example.gongiklifeclientbegraphql.service.community;

import dto.community.UnLikePostRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.community.unLikePost.UnLikePostResponseDto;
import org.example.gongiklifeclientbegraphql.exception.CommunityServiceException;
import org.example.gongiklifeclientbegraphql.producer.community.UnLikePostProducer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnLikePostService {

    private final UnLikePostProducer unLikePostProducer;

    public UnLikePostResponseDto unLikePost(UnLikePostRequestDto requestDto) {

        try {
            unLikePostProducer.sendUnLikePostRequest(requestDto);
            return UnLikePostResponseDto.builder().success(true).build();
        } catch (Exception ex) {
            log.error("Kafka 호출 중 오류 발생: ", ex);
            throw new CommunityServiceException("Kafka 호출 오류", ex);
        }
    }
}
