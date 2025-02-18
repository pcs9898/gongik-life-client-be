package org.example.gongiklifeclientbegraphql.dto.notification.myNotifications;

import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;
import org.example.gongiklifeclientbegraphql.dto.notification.NotificationForListDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyNotificationsResponseDto {

  private List<NotificationForListDto> listNotification;
  private PageInfoDto pageInfo;

  public static MyNotificationsResponseDto fromProto(
      MyNotificationsResponse myNotificationsResponseProto) {
    return MyNotificationsResponseDto.builder()
        .listNotification(NotificationForListDto.fromProto(
            myNotificationsResponseProto.getListNotificationList()))
        .pageInfo(
            PageInfoDto.fromMyNotificationResponseProto(myNotificationsResponseProto.getPageInfo()))
        .build();
  }

}
