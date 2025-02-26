package org.example.gongiklifeclientbereportservice.service;

import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetReportServiceTest {

    private static final String VALID_REPORT_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String VALID_USER_ID = "123e4567-e89b-12d3-a456-426614174001";
    private static final String OTHER_USER_ID = "123e4567-e89b-12d3-a456-426614174002";

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private GetReportService getReportService;

    // 성공 케이스에 사용할 더미 Report 엔티티 생성 헬퍼 메서드
    private Report createDummyReport(String reportId, String userId) {
        return Report.builder()
                .id(UUID.fromString(reportId))
                .userId(UUID.fromString(userId))
                .typeId(1)
                .statusId(1)
                .title("Test Report Title")
                .content("Test Report Content")
                .createdAt(new Date())
                .build();
    }

    @Test
    @DisplayName("성공: 신고 조회 성공")
    void report_success() {
        // Given: 유효한 ReportRequest와 해당 Report 엔티티가 존재하며, 요청 사용자가 동일한 경우
        ReportServiceOuterClass.ReportRequest request =
                ReportServiceOuterClass.ReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId(VALID_USER_ID)
                        .build();

        Report dummyReport = createDummyReport(VALID_REPORT_ID, VALID_USER_ID);
        when(reportRepository.findById(eq(UUID.fromString(VALID_REPORT_ID))))
                .thenReturn(Optional.of(dummyReport));

        // When: 서비스 호출
        ReportServiceOuterClass.ReportResponse response = getReportService.report(request);

        // Then: 응답 객체의 내용이 dummyReport와 일치하는지 검증
        assertNotNull(response);
        assertEquals(VALID_REPORT_ID, response.getId());
        assertEquals(dummyReport.getTypeId(), response.getTypeId());
        assertEquals(dummyReport.getStatusId(), response.getStatusId());
        assertEquals(dummyReport.getTitle(), response.getTitle());
        assertEquals(dummyReport.getContent(), response.getContent());
    }

    @Test
    @DisplayName("실패: 신고 조회 - 신고가 존재하지 않음")
    void report_reportNotFound() {
        // Given: 입력된 reportId로 조회 시 엔티티가 존재하지 않음
        ReportServiceOuterClass.ReportRequest request =
                ReportServiceOuterClass.ReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId(VALID_USER_ID)
                        .build();
        when(reportRepository.findById(eq(UUID.fromString(VALID_REPORT_ID))))
                .thenReturn(Optional.empty());

        // When & Then: "Report not found" 메시지의 StatusRuntimeException 발생 여부 확인
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                getReportService.report(request)
        );
        assertTrue(exception.getMessage().contains("Report not found"));
        verify(reportRepository).findById(eq(UUID.fromString(VALID_REPORT_ID)));
    }

    @Test
    @DisplayName("실패: 신고 조회 - 사용자 권한 불일치")
    void report_permissionDenied() {
        // Given: 요청 사용자가 실제 Report 작성자와 다를 경우
        ReportServiceOuterClass.ReportRequest request =
                ReportServiceOuterClass.ReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId(VALID_USER_ID)
                        .build();
        Report dummyReport = createDummyReport(VALID_REPORT_ID, OTHER_USER_ID);
        when(reportRepository.findById(eq(UUID.fromString(VALID_REPORT_ID))))
                .thenReturn(Optional.of(dummyReport));

        // When & Then: "You cannot look at other user's report" 메시지의 예외 발생 여부 확인
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                getReportService.report(request)
        );
        assertTrue(exception.getMessage().contains("You cannot look at other user's report"));
        verify(reportRepository).findById(eq(UUID.fromString(VALID_REPORT_ID)));
    }

    @Test
    @DisplayName("실패: 신고 조회 - 잘못된 UUID 형식 (reportId)")
    void report_invalidReportIdUuid() {
        // Given: 잘못된 reportId 형식
        ReportServiceOuterClass.ReportRequest request =
                ReportServiceOuterClass.ReportRequest.newBuilder()
                        .setReportId("invalid-uuid")
                        .setUserId(VALID_USER_ID)
                        .build();

        // When & Then: reportId 파싱 시 IllegalArgumentException 발생 여부 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                getReportService.report(request)
        );
        assertTrue(exception.getMessage().contains("Invalid UUID format for reportId"));
    }

    @Test
    @DisplayName("실패: 신고 조회 - 잘못된 UUID 형식 (userId)")
    void report_invalidUserIdUuid() {
        // Given: 잘못된 userId 형식
        ReportServiceOuterClass.ReportRequest request =
                ReportServiceOuterClass.ReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId("invalid-uuid")
                        .build();

        // When & Then: userId 파싱 시 IllegalArgumentException 발생 여부 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                getReportService.report(request)
        );
        assertTrue(exception.getMessage().contains("Invalid UUID format for userId"));
    }
}
