package org.example.gongiklifeclientbegraphql.dto.report.deleteReport;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.DeleteReportResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteReportResponseDto {

    private String reportId;
    private boolean success;

    public static DeleteReportResponseDto fromDeleteReportResponseProto(DeleteReportResponse response) {
        return DeleteReportResponseDto.builder()
                .reportId(response.getReportId())
                .success(response.getSuccess())
                .build();
    }


}
