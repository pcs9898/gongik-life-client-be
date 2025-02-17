package org.example.gongiklifeclientbereportservice.consumer;

import dto.report.CreateSystemReportRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbereportservice.service.ReportService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateSystemReportConsumer {

  private final ReportService reportService;

  @KafkaListener(topics = "create-system-report-topic")
  public void consume(CreateSystemReportRequestDto requestDto) {
    try {
      log.info("CreateSystemReportRequestDto: {}", requestDto);
      reportService.createSystemReport(requestDto);
    } catch (Exception e) {
      log.error("Error processing create system report: {}", requestDto, e);
      throw e; // 트랜잭션 롤백을 위해 예외 재발생
    }
  }

}
