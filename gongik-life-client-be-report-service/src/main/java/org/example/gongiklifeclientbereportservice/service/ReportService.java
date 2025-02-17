package org.example.gongiklifeclientbereportservice.service;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.ExistsCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.ExistsPostRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.ExistsInstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.ExistsInstitutionReviewRequest;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateReportRequest;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateReportResponse;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateSystemReportRequest;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateSystemReportResponse;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.DeleteReportRequest;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.DeleteReportResponse;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.ReportRequest;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.ReportResponse;
import io.grpc.Status;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  @GrpcClient("gongik-life-client-be-community-service")
  private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;
  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  public CreateSystemReportResponse createSystemReport(CreateSystemReportRequest request) {
    boolean exist = reportRepository.existsByUserIdAndTypeIdAndSystemCategoryId(
        UUID.fromString(request.getUserId()),
        1,
        request.getSystemCategoryId()
    );

    if (exist) {
      throw Status.ALREADY_EXISTS.withDescription(
          "You already have a system report for this system category").asRuntimeException();
    }

    Report newReport = reportRepository.save(Report.builder()
        .userId(UUID.fromString(request.getUserId()))
        .typeId(1)
        .systemCategoryId(request.getSystemCategoryId())
        .title(request.getTitle())
        .content(request.getContent())
        .statusId(1)
        .build());

    return CreateSystemReportResponse.newBuilder()
        .setReportId(newReport.getId().toString())
        .build();
  }

  public CreateReportResponse createReport(CreateReportRequest request) {
    boolean existTarget = false;
    String targetId = request.getTargetId();

    switch (request.getReportTypeId()) {
      case 2:
        existTarget = institutionBlockingStub.existsInstitution(
            ExistsInstitutionRequest.newBuilder().setInstitutionId(targetId).build()
        ).getExists();
        break;
      case 3:
        existTarget = institutionBlockingStub.existsInstitutionReview(
            ExistsInstitutionReviewRequest.newBuilder().setInstitutionReviewId(targetId).build()
        ).getExists();
        break;
      case 4:
        existTarget = communityServiceBlockingStub.existsPost(
            ExistsPostRequest.newBuilder().setPostId(targetId).build()
        ).getExists();
        break;
      default: // 5
        existTarget = communityServiceBlockingStub.existsComment(
            ExistsCommentRequest.newBuilder().setCommentId(targetId).build()
        ).getExists();
        break;
    }

    if (!existTarget) {
      throwCreateReportNotFoundException(request.getReportTypeId(), targetId);
    }

    // need to check if user already reported the target
    boolean existReport = reportRepository.existsByUserIdAndTargetId(
        UUID.fromString(request.getUserId()),
        UUID.fromString(targetId)
    );

    if (existReport) {
      throw Status.ALREADY_EXISTS.withDescription(
          "You already reported this target, can not report again").asRuntimeException();
    }

    Report newReport = reportRepository.save(Report.builder()
        .userId(UUID.fromString(request.getUserId()))
        .typeId(request.getReportTypeId())
        .targetId(UUID.fromString(request.getTargetId()))
        .title(request.getTitle())
        .content(request.getContent())
        .statusId(1)
        .build());

    return CreateReportResponse.newBuilder()
        .setReportId(newReport.getId().toString())
        .build();

  }

  private void throwCreateReportNotFoundException(int reportTypeId, String targetId) {
    String message;
    switch (reportTypeId) {
      case 2:
        message = "Institution not found : " + targetId + ", can not report";
        break;
      case 3:
        message = "Institution review not found : " + targetId + ", can not report";
        break;
      case 4:
        message = "Post not found : " + targetId + ", can not report";
        break;
      default: // 5
        message = "Comment not found : " + targetId + ", can not report";
        break;
    }
    throw Status.NOT_FOUND.withDescription(message).asRuntimeException();
  }


  public DeleteReportResponse deleteReport(DeleteReportRequest request) {
    Report report = reportRepository.findById(UUID.fromString(request.getReportId()))
        .orElseThrow(
            () -> Status.NOT_FOUND.withDescription("Report not found").asRuntimeException());

    if (!report.getUserId().equals(UUID.fromString(request.getUserId()))) {
      throw Status.PERMISSION_DENIED.withDescription("You can not delete other user's report")
          .asRuntimeException();
    }

    throwDeleteReportException(report.getStatusId());

    reportRepository.delete(report);

    return DeleteReportResponse.newBuilder()
        .setReportId(request.getReportId())
        .setSuccess(true)
        .build();
  }

  private void throwDeleteReportException(int reportStatus) {
    String message;
    switch (reportStatus) {
      case 2:
        message = "Can not delete a reviewing report";
        break;
      case 3:
        message = "Can not delete a resolved report";
        break;
      case 4:
        message = "Can not delete a rejected report";
        break;
      default:
        return; // 예외를 던지지 않음
    }
    throw Status.FAILED_PRECONDITION.withDescription(message).asRuntimeException();
  }

  public ReportResponse report(ReportRequest request) {
    Report report = reportRepository.findById(UUID.fromString(request.getReportId()))
        .orElseThrow(
            () -> Status.NOT_FOUND.withDescription("Report not found").asRuntimeException());

    if (!report.getUserId().equals(UUID.fromString(request.getUserId()))) {
      throw Status.PERMISSION_DENIED.withDescription("You can not look at other user's report")
          .asRuntimeException();
    }

    ReportResponse.Builder response = ReportResponse.newBuilder()
        .setId(report.getId().toString())
        .setTypeId(report.getTypeId())
        .setStatusId(report.getStatusId())
        .setTitle(report.getTitle())
        .setContent(report.getContent())
        .setCreatedAt(report.getCreatedAt().toString());

    if (report.getSystemCategoryId() != null) {
      response.setSystemCategoryId(report.getSystemCategoryId());
    }

    if (report.getTargetId() != null) {
      response.setTargetId(report.getTargetId().toString());
    }

    return response.build();

  }
}


