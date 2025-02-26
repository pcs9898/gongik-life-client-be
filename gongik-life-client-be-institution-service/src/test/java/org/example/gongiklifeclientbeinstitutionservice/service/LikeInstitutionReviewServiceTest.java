package org.example.gongiklifeclientbeinstitutionservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dto.institution.LikeInstitutionReviewRequestDto;
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLike;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewLikeRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeInstitutionReviewServiceTest {

  private static final String TEST_REVIEW_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174001";

  @Mock
  private InstitutionReviewRepository institutionReviewRepository;

  @Mock
  private InstitutionReviewLikeRepository institutionReviewLikeRepository;

  @InjectMocks
  private LikeInstitutionReviewService likeInstitutionReviewService;

  @Test
  @DisplayName("성공: 기관 리뷰 좋아요")
  void likeInstitutionReview_success() {
    // Given
    LikeInstitutionReviewRequestDto requestDto = createTestRequestDto();
    InstitutionReview review = createTestInstitutionReview();

    // 모의 동작 설정: 리뷰 검색 성공
    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.of(review));
    // 이미 좋아요가 누른 상태가 아님을 반환
    when(institutionReviewLikeRepository.existsByIdInstitutionReviewIdAndIdUserId(
        UUID.fromString(TEST_REVIEW_ID), UUID.fromString(TEST_USER_ID)))
        .thenReturn(false);

    // When
    likeInstitutionReviewService.likeInstitutionReview(requestDto);

    // Then
    verify(institutionReviewRepository).findById(UUID.fromString(TEST_REVIEW_ID));
    verify(institutionReviewLikeRepository).existsByIdInstitutionReviewIdAndIdUserId(
        UUID.fromString(TEST_REVIEW_ID), UUID.fromString(TEST_USER_ID));
    verify(institutionReviewLikeRepository).save(any(InstitutionReviewLike.class));
    // 리뷰의 좋아요 수가 1 증가되었음을 검증 (초기값 0 → 1)
    assertEquals(1, review.getLikeCount());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 리뷰")
  void likeInstitutionReview_reviewNotFound() {
    // Given
    LikeInstitutionReviewRequestDto requestDto = createTestRequestDto();
    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.empty());

    // When & Then: 리뷰가 없으면 예외 발생
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
        likeInstitutionReviewService.likeInstitutionReview(requestDto)
    );
    assertTrue(exception.getMessage().contains("Institution review not found"));
  }

  @Test
  @DisplayName("실패: 이미 좋아요한 리뷰")
  void likeInstitutionReview_alreadyLiked() {
    // Given
    LikeInstitutionReviewRequestDto requestDto = createTestRequestDto();
    InstitutionReview review = createTestInstitutionReview();
    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.of(review));
    // 이미 좋아요한 상태임을 반환
    when(institutionReviewLikeRepository.existsByIdInstitutionReviewIdAndIdUserId(
        UUID.fromString(TEST_REVIEW_ID), UUID.fromString(TEST_USER_ID)))
        .thenReturn(true);

    // When & Then: 이미 좋아요한 경우 예외 발생
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
        likeInstitutionReviewService.likeInstitutionReview(requestDto)
    );
    assertTrue(exception.getMessage().contains("User already liked this review"));
  }

  // 헬퍼 메소드

  private LikeInstitutionReviewRequestDto createTestRequestDto() {
    LikeInstitutionReviewRequestDto dto = new LikeInstitutionReviewRequestDto();
    dto.setInstitutionReviewId(TEST_REVIEW_ID);
    dto.setUserId(TEST_USER_ID);
    return dto;
  }

  private InstitutionReview createTestInstitutionReview() {
    InstitutionReview review = new InstitutionReview();
    review.setId(UUID.fromString(TEST_REVIEW_ID));
    review.setLikeCount(0);
    // 필요한 경우 다른 필드도 초기화
    return review;
  }
}
