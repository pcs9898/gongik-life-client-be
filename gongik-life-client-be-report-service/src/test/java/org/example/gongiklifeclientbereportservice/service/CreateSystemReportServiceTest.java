package org.example.gongiklifeclientbereportservice.service;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateSystemReportRequest;
import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateSystemReportResponse;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSystemReportServiceTest {

    // 테스트용 상수값
    private static final String TEST_USER_ID = "11111111-1111-1111-1111-111111111111";
    private static final int TEST_SYSTEM_CATEGORY_ID = 100;
    private static final String TEST_TITLE = "Test Title";
    private static final String TEST_CONTENT = "Test Content";
    private static final UUID GENERATED_REPORT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private CreateSystemReportService createSystemReportService;

    // 테스트용 생성 요청 객체
    private CreateSystemReportRequest createTestRequest() {
        return CreateSystemReportRequest.newBuilder()
                .setUserId(TEST_USER_ID)
                .setSystemCategoryId(TEST_SYSTEM_CATEGORY_ID)
                .setTitle(TEST_TITLE)
                .setContent(TEST_CONTENT)
                .build();
    }

    @Test
    @DisplayName("성공: 시스템 신고 생성")
    void createSystemReport_success() {
        // Given
        CreateSystemReportRequest request = createTestRequest();

        // 이미 신고가 없다고 가정
        when(reportRepository.existsByUserIdAndTypeIdAndSystemCategoryId(
                eq(UUID.fromString(TEST_USER_ID)),
                eq(1),
                eq(TEST_SYSTEM_CATEGORY_ID)
        )).thenReturn(false);

        // 신고 저장 시, 저장된 객체에 id 및 createdAt값을 부여하도록 모의함
        Report savedReport = Report.builder()
                .id(GENERATED_REPORT_ID)
                .userId(UUID.fromString(TEST_USER_ID))
                .typeId(1)
                .systemCategoryId(TEST_SYSTEM_CATEGORY_ID)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .statusId(1)
                .build();
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        // When
        CreateSystemReportResponse response = createSystemReportService.createSystemReport(request);

        // Then
        assertNotNull(response);
        assertEquals(response.getReportId(), GENERATED_REPORT_ID.toString());
        verify(reportRepository).existsByUserIdAndTypeIdAndSystemCategoryId(
                eq(UUID.fromString(TEST_USER_ID)),
                eq(1),
                eq(TEST_SYSTEM_CATEGORY_ID)
        );
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("실패: 이미 등록된 시스템 신고 존재 시")
    void createSystemReport_duplicateReport() {
        // Given
        CreateSystemReportRequest request = createTestRequest();

        // 이미 신고가 존재한다고 가정
        when(reportRepository.existsByUserIdAndTypeIdAndSystemCategoryId(
                eq(UUID.fromString(TEST_USER_ID)),
                eq(1),
                eq(TEST_SYSTEM_CATEGORY_ID)
        )).thenReturn(true);

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                createSystemReportService.createSystemReport(request)
        );
        assertTrue(exception.getMessage().contains("You already have a system report for this system category"));
    }

    @Test
    @DisplayName("실패: 잘못된 userId 형식으로 인한 예외 발생")
    void createSystemReport_invalidUserId() {
        // Given: userId가 잘못된 포맷인 경우
        CreateSystemReportRequest request = CreateSystemReportRequest.newBuilder()
                .setUserId("invalid-uuid")
                .setSystemCategoryId(TEST_SYSTEM_CATEGORY_ID)
                .setTitle(TEST_TITLE)
                .setContent(TEST_CONTENT)
                .build();

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                createSystemReportService.createSystemReport(request)
        );
        assertTrue(ex.getMessage().contains("Invalid userId"));
    }
}
