package org.example.gongiklifeclientbegraphql.dto.report.report;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.ReportResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {

  private String id;
  private Integer typeId;
  private Integer systemCategoryId;
  private String targetId;
  private Integer statusId;
  private String title;
  private String content;
  private String createdAt;

  public static ReportResponseDto fromProto(ReportResponse reportResponse) {
    return ReportResponseDto.builder()
        .id(reportResponse.getId())
        .typeId(reportResponse.getTypeId())
        .systemCategoryId(
            reportResponse.hasSystemCategoryId() ? reportResponse.getSystemCategoryId() : null)
        .targetId(reportResponse.hasTargetId() ? reportResponse.getTargetId() : null)
        .statusId(reportResponse.getStatusId())
        .title(reportResponse.getTitle())
        .content(reportResponse.getContent())
        .createdAt(reportResponse.getCreatedAt())
        .build();
  }

}
