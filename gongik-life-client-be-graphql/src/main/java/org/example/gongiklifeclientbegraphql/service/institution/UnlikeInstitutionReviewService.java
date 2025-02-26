package org.example.gongiklifeclientbegraphql.service.institution;

import dto.institution.UnlikeInstitutionReviewRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.institution.unlikeInstitutionReview.UnlikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.producer.institution.UnlikeInstitutionReviewProducer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnlikeInstitutionReviewService {

  private final UnlikeInstitutionReviewProducer unlikeInstitutionReviewProducer;

  public UnlikeInstitutionReviewResponseDto unlikeInstitutionReview(
      UnlikeInstitutionReviewRequestDto requestDto) {

    unlikeInstitutionReviewProducer.sendUnlikeInstitutionReviewRequest(requestDto);

    return UnlikeInstitutionReviewResponseDto.builder()
        .success(true)
        .build();
  }
}
