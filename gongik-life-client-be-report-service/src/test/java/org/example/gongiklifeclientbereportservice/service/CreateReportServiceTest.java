package org.example.gongiklifeclientbereportservice.service;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass;
import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateReportServiceTest {

    private static final String VALID_USER_ID = "11111111-1111-1111-1111-111111111111";
    private static final String VALID_TARGET_ID = "22222222-2222-2222-2222-222222222222";
    private static final int REPORT_TYPE_POST = 4;  // POST 타입
    private static final int REPORT_TYPE_INSTITUTION = 2;  // 기관 타입
    private static final String TEST_TITLE = "Test Title";
    private static final String TEST_CONTENT = "Test Content";

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @Mock
    private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

    @InjectMocks
    private CreateReportService createReportService;


    // 테스트 요청 객체 생성 헬퍼 메서드
    private ReportServiceOuterClass.CreateReportRequest createTestRequest(int reportTypeId, String userId, String targetId) {
        return ReportServiceOuterClass.CreateReportRequest.newBuilder()
                .setReportTypeId(reportTypeId)
                .setUserId(userId)
                .setTargetId(targetId)
                .setTitle(TEST_TITLE)
                .setContent(TEST_CONTENT)
                .build();
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(createReportService,
                "communityServiceBlockingStub", communityServiceBlockingStub);

        ReflectionTestUtils.setField(createReportService,
                "institutionBlockingStub", institutionBlockingStub);
    }

    @Test
    @DisplayName("성공: 신고 생성 - 대상 존재하고 중복 신고 없음")
    void createReport_success() {
        // REPORT_TYPE_POST 선택 시 communityServiceBlockingStub.existsPost 호출됨
        ReportServiceOuterClass.CreateReportRequest request = createTestRequest(REPORT_TYPE_POST, VALID_USER_ID, VALID_TARGET_ID);

        CommunityServiceOuterClass.ExistsPostRequest existsPostRequest = CommunityServiceOuterClass.ExistsPostRequest.newBuilder()
                .setPostId(VALID_TARGET_ID)
                .build();
        CommunityServiceOuterClass.ExistsPostResponse existsPostResponse = CommunityServiceOuterClass.ExistsPostResponse.newBuilder()
                .setExists(true)
                .build();
        when(communityServiceBlockingStub.existsPost(eq(existsPostRequest))).thenReturn(existsPostResponse);

        // 중복 신고 없음
        when(reportRepository.existsByUserIdAndTargetId(eq(UUID.fromString(VALID_USER_ID)), eq(UUID.fromString(VALID_TARGET_ID))))
                .thenReturn(false);

        // Repository save 시 더미 Report 엔티티 반환
        Report dummyReport = Report.builder()
                .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                .userId(UUID.fromString(VALID_USER_ID))
                .typeId(REPORT_TYPE_POST)
                .targetId(UUID.fromString(VALID_TARGET_ID))
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .statusId(1)
                .build();
        when(reportRepository.save(any(Report.class))).thenReturn(dummyReport);

        // When
        ReportServiceOuterClass.CreateReportResponse response = createReportService.createReport(request);

        // Then
        assertNotNull(response);
        assertEquals(dummyReport.getId().toString(), response.getReportId());
        verify(communityServiceBlockingStub).existsPost(eq(existsPostRequest));
        verify(reportRepository).existsByUserIdAndTargetId(eq(UUID.fromString(VALID_USER_ID)), eq(UUID.fromString(VALID_TARGET_ID)));
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("실패: 신고 생성 - 대상 존재하지 않음 (기관 타입)")
    void createReport_targetNotFound() {
        // 기관 타입 (REPORT_TYPE_INSTITUTION)의 대상이 존재하지 않을 경우
        ReportServiceOuterClass.CreateReportRequest request = createTestRequest(REPORT_TYPE_INSTITUTION, VALID_USER_ID, VALID_TARGET_ID);

        InstitutionServiceOuterClass.ExistsInstitutionRequest existsInstitutionRequest = InstitutionServiceOuterClass.ExistsInstitutionRequest.newBuilder()
                .setInstitutionId(VALID_TARGET_ID)
                .build();
        InstitutionServiceOuterClass.ExistsInstitutionResponse existsInstitutionResponse = InstitutionServiceOuterClass.ExistsInstitutionResponse.newBuilder()
                .setExists(false)
                .build();
        when(institutionBlockingStub.existsInstitution(eq(existsInstitutionRequest))).thenReturn(existsInstitutionResponse);

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            createReportService.createReport(request);
        });
//        assertTrue(exception.getMessage().contains("Institution not found"));
        verify(institutionBlockingStub).existsInstitution(eq(existsInstitutionRequest));
    }

    @Test
    @DisplayName("실패: 신고 생성 - 중복 신고 존재")
    void createReport_duplicateReport() {
        // REPORT_TYPE_POST 선택 시, 중복 신고가 이미 존재하는 경우
        ReportServiceOuterClass.CreateReportRequest request = createTestRequest(REPORT_TYPE_POST, VALID_USER_ID, VALID_TARGET_ID);

        CommunityServiceOuterClass.ExistsPostRequest existsPostRequest = CommunityServiceOuterClass.ExistsPostRequest.newBuilder()
                .setPostId(VALID_TARGET_ID)
                .build();
        CommunityServiceOuterClass.ExistsPostResponse existsPostResponse = CommunityServiceOuterClass.ExistsPostResponse.newBuilder()
                .setExists(true)
                .build();
        when(communityServiceBlockingStub.existsPost(eq(existsPostRequest))).thenReturn(existsPostResponse);

        // 중복 신고가 있다고 가정
        when(reportRepository.existsByUserIdAndTargetId(eq(UUID.fromString(VALID_USER_ID)), eq(UUID.fromString(VALID_TARGET_ID))))
                .thenReturn(true);

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            createReportService.createReport(request);
        });
        assertTrue(exception.getMessage().contains("You already reported this target"));
        verify(communityServiceBlockingStub).existsPost(eq(existsPostRequest));
        verify(reportRepository).existsByUserIdAndTargetId(eq(UUID.fromString(VALID_USER_ID)), eq(UUID.fromString(VALID_TARGET_ID)));
    }
}
