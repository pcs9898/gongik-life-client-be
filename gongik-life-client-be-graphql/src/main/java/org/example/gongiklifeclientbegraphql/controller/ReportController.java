package org.example.gongiklifeclientbegraphql.controller;

import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.example.gongiklifeclientbegraphql.service.report.*;
import org.example.gongiklifeclientbegraphql.util.ControllerExceptionHandlingUtil;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;
    private final CreateSystemReportService createSystemReportService;
    private final CreateReportService createReportService;
    private final DeleteReportService deleteReportService;
    private final GetReportService getReportService;
    private final MyReportsService myReportsService;

    @MutationMapping
    public CreateSystemReportResponseDto createSystemReport(
            @Argument("createSystemReportInput") @Valid CreateSystemReportRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return createSystemReportService.createSystemReport(requestDto);
        });
    }

    @MutationMapping
    public CreateReportResponseDto createReport(
            @Argument("createReportInput") @Valid CreateReportRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return createReportService.createReport(requestDto);
        });
    }

    @MutationMapping
    public DeleteReportResponseDto deleteReport(
            @Argument("deleteReportInput") @Valid DeleteReportRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return deleteReportService.deleteReport(requestDto);
        });
    }

    @QueryMapping
    public ReportResponseDto report(
            @Argument("reportInput") @Valid ReportRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return getReportService.getReport(requestDto);
        });
    }

    @QueryMapping
    public MyReportsResponseDto myReports(
            @Argument("myReportsFilter") @Valid MyReportsRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return myReportsService.myReports(requestDto);
        });
    }

}
