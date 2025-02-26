package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.CreateNotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.entity.Notification;
import org.example.gongiklifeclientbenotificationservice.producer.SendNotificationProducer;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateNotificationService {


    private final SendNotificationProducer sendNotificationProducer;
    private final NotificationRepository notificationRepository;

    public void createNotification(CreateNotificationRequestDto requestDto) {
        // 1. DTO를 Notification 엔티티로 변환
        Notification notification = convertToNotification(requestDto);
        // 2. Notification 엔티티 저장
        Notification savedNotification = notificationRepository.save(notification);
        // 3. 저장된 Notification을 기반으로 알림 전송 요청 발행
        publishNotification(savedNotification);
    }

    /**
     * CreateNotificationRequestDto를 Notification 엔티티로 변환합니다.
     *
     * @param requestDto 알림 생성 요청 DTO
     * @return 변환된 Notification 엔티티
     */
    private Notification convertToNotification(CreateNotificationRequestDto requestDto) {
        Notification.NotificationBuilder builder = Notification.builder()
                .userId(parseUuid(requestDto.getUserId(), "userId"))
                .notificationTypeId(requestDto.getNotificationTypeId())
                .title(requestDto.getTitle())
                .content(requestDto.getContent());

        if (requestDto.getPostId() != null) {
            builder.postId(parseUuid(requestDto.getPostId(), "postId"));
        }
        if (requestDto.getTargetCommentId() != null) {
            builder.targetCommentId(parseUuid(requestDto.getTargetCommentId(), "targetCommentId"));
        }
        return builder.build();
    }

    /**
     * 주어진 문자열을 UUID 객체로 변환합니다. 변환 실패 시 예외를 발생시킵니다.
     *
     * @param uuidStr   변환할 UUID 문자열
     * @param fieldName 필드 이름 (로그 메시지용)
     * @return 변환된 UUID 객체
     */
    private UUID parseUuid(String uuidStr, String fieldName) {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid UUID format for {}: {}", fieldName, uuidStr, ex);
            throw ex;
        }
    }

    /**
     * 저장된 Notification 엔티티를 기반으로 알림 전송 요청을 발행합니다.
     *
     * @param savedNotification 저장된 Notification 엔티티
     */
    private void publishNotification(Notification savedNotification) {
        sendNotificationProducer.sendNotificationRequest(savedNotification.toSendNotificationRequestDto());
        log.info("Notification published successfully for notification id: {}", savedNotification.getId());
    }
}