package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.MarkAllNotificationsAsReadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkAllNotificationsAsReadService {

    private final NotificationRepository notificationRepository;

    public void markAllNotificationsAsRead(MarkAllNotificationsAsReadRequestDto requestDto) {
        notificationRepository.markAllNotificationsAsRead(UUID.fromString(requestDto.getUserId()));
    }
}
