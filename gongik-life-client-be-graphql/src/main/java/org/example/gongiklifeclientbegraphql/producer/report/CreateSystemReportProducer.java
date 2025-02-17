package org.example.gongiklifeclientbegraphql.producer.report;

import dto.report.CreateSystemReportRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateSystemReportProducer {

  private static final String TOPIC = "create-system-report-topic";
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendCreateSystemReportRequest(CreateSystemReportRequestDto request) {

    kafkaTemplate.send(TOPIC, request);
  }


}
