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
public class CreateSystemReportService {


    private static final int SYSTEM_REPORT_TYPE_ID = 1;
    private static final int INITIAL_STATUS_ID = 1; // 최초 상태 값

    private final ReportRepository reportRepository;

    public ReportServiceOuterClass.CreateSystemReportResponse createSystemReport(
            ReportServiceOuterClass.CreateSystemReportRequest request) {

        UUID userId = parseUserId(request.getUserId());

        // 이미 존재하는 시스템 신고 여부 체크
        if (reportRepository.existsByUserIdAndTypeIdAndSystemCategoryId(
                userId,
                SYSTEM_REPORT_TYPE_ID,
                request.getSystemCategoryId())) {
            String errorMessage = "You already have a system report for this system category";
            log.error("Duplicate system report found for userId: {} and systemCategoryId: {}", userId, request.getSystemCategoryId());
            throw Status.ALREADY_EXISTS.withDescription(errorMessage).asRuntimeException();
        }

        // 신규 신고 저장
        Report newReport = saveNewReport(userId, request);

        // 응답 생성
        return buildResponse(newReport);
    }

    // 사용자 아이디 문자열을 UUID로 변환하는 유틸리티 메서드
    private UUID parseUserId(String userIdStr) {
        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            String msg = "Invalid userId: " + userIdStr;
            log.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
    }

    // 새로운 Report 객체를 생성 및 저장하는 메서드
    private Report saveNewReport(UUID userId, ReportServiceOuterClass.CreateSystemReportRequest request) {
        Report report = Report.builder()
                .userId(userId)
                .typeId(SYSTEM_REPORT_TYPE_ID)
                .systemCategoryId(request.getSystemCategoryId())
                .title(request.getTitle())
                .content(request.getContent())
                .statusId(INITIAL_STATUS_ID)
                .build();

        Report savedReport = reportRepository.save(report);
        log.info("System report created successfully with reportId: {}", savedReport.getId());
        return savedReport;
    }

    // 저장된 Report를 기반으로 gRPC 응답 객체를 생성하는 메서드
    private ReportServiceOuterClass.CreateSystemReportResponse buildResponse(Report report) {
        return ReportServiceOuterClass.CreateSystemReportResponse.newBuilder()
                .setReportId(report.getId().toString())
                .build();
    }
}