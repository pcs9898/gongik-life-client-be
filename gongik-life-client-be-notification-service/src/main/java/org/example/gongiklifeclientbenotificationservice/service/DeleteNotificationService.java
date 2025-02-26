package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.DeleteNotificationRequestDto;
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
public class DeleteNotificationService {


    private final NotificationRepository notificationRepository;

    public void deleteNotification(DeleteNotificationRequestDto requestDto) {
        // 1. 문자열 UUID를 안전하게 변환
        UUID notificationId = parseUuid(requestDto.getNotificationId(), "notificationId");
        UUID userId = parseUuid(requestDto.getUserId(), "userId");

        // 2. 사용자 소유의 삭제되지 않은 알림(Notification)을 조회
        Notification notification = fetchNotification(notificationId, userId);

        // 3. 이미 삭제된 경우 로그 호출 후 종료
        if (notification.getDeletedAt() != null) {
            log.info("Notification {} is already deleted", notificationId);
            return;
        }

        // 4. 알림 삭제(삭제 일시 할당) 처리
        markAsDeleted(notification);
    }

    /**
     * 입력 문자열을 UUID 객체로 변환합니다.
     *
     * @param uuidStr   변환할 UUID 문자열
     * @param fieldName 필드 이름 (에러 메시지를 위한)
     * @return 변환된 UUID 객체
     * @throws ResponseStatusException UUID 형식이 잘못된 경우 BAD_REQUEST 에러 발생
     */
    private UUID parseUuid(String uuidStr, String fieldName) {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Invalid UUID format for " + fieldName + ": " + uuidStr;
            log.error(errorMessage, ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    /**
     * 주어진 notificationId와 userId를 기준으로, 삭제되지 않은 알림을 조회합니다.
     *
     * @param notificationId 알림 ID
     * @param userId         사용자 ID
     * @return 조회된 Notification 엔티티
     * @throws ResponseStatusException 조회된 알림이 없을 경우 NOT_FOUND 에러 발생
     */
    private Notification fetchNotification(UUID notificationId, UUID userId) {
        return notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(notificationId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "You can delete only your own notifications"
                ));
    }

    /**
     * 알림 엔티티에 현재 시각을 할당하여 삭제 처리한 후 저장합니다.
     *
     * @param notification 삭제 처리할 Notification 엔티티
     */
    private void markAsDeleted(Notification notification) {
        notification.setDeletedAt(new Date());
        notificationRepository.save(notification);
        log.info("Notification {} marked as deleted", notification.getId());
    }
}