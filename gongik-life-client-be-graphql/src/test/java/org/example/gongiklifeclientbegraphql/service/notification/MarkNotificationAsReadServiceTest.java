package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.MarkNotificationAsReadRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.markNotificationAsRead.MarkNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.MarkNotificationAsReadProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarkNotificationAsReadServiceTest {


    @Mock
    private MarkNotificationAsReadProducer markNotificationAsReadProducer;

    @InjectMocks
    private MarkNotificationAsReadService markNotificationAsReadService;

    @Test
    @DisplayName("성공: 알림 읽음 처리 성공")
    void markNotificationAsRead_success() {
        // Given: 테스트용 Request DTO 생성
        MarkNotificationAsReadRequestDto requestDto = MarkNotificationAsReadRequestDto.builder()
                .notificationId("test-notification-id")
                .userId("test-user-id")
                .build();

        // When: Producer 호출 시 정상 동작(예외 없이 수행)
        // 별도 stub 설정 없이 producer.sendMarkNotificationAsReadRequest(requestDto)를 호출 — 이때 아무런 예외가 발생하지 않는 것으로 가정

        // Call 서비스 메서드
        MarkNotificationAsReadResponseDto responseDto = markNotificationAsReadService.markNotificationAsRead(requestDto);

        // Then: 응답 DTO의 success 값이 true로 설정되었는지 검증
        assertTrue(responseDto.isSuccess());
        // 그리고 producer.sendMarkNotificationAsReadRequest()가 올바르게 호출되었는지 확인
        verify(markNotificationAsReadProducer).sendMarkNotificationAsReadRequest(eq(requestDto));
    }

    @Test
    @DisplayName("실패: 알림 읽음 처리 중 예외 발생 시 예외 전파")
    void markNotificationAsRead_exception() {
        // Given: 테스트용 Request DTO 생성
        MarkNotificationAsReadRequestDto requestDto = MarkNotificationAsReadRequestDto.builder()
                .notificationId("test-notification-id")
                .userId("test-user-id")
                .build();

        // Producer 호출 시 예외 발생하도록 stub 설정
        RuntimeException dummyException = new RuntimeException("Producer error");
        doThrow(dummyException)
                .when(markNotificationAsReadProducer)
                .sendMarkNotificationAsReadRequest(eq(requestDto));

        // When & Then: 서비스 호출 시 예외가 전파되는지 검증
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                markNotificationAsReadService.markNotificationAsRead(requestDto)
        );
        // 예외 메시지에 "Producer error" 등이 포함되어 있음을 확인(또는 단순히 예외 전파 여부만 검증)
        assertEquals("Producer error", exception.getMessage());
    }
}