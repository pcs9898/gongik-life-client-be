package org.example.gongiklifeclientbereportservice.service;

import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetReportService {

    
    private final ReportRepository reportRepository;

    public ReportServiceOuterClass.ReportResponse report(ReportServiceOuterClass.ReportRequest request) {
        UUID reportId = parseUuid(request.getReportId(), "reportId");
        UUID requestUserId = parseUuid(request.getUserId(), "userId");

        Report report = getReportById(reportId);
        validateUserAccess(report, requestUserId);
        return buildReportResponse(report);
    }

    // UUID 문자열을 UUID 객체로 변환 (필드명 포함하여 검증 메시지 제공)
    private UUID parseUuid(String uuidStr, String fieldName) {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException ex) {
            String msg = String.format("Invalid UUID format for %s: %s", fieldName, uuidStr);
            log.error(msg, ex);
            throw new IllegalArgumentException(msg, ex);
        }
    }

    // 신고 아이디로 신고 엔티티를 조회 (존재하지 않으면 NOT_FOUND 예외 발생)
    private Report getReportById(UUID reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    String msg = "Report not found, reportId: " + reportId;
                    log.error(msg);
                    return Status.NOT_FOUND.withDescription("Report not found").asRuntimeException();
                });
    }

    // 요청 사용자가 신고의 소유자인지 검증 (불일치 시 PERMISSION_DENIED 예외 발생)
    private void validateUserAccess(Report report, UUID requestUserId) {
        if (!report.getUserId().equals(requestUserId)) {
            String msg = "User " + requestUserId + " cannot view report " + report.getId();
            log.error(msg);
            throw Status.PERMISSION_DENIED.withDescription("You cannot look at other user's report").asRuntimeException();
        }
    }

    // Report 엔티티의 정보를 기반으로 gRPC 응답 객체를 빌드
    private ReportServiceOuterClass.ReportResponse buildReportResponse(Report report) {
        ReportServiceOuterClass.ReportResponse.Builder builder = ReportServiceOuterClass.ReportResponse.newBuilder()
                .setId(report.getId().toString())
                .setTypeId(report.getTypeId())
                .setStatusId(report.getStatusId())
                .setTitle(report.getTitle())
                .setContent(report.getContent())
                .setCreatedAt(report.getCreatedAt().toString());

        if (report.getSystemCategoryId() != null) {
            builder.setSystemCategoryId(report.getSystemCategoryId());
        }

        if (report.getTargetId() != null) {
            builder.setTargetId(report.getTargetId().toString());
        }

        return builder.build();
    }
}