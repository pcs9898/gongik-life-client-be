package org.example.gongiklifeclientbegraphql.producer.institution;

import dto.institution.LikeInstitutionReviewRequestDto;
import kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeInstitutionReviewProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLikeInstitutionReviewRequest(LikeInstitutionReviewRequestDto request) {

        kafkaTemplate.send(KafkaTopics.LIKE_INSTITUTION_REVIEW_TOPIC, request);
    }

}
