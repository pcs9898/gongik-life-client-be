package org.example.gongiklifeclientbereportservice.service;

import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbereportservice.dto.ReportProjection;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyReportsService {


    private final ReportRepository reportRepository;

    /**
     * MyReportsRequest를 기반으로 페이징 처리된 신고 목록 응답을 반환한다.
     *
     * @param request MyReportsRequest 객체 (사용자 ID, 커서, 페이지 사이즈 등 포함)
     * @return MyReportsResponse gRPC 응답 객체
     */
    public ReportServiceOuterClass.MyReportsResponse myReports(ReportServiceOuterClass.MyReportsRequest request) {
        List<ReportProjection> reportProjections = getReportProjections(request);

        List<ReportServiceOuterClass.ReportForList> reportForListItems = mapToReportForList(reportProjections);

        ReportServiceOuterClass.PageInfo pageInfo = buildPageInfo(reportProjections, request.getPageSize());

        return ReportServiceOuterClass.MyReportsResponse.newBuilder()
                .addAllListReport(reportForListItems)
                .setPageInfo(pageInfo)
                .build();
    }

    /**
     * ReportRepository를 통해 사용자 신고 목록을 조회한다.
     *
     * @param request MyReportsRequest 객체
     * @return 조회된 ReportProjection 목록
     */
    private List<ReportProjection> getReportProjections(ReportServiceOuterClass.MyReportsRequest request) {
        return reportRepository.myReportsWithCursor(
                request.getUserId(),
                request.hasCursor() ? request.getCursor() : null,
                request.getPageSize()
        );
    }

    /**
     * ReportProjection 리스트를 ReportForList gRPC 객체 리스트로 변환한다.
     *
     * @param reports ReportProjection 리스트
     * @return ReportForList 객체 리스트
     */
    private List<ReportServiceOuterClass.ReportForList> mapToReportForList(List<ReportProjection> reports) {
        return reports.stream()
                .map(this::convertToReportForList)
                .toList();
    }

    /**
     * 단일 ReportProjection을 ReportForList gRPC 객체로 변환한다.
     *
     * @param report ReportProjection 객체
     * @return ReportForList 객체
     */
    private ReportServiceOuterClass.ReportForList convertToReportForList(ReportProjection report) {
        ReportServiceOuterClass.ReportForList.Builder builder = ReportServiceOuterClass.ReportForList.newBuilder()
                .setId(report.getId())
                .setTypeId(report.getTypeId())
                .setStatusId(report.getStatusId())
                .setTitle(report.getTitle())
                .setCreatedAt(report.getCreatedAt());

        if (report.getTargetId() != null) {
            builder.setTargetId(report.getTargetId());
        }
        if (report.getSystemCategoryId() != null) {
            builder.setSystemCategoryId(report.getSystemCategoryId());
        }
        return builder.build();
    }

    /**
     * 페이징 정보를 생성한다.
     *
     * @param reports  조회된 ReportProjection 리스트
     * @param pageSize 페이지 사이즈
     * @return PageInfo gRPC 객체
     */
    private ReportServiceOuterClass.PageInfo buildPageInfo(List<ReportProjection> reports, int pageSize) {
        ReportServiceOuterClass.PageInfo.Builder pageInfoBuilder = ReportServiceOuterClass.PageInfo.newBuilder()
                .setHasNextPage(reports.size() == pageSize);
        if (!reports.isEmpty()) {
            pageInfoBuilder.setEndCursor(reports.get(reports.size() - 1).getId());
        }
        return pageInfoBuilder.build();
    }
}