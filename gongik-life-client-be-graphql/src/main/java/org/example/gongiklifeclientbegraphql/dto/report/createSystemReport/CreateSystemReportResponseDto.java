package org.example.gongiklifeclientbegraphql.dto.report.createSystemReport;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateSystemReportResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSystemReportResponseDto {

  private String reportId;

  public static CreateSystemReportResponseDto fromProto(
      CreateSystemReportResponse createSystemReportResponse) {
    return CreateSystemReportResponseDto.builder()
        .reportId(createSystemReportResponse.getReportId())
        .build();
  }

}
