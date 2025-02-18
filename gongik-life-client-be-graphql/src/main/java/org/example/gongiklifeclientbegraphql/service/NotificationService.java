package org.example.gongiklifeclientbegraphql.service;

import com.gongik.notificationService.domain.service.NotificationServiceGrpc;
import dto.notification.MarkNotificationAsReadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.notification.markNotificationAsRead.MarkNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.MarkNotificationAsReadProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final MarkNotificationAsReadProducer markNotificationAsReadProducer;
  @GrpcClient("gongik-life-client-be-notification-service")
  private NotificationServiceGrpc.NotificationServiceBlockingStub notificationServiceBlockingStub;

  public MyNotificationsResponseDto myNotifications(MyNotificationsRequestDto requestDto) {
    try {
      return MyNotificationsResponseDto.fromProto(
          notificationServiceBlockingStub.myNotifications(requestDto.toProto()));
    } catch (Exception e) {
      log.error("Failed to get my notifications", e);
      throw e;
    }
  }

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
