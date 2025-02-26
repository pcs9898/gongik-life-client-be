package org.example.gongiklifeclientbegraphql.dto.report.createSystemReport;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateSystemReportRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSystemReportRequestDto {


    private String userId;

    @Range(min = 1, max = 5)
    private Integer systemCategoryId;
  
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    public CreateSystemReportRequest toProto() {
        return CreateSystemReportRequest.newBuilder()
                .setUserId(userId)
                .setSystemCategoryId(systemCategoryId)
                .setTitle(title)
                .setContent(content)
                .build();
    }
}
