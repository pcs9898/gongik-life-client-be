package org.example.gongiklifeclientbegraphql.producer;

import dto.institution.LikeInstitutionReviewRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeInstitutionReviewProducer {

  private static final String TOPIC = "like-institution-review-topic";
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendLikeInstitutionReviewRequest(LikeInstitutionReviewRequestDto request) {

    kafkaTemplate.send(TOPIC, request);
  }

}
