package org.example.gongiklifeclientbegraphql.service.community;

import dto.community.UnLikePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.unLikePost.UnLikePostResponseDto;
import org.example.gongiklifeclientbegraphql.exception.CommunityServiceException;
import org.example.gongiklifeclientbegraphql.producer.community.UnLikePostProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnLikePostServiceTest {

    @Mock
    private UnLikePostProducer unLikePostProducer;

    @InjectMocks
    private UnLikePostService unLikePostService;

    @Test
    @DisplayName("unLikePost 정상 호출 시 성공 응답 반환")
    void unLikePost_Success() {
        // Given: 테스트용 UnLikePostRequestDto 생성
        UnLikePostRequestDto requestDto = createTestRequestDto();

        // When: unLikePost 메서드 호출
        UnLikePostResponseDto response = unLikePostService.unLikePost(requestDto);

        // Then: 반환된 응답 DTO가 null이 아니며 success가 true임
        assertNotNull(response);
        assertTrue(response.getSuccess());
        // producer의 sendUnLikePostRequest 메서드가 한 번 호출되었는지 검증
        verify(unLikePostProducer, times(1)).sendUnLikePostRequest(requestDto);
    }

    @Test
    @DisplayName("Kafka 호출 중 producer 예외 발생 시 CommunityServiceException 발생")
    void unLikePost_WhenProducerThrowsException() {
        // Given: 테스트용 UnLikePostRequestDto 생성
        UnLikePostRequestDto requestDto = createTestRequestDto();
        Exception producerException = new RuntimeException("Producer error");
        doThrow(producerException).when(unLikePostProducer).sendUnLikePostRequest(requestDto);

        // When & Then: 메시지에 "Kafka 호출 오류"가 포함된 CommunityServiceException이 발생하는지 검증
        CommunityServiceException exception = assertThrows(CommunityServiceException.class,
                () -> unLikePostService.unLikePost(requestDto));
        assertTrue(exception.getMessage().contains("Kafka 호출 오류"));
        assertEquals(producerException, exception.getCause());
    }

    // 테스트용 UnLikePostRequestDto 생성 메서드
    private UnLikePostRequestDto createTestRequestDto() {
        return UnLikePostRequestDto.builder()
                .postId("test-post-id")
                .userId("test-user-id")
                .build();
    }
}
