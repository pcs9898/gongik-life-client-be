package org.example.gongiklifeclientbegraphql.dto.report.createReport;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateReportRequest;
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
public class CreateReportRequestDto {

    private String userId;

    @Range(min = 2, max = 5)
    private Integer reportTypeId;

    @NotBlank
    private String title;
    
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
