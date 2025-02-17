package org.example.gongiklifeclientbegraphql.dto.report.report;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.ReportRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {


  private String userId;
  
  @NotNull
  @NotBlank
  private String reportId;

  public ReportRequest toProto() {
    return ReportRequest.newBuilder()
        .setUserId(userId)
        .setReportId(reportId)
        .build();
  }
}
