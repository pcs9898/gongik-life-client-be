package org.example.gongiklifeclientbeinstitutionservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdResponse;
import io.grpc.StatusRuntimeException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionReviewProjection;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MyInstitutionReviewsServiceTest {

  private static final String TEST_USER_ID = "0988961f-e359-46b2-b240-af1dd8b473dc";
  private static final String TEST_INSTITUTION_ID = "123e4567-e89b-12d3-a456-426614174001";
  private static final String TEST_REVIEW_ID = "123e4567-e89b-12d3-a456-426614174002";
  private static final String TEST_USER_NAME = "testUser";
  private static final String TEST_INSTITUTION_NAME = "Test Institution";
  private static final Integer TEST_CATEGORY_ID = 1;
  private static final Logger log = LoggerFactory.getLogger(MyInstitutionReviewsServiceTest.class);

  @Mock
  private InstitutionReviewRepository institutionReviewRepository;

  @Mock
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  @InjectMocks
  private MyInstitutionReviewsService myInstitutionReviewsService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(myInstitutionReviewsService, "userServiceBlockingStub",
        userServiceBlockingStub);
  }

  @Test
  @DisplayName("성공: 내 기관 리뷰 목록 조회")
  void myInstitutionReviews_success() {
    // Given
    MyInstitutionReviewsRequest request = createTestRequest();
    List<InstitutionReviewProjection> reviews = createTestReviews();

    when(userServiceBlockingStub.getUserNameById(any(GetUserNameByIdRequest.class)))
        .thenReturn(GetUserNameByIdResponse.newBuilder().setUserName(TEST_USER_NAME).build());
    when(institutionReviewRepository.findMyInstitutionReviews(any(UUID.class)))
        .thenReturn(reviews);

    // When
    MyInstitutionReviewsResponse response = myInstitutionReviewsService.myInstitutionReviews(
        request);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(1, response.getListMyInstitutionReviewCount()),
        () -> verify(userServiceBlockingStub).getUserNameById(any(GetUserNameByIdRequest.class)),
        () -> verify(institutionReviewRepository).findMyInstitutionReviews(
            UUID.fromString(TEST_USER_ID))
    );
  }


  @Test
  @DisplayName("실패: 사용자 정보 조회 실패")
  void myInstitutionReviews_userServiceFailure() {
    // Given
    MyInstitutionReviewsRequest request = createTestRequest();
    when(userServiceBlockingStub.getUserNameById(any(GetUserNameByIdRequest.class)))
        .thenThrow(new RuntimeException("User service error"));

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> myInstitutionReviewsService.myInstitutionReviews(request));
    assertTrue(exception.getMessage().contains("Failed to fetch user information"));
  }

  private MyInstitutionReviewsRequest createTestRequest() {
    return MyInstitutionReviewsRequest.newBuilder()
        .setUserId(TEST_USER_ID)
        .build();
  }

  private List<InstitutionReviewProjection> createTestReviews() {
    InstitutionReviewProjection review = mock(InstitutionReviewProjection.class);
    when(review.getId()).thenReturn(UUID.fromString(TEST_REVIEW_ID));
    when(review.getInstitutionId()).thenReturn(UUID.fromString(TEST_INSTITUTION_ID));
    when(review.getInstitutionName()).thenReturn(TEST_INSTITUTION_NAME);
    when(review.getInstitutionCategoryId()).thenReturn(TEST_CATEGORY_ID);
    when(review.getRating()).thenReturn(4.5);
    when(review.getLikeCount()).thenReturn(10);
    when(review.getCreatedAt()).thenReturn(LocalDateTime.now());
    when(review.getIsLiked()).thenReturn(false);
    when(review.getUserId()).thenReturn(UUID.fromString(TEST_USER_ID));
    when(review.getMainTasks()).thenReturn("Main tasks");
    when(review.getProsCons()).thenReturn("Pros and cons");
    when(review.getAverageWorkhours()).thenReturn(40);

    return Collections.singletonList(review);
  }
}
