package org.example.gongiklifeclientbeinstitutionservice.consumer;

import dto.institution.UnlikeInstitutionReviewRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.service.UnlikeInstitutionReviewService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnlikeInstitutionReviewConsumer {

  private final UnlikeInstitutionReviewService unlikeInstitutionReviewService;

  @KafkaListener(topics = "unlike-institution-review-topic")
  public void consume(UnlikeInstitutionReviewRequestDto requestDto) {
    try {
      log.info("Received reviewId: {}, userId : {}", requestDto.getInstitutionReviewId(),
          requestDto.getUserId());
      unlikeInstitutionReviewService.unlikeInstitutionReview(requestDto);
    } catch (Exception e) {
      log.error("Error processing unlike institution review: {}", requestDto, e);
      throw e; // 트랜잭션 롤백을 위해 예외 재발생
    }
  }


}
