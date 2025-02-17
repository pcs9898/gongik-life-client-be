package org.example.gongiklifeclientbegraphql.service;

import dto.report.CreateSystemReportRequestDto;
import lombok.RequiredArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.report.createSystemReport.CreateSystemReportResponseDto;
import org.example.gongiklifeclientbegraphql.producer.report.CreateSystemReportProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final CreateSystemReportProducer createSystemReportProducer;

  public CreateSystemReportResponseDto createSystemReport(CreateSystemReportRequestDto requestDto) {

    createSystemReportProducer.sendCreateSystemReportRequest(requestDto);

    return CreateSystemReportResponseDto.builder()
        .success(true)
        .build();
  }
}

