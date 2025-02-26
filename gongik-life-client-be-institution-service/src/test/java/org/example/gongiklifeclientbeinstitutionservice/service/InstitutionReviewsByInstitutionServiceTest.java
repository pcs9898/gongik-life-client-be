package org.example.gongiklifeclientbeinstitutionservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdsRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdsResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
class InstitutionReviewsByInstitutionServiceTest {

  private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String TEST_INSTITUTION_ID = "123e4567-e89b-12d3-a456-426614174001";
  private static final String TEST_REVIEW_ID = "123e4567-e89b-12d3-a456-426614174002";
  private static final String TEST_USER_NAME = "testUser";
  private static final String TEST_INSTITUTION_NAME = "Test Institution";
  private static final Integer TEST_CATEGORY_ID = 1;
  private static final Logger log = LoggerFactory.getLogger(
      InstitutionReviewsByInstitutionServiceTest.class);

  @Mock
  private InstitutionReviewRepository institutionReviewRepository;

  @Mock
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  @InjectMocks
  private InstitutionReviewsByInstitutionService institutionReviewsByInstitutionService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(institutionReviewsByInstitutionService, "userServiceBlockingStub",
        userServiceBlockingStub);
  }

  @Test
  @DisplayName("성공: 기관별 리뷰 목록 조회")
  void institutionReviewsByInstitution_success() {
    // Given
    InstitutionReviewsByInstitutionRequest request = createTestRequest();
    List<InstitutionReviewProjection> reviews = createTestReviews();
    Map<String, String> userNameMap = createTestUserNameMap();

    when(institutionReviewRepository.findInstitutionReviewsByInstitutionIdWithCursor(
        any(UUID.class), any(UUID.class), any(), anyInt()))
        .thenReturn(reviews);

    GetUserNameByIdsResponse userResponse = GetUserNameByIdsResponse.newBuilder()
        .putAllUsers(userNameMap)
        .build();
    when(userServiceBlockingStub.getUserNameByIds(argThat(req ->
        req.getUserIdsList().contains(TEST_USER_ID))))
        .thenReturn(userResponse);

    // When
    InstitutionReviewsByInstitutionResponse response =
        institutionReviewsByInstitutionService.institutionReviewsByInstitution(request);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(1, response.getListInstitutionReviewByInstitutionCount()),
        () -> assertFalse(response.getPageInfo().getHasNextPage()),
        () -> verify(institutionReviewRepository).findInstitutionReviewsByInstitutionIdWithCursor(
            any(UUID.class), eq(UUID.fromString(TEST_INSTITUTION_ID)), any(), eq(10)),
        () -> verify(userServiceBlockingStub).getUserNameByIds(any(GetUserNameByIdsRequest.class))
    );
  }

  @Test
  @DisplayName("실패: 사용자 정보 조회 실패")
  void institutionReviewsByInstitution_userServiceFailure() {
    // Given
    InstitutionReviewsByInstitutionRequest request = createTestRequest();
    List<InstitutionReviewProjection> reviews = createTestReviews();

    when(institutionReviewRepository.findInstitutionReviewsByInstitutionIdWithCursor(
        any(UUID.class), any(UUID.class), any(), anyInt()))
        .thenReturn(reviews);

    Status status = Status.INTERNAL.withDescription("User service error");
    when(userServiceBlockingStub.getUserNameByIds(any(GetUserNameByIdsRequest.class)))
        .thenThrow(status.asRuntimeException());

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> institutionReviewsByInstitutionService.institutionReviewsByInstitution(request));
    assertEquals(Status.Code.INTERNAL, exception.getStatus().getCode());
    assertTrue(exception.getMessage().contains("Failed to fetch user information"));
  }

  private Map<String, String> createTestUserNameMap() {
    Map<String, String> map = new HashMap<>();
    map.put(TEST_USER_ID, TEST_USER_NAME);
    return map;
  }


  private InstitutionReviewsByInstitutionRequest createTestRequest() {
    return InstitutionReviewsByInstitutionRequest.newBuilder()
        .setInstitutionId(TEST_INSTITUTION_ID)
        .setUserId(TEST_USER_ID)
        .setCursor("")
        .setPageSize(10)
        .build();
  }

  private List<InstitutionReviewProjection> createTestReviews() {
    InstitutionReviewProjection review = mock(InstitutionReviewProjection.class);
    when(review.getId()).thenReturn(UUID.fromString(TEST_REVIEW_ID));
    when(review.getUserId()).thenReturn(UUID.fromString(TEST_USER_ID));
    when(review.getInstitutionId()).thenReturn(UUID.fromString(TEST_INSTITUTION_ID));
    when(review.getInstitutionName()).thenReturn(TEST_INSTITUTION_NAME);
    when(review.getInstitutionCategoryId()).thenReturn(TEST_CATEGORY_ID);
    when(review.getRating()).thenReturn(4.5);
    when(review.getMainTasks()).thenReturn("Test Tasks");
    when(review.getProsCons()).thenReturn("Test Pros and Cons");
    when(review.getAverageWorkhours()).thenReturn(40);
    when(review.getLikeCount()).thenReturn(10);
    when(review.getCreatedAt()).thenReturn(LocalDateTime.now());
    when(review.getIsLiked()).thenReturn(false);

    return Collections.singletonList(review);
  }
}
