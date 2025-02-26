package org.example.gongiklifeclientbegraphql.service.notification;

import com.gongik.notificationService.domain.service.NotificationServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyNotificationsService {

    @GrpcClient("gongik-life-client-be-notification-service")
    private NotificationServiceGrpc.NotificationServiceBlockingStub notificationServiceBlockingStub;

    public MyNotificationsResponseDto myNotifications(MyNotificationsRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("MyNotificationsService", () -> {
            return MyNotificationsResponseDto.fromMyNotificationsResponseProto(
                    notificationServiceBlockingStub.myNotifications(requestDto.toMyNotificationsRequestProto()));
        });
    }

}
