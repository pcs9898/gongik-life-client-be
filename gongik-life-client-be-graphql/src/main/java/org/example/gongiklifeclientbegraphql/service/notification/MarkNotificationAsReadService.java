package org.example.gongiklifeclientbegraphql.service.notification;

import dto.notification.MarkNotificationAsReadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.markNotificationAsRead.MarkNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.MarkNotificationAsReadProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkNotificationAsReadService {

    private final MarkNotificationAsReadProducer markNotificationAsReadProducer;

    public MarkNotificationAsReadResponseDto markNotificationAsRead(
            MarkNotificationAsReadRequestDto requestDto) {

        try {

            markNotificationAsReadProducer.sendMarkNotificationAsReadRequest(requestDto);

            return MarkNotificationAsReadResponseDto.builder()
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Failed to mark notification as read", e);
            throw e;
        }
    }
}
