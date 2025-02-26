package org.example.gongiklifeclientbegraphql.dto.report.myReports;


import com.gongik.reportService.domain.service.ReportServiceOuterClass.MyReportsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyReportsResponseDto {

    private List<ReportForListDto> listReport;
    private PageInfoDto pageInfo;

    public static MyReportsResponseDto fromMyReportsResponseProto(MyReportsResponse myReportsResponseProto) {
        return MyReportsResponseDto.builder()
                .listReport(
                        ReportForListDto.fromReportsResponseProto(myReportsResponseProto.getListReportList()))
                .pageInfo(PageInfoDto.fromReportServiceProto(myReportsResponseProto.getPageInfo()))
                .build();
    }

}
