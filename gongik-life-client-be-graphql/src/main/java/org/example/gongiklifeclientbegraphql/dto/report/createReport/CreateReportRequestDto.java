package org.example.gongiklifeclientbegraphql.dto.report.createReport;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateReportRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequestDto {

  private String userId;

  @Range(min = 2, max = 5)
  private Integer reportTypeId;

  @NotNull
  @NotBlank
  private String title;

  @NotNull
  @NotBlank
  private String content;

  private String targetId;

  public CreateReportRequest toProto() {
    return CreateReportRequest.newBuilder()
        .setUserId(userId)
        .setReportTypeId(reportTypeId)
        .setTitle(title)
        .setContent(content)
        .setTargetId(targetId)
        .build();
  }
}
