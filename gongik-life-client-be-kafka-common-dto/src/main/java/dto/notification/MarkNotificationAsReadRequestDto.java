package dto.notification;

//import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarkNotificationAsReadRequestDto {

  private String userId;

  //  @NotNull
  private String notificationId;

}
