package org.example.gongiklifeclientbereportservice.grpc;

import com.gongik.reportService.domain.service.ReportServiceGrpc;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbereportservice.service.*;
import util.GrpcServiceExceptionHandlingUtil;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class ReportGrpcService extends ReportServiceGrpc.ReportServiceImplBase {

    private final ReportService reportService;
    private final CreateReportService createReportService;
    private final CreateSystemReportService createSystemReportService;
    private final DeleteReportService deleteReportService;
    private final GetReportService getReportService;
    private final MyReportsService myReportsService;

    @Override
    public void createSystemReport(CreateSystemReportRequest request,
                                   StreamObserver<CreateSystemReportResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("createSystemReport",
                () -> createSystemReportService.createSystemReport(request),
                responseObserver);
    }

    @Override
    public void createReport(CreateReportRequest request,
                             StreamObserver<CreateReportResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("createReport",
                () -> createReportService.createReport(request),
                responseObserver);
    }

    @Override
    public void deleteReport(DeleteReportRequest request,
                             StreamObserver<DeleteReportResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("deleteReport",
                () -> deleteReportService.deleteReport(request),
                responseObserver);
    }

    @Override
    public void report(ReportRequest request, StreamObserver<ReportResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("report",
                () -> getReportService.report(request),
                responseObserver);
    }

    @Override
    public void myReports(MyReportsRequest request,
                          StreamObserver<MyReportsResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("myReports",
                () -> myReportsService.myReports(request),
                responseObserver);
    }
}
