package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.MarkNotificationAsReadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.entity.Notification;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkNotificationAsReadService {


    private final NotificationRepository notificationRepository;

    public void markNotificationAsRead(MarkNotificationAsReadRequestDto requestDto) {
        // UUID 변환
        UUID notificationId = parseUuid(requestDto.getNotificationId(), "notificationId");
        UUID userId = parseUuid(requestDto.getUserId(), "userId");

        // 알림 조회: 사용자 소유이면서 삭제되지 않은(notification.deletedAt is null) 조건
        Notification notification = fetchNotification(notificationId, userId);

        // 이미 읽음 처리된 알림이면 로그 후 종료
        if (notification.getReadAt() != null) {
            log.info("Notification {} is already marked as read", notificationId);
            return;
        }

        // 읽음 처리 업데이트
        markAsRead(notification);
    }

    /**
     * 문자열로 전달된 UUID를 파싱합니다.
     *
     * @param uuidStr   변환할 UUID 문자열
     * @param fieldName 필드 이름 (에러 메시지용)
     * @return 변환된 UUID 객체
     */
    private UUID parseUuid(String uuidStr, String fieldName) {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid UUID for {}: {}", fieldName, uuidStr, ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + fieldName);
        }
    }

    /**
     * notificationId와 userId를 기준으로, 삭제되지 않은 알림(Notification)을 조회합니다.
     *
     * @param notificationId 알림 ID
     * @param userId         요청 사용자 ID
     * @return 조회된 Notification 엔티티
     * @throws ResponseStatusException 알림이 존재하지 않거나 사용자 소유가 아닌 경우
     */
    private Notification fetchNotification(UUID notificationId, UUID userId) {
        return notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(notificationId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "You can mark only your own notifications as read"
                ));
    }

    /**
     * 조회된 Notification 엔티티에 현재 시간을 readAt 필드에 설정하고 업데이트합니다.
     *
     * @param notification 업데이트할 Notification 엔티티
     */
    private void markAsRead(Notification notification) {
        notification.setReadAt(new Date());
        notificationRepository.save(notification);
        log.info("Notification {} marked as read", notification.getId());
    }
}