package org.example.gongiklifeclientbegraphql.dto.report.createReport;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.CreateReportResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportResponseDto {

    private String reportId;


    public static CreateReportResponseDto fromCreateReportResponseProto(CreateReportResponse report) {
        return CreateReportResponseDto.builder()
                .reportId(report.getReportId())
                .build();
    }
}
