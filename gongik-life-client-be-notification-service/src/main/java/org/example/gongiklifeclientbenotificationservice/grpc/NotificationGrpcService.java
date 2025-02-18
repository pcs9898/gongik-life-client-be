package org.example.gongiklifeclientbenotificationservice.grpc;

import com.gongik.notificationService.domain.service.NotificationServiceGrpc;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsRequest;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbenotificationservice.service.NotificationService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class NotificationGrpcService extends NotificationServiceGrpc.NotificationServiceImplBase {

  private final NotificationService notificationService;

  @Override
  public void myNotifications(MyNotificationsRequest request,
      StreamObserver<MyNotificationsResponse> responseObserver) {
    try {
      MyNotificationsResponse response = notificationService.myNotifications(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {

      log.error("myNotifications error: {} - {}",
          e.getMessage(), e.getLocalizedMessage());

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }
}
