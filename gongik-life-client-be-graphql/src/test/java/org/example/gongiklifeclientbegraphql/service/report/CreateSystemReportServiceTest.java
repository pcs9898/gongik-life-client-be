package org.example.gongiklifeclientbegraphql.service.report;

import com.gongik.reportService.domain.service.ReportServiceGrpc;
import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.report.createSystemReport.CreateSystemReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.createSystemReport.CreateSystemReportResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSystemReportServiceTest {


    @Mock
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    @InjectMocks
    private CreateSystemReportService createSystemReportService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 통해 수동으로 주입합니다.
        ReflectionTestUtils.setField(createSystemReportService, "reportServiceBlockingStub", reportServiceBlockingStub);
    }

    @Test
    @DisplayName("시스템 신고 생성 성공")
    void createSystemReport_Success() {
        // Given
        CreateSystemReportRequestDto requestDto = createTestRequestDto();
        // DTO를 Proto 객체로 변환
        ReportServiceOuterClass.CreateSystemReportRequest protoRequest = requestDto.toCreateSystemReportRequestProto();

        // dummy gRPC 응답 객체 생성 – 실제 응답에 필요한 필드 값(예: id, message 등)을 설정합니다.
        ReportServiceOuterClass.CreateSystemReportResponse grpcResponse = ReportServiceOuterClass.CreateSystemReportResponse.newBuilder()
                .setReportId("dummy-report-id")
                .build();

        when(reportServiceBlockingStub.createSystemReport(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        CreateSystemReportResponseDto responseDto = createSystemReportService.createSystemReport(requestDto);

        // Then
        assertNotNull(responseDto);
        // 필요에 따라 responseDto의 필드 값들을 추가 검증할 수 있습니다.
        verify(reportServiceBlockingStub).createSystemReport(eq(protoRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 시스템 신고 생성 예외 처리")
    void createSystemReport_WhenGrpcError() {
        // Given
        CreateSystemReportRequestDto requestDto = createTestRequestDto();

        when(reportServiceBlockingStub.createSystemReport(any(ReportServiceOuterClass.CreateSystemReportRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then: ServiceExceptionHandlingUtil.handle() 내부에서 발생한 예외를 RuntimeException으로 포장했다고 가정합니다.
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                createSystemReportService.createSystemReport(requestDto));
        assertTrue(exception.getMessage().contains("CreateSystemReportService"));
    }

    // 테스트용 CreateSystemReportRequestDto 객체 생성 메서드
    private CreateSystemReportRequestDto createTestRequestDto() {
        // DTO 빌더를 이용해 필요한 값들을 설정합니다.
        return CreateSystemReportRequestDto.builder()
                .userId("test-user-id")
                .systemCategoryId(1)
                .title("테스트 시스템 신고 제목")
                .content("테스트 시스템 신고 내용")
                // 추가 필드가 있다면 설정
                .build();
    }
}