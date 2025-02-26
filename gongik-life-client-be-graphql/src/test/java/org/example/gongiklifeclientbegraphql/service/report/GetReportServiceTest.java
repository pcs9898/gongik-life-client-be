package org.example.gongiklifeclientbegraphql.service.report;


import com.gongik.reportService.domain.service.ReportServiceGrpc;
import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.report.report.ReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.report.ReportResponseDto;
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
class GetReportServiceTest {


    @Mock
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    @InjectMocks
    private GetReportService getReportService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 통해 수동으로 주입합니다.
        ReflectionTestUtils.setField(getReportService, "reportServiceBlockingStub", reportServiceBlockingStub);
    }

    @Test
    @DisplayName("신고 조회 성공")
    void getReport_Success() {
        // Given
        ReportRequestDto requestDto = createTestRequestDto();
        ReportServiceOuterClass.ReportRequest protoRequest = requestDto.toReportRequestProto();

        // 더미 gRPC 응답 객체 생성 (실제 응답에 필요한 필드들을 설정)
        ReportServiceOuterClass.ReportResponse grpcResponse = ReportServiceOuterClass.ReportResponse.newBuilder()
                .setId("dummy-id")
                .setContent("테스트 신고 내용")
                .build();

        when(reportServiceBlockingStub.report(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        ReportResponseDto responseDto = getReportService.getReport(requestDto);

        // Then
        assertNotNull(responseDto);
        verify(reportServiceBlockingStub).report(eq(protoRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 신고 조회 예외 처리")
    void getReport_WhenGrpcError() {
        // Given
        ReportRequestDto requestDto = createTestRequestDto();

        when(reportServiceBlockingStub.report(any(ReportServiceOuterClass.ReportRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                getReportService.getReport(requestDto)
        );
        // ServiceExceptionHandlingUtil.handle()에서 "getReportService"를 메시지에 포함시키도록 구현했다고 가정합니다.
        assertTrue(exception.getMessage().contains("GetReportService"));
    }

    // 테스트용 ReportRequestDto 객체 생성 메서드
    private ReportRequestDto createTestRequestDto() {
        return ReportRequestDto.builder()
                .reportId("test-report-id")
                .userId("test-user-id")
                // 필요한 다른 필드가 있다면 추가
                .build();
    }
}