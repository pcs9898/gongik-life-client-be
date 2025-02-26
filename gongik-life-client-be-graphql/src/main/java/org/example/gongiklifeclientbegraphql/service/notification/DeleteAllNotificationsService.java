package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.DeleteAllNotificationsRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.deleteAllNotifications.DeleteAllNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.DeleteAllNotificationsProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteAllNotificationsService {

    private final DeleteAllNotificationsProducer deleteAllNotificationsProducer;

    public DeleteAllNotificationsResponseDto deleteAllNotifications(
            DeleteAllNotificationsRequestDto requestDto) {

        try {

            deleteAllNotificationsProducer.sendDeleteAllNotificationsRequest(requestDto);

            return DeleteAllNotificationsResponseDto.builder()
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Failed to delete all notifications", e);
            throw e;
        }
    }
}
