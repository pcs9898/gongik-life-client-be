package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.DeleteNotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.deleteNotification.DeleteNotificationResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.DeleteNotificationProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteNotificationService {

    private final DeleteNotificationProducer deleteNotificationProducer;

    public DeleteNotificationResponseDto deleteNotification(DeleteNotificationRequestDto requestDto) {
        try {

            deleteNotificationProducer.sendDeleteNotificationRequest(requestDto);

            return DeleteNotificationResponseDto.builder()
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Failed to delete notification", e);
            throw e;
        }
    }
}
