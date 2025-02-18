package org.example.gongiklifeclientbenotificationservice.service;

import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsRequest;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsResponse;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.NotificationForList;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.PageInfo;
import dto.notification.CreateNotificationRequestDto;
import dto.notification.DeleteNotificationRequestDto;
import dto.notification.MarkAllNotificationsAsReadRequestDto;
import dto.notification.MarkNotificationAsReadRequestDto;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbenotificationservice.entity.Notification;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public void createNotification(CreateNotificationRequestDto requestDto) {

    Notification notification = Notification.builder()
        .userId(UUID.fromString(requestDto.getUserId()))
        .notificationTypeId(requestDto.getNotificationTypeId())
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .build();

    if (requestDto.getPostId() != null) {
      notification.setPostId(UUID.fromString(requestDto.getPostId()));
    }

    if (requestDto.getTargetCommentId() != null) {
      notification.setTargetCommentId(UUID.fromString(requestDto.getTargetCommentId()));
    }

    notificationRepository.save(notification);
  }

  public MyNotificationsResponse myNotifications(MyNotificationsRequest request) {

    List<Notification> notifications = notificationRepository.findMyNotificationsWithCursor(
        request.getUserId(),
        request.hasCursor() ? request.getCursor() : null,
        request.getPageSize());

    List<NotificationForList> protoNotifications = notifications.stream()
        .map(notification -> {
          NotificationForList.Builder builder = NotificationForList.newBuilder()
              .setId(notification.getId().toString())
              .setNotificationTypeId(notification.getNotificationTypeId())
              .setTitle(notification.getTitle())
              .setContent(notification.getContent())
              .setCreatedAt(notification.getCreatedAt().toString());

          if (notification.getPostId() != null) {
            builder.setPostId(notification.getPostId().toString());
          }

          if (notification.getTargetCommentId() != null) {
            builder.setTargetCommentId(notification.getTargetCommentId().toString());
          }

          if (notification.getNoticeId() != null) {
            builder.setNoticeId(notification.getNoticeId().toString());
          }

          if (notification.getTargetedNotificationTypeId() != null) {
            builder.setTargetedNotificationTypeId(notification.getTargetedNotificationTypeId());
          }

          if (notification.getTargetedNotificationId() != null) {
            builder.setTargetedNotificationId(notification.getTargetedNotificationId().toString());
          }

          if (notification.getReportId() != null) {
            builder.setReportId(notification.getReportId().toString());
          }

          if (notification.getReadAt() != null) {
            builder.setReadAt(notification.getReadAt().toString());
          }

          return builder.build();
        }).toList();

    PageInfo.Builder pageInfoBuilder = PageInfo.newBuilder()
        .setHasNextPage(notifications.size() == request.getPageSize());

    if (!notifications.isEmpty()) {
      pageInfoBuilder.setEndCursor(notifications.get(notifications.size() - 1).getId().toString());
    }

    return MyNotificationsResponse.newBuilder()
        .addAllListNotification(protoNotifications)
        .setPageInfo(pageInfoBuilder.build())
        .build();
  }

  public void markNotificationAsRead(MarkNotificationAsReadRequestDto requestDto) {

    Notification notification = notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(
        UUID.fromString(requestDto.getNotificationId()),
        UUID.fromString(requestDto.getUserId())
    ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
        "You can mark only your own notifications as read"));

    if (notification.getReadAt() != null) {
      log.info("Notification already read");
      return;
    }

    notification.setReadAt(new Date());

    notificationRepository.save(notification);
  }

  public void markAllNotificationsAsRead(MarkAllNotificationsAsReadRequestDto requestDto) {
    notificationRepository.markAllNotificationsAsRead(UUID.fromString(requestDto.getUserId()));
  }

  public void deleteNotification(DeleteNotificationRequestDto requestDto) {
    Notification notification = notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(
        UUID.fromString(requestDto.getNotificationId()),
        UUID.fromString(requestDto.getUserId())
    ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
        "You can delete only your own notifications"));

    if (notification.getDeletedAt() != null) {
      log.info("Notification already deleted");
      return;
    }

    notification.setDeletedAt(new Date());

    notificationRepository.save(notification);
  }
}
