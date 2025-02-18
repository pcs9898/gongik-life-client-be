package org.example.gongiklifeclientbegraphql.dto.notification;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationForListDto {

  private String id;
  private Integer notificationTypeId;
  private String title;
  private String content;
  private String postId;
  private String targetCommentId;
  private String noticeId;
  private Integer targetedNotificationTypeId;
  private String targetedNotificationId;
  private String reportId;
  private String createdAt;

  public static List<NotificationForListDto> fromProto(
      List<com.gongik.notificationService.domain.service.NotificationServiceOuterClass.NotificationForList> listNotification) {
    return listNotification.stream()
        .map(notification -> NotificationForListDto.builder()
            .id(notification.getId())
            .notificationTypeId(notification.getNotificationTypeId())
            .title(notification.getTitle())
            .content(notification.getContent())
            .postId(notification.hasPostId() ? notification.getPostId() : null)
            .targetCommentId(
                notification.hasTargetCommentId() ? notification.getTargetCommentId() : null)
            .noticeId(notification.hasNoticeId() ? notification.getNoticeId() : null)
            .targetedNotificationTypeId(
                notification.hasTargetedNotificationTypeId()
                    ? notification.getTargetedNotificationTypeId() : null)
            .targetedNotificationId(
                notification.hasTargetedNotificationId() ? notification.getTargetedNotificationId()
                    : null)
            .reportId(notification.hasReportId() ? notification.getReportId() : null)
            .createdAt(notification.getCreatedAt())
            .build())
        .toList();
  }
}
