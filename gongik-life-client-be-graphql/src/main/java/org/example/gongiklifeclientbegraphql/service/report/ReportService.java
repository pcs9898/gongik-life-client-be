package org.example.gongiklifeclientbegraphql.service.report;

import com.gongik.reportService.domain.service.ReportServiceGrpc;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.report.createReport.CreateReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.createReport.CreateReportResponseDto;
import org.example.gongiklifeclientbegraphql.dto.report.createSystemReport.CreateSystemReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.createSystemReport.CreateSystemReportResponseDto;
import org.example.gongiklifeclientbegraphql.dto.report.deleteReport.DeleteReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.deleteReport.DeleteReportResponseDto;
import org.example.gongiklifeclientbegraphql.dto.report.myReports.MyReportsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.myReports.MyReportsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.report.report.ReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.report.ReportResponseDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {


    @GrpcClient("gongik-life-client-be-report-service")
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    public CreateSystemReportResponseDto createSystemReport(CreateSystemReportRequestDto requestDto) {
        try {

            return CreateSystemReportResponseDto.fromProto(
                    reportServiceBlockingStub.createSystemReport(
                            requestDto.toProto()
                    )
            );

        } catch (Exception e) {
            log.error("Failed to create system report", e);

            throw e;
        }

    }

    public CreateReportResponseDto createReport(@Valid CreateReportRequestDto requestDto) {
        try {

            return CreateReportResponseDto.fromProto(
                    reportServiceBlockingStub.createReport(
                            requestDto.toProto()
                    )
            );
        } catch (Exception e) {
            log.error("Failed to create report", e);
            throw e;
        }
    }

    public DeleteReportResponseDto deleteReport(@Valid DeleteReportRequestDto requestDto) {
        try {

            return DeleteReportResponseDto.fromProto(
                    reportServiceBlockingStub.deleteReport(
                            requestDto.toProto()
                    )
            );
        } catch (Exception e) {
            log.error("Failed to delete report", e);
            throw e;
        }
    }

    public ReportResponseDto getReport(ReportRequestDto requestDto) {
        try {

            return ReportResponseDto.fromProto(
                    reportServiceBlockingStub.report(
                            requestDto.toProto()
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get report", e);
            throw e;
        }
    }

    public MyReportsResponseDto myReports(MyReportsRequestDto requestDto) {
        try {

            return MyReportsResponseDto.fromProto(
                    reportServiceBlockingStub.myReports(
                            requestDto.toProto()
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get my reports", e);
            throw e;
        }
    }
}
