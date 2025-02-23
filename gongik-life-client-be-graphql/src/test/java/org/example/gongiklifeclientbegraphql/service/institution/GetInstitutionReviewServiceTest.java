package org.example.gongiklifeclientbegraphql.service.institution;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetInstitutionReviewServiceTest {

  private static final String TEST_REVIEW_ID = "test-review-id";
  private static final String TEST_USER_ID = "test-user-id";

  @Mock
  private InstitutionCacheService institutionCacheService;

  @Mock
  private InstitutionService institutionService;

  @InjectMocks
  private GetInstitutionReviewService getInstitutionReviewService;

  @Test
  @DisplayName("기관 리뷰 조회 성공 - 사용자 ID 있는 경우")
  void institutionReview_Success_WithUserId() {
    // Given
    InstitutionReviewRequestDto requestDto = createTestRequestDto(TEST_USER_ID);
    InstitutionReviewResponseDto cachedResponse = createTestResponseDto();

    when(institutionCacheService.getInstitutionReview(TEST_REVIEW_ID))
        .thenReturn(cachedResponse);
    when(institutionService.isLikedInstitutionReview(TEST_REVIEW_ID, TEST_USER_ID))
        .thenReturn(true);

    // When
    InstitutionReviewResponseDto response =
        getInstitutionReviewService.institutionReview(requestDto);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertTrue(response.getIsLiked()),
        () -> verify(institutionCacheService).getInstitutionReview(TEST_REVIEW_ID),
        () -> verify(institutionService).isLikedInstitutionReview(TEST_REVIEW_ID, TEST_USER_ID)
    );
  }

  @Test
  @DisplayName("기관 리뷰 조회 성공 - 사용자 ID 없는 경우")
  void institutionReview_Success_WithoutUserId() {
    // Given
    InstitutionReviewRequestDto requestDto = createTestRequestDto(null);
    InstitutionReviewResponseDto cachedResponse = createTestResponseDto();

    when(institutionCacheService.getInstitutionReview(TEST_REVIEW_ID))
        .thenReturn(cachedResponse);

    // When
    InstitutionReviewResponseDto response =
        getInstitutionReviewService.institutionReview(requestDto);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertFalse(response.getIsLiked()),
        () -> verify(institutionCacheService).getInstitutionReview(TEST_REVIEW_ID),
        () -> verify(institutionService, never()).isLikedInstitutionReview(any(), any())
    );
  }

  @Test
  @DisplayName("캐시 서비스 에러 발생 시 예외 처리")
  void institutionReview_WhenCacheServiceFails() {
    // Given
    InstitutionReviewRequestDto requestDto = createTestRequestDto(TEST_USER_ID);
    when(institutionCacheService.getInstitutionReview(any()))
        .thenThrow(new RuntimeException("Cache service error"));

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> getInstitutionReviewService.institutionReview(requestDto));
    assertTrue(exception.getMessage().contains("Error occurred in institutionReview"));
  }

  private InstitutionReviewRequestDto createTestRequestDto(String userId) {
    return InstitutionReviewRequestDto.builder()
        .institutionReviewId(TEST_REVIEW_ID)
        .userId(userId)
        .build();
  }

  private InstitutionReviewResponseDto createTestResponseDto() {
    InstitutionReviewResponseDto responseDto = new InstitutionReviewResponseDto();
    // 필요한 응답 데이터 설정
    responseDto.setIsLiked(false);
    return responseDto;
  }
}
