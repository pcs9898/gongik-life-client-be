package org.example.gongiklifeclientbegraphql.service.report;

import com.gongik.reportService.domain.service.ReportServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.report.myReports.MyReportsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.report.myReports.MyReportsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyReportsService {

    @GrpcClient("gongik-life-client-be-report-service")
    private ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;

    public MyReportsResponseDto myReports(MyReportsRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("MyReportsService",
                () -> MyReportsResponseDto.fromMyReportsResponseProto(
                        reportServiceBlockingStub.myReports(
                                requestDto.toMyReportsRequestProto())
                ));
    }
}
