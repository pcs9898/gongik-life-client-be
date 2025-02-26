package org.example.gongiklifeclientbegraphql.service.report;


import com.gongik.reportService.domain.service.ReportServiceGrpc;
import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.report.deleteReport.DeleteReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.deleteReport.DeleteReportResponseDto;
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
class DeleteReportServiceTest {


    @Mock
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    @InjectMocks
    private DeleteReportService deleteReportService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 통해 수동으로 주입합니다.
        ReflectionTestUtils.setField(deleteReportService, "reportServiceBlockingStub", reportServiceBlockingStub);
    }

    @Test
    @DisplayName("신고 삭제 성공")
    void deleteReport_Success() {
        // Given
        DeleteReportRequestDto requestDto = createTestRequestDto();
        // DTO를 Proto 객체로 변환
        ReportServiceOuterClass.DeleteReportRequest protoRequest = requestDto.toDeleteReportRequestProto();

        // dummy gRPC 응답 객체 생성 – 실제 응답에 필요한 필드(예: id, message 등)를 설정합니다.
        ReportServiceOuterClass.DeleteReportResponse grpcResponse = ReportServiceOuterClass.DeleteReportResponse.newBuilder()
                .setReportId("dummy-id")
                .setSuccess(true)
                .build();

        when(reportServiceBlockingStub.deleteReport(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        DeleteReportResponseDto responseDto = deleteReportService.deleteReport(requestDto);

        // Then
        assertNotNull(responseDto);
        // 필요에 따라 responseDto의 필드 값을 추가로 검증할 수 있습니다.
        verify(reportServiceBlockingStub).deleteReport(eq(protoRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 신고 삭제 예외 처리")
    void deleteReport_WhenGrpcError() {
        // Given
        DeleteReportRequestDto requestDto = createTestRequestDto();

        when(reportServiceBlockingStub.deleteReport(any(ReportServiceOuterClass.DeleteReportRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                deleteReportService.deleteReport(requestDto)
        );
        // ServiceExceptionHandlingUtil.handle()에서 "deleteReportService"를 메시지에 포함시킨다고 가정합니다.
        assertTrue(exception.getMessage().contains("DeleteReportService"));
    }

    // 테스트용 DeleteReportRequestDto 객체 생성 메서드
    private DeleteReportRequestDto createTestRequestDto() {
        // 필요한 필드들을 채워서 DTO 객체를 생성합니다.
        return DeleteReportRequestDto.builder()
                .reportId("test-report-id")
                .userId("test-user-id")
                // 추가 필드가 있다면 설정
                .build();
    }
}