package org.example.gongiklifeclientbegraphql.dto.report.myReports;


import com.gongik.reportService.domain.service.ReportServiceOuterClass.MyReportsResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyReportsResponseDto {

  private List<ReportForListDto> listReport;
  private PageInfoDto pageInfo;

  public static MyReportsResponseDto fromProto(MyReportsResponse myReportsResponseProto) {
    return MyReportsResponseDto.builder()
        .listReport(
            ReportForListDto.fromReportsResponseProto(myReportsResponseProto.getListReportList()))
        .pageInfo(PageInfoDto.fromReportServiceProto(myReportsResponseProto.getPageInfo()))
        .build();
  }

}
