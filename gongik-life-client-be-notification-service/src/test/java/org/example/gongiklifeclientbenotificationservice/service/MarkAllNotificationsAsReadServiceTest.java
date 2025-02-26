package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.MarkAllNotificationsAsReadRequestDto;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MarkAllNotificationsAsReadServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private MarkAllNotificationsAsReadService markAllNotificationsAsReadService;

    @Test
    @DisplayName("성공: 모든 알림 읽음 처리 요청 - 올바른 userId 전달")
    void testMarkAllNotificationsAsRead_success() {
        // Arrange: 유효한 UUID 문자열을 갖는 요청 DTO 생성
        String validUserId = "123e4567-e89b-12d3-a456-426614174000";
        MarkAllNotificationsAsReadRequestDto requestDto = MarkAllNotificationsAsReadRequestDto.builder()
                .userId(validUserId)
                .build();

        // Act
        markAllNotificationsAsReadService.markAllNotificationsAsRead(requestDto);

        // Assert: NotificationRepository의 markAllNotificationsAsRead()가 UUID 변환된 userId로 호출되었는지 검증
        verify(notificationRepository).markAllNotificationsAsRead(eq(UUID.fromString(validUserId)));
    }

    @Test
    @DisplayName("실패: 잘못된 UUID 형식이 전달될 경우 예외 발생")
    void testMarkAllNotificationsAsRead_invalidUuid() {
        // Arrange: 잘못된 UUID 문자열을 가진 요청 DTO 생성
        String invalidUserId = "invalid-uuid";
        MarkAllNotificationsAsReadRequestDto requestDto = MarkAllNotificationsAsReadRequestDto.builder()
                .userId(invalidUserId)
                .build();

        // Act & Assert: UUID 변환 실패로 IllegalArgumentException 발생을 검증
        assertThrows(IllegalArgumentException.class, () ->
                markAllNotificationsAsReadService.markAllNotificationsAsRead(requestDto)
        );
    }
}
