package org.example.gongiklifeclientbeinstitutionservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import io.grpc.StatusRuntimeException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteInstitutionReviewServiceTest {

  private static final String TEST_REVIEW_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174001";
  private static final UUID TEST_INSTITUTION_ID = UUID.randomUUID();

  @Mock
  private InstitutionReviewRepository institutionReviewRepository;

  @Mock
  private InstitutionRepository institutionRepository;

  @InjectMocks
  private DeleteInstitutionReviewService deleteInstitutionReviewService;

  @Test
  @DisplayName("성공: 기관 리뷰 삭제")
  void deleteInstitutionReview_success() {
    // Given
    DeleteInstitutionReviewRequest request = createTestRequest();
    InstitutionReview review = createTestReview();
    Institution institution = createTestInstitution();

    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.of(review));
    when(institutionRepository.findById(TEST_INSTITUTION_ID))
        .thenReturn(Optional.of(institution));

    // When
    DeleteInstitutionReviewResponse response = deleteInstitutionReviewService.deleteInstitutionReview(
        request);

    // Then
    assertNotNull(response);
    assertTrue(response.getSuccess());
    verify(institutionReviewRepository).findById(UUID.fromString(TEST_REVIEW_ID));
    verify(institutionRepository).findById(TEST_INSTITUTION_ID);
    verify(institutionRepository).save(any(Institution.class));
    verify(institutionReviewRepository).save(any(InstitutionReview.class));
  }

  @Test
  @DisplayName("실패: 존재하지 않는 리뷰")
  void deleteInstitutionReview_reviewNotFound() {
    // Given
    DeleteInstitutionReviewRequest request = createTestRequest();
    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.empty());

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
        deleteInstitutionReviewService.deleteInstitutionReview(request)
    );
    assertTrue(exception.getMessage().contains("Institution review not found"));
  }

  @Test
  @DisplayName("실패: 권한 없는 사용자")
  void deleteInstitutionReview_unauthorizedUser() {
    // Given
    DeleteInstitutionReviewRequest request = createTestRequest();
    InstitutionReview review = createTestReview();
    review.setUserId(UUID.randomUUID()); // 다른 사용자의 ID 설정

    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.of(review));

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
        deleteInstitutionReviewService.deleteInstitutionReview(request)
    );
    assertTrue(exception.getMessage().contains("You can delete only your institution review"));
  }

  @Test
  @DisplayName("실패: 존재하지 않는 기관")
  void deleteInstitutionReview_institutionNotFound() {
    // Given
    DeleteInstitutionReviewRequest request = createTestRequest();
    InstitutionReview review = createTestReview();

    when(institutionReviewRepository.findById(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.of(review));
    when(institutionRepository.findById(TEST_INSTITUTION_ID))
        .thenReturn(Optional.empty());

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
        deleteInstitutionReviewService.deleteInstitutionReview(request)
    );
    assertTrue(exception.getMessage().contains("Institution not found"));
  }

  // 헬퍼 메소드

  private DeleteInstitutionReviewRequest createTestRequest() {
    return DeleteInstitutionReviewRequest.newBuilder()
        .setInstitutionReviewId(TEST_REVIEW_ID)
        .setUserId(TEST_USER_ID)
        .build();
  }

  private Institution createTestInstitution() {
    Institution institution = new Institution();
    institution.setId(TEST_INSTITUTION_ID);
    institution.setName("Test Institution");
    institution.setReviewCount(1);
    return institution;
  }

  private InstitutionReview createTestReview() {
    Institution institution = new Institution();
    institution.setId(TEST_INSTITUTION_ID);

    InstitutionReview review = new InstitutionReview();
    review.setId(UUID.fromString(TEST_REVIEW_ID));
    review.setUserId(UUID.fromString(TEST_USER_ID));
    review.setCreatedAt(new Date());
    review.setDeletedAt(null);
    review.setInstitution(institution);

    return review;
  }
}
