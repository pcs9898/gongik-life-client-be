package org.example.gongiklifeclientbegraphql.service.report;

import com.gongik.reportService.domain.service.ReportServiceGrpc;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.report.createReport.CreateReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.createReport.CreateReportResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateReportService {

    @GrpcClient("gongik-life-client-be-report-service")
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;
    
    public CreateReportResponseDto createReport(@Valid CreateReportRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("createReportService",
                () -> CreateReportResponseDto.fromCreateReportResponseProto(
                        reportServiceBlockingStub.createReport(
                                requestDto.toCreateReportRequestProto())
                ));
    }
}
