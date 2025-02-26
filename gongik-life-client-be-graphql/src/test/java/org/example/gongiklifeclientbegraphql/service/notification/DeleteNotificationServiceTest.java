package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.DeleteNotificationRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.deleteNotification.DeleteNotificationResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.DeleteNotificationProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteNotificationServiceTest {

    @Mock
    private DeleteNotificationProducer deleteNotificationProducer;

    @InjectMocks
    private DeleteNotificationService deleteNotificationService;

    @Test
    @DisplayName("성공: 알림 삭제 성공")
    void deleteNotification_success() {
        // Given: 테스트용 DeleteNotificationRequestDto 객체 생성
        DeleteNotificationRequestDto requestDto = DeleteNotificationRequestDto.builder()
                .notificationId("test-notification-id")
                .userId("test-user-id")
                .build();

        // When: 서비스 호출 (producer에서 예외 없이 정상 실행됨)
        DeleteNotificationResponseDto responseDto = deleteNotificationService.deleteNotification(requestDto);

        // Then: 응답 객체의 success가 true인지 검증하고, producer의 메서드 호출을 확인함
        assertNotNull(responseDto);
        assertTrue(responseDto.isSuccess());
        verify(deleteNotificationProducer).sendDeleteNotificationRequest(eq(requestDto));
    }

    @Test
    @DisplayName("실패: 알림 삭제 중 예외 발생 시 예외 전파")
    void deleteNotification_exception() {
        // Given: 테스트용 DeleteNotificationRequestDto 객체 생성
        DeleteNotificationRequestDto requestDto = DeleteNotificationRequestDto.builder()
                .notificationId("test-notification-id")
                .userId("test-user-id")
                .build();

        // Producer 호출 시 예외 발생하도록 모킹함
        RuntimeException dummyException = new RuntimeException("Producer error");
        doThrow(dummyException).when(deleteNotificationProducer).sendDeleteNotificationRequest(eq(requestDto));

        // When & Then: 서비스 호출 시 예외가 전파되는지 검증
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                deleteNotificationService.deleteNotification(requestDto)
        );
        assertTrue(exception.getMessage().contains("Producer error"));
    }
}
