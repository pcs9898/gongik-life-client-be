package org.example.gongiklifeclientbeworkhoursservice.grpc;

import com.gongik.workhoursService.domain.service.WorkhoursServiceGrpc;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.Empty;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.GetAverageWorkhoursResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbeworkhoursservice.service.WorkhoursService;


@GrpcService
@Slf4j
@RequiredArgsConstructor
public class WorkhoursGrpcService extends WorkhoursServiceGrpc.WorkhoursServiceImplBase {

  private final WorkhoursService workhoursService;

  @Override
  public void getAverageWorkhours(Empty request,
      StreamObserver<GetAverageWorkhoursResponse> responseObserver) {
    try {
      GetAverageWorkhoursResponse response = workhoursService.getAverageWorkhours(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {

      log.error("getAverageWorkhours error: {} - {}",
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
