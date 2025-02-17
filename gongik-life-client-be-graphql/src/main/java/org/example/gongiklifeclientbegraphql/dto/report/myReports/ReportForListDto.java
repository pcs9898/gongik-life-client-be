package org.example.gongiklifeclientbegraphql.dto.report.myReports;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.ReportForList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportForListDto {

  private String id;
  private Integer typeId;
  private Integer systemCategoryId;
  private String targetId;
  private Integer statusId;
  private String title;
  private String createdAt;

  public static ReportForListDto fromProto(ReportForList reportResponse) {
    return ReportForListDto.builder()
        .id(reportResponse.getId())
        .typeId(reportResponse.getTypeId())
        .systemCategoryId(
            reportResponse.hasSystemCategoryId() ? reportResponse.getSystemCategoryId() : null)
        .targetId(reportResponse.hasTargetId() ? reportResponse.getTargetId() : null)
        .statusId(reportResponse.getStatusId())
        .title(reportResponse.getTitle())
        .createdAt(reportResponse.getCreatedAt())
        .build();
  }

  public static List<ReportForListDto> fromReportsResponseProto(
      List<ReportForList> listReportList) {
    return listReportList.stream()
        .map(ReportForListDto::fromProto)
        .toList();
  }
}
