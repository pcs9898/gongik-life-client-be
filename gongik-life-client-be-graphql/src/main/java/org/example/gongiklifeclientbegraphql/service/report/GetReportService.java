package org.example.gongiklifeclientbegraphql.service.report;

import com.gongik.reportService.domain.service.ReportServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.report.report.ReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.report.ReportResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetReportService {

    @GrpcClient("gongik-life-client-be-report-service")
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    public ReportResponseDto getReport(ReportRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("GetReportService",
                () -> ReportResponseDto.fromReportResponseProto(
                        reportServiceBlockingStub.report(
                                requestDto.toReportRequestProto())
                ));
    }
}
