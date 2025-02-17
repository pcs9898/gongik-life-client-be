package org.example.gongiklifeclientbegraphql.controller;

import dto.report.CreateSystemReportRequestDto;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.report.createSystemReport.CreateSystemReportResponseDto;
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


}
