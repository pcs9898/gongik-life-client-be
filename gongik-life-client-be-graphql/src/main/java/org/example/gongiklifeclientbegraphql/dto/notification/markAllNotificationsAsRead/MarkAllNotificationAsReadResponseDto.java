package org.example.gongiklifeclientbegraphql.dto.notification.markAllNotificationsAsRead;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkAllNotificationAsReadResponseDto {

  private boolean success;
}
