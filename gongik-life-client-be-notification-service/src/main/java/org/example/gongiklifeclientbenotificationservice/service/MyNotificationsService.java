package org.example.gongiklifeclientbenotificationservice.service;

import com.gongik.notificationService.domain.service.NotificationServiceOuterClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.entity.Notification;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyNotificationsService {


    private final NotificationRepository notificationRepository;

    public NotificationServiceOuterClass.MyNotificationsResponse myNotifications(
            NotificationServiceOuterClass.MyNotificationsRequest request) {

        // 1. 사용자, 커서, 페이지 사이즈에 따른 알림 목록 조회
        List<Notification> notifications = fetchNotifications(request);

        // 2. 조회된 엔티티 목록을 gRPC 응답 객체 목록으로 변환
        List<NotificationServiceOuterClass.NotificationForList> protoNotifications = convertToProtoNotifications(notifications);

        // 3. 페이징 정보를 생성
        NotificationServiceOuterClass.PageInfo pageInfo = buildPageInfo(notifications, request.getPageSize());

        // 4. 응답 객체 생성 후 반환
        return NotificationServiceOuterClass.MyNotificationsResponse.newBuilder()
                .addAllListNotification(protoNotifications)
                .setPageInfo(pageInfo)
                .build();
    }

    /**
     * Repository에서 알림 목록을 조회합니다.
     */
    private List<Notification> fetchNotifications(NotificationServiceOuterClass.MyNotificationsRequest request) {
        return notificationRepository.findMyNotificationsWithCursor(
                request.getUserId(),
                request.hasCursor() ? request.getCursor() : null,
                request.getPageSize()
        );
    }

    /**
     * 알림 엔티티 목록을 gRPC 응답용 객체 목록으로 변환합니다.
     */
    private List<NotificationServiceOuterClass.NotificationForList> convertToProtoNotifications(List<Notification> notifications) {
        return notifications.stream()
                .map(this::convertToProtoNotification)
                .toList();
    }

    /**
     * 단일 Notification 엔티티를 NotificationForList 프로토 객체로 변환합니다.
     */
    private NotificationServiceOuterClass.NotificationForList convertToProtoNotification(Notification notification) {
        NotificationServiceOuterClass.NotificationForList.Builder builder =
                NotificationServiceOuterClass.NotificationForList.newBuilder()
                        .setId(notification.getId().toString())
                        .setNotificationTypeId(notification.getNotificationTypeId())
                        .setTitle(notification.getTitle())
                        .setContent(notification.getContent())
                        .setCreatedAt(notification.getCreatedAt().toString());

        if (notification.getPostId() != null) {
            builder.setPostId(notification.getPostId().toString());
        }
        if (notification.getTargetCommentId() != null) {
            builder.setTargetCommentId(notification.getTargetCommentId().toString());
        }
        if (notification.getNoticeId() != null) {
            builder.setNoticeId(notification.getNoticeId().toString());
        }
        if (notification.getTargetedNotificationTypeId() != null) {
            builder.setTargetedNotificationTypeId(notification.getTargetedNotificationTypeId());
        }
        if (notification.getTargetedNotificationId() != null) {
            builder.setTargetedNotificationId(notification.getTargetedNotificationId().toString());
        }
        if (notification.getReportId() != null) {
            builder.setReportId(notification.getReportId().toString());
        }
        if (notification.getReadAt() != null) {
            builder.setReadAt(notification.getReadAt().toString());
        }
        return builder.build();
    }

    /**
     * 페이징 정보를 생성합니다.
     */
    private NotificationServiceOuterClass.PageInfo buildPageInfo(List<Notification> notifications, int pageSize) {
        NotificationServiceOuterClass.PageInfo.Builder pageInfoBuilder =
                NotificationServiceOuterClass.PageInfo.newBuilder()
                        .setHasNextPage(notifications.size() == pageSize);

        if (!notifications.isEmpty()) {
            pageInfoBuilder.setEndCursor(notifications.get(notifications.size() - 1).getId().toString());
        }
        return pageInfoBuilder.build();
    }
}