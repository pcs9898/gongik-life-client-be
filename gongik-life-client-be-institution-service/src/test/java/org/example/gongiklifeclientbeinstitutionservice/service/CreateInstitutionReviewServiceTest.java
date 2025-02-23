package org.example.gongiklifeclientbeinstitutionservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.CreateInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.CheckUserInstitutionRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.CheckUserInstitutionResponse;
import io.grpc.StatusRuntimeException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class CreateInstitutionReviewServiceTest {


  private static final String TEST_INSTITUTION_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174001";
  private static final String TEST_USER_NAME = "testUser";
  private static final Logger log = LoggerFactory.getLogger(
      CreateInstitutionReviewServiceTest.class);

  @Mock
  private InstitutionRepository institutionRepository;

  @Mock
  private InstitutionReviewRepository institutionReviewRepository;

  @Mock
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  @InjectMocks
  private CreateInstitutionReviewService createInstitutionReviewService;


  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(createInstitutionReviewService, "userServiceBlockingStub",
        userServiceBlockingStub);
  }

  @Test
  @DisplayName("성공: 기관 리뷰 생성")
  void createInstitutionReview_success() {
    // Given
    CreateInstitutionReviewRequest request = createTestRequest();
    Institution institution = createTestInstitution();
    InstitutionReview review = createTestReview(institution);
    CheckUserInstitutionResponse userResponse = createTestUserResponse();

    // 모의 동작 설정
    when(institutionRepository.findById(UUID.fromString(TEST_INSTITUTION_ID)))
        .thenReturn(Optional.of(institution));
    when(userServiceBlockingStub.checkUserInstitution(any(CheckUserInstitutionRequest.class)))
        .thenReturn(userResponse);
    when(institutionReviewRepository.existsByUserIdAndInstitutionId(
        UUID.fromString(TEST_USER_ID),
        UUID.fromString(TEST_INSTITUTION_ID)
    )).thenReturn(false);
    // save() 호출 시, 저장된 객체에 id와 createdAt 값을 부여하도록 처리
    when(institutionReviewRepository.save(any(InstitutionReview.class))).thenAnswer(invocation -> {
      InstitutionReview argReview = invocation.getArgument(0);
      argReview.setId(UUID.randomUUID());
      if (argReview.getCreatedAt() == null) {
        argReview.setCreatedAt(new Date());
      }
      return argReview;
    });

    // When
    InstitutionReviewResponse response =
        createInstitutionReviewService.createInstitutionReview(request);

    // Then
    assertNotNull(response);
    verify(institutionRepository).findById(UUID.fromString(TEST_INSTITUTION_ID));
    // getUserNameFromUserService는 서비스 내에서 2번 호출됨
    verify(userServiceBlockingStub, times(2))
        .checkUserInstitution(any(CheckUserInstitutionRequest.class));
    verify(institutionReviewRepository).existsByUserIdAndInstitutionId(
        UUID.fromString(TEST_USER_ID),
        UUID.fromString(TEST_INSTITUTION_ID));
    verify(institutionRepository).save(institution);
    verify(institutionReviewRepository).save(any(InstitutionReview.class));
  }

  @Test
  @DisplayName("실패: 존재하지 않는 기관")
  void createInstitutionReview_institutionNotFound() {
    // Given
    CreateInstitutionReviewRequest request = createTestRequest();
    when(institutionRepository.findById(UUID.fromString(TEST_INSTITUTION_ID)))
        .thenReturn(Optional.empty());

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
        createInstitutionReviewService.createInstitutionReview(request)
    );
    assertTrue(exception.getMessage().contains("Institution not found"));
  }

  @Test
  @DisplayName("실패: 사용자와 기관 매칭 실패 (빈 userName)")
  void createInstitutionReview_userMismatch() {
    // Given
    CreateInstitutionReviewRequest request = createTestRequest();
    Institution institution = createTestInstitution();
    // userService에서 빈 userName을 반환하는 경우
    CheckUserInstitutionResponse emptyResponse = CheckUserInstitutionResponse.newBuilder()
        .setUserName("")
        .build();

    when(institutionRepository.findById(UUID.fromString(TEST_INSTITUTION_ID)))
        .thenReturn(Optional.of(institution));
    when(userServiceBlockingStub.checkUserInstitution(any(CheckUserInstitutionRequest.class)))
        .thenReturn(emptyResponse);

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
        createInstitutionReviewService.createInstitutionReview(request)
    );

    log.info(exception.getMessage());
    assertTrue(exception.getMessage().contains("User and institution does not match"));
  }

  @Test
  @DisplayName("실패: 중복 리뷰 존재")
  void createInstitutionReview_duplicateReview() {
    // Given
    CreateInstitutionReviewRequest request = createTestRequest();
    Institution institution = createTestInstitution();
    CheckUserInstitutionResponse userResponse = createTestUserResponse();

    when(institutionRepository.findById(UUID.fromString(TEST_INSTITUTION_ID)))
        .thenReturn(Optional.of(institution));
    when(userServiceBlockingStub.checkUserInstitution(any(CheckUserInstitutionRequest.class)))
        .thenReturn(userResponse);
    when(institutionReviewRepository.existsByUserIdAndInstitutionId(
        UUID.fromString(TEST_USER_ID),
        UUID.fromString(TEST_INSTITUTION_ID)
    )).thenReturn(true);

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
        createInstitutionReviewService.createInstitutionReview(request)
    );
    assertTrue(exception.getMessage().contains("User already reviewed this institution"));
  }

// 헬퍼 메소드

  private CreateInstitutionReviewRequest createTestRequest() {
    return CreateInstitutionReviewRequest.newBuilder()
        .setInstitutionId(TEST_INSTITUTION_ID)
        .setUserId(TEST_USER_ID)
        .setFacilityRating(5)
        .setLocationRating(4)
        .setStaffRating(5)
        .setVisitorRating(3)
        .setVacationFreedomRating(4)
        .build();
  }

  private Institution createTestInstitution() {
    Institution institution = new Institution();
    institution.setId(UUID.fromString(TEST_INSTITUTION_ID));
    institution.setReviewCount(0);
    institution.setName("Test Institution");
    return institution;
  }

  private InstitutionReview createTestReview(Institution institution) {
    InstitutionReview review = new InstitutionReview();
    review.setId(UUID.randomUUID());
    review.setInstitution(institution);
    review.setUserId(UUID.fromString(TEST_USER_ID));
    // 필수 필드인 createdAt을 초기화하여 NullPointerException을 방지합니다.
    review.setCreatedAt(new Date());
    return review;
  }

  private CheckUserInstitutionResponse createTestUserResponse() {
    return CheckUserInstitutionResponse.newBuilder()
        .setUserName(TEST_USER_NAME)
        .build();
  }
}