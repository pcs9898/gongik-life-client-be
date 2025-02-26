package org.example.gongiklifeclientbegraphql.producer.institution;

import dto.institution.UnlikeInstitutionReviewRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnlikeInstitutionReviewProducer {

    private static final String TOPIC = KafkaTopics.UNLIKE_INSTITUTION_REVIEW_TOPIC;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUnlikeInstitutionReviewRequest(UnlikeInstitutionReviewRequestDto request) {

        kafkaTemplate.send(TOPIC, request);
    }

}
