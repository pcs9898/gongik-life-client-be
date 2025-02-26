package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.SendNotificationRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.notificationRealTime.NotificationRealTimeResponseDto;
import org.example.gongiklifeclientbegraphql.publisher.NotificationPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private NotificationService notificationService;

    // 헬퍼 메서드: 테스트용 SendNotificationRequestDto 생성
    private SendNotificationRequestDto createTestRequestDto() {
        return SendNotificationRequestDto.builder()
                .id("test-notification-id")
                .userId("test-user-id")
                .notificationTypeId(1)
                .title("Test Title")
                .content("Test Content")
                .createdAt("2024-01-01T00:00:00Z")
                .postId("test-post-id")
                .targetCommentId("test-comment-id")
                .noticeId("test-notice-id")
                .targetedNotificationTypeId(2)
                .targetedNotificationId("test-targeted-notification-id")
                .reportId("test-report-id")
                .build();
    }

    @Test
    public void testSendNotification_success() {
        // Given: 테스트용 DTO 생성
        SendNotificationRequestDto requestDto = createTestRequestDto();

        // When: service 메서드 호출
        notificationService.sendNotification(requestDto);

        // Then: notificationPublisher.publish() 가 호출되었는지 검증하고, 전달된 NotificationRealTimeResponseDto의 값들이 요청 DTO와 일치하는지 확인
        ArgumentCaptor<NotificationRealTimeResponseDto> captor = ArgumentCaptor.forClass(NotificationRealTimeResponseDto.class);
        verify(notificationPublisher, times(1)).publish(captor.capture());
        NotificationRealTimeResponseDto capturedResponse = captor.getValue();

        assertNotNull(capturedResponse);
        assertEquals(requestDto.getId(), capturedResponse.getId());
        assertEquals(requestDto.getUserId(), capturedResponse.getUserId());
        assertEquals(requestDto.getNotificationTypeId(), capturedResponse.getNotificationTypeId());
        assertEquals(requestDto.getTitle(), capturedResponse.getTitle());
        assertEquals(requestDto.getContent(), capturedResponse.getContent());
        // createdAt은 toString() 비교로 확인 (날짜 객체의 경우)
        assertEquals(requestDto.getCreatedAt(), capturedResponse.getCreatedAt());
        assertEquals(requestDto.getPostId(), capturedResponse.getPostId());
        assertEquals(requestDto.getTargetCommentId(), capturedResponse.getTargetCommentId());
        assertEquals(requestDto.getNoticeId(), capturedResponse.getNoticeId());
        assertEquals(requestDto.getTargetedNotificationTypeId(), capturedResponse.getTargetedNotificationTypeId());
        assertEquals(requestDto.getTargetedNotificationId(), capturedResponse.getTargetedNotificationId());
        assertEquals(requestDto.getReportId(), capturedResponse.getReportId());
    }
}
