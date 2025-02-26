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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteReportServiceTest {

    private static final String VALID_REPORT_ID = "11111111-1111-1111-1111-111111111111";
    private static final String VALID_USER_ID = "22222222-2222-2222-2222-222222222222";
    private static final String OTHER_USER_ID = "33333333-3333-3333-3333-333333333333";

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private DeleteReportService deleteReportService;

    // 테스트용 더미 Report 객체 생성 헬퍼 메서드
    private Report createDummyReport(String reportId, String userId, int status) {
        return Report.builder()
                .id(UUID.fromString(reportId))
                .userId(UUID.fromString(userId))
                .statusId(status)

                .build();
    }

    @Test
    @DisplayName("성공: 신고 삭제 성공")
    void deleteReport_success() {
        // Arrange: 올바른 신고ID와 사용자ID를 포함한 요청 생성 (삭제 가능 상태: status 1)
        ReportServiceOuterClass.DeleteReportRequest request =
                ReportServiceOuterClass.DeleteReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId(VALID_USER_ID)
                        .build();

        Report dummyReport = createDummyReport(VALID_REPORT_ID, VALID_USER_ID, 1);
        when(reportRepository.findById(UUID.fromString(VALID_REPORT_ID)))
                .thenReturn(Optional.of(dummyReport));

        // Act
        ReportServiceOuterClass.DeleteReportResponse response = deleteReportService.deleteReport(request);

        // Assert: 삭제 시각이 설정되었고, 응답에 올바른 reportId와 성공 플래그가 포함되었는지 검증
        assertNotNull(dummyReport.getDeletedAt());
        assertEquals(VALID_REPORT_ID, response.getReportId());
        assertTrue(response.getSuccess());
        verify(reportRepository).findById(UUID.fromString(VALID_REPORT_ID));
    }

    @Test
    @DisplayName("실패: 신고 삭제 - 신고가 존재하지 않음")
    void deleteReport_reportNotFound() {
        // Arrange: 존재하지 않는 신고 ID
        ReportServiceOuterClass.DeleteReportRequest request =
                ReportServiceOuterClass.DeleteReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId(VALID_USER_ID)
                        .build();

        when(reportRepository.findById(UUID.fromString(VALID_REPORT_ID)))
                .thenReturn(Optional.empty());

        // Act & Assert: NOT_FOUND 예외가 발생하는지 검증
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                deleteReportService.deleteReport(request)
        );
        assertTrue(exception.getMessage().contains("Report not found"));
        verify(reportRepository).findById(UUID.fromString(VALID_REPORT_ID));
    }

    @Test
    @DisplayName("실패: 신고 삭제 - 사용자가 신고 작성자가 아님")
    void deleteReport_permissionDenied() {
        // Arrange: 요청한 사용자와 실제 신고 작성자가 다른 경우
        ReportServiceOuterClass.DeleteReportRequest request =
                ReportServiceOuterClass.DeleteReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId(VALID_USER_ID)
                        .build();

        Report dummyReport = createDummyReport(VALID_REPORT_ID, OTHER_USER_ID, 1);
        when(reportRepository.findById(UUID.fromString(VALID_REPORT_ID)))
                .thenReturn(Optional.of(dummyReport));

        // Act & Assert: PERMISSION_DENIED 예외를 발생시키는지 검증
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                deleteReportService.deleteReport(request)
        );
        assertTrue(exception.getMessage().contains("cannot delete other user's report"));
        verify(reportRepository).findById(UUID.fromString(VALID_REPORT_ID));
    }

    @Test
    @DisplayName("실패: 신고 삭제 - 삭제 불가능한 상태 (리뷰 중)")
    void deleteReport_invalidStatusReviewing() {
        // Arrange: 신고 상태가 '리뷰 중'(status 2)인 경우
        ReportServiceOuterClass.DeleteReportRequest request =
                ReportServiceOuterClass.DeleteReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId(VALID_USER_ID)
                        .build();

        Report dummyReport = createDummyReport(VALID_REPORT_ID, VALID_USER_ID, 2);
        when(reportRepository.findById(UUID.fromString(VALID_REPORT_ID)))
                .thenReturn(Optional.of(dummyReport));

        // Act & Assert: REVIEWING 상태에 대해 FAILED_PRECONDITION 예외가 발생하는지 검증
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                deleteReportService.deleteReport(request)
        );
        assertTrue(exception.getMessage().contains("Cannot delete a reviewing report"));
        verify(reportRepository).findById(UUID.fromString(VALID_REPORT_ID));
    }

    @Test
    @DisplayName("실패: 신고 삭제 - 삭제 불가능한 상태 (해결됨)")
    void deleteReport_invalidStatusResolved() {
        // Arrange: 신고 상태가 '해결됨'(status 3)인 경우
        ReportServiceOuterClass.DeleteReportRequest request =
                ReportServiceOuterClass.DeleteReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId(VALID_USER_ID)
                        .build();

        Report dummyReport = createDummyReport(VALID_REPORT_ID, VALID_USER_ID, 3);
        when(reportRepository.findById(UUID.fromString(VALID_REPORT_ID)))
                .thenReturn(Optional.of(dummyReport));

        // Act & Assert: RESOLVED 상태에 대해 FAILED_PRECONDITION 예외가 발생하는지 검증
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                deleteReportService.deleteReport(request)
        );
        assertTrue(exception.getMessage().contains("Cannot delete a resolved report"));
        verify(reportRepository).findById(UUID.fromString(VALID_REPORT_ID));
    }

    @Test
    @DisplayName("실패: 신고 삭제 - 삭제 불가능한 상태 (거절됨)")
    void deleteReport_invalidStatusRejected() {
        // Arrange: 신고 상태가 '거절됨'(status 4)인 경우
        ReportServiceOuterClass.DeleteReportRequest request =
                ReportServiceOuterClass.DeleteReportRequest.newBuilder()
                        .setReportId(VALID_REPORT_ID)
                        .setUserId(VALID_USER_ID)
                        .build();

        Report dummyReport = createDummyReport(VALID_REPORT_ID, VALID_USER_ID, 4);
        when(reportRepository.findById(UUID.fromString(VALID_REPORT_ID)))
                .thenReturn(Optional.of(dummyReport));

        // Act & Assert: REJECTED 상태에 대해 FAILED_PRECONDITION 예외가 발생하는지 검증
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                deleteReportService.deleteReport(request)
        );
        assertTrue(exception.getMessage().contains("Cannot delete a rejected report"));
        verify(reportRepository).findById(UUID.fromString(VALID_REPORT_ID));
    }

    @Test
    @DisplayName("실패: 신고 삭제 - 잘못된 UUID 형식")
    void deleteReport_invalidUuidFormat() {
        // Arrange: 올바르지 않은 UUID 문자열을 사용한 경우
        ReportServiceOuterClass.DeleteReportRequest request =
                ReportServiceOuterClass.DeleteReportRequest.newBuilder()
                        .setReportId("invalid-uuid")
                        .setUserId("invalid-uuid")
                        .build();

        // Act & Assert: IllegalArgumentException 발생 여부 검증
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                deleteReportService.deleteReport(request)
        );
        assertTrue(exception.getMessage().contains("Invalid UUID format"));
    }
}
