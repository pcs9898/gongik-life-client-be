package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.MarkAllNotificationsAsReadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.markAllNotificationsAsRead.MarkAllNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.MarkAllNotificationsAsReadProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkAllNotificationsAsReadService {

    private final MarkAllNotificationsAsReadProducer markAllNotificationsAsReadProducer;

    public MarkAllNotificationAsReadResponseDto markAllNotificationsAsRead(
            MarkAllNotificationsAsReadRequestDto requestDto) {

        try {
            markAllNotificationsAsReadProducer.sendMarkAllNotificationsAsReadRequest(requestDto);

            return MarkAllNotificationAsReadResponseDto.builder()
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Failed to mark all notifications as read", e);
            throw e;
        }
    }
}
