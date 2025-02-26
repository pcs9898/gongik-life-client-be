package org.example.gongiklifeclientbegraphql.dto.notification.myNotifications;

import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyNotificationsRequestDto {

    private String userId;

    private String cursor;

    @Range(min = 1, max = 20)
    private Integer pageSize;

    public MyNotificationsRequest toMyNotificationsRequestProto() {
        MyNotificationsRequest.Builder builder = MyNotificationsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize);

        if (cursor != null) {
            builder.setCursor(cursor);
        }

        return builder.build();
    }
}
