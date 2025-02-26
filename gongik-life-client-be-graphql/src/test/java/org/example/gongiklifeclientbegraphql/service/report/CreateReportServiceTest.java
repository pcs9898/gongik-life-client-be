package org.example.gongiklifeclientbegraphql.service.report;


import com.gongik.reportService.domain.service.ReportServiceGrpc;
import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.report.createReport.CreateReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.createReport.CreateReportResponseDto;
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
class CreateReportServiceTest {


    @Mock
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    @InjectMocks
    private CreateReportService createReportService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 사용해 수동으로 주입합니다.
        ReflectionTestUtils.setField(createReportService, "reportServiceBlockingStub", reportServiceBlockingStub);
    }

    @Test
    @DisplayName("신고 생성 성공")
    void createReport_Success() {
        // Given
        CreateReportRequestDto requestDto = createTestRequestDto();
        // DTO를 Proto 객체로 변환
        ReportServiceOuterClass.CreateReportRequest protoRequest = requestDto.toCreateReportRequestProto();

        // dummy gRPC 응답 객체 생성 – 실제 응답에 필요한 필드 값(예: id, detail 등)을 설정합니다.
        ReportServiceOuterClass.CreateReportResponse grpcResponse = ReportServiceOuterClass.CreateReportResponse.newBuilder()
                .setReportId("dummy-id")
                .build();

        when(reportServiceBlockingStub.createReport(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        CreateReportResponseDto responseDto = createReportService.createReport(requestDto);

        // Then
        assertNotNull(responseDto);
        // 추가로 responseDto 내부의 필드 값에 대한 검증도 가능합니다.
        verify(reportServiceBlockingStub).createReport(eq(protoRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 신고 생성 예외 처리")
    void createReport_WhenGrpcError() {
        // Given
        CreateReportRequestDto requestDto = createTestRequestDto();

        when(reportServiceBlockingStub.createReport(any(ReportServiceOuterClass.CreateReportRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                createReportService.createReport(requestDto)
        );
        // ServiceExceptionHandlingUtil.handle()에서 "createReportService"를 메시지에 포함시킨다고 가정합니다.
        assertTrue(exception.getMessage().contains("CreateReportService"));
    }

    // 테스트용 CreateReportRequestDto 객체 생성 메서드
    private CreateReportRequestDto createTestRequestDto() {
        // 필요한 필드들을 채워서 DTO 객체를 생성합니다.
        return CreateReportRequestDto.builder()
                .reportTypeId(2)
                .userId("test-user-id")
                .title("Test Title")
                .content("Test Content")
                .targetId("test-target-id")


                // 추가 필드가 있다면 설정
                .build();
    }
}