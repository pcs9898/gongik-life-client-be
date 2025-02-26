package org.example.gongiklifeclientbereportservice.service;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass;
import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateReportService {


    // 상수: 리포트 타입과 초기 상태 값
    private static final int REPORT_TYPE_INSTITUTION = 2;
    private static final int REPORT_TYPE_INSTITUTION_REVIEW = 3;
    private static final int REPORT_TYPE_POST = 4;
    private static final int REPORT_TYPE_COMMENT = 5;
    private static final int INITIAL_STATUS_ID = 1;
    private final ReportRepository reportRepository;
    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;
    @GrpcClient("gongik-life-client-be-institution-service")
    private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

    public ReportServiceOuterClass.CreateReportResponse createReport(ReportServiceOuterClass.CreateReportRequest request) {
        String targetId = request.getTargetId();
        int reportTypeId = request.getReportTypeId();

        // 대상 존재 여부 체크
        if (!isTargetExist(reportTypeId, targetId)) {
            throwReportNotFoundException(reportTypeId, targetId);
        }

        // 같은 사용자가 이미 신고했는지 확인
        UUID userId = parseUuid(request.getUserId());
        UUID targetUuid = parseUuid(targetId);
        if (reportRepository.existsByUserIdAndTargetId(userId, targetUuid)) {
            throw Status.ALREADY_EXISTS.withDescription(
                    "You already reported this target, can not report again").asRuntimeException();
        }

        // 신고 저장 후 응답 빌드
        Report savedReport = saveReport(userId, request, targetUuid);
        log.info("Report created successfully with reportId: {}", savedReport.getId());
        return buildResponse(savedReport);
    }

    // 신고 대상(기관, 기관리뷰, 게시글, 댓글) 존재 여부를 확인하는 메서드
    private boolean isTargetExist(int reportTypeId, String targetId) {
        switch (reportTypeId) {
            case REPORT_TYPE_INSTITUTION:
                return institutionBlockingStub.existsInstitution(
                        InstitutionServiceOuterClass.ExistsInstitutionRequest.newBuilder()
                                .setInstitutionId(targetId)
                                .build()
                ).getExists();
            case REPORT_TYPE_INSTITUTION_REVIEW:
                return institutionBlockingStub.existsInstitutionReview(
                        InstitutionServiceOuterClass.ExistsInstitutionReviewRequest.newBuilder()
                                .setInstitutionReviewId(targetId)
                                .build()
                ).getExists();
            case REPORT_TYPE_POST:
                return communityServiceBlockingStub.existsPost(
                        CommunityServiceOuterClass.ExistsPostRequest.newBuilder()
                                .setPostId(targetId)
                                .build()
                ).getExists();
            case REPORT_TYPE_COMMENT:
            default:
                return communityServiceBlockingStub.existsComment(
                        CommunityServiceOuterClass.ExistsCommentRequest.newBuilder()
                                .setCommentId(targetId)
                                .build()
                ).getExists();
        }
    }

    // 대상이 존재하지 않을 경우 예외를 던지는 메서드
    private void throwReportNotFoundException(int reportTypeId, String targetId) {
        String message;
        switch (reportTypeId) {
            case REPORT_TYPE_INSTITUTION:
                message = "Institution not found: " + targetId + ", can not report";
                break;
            case REPORT_TYPE_INSTITUTION_REVIEW:
                message = "Institution review not found: " + targetId + ", can not report";
                break;
            case REPORT_TYPE_POST:
                message = "Post not found: " + targetId + ", can not report";
                break;
            default: // REPORT_TYPE_COMMENT
                message = "Comment not found: " + targetId + ", can not report";
                break;
        }
        log.error("Target not found for reportType {}: {}", reportTypeId, targetId);
        throw Status.NOT_FOUND.withDescription(message).asRuntimeException();
    }

    // UUID 형식의 문자열을 UUID 객체로 변환하는 메서드
    private UUID parseUuid(String uuidStr) {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid UUID format: {}", uuidStr, ex);
            throw new IllegalArgumentException("Invalid UUID format: " + uuidStr, ex);
        }
    }

    // 신고 정보를 기반으로 Report 엔티티를 생성, 저장하는 메서드
    private Report saveReport(UUID userId, ReportServiceOuterClass.CreateReportRequest request, UUID targetUuid) {
        Report report = Report.builder()
                .userId(userId)
                .typeId(request.getReportTypeId())
                .targetId(targetUuid)
                .title(request.getTitle())
                .content(request.getContent())
                .statusId(INITIAL_STATUS_ID)
                .build();
        return reportRepository.save(report);
    }

    // 저장된 Report 정보를 기반으로 응답 Proto 객체를 빌드하는 메서드
    private ReportServiceOuterClass.CreateReportResponse buildResponse(Report report) {
        return ReportServiceOuterClass.CreateReportResponse.newBuilder()
                .setReportId(report.getId().toString())
                .build();
    }
}