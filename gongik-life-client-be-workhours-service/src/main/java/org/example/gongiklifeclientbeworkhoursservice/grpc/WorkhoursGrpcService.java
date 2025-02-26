package org.example.gongiklifeclientbeworkhoursservice.grpc;

import com.gongik.workhoursService.domain.service.WorkhoursServiceGrpc;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.Empty;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.GetAverageWorkhoursResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbeworkhoursservice.service.WorkhoursService;
import util.GrpcServiceExceptionHandlingUtil;


@GrpcService
@Slf4j
@RequiredArgsConstructor
public class WorkhoursGrpcService extends WorkhoursServiceGrpc.WorkhoursServiceImplBase {

    private final WorkhoursService workhoursService;

    @Override
    public void getAverageWorkhours(Empty request,
                                    StreamObserver<GetAverageWorkhoursResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("getAverageWorkhours",
                () -> workhoursService.getAverageWorkhours(request),
                responseObserver
        );
    }
}
