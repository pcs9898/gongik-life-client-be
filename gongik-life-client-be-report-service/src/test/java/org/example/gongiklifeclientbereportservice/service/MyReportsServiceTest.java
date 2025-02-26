package org.example.gongiklifeclientbereportservice.service;

import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import org.example.gongiklifeclientbereportservice.dto.ReportProjection;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReportsServiceTest {

    private static final String USER_ID = "11111111-1111-1111-1111-111111111111";
    private static final int PAGE_SIZE = 2;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private MyReportsService myReportsService;

    /**
     * 성공 케이스: ReportRepository가 제공한 ReportProjection 목록을 기반으로
     * gRPC 응답(MyReportsResponse)이 올바르게 생성됨을 검증한다.
     */
    @Test
    @DisplayName("성공: MyReports 응답 생성")
    void myReports_success() {
        // Given: MyReportsRequest 생성 (cursor 없음)
        ReportServiceOuterClass.MyReportsRequest request =
                ReportServiceOuterClass.MyReportsRequest.newBuilder()
                        .setUserId(USER_ID)
                        .setPageSize(PAGE_SIZE)
                        .build();

        // ReportProjection은 Repository가 조회하여 반환하는 projection 객체로 가정
        ReportProjection rp1 = new ReportProjection() {
            @Override
            public String getId() {
                return "report1";
            }

            @Override
            public Integer getTypeId() {
                return 4;
            }

            @Override
            public Integer getStatusId() {
                return 1;
            }

            @Override
            public String getTitle() {
                return "Title1";
            }

            @Override
            public String getCreatedAt() {
                return "2025-02-26T10:00:00";
            }

            @Override
            public String getTargetId() {
                return "target1";
            }

            @Override
            public Integer getSystemCategoryId() {
                return 5;
            }
        };

        ReportProjection rp2 = new ReportProjection() {
            @Override
            public String getId() {
                return "report2";
            }

            @Override
            public Integer getTypeId() {
                return 4;
            }

            @Override
            public Integer getStatusId() {
                return 2;
            }

            @Override
            public String getTitle() {
                return "Title2";
            }

            @Override
            public String getCreatedAt() {
                return "2025-02-26T12:00:00";
            }

            @Override
            public String getTargetId() {
                return null;
            }

            @Override
            public Integer getSystemCategoryId() {
                return 5;
            }
        };

        List<ReportProjection> projections = List.of(rp1, rp2);
        when(reportRepository.myReportsWithCursor(eq(USER_ID), eq(null), eq(PAGE_SIZE))).thenReturn(projections);

        // When: MyReportsService.myReports() 호출
        ReportServiceOuterClass.MyReportsResponse response = myReportsService.myReports(request);

        // Then: 응답 객체 검증
        assertNotNull(response);
        assertEquals(2, response.getListReportCount());
        // 응답에 포함된 각 ReportForList의 필드 값이 projection의 값과 일치하는지 검증
        ReportServiceOuterClass.ReportForList report1 = response.getListReport(0);
        assertEquals("report1", report1.getId());
        assertEquals(4, report1.getTypeId());
        assertEquals(1, report1.getStatusId());
        assertEquals("Title1", report1.getTitle());
        assertEquals("2025-02-26T10:00:00", report1.getCreatedAt());
        assertEquals("target1", report1.getTargetId());
        assertEquals(5, report1.getSystemCategoryId());

        ReportServiceOuterClass.ReportForList report2 = response.getListReport(1);
        assertEquals("report2", report2.getId());
        assertEquals("2025-02-26T12:00:00", report2.getCreatedAt());
        // report2의 선택적 필드가 null이면 기본값(빈 문자열)이 설정되었을 수 있으므로 검증
        assertTrue(report2.getTargetId().isEmpty());

        // 페이징 정보 검증: endCursor는 마지막 element의 id, hasNextPage는 (목록 크기 == pageSize)로 설정됨
        assertEquals("report2", response.getPageInfo().getEndCursor());
        assertTrue(response.getPageInfo().getHasNextPage());
    }

    /**
     * 성공 케이스: 조회 결과가 없는 경우, 빈 응답과 올바른 페이지정보(hasNextPage=false)를 반환함.
     */
    @Test
    @DisplayName("성공: MyReports 응답 - 빈 목록")
    void myReports_empty() {
        // Given: 빈 ReportProjection 목록 반환
        ReportServiceOuterClass.MyReportsRequest request =
                ReportServiceOuterClass.MyReportsRequest.newBuilder()
                        .setUserId(USER_ID)
                        .setPageSize(PAGE_SIZE)
                        .build();
        when(reportRepository.myReportsWithCursor(eq(USER_ID), eq(null), eq(PAGE_SIZE)))
                .thenReturn(List.of());

        // When
        ReportServiceOuterClass.MyReportsResponse response = myReportsService.myReports(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getListReportCount());
        // 빈 목록인 경우, hasNextPage는 false로 설정된다.
        assertFalse(response.getPageInfo().getHasNextPage());
        // endCursor가 설정되지 않았다면 기본값(빈 문자열)이어야 함
        assertEquals("", response.getPageInfo().getEndCursor());
    }
}
