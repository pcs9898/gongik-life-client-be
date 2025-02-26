package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.DeleteAllNotificationsRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteAllNotificationsService {

    private final NotificationRepository notificationRepository;

    public void deleteAllNotifications(DeleteAllNotificationsRequestDto requestDto) {

        notificationRepository.deleteAllNotifications(UUID.fromString(requestDto.getUserId()));
    }
}
