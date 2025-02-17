package org.example.gongiklifeclientbereportservice.grpc;

import com.gongik.reportService.domain.service.ReportServiceGrpc;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateReportRequest;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateReportResponse;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateSystemReportRequest;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateSystemReportResponse;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.DeleteReportRequest;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.DeleteReportResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbereportservice.service.ReportService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class ReportGrpcService extends ReportServiceGrpc.ReportServiceImplBase {

  private final ReportService reportService;

  @Override
  public void createSystemReport(CreateSystemReportRequest request,
      StreamObserver<CreateSystemReportResponse> responseObserver) {
    try {
      CreateSystemReportResponse response = reportService.createSystemReport(request);
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Error occurred while creating system report", e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void createReport(CreateReportRequest request,
      StreamObserver<CreateReportResponse> responseObserver) {
    try {
      CreateReportResponse response = reportService.createReport(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Error occurred while creating report", e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void deleteReport(DeleteReportRequest request,
      StreamObserver<DeleteReportResponse> responseObserver) {
    try {
      DeleteReportResponse response = reportService.deleteReport(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Error occurred while deleting report", e);
      responseObserver.onError(e);
    }
  }
}
