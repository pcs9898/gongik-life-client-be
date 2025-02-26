package org.example.gongiklifeclientbenotificationservice.grpc;

import com.gongik.notificationService.domain.service.NotificationServiceGrpc;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsRequest;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbenotificationservice.service.MyNotificationsService;
import org.example.gongiklifeclientbenotificationservice.service.NotificationService;
import util.GrpcServiceExceptionHandlingUtil;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class NotificationGrpcService extends NotificationServiceGrpc.NotificationServiceImplBase {

    private final NotificationService notificationService;
    private final MyNotificationsService myNotificationsService;

    @Override
    public void myNotifications(MyNotificationsRequest request,
                                StreamObserver<MyNotificationsResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("myNotifications",
                () -> myNotificationsService.myNotifications(request),
                responseObserver);
    }
}
