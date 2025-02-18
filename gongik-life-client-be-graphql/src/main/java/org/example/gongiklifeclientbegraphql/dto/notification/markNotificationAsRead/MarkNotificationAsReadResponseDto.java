package org.example.gongiklifeclientbegraphql.dto.notification.markNotificationAsRead;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkNotificationAsReadResponseDto {

  private boolean success;

}
