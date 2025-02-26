package org.example.gongiklifeclientbeinstitutionservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dto.institution.UnlikeInstitutionReviewRequestDto;
import io.grpc.StatusRuntimeException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLike;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLikeId;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewLikeRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnlikeInstitutionReviewServiceTest {

  private static final String TEST_REVIEW_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174001";

  @Mock
  private InstitutionReviewRepository institutionReviewRepository;

  @Mock
  private InstitutionReviewLikeRepository institutionReviewLikeRepository;

  @InjectMocks
  private UnlikeInstitutionReviewService unlikeInstitutionReviewService;

  @Test
  @DisplayName("성공: 기관 리뷰 좋아요 취소")
  void unlikeInstitutionReview_success() {
    // Given
    UnlikeInstitutionReviewRequestDto requestDto = createTestRequestDto();
    InstitutionReview review = createTestInstitutionReview();
    InstitutionReviewLike like = createTestInstitutionReviewLike();

    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.of(review));
    when(institutionReviewLikeRepository.findById(any(InstitutionReviewLikeId.class)))
        .thenReturn(Optional.of(like));

    // When
    unlikeInstitutionReviewService.unlikeInstitutionReview(requestDto);

    // Then
    verify(institutionReviewRepository).findById(UUID.fromString(TEST_REVIEW_ID));
    verify(institutionReviewLikeRepository).findById(any(InstitutionReviewLikeId.class));
    verify(institutionReviewLikeRepository).delete(like);
    assertEquals(0, review.getLikeCount()); // 좋아요 수가 1 감소했는지 확인
  }

  @Test
  @DisplayName("실패: 존재하지 않는 리뷰")
  void unlikeInstitutionReview_reviewNotFound() {
    // Given
    UnlikeInstitutionReviewRequestDto requestDto = createTestRequestDto();
    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.empty());

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> unlikeInstitutionReviewService.unlikeInstitutionReview(requestDto));
    assertTrue(exception.getMessage().contains("Institution review not found"));
  }

  @Test
  @DisplayName("실패: 존재하지 않는 좋아요")
  void unlikeInstitutionReview_likeNotFound() {
    // Given
    UnlikeInstitutionReviewRequestDto requestDto = createTestRequestDto();
    InstitutionReview review = createTestInstitutionReview();

    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.of(review));
    when(institutionReviewLikeRepository.findById(any(InstitutionReviewLikeId.class)))
        .thenReturn(Optional.empty());

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> unlikeInstitutionReviewService.unlikeInstitutionReview(requestDto));
    assertTrue(exception.getMessage().contains("Institution review like not found"));
  }

  private UnlikeInstitutionReviewRequestDto createTestRequestDto() {
    UnlikeInstitutionReviewRequestDto dto = new UnlikeInstitutionReviewRequestDto();
    dto.setInstitutionReviewId(TEST_REVIEW_ID);
    dto.setUserId(TEST_USER_ID);
    return dto;
  }

  private InstitutionReview createTestInstitutionReview() {
    InstitutionReview review = new InstitutionReview();
    review.setId(UUID.fromString(TEST_REVIEW_ID));
    review.setLikeCount(1); // 초기 좋아요 수를 1로 설정
    return review;
  }

  private InstitutionReviewLike createTestInstitutionReviewLike() {
    return new InstitutionReviewLike(
        new InstitutionReviewLikeId(
            UUID.fromString(TEST_REVIEW_ID),
            UUID.fromString(TEST_USER_ID)
        ),
        LocalDateTime.now()
    );
  }
}
