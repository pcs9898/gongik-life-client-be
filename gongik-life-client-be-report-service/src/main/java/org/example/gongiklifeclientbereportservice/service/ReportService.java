package org.example.gongiklifeclientbereportservice.service;

import dto.report.CreateSystemReportRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;

  public void createSystemReport(CreateSystemReportRequestDto requestDto) {
    reportRepository.save(Report.builder()
        .userId(UUID.fromString(requestDto.getUserId()))
        .typeId(1)
        .systemCategoryId(requestDto.getSystemCategoryId())
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .statusId(1)
        .build());
  }
}
