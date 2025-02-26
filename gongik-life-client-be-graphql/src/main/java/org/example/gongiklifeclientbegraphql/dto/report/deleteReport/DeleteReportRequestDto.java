package org.example.gongiklifeclientbegraphql.dto.report.deleteReport;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.DeleteReportRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteReportRequestDto {

    private String userId;
  
    @NotBlank
    private String reportId;

    public DeleteReportRequest toProto() {
        return DeleteReportRequest.newBuilder()
                .setUserId(userId)
                .setReportId(reportId)
                .build();
    }

}
