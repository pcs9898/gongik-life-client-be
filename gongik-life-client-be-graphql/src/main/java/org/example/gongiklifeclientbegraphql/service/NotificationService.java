package org.example.gongiklifeclientbegraphql.service;

import com.gongik.notificationService.domain.service.NotificationServiceGrpc;
import dto.notification.DeleteAllNotificationsRequestDto;
import dto.notification.DeleteNotificationRequestDto;
import dto.notification.MarkAllNotificationsAsReadRequestDto;
import dto.notification.MarkNotificationAsReadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.notification.deleteAllNotifications.DeleteAllNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.deleteNotification.DeleteNotificationResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.markAllNotificationsAsRead.MarkAllNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.markNotificationAsRead.MarkNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.producer.notification.DeleteAllNotificationsProducer;
import org.example.gongiklifeclientbegraphql.producer.notification.DeleteNotificationProducer;
import org.example.gongiklifeclientbegraphql.producer.notification.MarkAllNotificationsAsReadProducer;
import org.example.gongiklifeclientbegraphql.producer.notification.MarkNotificationAsReadProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final MarkNotificationAsReadProducer markNotificationAsReadProducer;
  private final MarkAllNotificationsAsReadProducer markAllNotificationsAsReadProducer;
  private final DeleteNotificationProducer deleteNotificationProducer;
  private final DeleteAllNotificationsProducer deleteAllNotificationsProducer;

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
