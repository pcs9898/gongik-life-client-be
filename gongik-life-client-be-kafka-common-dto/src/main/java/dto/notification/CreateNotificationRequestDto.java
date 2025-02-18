package dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateNotificationRequestDto {

  private String userId;
  private Integer notificationTypeId;
  private String title;
  private String content;
  private String postId;
  private String targetCommentId;

  
}
