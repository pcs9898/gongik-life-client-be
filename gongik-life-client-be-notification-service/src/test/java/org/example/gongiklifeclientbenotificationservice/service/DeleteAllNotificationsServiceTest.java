package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.DeleteAllNotificationsRequestDto;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteAllNotificationsServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private DeleteAllNotificationsService deleteAllNotificationsService;

    @Test
    @DisplayName("성공: 모든 알림 삭제 처리")
    void deleteAllNotifications_success() {
        // Given: 테스트용 DeleteAllNotificationsRequestDto 생성 (userId를 올바른 UUID 문자열로)
        DeleteAllNotificationsRequestDto requestDto = DeleteAllNotificationsRequestDto.builder()
                .userId("123e4567-e89b-12d3-a456-426614174000")
                .build();

        // When: 서비스 메서드 호출
        deleteAllNotificationsService.deleteAllNotifications(requestDto);

        // Then: notificationRepository.deleteAllNotifications()가 올바른 UUID로 호출되는지 검증
        verify(notificationRepository).deleteAllNotifications(eq(UUID.fromString(requestDto.getUserId())));
    }
}
