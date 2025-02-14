package org.example.gongiklifeclientbeinstitutionservice.consumer;

import dto.institution.LikeInstitutionReviewRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.service.InstitutionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeInstitutionReviewConsumer {

  private final InstitutionService institutionService;

  @KafkaListener(topics = "like-institution-review-topic")
  public void consume(LikeInstitutionReviewRequestDto requestDto) {
    try {
      log.info("Received reviewId: {}, userId : {}", requestDto.getInstitutionReviewId(),
          requestDto.getUserId());
      institutionService.likeInstitutionReview(requestDto);
    } catch (Exception e) {
      log.error("Error processing like institution review: {}", requestDto, e);
      throw e; // 트랜잭션 롤백을 위해 예외 재발생
    }
  }

}
