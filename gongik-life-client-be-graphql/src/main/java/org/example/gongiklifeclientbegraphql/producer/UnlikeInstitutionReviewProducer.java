package org.example.gongiklifeclientbegraphql.producer;

import dto.institution.UnlikeInstitutionReviewRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnlikeInstitutionReviewProducer {

  private static final String TOPIC = "unlike-institution-review-topic";
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendUnlikeInstitutionReviewRequest(UnlikeInstitutionReviewRequestDto request) {

    kafkaTemplate.send(TOPIC, request);
  }

}
