package org.example.gongiklifeclientbegraphql.dto.report.report;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.ReportRequest;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String reportId;

    public ReportRequest toReportRequestProto() {
        return ReportRequest.newBuilder()
                .setUserId(userId)
                .setReportId(reportId)
                .build();
    }
}
