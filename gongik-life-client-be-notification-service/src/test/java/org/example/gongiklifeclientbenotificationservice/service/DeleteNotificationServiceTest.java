package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.DeleteNotificationRequestDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private DeleteNotificationService deleteNotificationService;

    @Test
    @DisplayName("성공: 유효한 요청으로 알림 삭제 처리")
    void deleteNotification_success() {
        // Arrange: 유효한 UUID 문자열을 가진 요청 DTO 생성
        String notificationIdStr = "11111111-1111-1111-1111-111111111111";
        String userIdStr = "22222222-2222-2222-2222-222222222222";
        DeleteNotificationRequestDto requestDto = DeleteNotificationRequestDto.builder()
                .notificationId(notificationIdStr)
                .userId(userIdStr)
                .build();

        // repository에서 해당 알림이 조회되고, deletedAt 값은 null인 상태
        Notification notification = Notification.builder()
                .id(UUID.fromString(notificationIdStr))
                .userId(UUID.fromString(userIdStr))
                .createdAt(new Date())
                .build();
        when(notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(eq(UUID.fromString(notificationIdStr)), eq(UUID.fromString(userIdStr))))
                .thenReturn(Optional.of(notification));

        // Act: 서비스 메서드 호출
        deleteNotificationService.deleteNotification(requestDto);

        // Assert: 알림이 삭제 처리되어 deletedAt이 설정되었으며, repository.save()가 호출됨
        assertNotNull(notification.getDeletedAt(), "deletedAt 필드가 설정되어야 합니다.");
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("성공: 이미 삭제된 알림인 경우, 추가 저장 없이 바로 종료")
    void deleteNotification_alreadyDeleted() {
        // Arrange: 요청 DTO 생성
        String notificationIdStr = "11111111-1111-1111-1111-111111111111";
        String userIdStr = "22222222-2222-2222-2222-222222222222";
        DeleteNotificationRequestDto requestDto = DeleteNotificationRequestDto.builder()
                .notificationId(notificationIdStr)
                .userId(userIdStr)
                .build();

        // repository에서 삭제된 알림(이미 deletedAt 값이 설정된)을 반환하도록 설정
        Date deletedDate = new Date();
        Notification notification = Notification.builder()
                .id(UUID.fromString(notificationIdStr))
                .userId(UUID.fromString(userIdStr))
                .deletedAt(deletedDate)
                .build();
        when(notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(eq(UUID.fromString(notificationIdStr)), eq(UUID.fromString(userIdStr))))
                .thenReturn(Optional.of(notification));

        // Act: 메서드 호출 시 이미 삭제된 경우이므로 아무런 처리 없이 return 되어야 함
        deleteNotificationService.deleteNotification(requestDto);

        // Assert: repository.save()가 호출되지 않아야 함.
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패: 잘못된 UUID 형식으로 인해 BAD_REQUEST 예외 발생")
    void deleteNotification_invalidUuid() {
        // Arrange: 잘못된 UUID가 포함된 요청 DTO 생성 (notificationId가 올바르지 않음)
        DeleteNotificationRequestDto requestDto = DeleteNotificationRequestDto.builder()
                .notificationId("invalid-uuid")
                .userId("22222222-2222-2222-2222-222222222222")
                .build();

        // Act & Assert: parseUuid()에서 IllegalArgumentException이 발생하여 BAD_REQUEST ResponseStatusException 발생
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                deleteNotificationService.deleteNotification(requestDto)
        );

        assertTrue(exception.getReason().contains("Invalid UUID format for notificationId"));

        // repository.save()나 조회는 호출되지 않아야 함.
        verify(notificationRepository, never()).findByIdAndUserIdAndDeletedAtIsNull(any(), any());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패: 알림 조회 결과가 없을 경우 NOT_FOUND 예외 발생")
    void deleteNotification_notificationNotFound() {
        // Arrange: 유효한 UUID 문자열을 가진 요청 DTO 생성
        String notificationIdStr = "11111111-1111-1111-1111-111111111111";
        String userIdStr = "22222222-2222-2222-2222-222222222222";
        DeleteNotificationRequestDto requestDto = DeleteNotificationRequestDto.builder()
                .notificationId(notificationIdStr)
                .userId(userIdStr)
                .build();

        // repository 조회 결과 없도록 설정
        when(notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(eq(UUID.fromString(notificationIdStr)), eq(UUID.fromString(userIdStr))))
                .thenReturn(Optional.empty());

        // Act & Assert: NOT_FOUND 예외 발생 확인
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                deleteNotificationService.deleteNotification(requestDto)
        );


        assertTrue(exception.getReason().contains("You can delete only your own notifications"));
    }
}
