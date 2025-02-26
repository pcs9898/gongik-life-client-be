package org.example.gongiklifeclientbereportservice.service;

import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteReportService {


    // 삭제가 불가능한 신고 상태 코드 (예: 리뷰 중, 해결됨, 거절됨)
    private static final int REPORT_STATUS_REVIEWING = 2;
    private static final int REPORT_STATUS_RESOLVED = 3;
    private static final int REPORT_STATUS_REJECTED = 4;
    private final ReportRepository reportRepository;

    /**
     * 신고 삭제 처리
     * <p>
     * 1. 요청으로부터 신고ID와 사용자ID를 UUID로 변환합니다.
     * 2. 해당 신고가 존재하는지 확인하고, 존재하지 않으면 NOT_FOUND 예외를 발생시킵니다.
     * 3. 요청 사용자가 신고 작성자와 일치하는지 검증합니다.
     * 4. 신고 상태에 따라 삭제가 가능한지 검증합니다.
     * 5. 삭제 가능하면, 삭제 일시(deletedAt)를 설정하고 성공 응답을 반환합니다.
     */
    @Transactional
    public ReportServiceOuterClass.DeleteReportResponse deleteReport(ReportServiceOuterClass.DeleteReportRequest request) {
        UUID reportId = parseUuid(request.getReportId(), "reportId");
        UUID requestUserId = parseUuid(request.getUserId(), "userId");

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("Report not found").asRuntimeException());

        if (!report.getUserId().equals(requestUserId)) {
            log.error("User {} attempted to delete report {} which does not belong to them.", requestUserId, reportId);
            throw Status.PERMISSION_DENIED
                    .withDescription("You cannot delete other user's report")
                    .asRuntimeException();
        }

        validateDeletionStatus(report.getStatusId());

        report.setDeletedAt(new Date());
        log.info("User {} deleted report {} successfully.", requestUserId, reportId);

        return ReportServiceOuterClass.DeleteReportResponse.newBuilder()
                .setReportId(report.getId().toString())
                .setSuccess(true)
                .build();
    }

    /**
     * 신고 상태에 따른 삭제 가능 여부 검사.
     *
     * @param status 신고 상태 코드
     * @throws RuntimeException 삭제할 수 없는 상태인 경우 FAILED_PRECONDITION 예외 발생
     */
    private void validateDeletionStatus(int status) {
        String message = null;
        switch (status) {
            case REPORT_STATUS_REVIEWING:
                message = "Cannot delete a reviewing report";
                break;
            case REPORT_STATUS_RESOLVED:
                message = "Cannot delete a resolved report";
                break;
            case REPORT_STATUS_REJECTED:
                message = "Cannot delete a rejected report";
                break;
            default:
                // 삭제 가능 상태인 경우 - 별도 처리 없음
                break;
        }
        if (message != null) {
            log.error("Report deletion failure due to status {}: {}", status, message);
            throw Status.FAILED_PRECONDITION.withDescription(message).asRuntimeException();
        }
    }

    /**
     * 문자열 UUID를 UUID 객체로 변환하는 유틸리티 메서드.
     *
     * @param uuidStr   변환할 문자열
     * @param fieldName 필드 이름 (에러 메시지 용)
     * @return 변환된 UUID 객체
     * @throws IllegalArgumentException 변환 실패 시 발생
     */
    private UUID parseUuid(String uuidStr, String fieldName) {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            String msg = "Invalid UUID format for " + fieldName + ": " + uuidStr;
            log.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
    }
}