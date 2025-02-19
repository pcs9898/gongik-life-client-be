package dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequestDto {

  private String id;
  private String userId;
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
  private String readAt;


}
