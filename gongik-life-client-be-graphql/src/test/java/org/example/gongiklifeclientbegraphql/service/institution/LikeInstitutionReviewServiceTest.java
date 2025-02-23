package org.example.gongiklifeclientbegraphql.service.institution;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import dto.institution.LikeInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.likeInstitutionReview.LikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.producer.institution.LikeInstitutionReviewProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeInstitutionReviewServiceTest {

  @Mock
  private LikeInstitutionReviewProducer likeInstitutionReviewProducer;

  @InjectMocks
  private LikeInstitutionReviewService likeInstitutionReviewService;

  @Test
  @DisplayName("기관 리뷰 좋아요 요청 성공")
  void likeInstitutionReview_Success() {
    // Given
    LikeInstitutionReviewRequestDto requestDto = createTestRequestDto();

    doNothing().when(likeInstitutionReviewProducer)
        .sendLikeInstitutionReviewRequest(requestDto);

    // When
    LikeInstitutionReviewResponseDto response =
        likeInstitutionReviewService.likeInstitutionReview(requestDto);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertTrue(response.getSuccess()),
        () -> verify(likeInstitutionReviewProducer)
            .sendLikeInstitutionReviewRequest(requestDto)
    );
  }

  @Test
  @DisplayName("Producer 에러 발생 시 예외 처리")
  void likeInstitutionReview_WhenProducerFails() {
    // Given
    LikeInstitutionReviewRequestDto requestDto = createTestRequestDto();
    doThrow(new RuntimeException("Producer error"))
        .when(likeInstitutionReviewProducer)
        .sendLikeInstitutionReviewRequest(ArgumentMatchers.any());

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> likeInstitutionReviewService.likeInstitutionReview(requestDto));
    assertTrue(exception.getMessage().contains("Producer error"));
  }

  private LikeInstitutionReviewRequestDto createTestRequestDto() {
    return LikeInstitutionReviewRequestDto.builder()
        // 필요한 테스트 데이터 설정
        .build();
  }
}
