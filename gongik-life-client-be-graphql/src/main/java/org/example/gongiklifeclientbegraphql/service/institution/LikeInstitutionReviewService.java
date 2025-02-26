package org.example.gongiklifeclientbegraphql.service.institution;

import dto.institution.LikeInstitutionReviewRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.institution.likeInstitutionReview.LikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.producer.institution.LikeInstitutionReviewProducer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeInstitutionReviewService {

  private final LikeInstitutionReviewProducer likeInstitutionReviewProducer;

  public LikeInstitutionReviewResponseDto likeInstitutionReview(
      LikeInstitutionReviewRequestDto requestDto) {

    likeInstitutionReviewProducer.sendLikeInstitutionReviewRequest(requestDto);

    return LikeInstitutionReviewResponseDto.builder()
        .success(true)
        .build();
  }


}
