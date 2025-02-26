package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.MarkAllNotificationsAsReadRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.markAllNotificationsAsRead.MarkAllNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.MarkAllNotificationsAsReadProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkAllNotificationsAsReadServiceTest {


    @Mock
    private MarkAllNotificationsAsReadProducer markAllNotificationsAsReadProducer;

    @InjectMocks
    private MarkAllNotificationsAsReadService markAllNotificationsAsReadService;

    @Test
    @DisplayName("성공: 모든 알림 읽음 처리 성공")
    void markAllNotificationsAsRead_success() {
        // Given: 테스트용 MarkAllNotificationsAsReadRequestDto 생성
        MarkAllNotificationsAsReadRequestDto requestDto = MarkAllNotificationsAsReadRequestDto.builder()
                .userId("test-user-id")
                // 추가 필드가 있다면 여기에 설정
                .build();

        // When: producer 호출 시 예외 없이 정상 동작한다고 가정하고 서비스 호출
        MarkAllNotificationAsReadResponseDto responseDto = markAllNotificationsAsReadService.markAllNotificationsAsRead(requestDto);

        // Then: 응답의 success 값이 true임을 검증하고, producer의 메서드가 올바른 인자로 호출되었는지 확인
        assertNotNull(responseDto);
        assertTrue(responseDto.isSuccess());
        verify(markAllNotificationsAsReadProducer).sendMarkAllNotificationsAsReadRequest(eq(requestDto));
    }

    @Test
    @DisplayName("실패: 알림 읽음 처리 중 예외 발생 시 예외 전파")
    void markAllNotificationsAsRead_exception() {
        // Given: 테스트용 MarkAllNotificationsAsReadRequestDto 생성
        MarkAllNotificationsAsReadRequestDto requestDto = MarkAllNotificationsAsReadRequestDto.builder()
                .userId("test-user-id")
                .build();

        // Producer 호출 시 예외 발생하도록 모킹
        RuntimeException dummyException = new RuntimeException("Producer error");
        doThrow(dummyException).when(markAllNotificationsAsReadProducer).sendMarkAllNotificationsAsReadRequest(eq(requestDto));

        // When & Then: 서비스 호출 시 예외가 전파되는지 검증
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                markAllNotificationsAsReadService.markAllNotificationsAsRead(requestDto)
        );
        assertEquals("Producer error", exception.getMessage());
    }
}