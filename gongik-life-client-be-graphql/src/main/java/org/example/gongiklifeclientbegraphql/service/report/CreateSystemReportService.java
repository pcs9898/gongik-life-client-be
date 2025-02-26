package org.example.gongiklifeclientbegraphql.service.report;

import com.gongik.reportService.domain.service.ReportServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.report.createSystemReport.CreateSystemReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.createSystemReport.CreateSystemReportResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSystemReportService {

    @GrpcClient("gongik-life-client-be-report-service")
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    public CreateSystemReportResponseDto createSystemReport(CreateSystemReportRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("CreateSystemReportService",
                () -> CreateSystemReportResponseDto.fromCreateSystemReportResponseProto(
                        reportServiceBlockingStub.createSystemReport(
                                requestDto.toCreateSystemReportRequestProto())
                ));
    }
}
