package org.example.gongiklifeclientbenotificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.producer.SendNotificationProducer;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SendNotificationProducer sendNotificationProducer;
    private final NotificationRepository notificationRepository;


}
