package org.example.gongiklifeclientbegraphql.service.community;

import dto.community.LikePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.likePost.LikePostResponseDto;
import org.example.gongiklifeclientbegraphql.exception.CommunityServiceException;
import org.example.gongiklifeclientbegraphql.producer.community.LikePostProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LikePostServiceTest {

    @Mock
    private LikePostProducer likePostProducer;

    @InjectMocks
    private LikePostService likePostService;

    @Test
    @DisplayName("likePost 성공 시 성공 응답 반환")
    void likePost_Success() {
        // Given: 테스트용 요청 DTO 생성
        LikePostRequestDto requestDto = createTestRequestDto();

        // When: LikePostService의 likePost 메서드 호출
        LikePostResponseDto response = likePostService.likePost(requestDto);

        // Then: 반환된 DTO가 null이 아니고 success 값이 true인지 검증하며, producer의 메서드 호출 여부를 확인
        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(likePostProducer).sendLikePostRequest(requestDto);
    }

    @Test
    @DisplayName("likePost 호출 중 producer에서 예외 발생 시 CommunityServiceException 발생")
    void likePost_WhenProducerThrowsException() {
        // Given: 테스트용 요청 DTO 생성 및 producer 메서드 호출 시 Exception 발생하도록 설정
        LikePostRequestDto requestDto = createTestRequestDto();
        Exception producerException = new RuntimeException("Producer error");
        doThrow(producerException).when(likePostProducer).sendLikePostRequest(requestDto);

        // When & Then: CommunityServiceException이 발생하고, 메시지에 "Kafka 호출 오류"가 포함되며 원인이 producer의 예외와 동일한지 검증
        CommunityServiceException exception = assertThrows(CommunityServiceException.class,
                () -> likePostService.likePost(requestDto));
        assertTrue(exception.getMessage().contains("Kafka 호출 오류"));
        assertEquals(producerException, exception.getCause());
    }

    // 테스트용 LikePostRequestDto 객체 생성 메서드 (필요한 필드를 채워서 생성)
    private LikePostRequestDto createTestRequestDto() {
        return LikePostRequestDto.builder()
                .postId("test-post-id")
                .userId("test-user-id")
                .build();
    }
}
