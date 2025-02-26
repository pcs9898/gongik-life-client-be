package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.SendNotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.notificationRealTime.NotificationRealTimeResponseDto;
import org.example.gongiklifeclientbegraphql.publisher.NotificationPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    
    private final NotificationPublisher notificationPublisher;

    public void sendNotification(SendNotificationRequestDto requestDto) {
        log.info("Sending notification: {}", requestDto.getTitle());

        NotificationRealTimeResponseDto response = buildNotificationResponse(requestDto);
        notificationPublisher.publish(response);
    }

    /**
     * SendNotificationRequestDto의 값을 기반으로 NotificationRealTimeResponseDto를 생성합니다.
     *
     * @param requestDto 알림 전송 요청 DTO
     * @return 완성된 NotificationRealTimeResponseDto
     */
    private NotificationRealTimeResponseDto buildNotificationResponse(SendNotificationRequestDto requestDto) {
        NotificationRealTimeResponseDto.NotificationRealTimeResponseDtoBuilder builder =
                NotificationRealTimeResponseDto.builder()
                        .id(requestDto.getId())
                        .userId(requestDto.getUserId())
                        .notificationTypeId(requestDto.getNotificationTypeId())
                        .title(requestDto.getTitle())
                        .content(requestDto.getContent())
                        .createdAt(requestDto.getCreatedAt());

        if (requestDto.getPostId() != null) {
            builder.postId(requestDto.getPostId());
        }
        if (requestDto.getTargetCommentId() != null) {
            builder.targetCommentId(requestDto.getTargetCommentId());
        }
        if (requestDto.getNoticeId() != null) {
            builder.noticeId(requestDto.getNoticeId());
        }
        if (requestDto.getTargetedNotificationTypeId() != null) {
            builder.targetedNotificationTypeId(requestDto.getTargetedNotificationTypeId());
        }
        if (requestDto.getTargetedNotificationId() != null) {
            builder.targetedNotificationId(requestDto.getTargetedNotificationId());
        }
        if (requestDto.getReportId() != null) {
            builder.reportId(requestDto.getReportId());
        }

        return builder.build();
    }
}