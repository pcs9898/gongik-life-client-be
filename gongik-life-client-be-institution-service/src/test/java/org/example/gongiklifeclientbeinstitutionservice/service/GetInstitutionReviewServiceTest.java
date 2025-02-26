package org.example.gongiklifeclientbeinstitutionservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdResponse;
import io.grpc.StatusRuntimeException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GetInstitutionReviewServiceTest {

  private static final String TEST_REVIEW_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174001";
  private static final String TEST_USER_NAME = "testUser";

  @Mock
  private InstitutionReviewRepository institutionReviewRepository;

  @Mock
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  @InjectMocks
  private GetInstitutionReviewService getInstitutionReviewService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(getInstitutionReviewService, "userServiceBlockingStub",
        userServiceBlockingStub);
  }

  @Test
  @DisplayName("성공: 기관 리뷰 조회")
  void institutionReview_success() {
    // Given
    InstitutionReviewRequest request = createTestRequest();
    InstitutionReview review = createTestReview();
    GetUserNameByIdResponse userResponse = createTestUserResponse();

    when(institutionReviewRepository.findByIdWithInstitution(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.of(review));
    when(userServiceBlockingStub.getUserNameById(any(GetUserNameByIdRequest.class)))
        .thenReturn(userResponse);

    // When
    InstitutionReviewResponse response = getInstitutionReviewService.institutionReview(request);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> verify(institutionReviewRepository).findByIdWithInstitution(
            UUID.fromString(TEST_REVIEW_ID)),
        () -> verify(userServiceBlockingStub).getUserNameById(any(GetUserNameByIdRequest.class))
    );
  }

  @Test
  @DisplayName("실패: 존재하지 않는 리뷰")
  void institutionReview_reviewNotFound() {
    // Given
    InstitutionReviewRequest request = createTestRequest();
    when(institutionReviewRepository.findByIdWithInstitution(any(UUID.class)))
        .thenReturn(Optional.empty());

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> getInstitutionReviewService.institutionReview(request));
    assertTrue(exception.getMessage().contains("Institution review not found"));
  }

  @Test
  @DisplayName("실패: 잘못된 UUID 형식")
  void institutionReview_invalidUUID() {
    // Given
    InstitutionReviewRequest request = InstitutionReviewRequest.newBuilder()
        .setInstitutionReviewId("invalid-uuid")
        .build();

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> getInstitutionReviewService.institutionReview(request));
    assertTrue(exception.getMessage().contains("Invalid UUID format"));
  }

  @Test
  @DisplayName("실패: 사용자 정보 조회 실패")
  void institutionReview_userServiceFailure() {
    // Given
    InstitutionReviewRequest request = createTestRequest();
    InstitutionReview review = createTestReview();

    when(institutionReviewRepository.findByIdWithInstitution(UUID.fromString(TEST_REVIEW_ID)))
        .thenReturn(Optional.of(review));
    when(userServiceBlockingStub.getUserNameById(any(GetUserNameByIdRequest.class)))
        .thenThrow(new RuntimeException("User service error"));

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> getInstitutionReviewService.institutionReview(request));
    assertTrue(exception.getMessage().contains("Failed to fetch user information"));
  }

  private InstitutionReviewRequest createTestRequest() {
    return InstitutionReviewRequest.newBuilder()
        .setInstitutionReviewId(TEST_REVIEW_ID)
        .build();
  }

  private InstitutionReview createTestReview() {
    InstitutionReview review = new InstitutionReview();
    review.setId(UUID.fromString(TEST_REVIEW_ID));
    review.setUserId(UUID.fromString(TEST_USER_ID));
    review.setCreatedAt(new Date());

    review.setFacilityRating(5);
    review.setLocationRating(5);
    review.setVisitorRating(5);
    review.setVacationFreedomRating(4);
    review.setStaffRating(4);
    review.setMainTasks("Test main tasks");
    review.setProsCons("Test pros and cons");
    review.setRating(5.0);
    review.setAverageWorkhours(40);
    review.setUniformWearingRuleId(1);
    review.setWorkTypeRuleId(1);
    review.setSocialServicePeopleCountId(1);
    Institution institution = new Institution();
    institution.setId(UUID.randomUUID());
    institution.setName("Test Institution");
    review.setInstitution(institution);

    return review;
  }

  private GetUserNameByIdResponse createTestUserResponse() {
    return GetUserNameByIdResponse.newBuilder()
        .setUserName(TEST_USER_NAME)
        .build();
  }
}
