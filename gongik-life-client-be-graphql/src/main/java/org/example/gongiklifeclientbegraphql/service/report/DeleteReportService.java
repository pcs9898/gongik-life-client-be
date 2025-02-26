package org.example.gongiklifeclientbegraphql.service.report;

import com.gongik.reportService.domain.service.ReportServiceGrpc;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.report.deleteReport.DeleteReportRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.deleteReport.DeleteReportResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteReportService {

    @GrpcClient("gongik-life-client-be-report-service")
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    public DeleteReportResponseDto deleteReport(@Valid DeleteReportRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("DeleteReportService",
                () -> DeleteReportResponseDto.fromDeleteReportResponseProto(
                        reportServiceBlockingStub.deleteReport(
                                requestDto.toDeleteReportRequestProto())
                ));
    }
}
