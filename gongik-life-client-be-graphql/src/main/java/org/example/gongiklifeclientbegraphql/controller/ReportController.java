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
import org.example.gongiklifeclientbegraphql.service.ReportService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReportController {

  private final ReportService reportService;

  @MutationMapping
  public CreateSystemReportResponseDto createSystemReport(
      @Argument("createSystemReportInput") @Valid CreateSystemReportRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return reportService.createSystemReport(requestDto);

    } catch (Exception e) {
      log.error("Failed to create system report", e);
      throw e;
    }
  }

  @MutationMapping
  public CreateReportResponseDto createReport(
      @Argument("createReportInput") @Valid CreateReportRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return reportService.createReport(requestDto);

    } catch (Exception e) {
      log.error("Failed to create report", e);
      throw e;
    }
  }

  @MutationMapping
  public DeleteReportResponseDto deleteReport(
      @Argument("deleteReportInput") @Valid DeleteReportRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return reportService.deleteReport(requestDto);

    } catch (Exception e) {
      log.error("Failed to delete report", e);
      throw e;
    }
  }

}
