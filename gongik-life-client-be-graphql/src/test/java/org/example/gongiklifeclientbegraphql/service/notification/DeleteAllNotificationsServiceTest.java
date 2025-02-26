package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.DeleteAllNotificationsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.deleteAllNotifications.DeleteAllNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.DeleteAllNotificationsProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteAllNotificationsServiceTest {


    @Mock
    private DeleteAllNotificationsProducer deleteAllNotificationsProducer;

    @InjectMocks
    private DeleteAllNotificationsService deleteAllNotificationsService;

    @Test
    @DisplayName("성공: 모든 알림 삭제 성공")
    void deleteAllNotifications_success() {
        // Given: 테스트용 DeleteAllNotificationsRequestDto 객체 생성
        DeleteAllNotificationsRequestDto requestDto = DeleteAllNotificationsRequestDto.builder()
                .userId("test-user-id")
                // 필요한 추가 필드가 있다면 설정합니다.
                .build();

        // When: 서비스 메서드 호출 (producer 호출 시 예외 없이 정상 처리)
        DeleteAllNotificationsResponseDto responseDto = deleteAllNotificationsService.deleteAllNotifications(requestDto);

        // Then: 응답의 success 값이 true인지 검증
        assertNotNull(responseDto);
        assertTrue(responseDto.isSuccess());

        // producer의 sendDeleteAllNotificationsRequest()가 올바른 인자로 호출되었는지 검증
        verify(deleteAllNotificationsProducer).sendDeleteAllNotificationsRequest(eq(requestDto));
    }

    @Test
    @DisplayName("실패: 알림 삭제 중 예외 발생 시 예외 전파")
    void deleteAllNotifications_exception() {
        // Given: 테스트용 DeleteAllNotificationsRequestDto 객체 생성
        DeleteAllNotificationsRequestDto requestDto = DeleteAllNotificationsRequestDto.builder()
                .userId("test-user-id")
                .build();

        // producer 호출 시 예외 발생하도록 설정
        RuntimeException dummyException = new RuntimeException("Producer error");
        doThrow(dummyException)
                .when(deleteAllNotificationsProducer)
                .sendDeleteAllNotificationsRequest(eq(requestDto));

        // When & Then: 서비스 호출 시 예외가 전파되는지 검증한다.
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                deleteAllNotificationsService.deleteAllNotifications(requestDto)
        );
        assertEquals("Producer error", exception.getMessage());
    }
}