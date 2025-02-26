package org.example.gongiklifeclientbegraphql.service.report;


import com.gongik.reportService.domain.service.ReportServiceGrpc;
import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.report.myReports.MyReportsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.myReports.MyReportsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReportsServiceTest {

    @Mock
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    @InjectMocks
    private MyReportsService myReportsService;

    public static List<ReportServiceOuterClass.ReportForList> generateRandomReportForListData() {
        return List.of(
                createRandomReportForList(),
                createRandomReportForList(),
                createRandomReportForList()
        );
    }

    private static ReportServiceOuterClass.ReportForList createRandomReportForList() {
        return ReportServiceOuterClass.ReportForList.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTypeId((int) (Math.random() * 10))
                .setSystemCategoryId((int) (Math.random() * 10))
                .setTargetId(UUID.randomUUID().toString())
                .setStatusId((int) (Math.random() * 10))
                .setTitle("Random Title " + UUID.randomUUID())
                .setCreatedAt("2023-11-11T00:00:00Z")
                .build();
    }

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 통해 수동으로 주입합니다.
        ReflectionTestUtils.setField(myReportsService, "reportServiceBlockingStub", reportServiceBlockingStub);
    }

    @Test
    @DisplayName("MyReports 조회 성공")
    void myReports_Success() {
        // Given
        MyReportsRequestDto requestDto = createTestRequestDto();
        ReportServiceOuterClass.MyReportsRequest protoRequest = requestDto.toMyReportsRequestProto();


        // 더미 gRPC 응답 객체 생성 – 실제 응답에 필요한 필드 값들을 설정합니다.
        ReportServiceOuterClass.MyReportsResponse grpcResponse = ReportServiceOuterClass.MyReportsResponse.newBuilder()
                .addAllListReport(generateRandomReportForListData())
                .setPageInfo(ReportServiceOuterClass.PageInfo.newBuilder()
                        .setHasNextPage(true)
                        .setEndCursor("dummy-end-cursor")
                        .build())
                .build();

        when(reportServiceBlockingStub.myReports(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        MyReportsResponseDto responseDto = myReportsService.myReports(requestDto);

        // Then
        assertNotNull(responseDto);
        verify(reportServiceBlockingStub).myReports(eq(protoRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 MyReports 조회 예외 처리")
    void myReports_WhenGrpcError() {
        // Given
        MyReportsRequestDto requestDto = createTestRequestDto();

        when(reportServiceBlockingStub.myReports(any(ReportServiceOuterClass.MyReportsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                myReportsService.myReports(requestDto)
        );
        // ServiceExceptionHandlingUtil.handle()에서 "myReportsService" 메시지가 포함된다고 가정합니다.
        assertTrue(exception.getMessage().contains("MyReportsService"));
    }

    // 테스트용 MyReportsRequestDto 객체 생성 메서드
    private MyReportsRequestDto createTestRequestDto() {
        return MyReportsRequestDto.builder()
                .userId("test-user-id")
                // 필요한 다른 필드가 있다면 추가
                .build();
    }
}
