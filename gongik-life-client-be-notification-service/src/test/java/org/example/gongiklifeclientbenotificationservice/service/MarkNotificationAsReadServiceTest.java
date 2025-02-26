package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.MarkNotificationAsReadRequestDto;
import org.example.gongiklifeclientbenotificationservice.entity.Notification;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MarkNotificationAsReadServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private MarkNotificationAsReadService markNotificationAsReadService;

    @Test
    @DisplayName("성공: 아직 읽지 않은 알림을 읽음 처리")
    void markNotificationAsRead_success() {
        // Arrange
        String notifIdStr = "11111111-1111-1111-1111-111111111111";
        String userIdStr = "22222222-2222-2222-2222-222222222222";
        MarkNotificationAsReadRequestDto requestDto = MarkNotificationAsReadRequestDto.builder()
                .notificationId(notifIdStr)
                .userId(userIdStr)
                .build();

        // 알림은 존재하며 아직 읽음 처리되지 않은 상태 (readAt == null)
        Notification notification = Notification.builder()
                .id(UUID.fromString(notifIdStr))
                .userId(UUID.fromString(userIdStr))
                .readAt(null)
                .build();
        when(notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(
                eq(UUID.fromString(notifIdStr)),
                eq(UUID.fromString(userIdStr))
        )).thenReturn(Optional.of(notification));

        // Act
        markNotificationAsReadService.markNotificationAsRead(requestDto);

        // Assert
        // save() 호출 시 readAt 이 현재 시각으로 업데이트되었음을 검증 (간단히 save() 메서드가 호출되었는지 확인)
        verify(notificationRepository).save(notification);
        assertNotNull(notification.getReadAt(), "readAt 필드가 업데이트되어야 합니다.");
    }

    @Test
    @DisplayName("성공: 이미 읽음 처리된 알림인 경우 업데이트 없이 종료")
    void markNotificationAsRead_alreadyRead() {
        // Arrange
        String notifIdStr = "11111111-1111-1111-1111-111111111111";
        String userIdStr = "22222222-2222-2222-2222-222222222222";
        MarkNotificationAsReadRequestDto requestDto = MarkNotificationAsReadRequestDto.builder()
                .notificationId(notifIdStr)
                .userId(userIdStr)
                .build();

        // 이미 읽은 상태의 알림 (readAt != null)
        Notification notification = Notification.builder()
                .id(UUID.fromString(notifIdStr))
                .userId(UUID.fromString(userIdStr))
                .readAt(new Date())
                .build();
        when(notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(
                eq(UUID.fromString(notifIdStr)),
                eq(UUID.fromString(userIdStr))
        )).thenReturn(Optional.of(notification));

        // Act
        markNotificationAsReadService.markNotificationAsRead(requestDto);

        // Assert: 이미 읽음 처리된 경우, save() 호출이 발생하지 않아야 함
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패: 잘못된 UUID 형식 (notificationId)로 인해 BAD_REQUEST 발생")
    void markNotificationAsRead_invalidNotificationId() {
        // Arrange: 잘못된 notificationId
        String invalidNotifId = "invalid-uuid";
        String validUserId = "22222222-2222-2222-2222-222222222222";
        MarkNotificationAsReadRequestDto requestDto = MarkNotificationAsReadRequestDto.builder()
                .notificationId(invalidNotifId)
                .userId(validUserId)
                .build();

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                markNotificationAsReadService.markNotificationAsRead(requestDto)
        );

        assertTrue(ex.getReason().contains("Invalid notificationId"));

        // repository 조회나 save() 호출은 없어야 함
        verify(notificationRepository, never()).findByIdAndUserIdAndDeletedAtIsNull(any(), any());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패: 알림 미조회 시 NOT_FOUND 발생")
    void markNotificationAsRead_notificationNotFound() {
        // Arrange
        String notifIdStr = "11111111-1111-1111-1111-111111111111";
        String userIdStr = "22222222-2222-2222-2222-222222222222";
        MarkNotificationAsReadRequestDto requestDto = MarkNotificationAsReadRequestDto.builder()
                .notificationId(notifIdStr)
                .userId(userIdStr)
                .build();

        // repository에서 알림을 찾지 못하면 Optional.empty() 반환
        when(notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(
                eq(UUID.fromString(notifIdStr)),
                eq(UUID.fromString(userIdStr))
        )).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                markNotificationAsReadService.markNotificationAsRead(requestDto)
        );

        assertTrue(ex.getReason().contains("You can mark only your own notifications as read"));

        verify(notificationRepository).findByIdAndUserIdAndDeletedAtIsNull(
                eq(UUID.fromString(notifIdStr)),
                eq(UUID.fromString(userIdStr))
        );
    }
}
