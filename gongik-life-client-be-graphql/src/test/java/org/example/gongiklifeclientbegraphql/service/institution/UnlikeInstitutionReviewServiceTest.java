package org.example.gongiklifeclientbegraphql.service.institution;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import dto.institution.UnlikeInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.unlikeInstitutionReview.UnlikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.producer.institution.UnlikeInstitutionReviewProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnlikeInstitutionReviewServiceTest {

  @Mock
  private UnlikeInstitutionReviewProducer unlikeInstitutionReviewProducer;

  @InjectMocks
  private UnlikeInstitutionReviewService unlikeInstitutionReviewService;

  @Test
  @DisplayName("기관 리뷰 좋아요 취소 요청 성공")
  void unlikeInstitutionReview_Success() {
    // Given
    UnlikeInstitutionReviewRequestDto requestDto = createTestRequestDto();

    doNothing().when(unlikeInstitutionReviewProducer)
        .sendUnlikeInstitutionReviewRequest(requestDto);

    // When
    UnlikeInstitutionReviewResponseDto response =
        unlikeInstitutionReviewService.unlikeInstitutionReview(requestDto);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertTrue(response.getSuccess()),
        () -> verify(unlikeInstitutionReviewProducer)
            .sendUnlikeInstitutionReviewRequest(requestDto)
    );
  }

  @Test
  @DisplayName("Producer 에러 발생 시 예외 처리")
  void unlikeInstitutionReview_WhenProducerFails() {
    // Given
    UnlikeInstitutionReviewRequestDto requestDto = createTestRequestDto();
    doThrow(new RuntimeException("Producer error"))
        .when(unlikeInstitutionReviewProducer)
        .sendUnlikeInstitutionReviewRequest(any());

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> unlikeInstitutionReviewService.unlikeInstitutionReview(requestDto));
    assertTrue(exception.getMessage().contains("Producer error"));
  }

  private UnlikeInstitutionReviewRequestDto createTestRequestDto() {
    return UnlikeInstitutionReviewRequestDto.builder()
        // 필요한 테스트 데이터 설정
        .institutionReviewId("1234")
        .userId("12345")
        .build();
  }
}
