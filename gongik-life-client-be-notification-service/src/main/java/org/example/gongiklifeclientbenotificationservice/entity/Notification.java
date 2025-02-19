package org.example.gongiklifeclientbenotificationservice.entity;

import dto.notification.SendNotificationRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at is null")
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "notification_type_id", nullable = false)
  private Integer notificationTypeId;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "post_id")
  private UUID postId;

  @Column(name = "target_comment_id")
  private UUID targetCommentId;

  @Column(name = "notice_id")
  private UUID noticeId;

  @Column(name = "targeted_notification_type_id")
  private Integer targetedNotificationTypeId;

  @Column(name = "targeted_notification_id")
  private UUID targetedNotificationId;

  @Column(name = "report_id")
  private UUID reportId;

  // read_at: null 이면 읽지 않은 상태
  @Column(name = "read_at")
  private Date readAt;

  // created_at은 기본값이 CURRENT_TIMESTAMP이지만, JPA에서도 설정하도록 합니다.
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at")
  private Date createdAt;

  @Column(name = "deleted_at")
  private Date deletedAt;

  public SendNotificationRequestDto toSendNotificationRequestDto(
  ) {
    SendNotificationRequestDto.SendNotificationRequestDtoBuilder response = SendNotificationRequestDto.builder()
        .id(getId().toString())
        .userId(getUserId().toString())
        .notificationTypeId(getNotificationTypeId())
        .title(getTitle())
        .content(getContent())
        .createdAt(getCreatedAt().toString());

    if (getPostId() != null) {
      response.postId(getPostId().toString());
    }

    if (getTargetCommentId() != null) {
      response.targetCommentId(getTargetCommentId().toString());
    }

    if (getNoticeId() != null) {
      response.noticeId(noticeId.toString());
    }

    if (getTargetedNotificationTypeId() != null) {
      response.targetedNotificationTypeId(getTargetedNotificationTypeId());
    }

    if (getTargetedNotificationId() != null) {
      response.targetedNotificationId(getTargetedNotificationId().toString());
    }

    if (getReportId() != null) {
      response.reportId(getReportId().toString());
    }

    return response.build();
  }
}