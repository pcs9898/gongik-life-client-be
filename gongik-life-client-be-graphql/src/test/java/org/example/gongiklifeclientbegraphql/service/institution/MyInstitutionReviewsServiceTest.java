package org.example.gongiklifeclientbegraphql.service.institution;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.institution.myInstitutionReviews.MyInstitutionReviewsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MyInstitutionReviewsServiceTest {

  private static final String TEST_USER_ID = "test-user-id";

  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  @InjectMocks
  private MyInstitutionReviewsService myInstitutionReviewsService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(myInstitutionReviewsService, "institutionBlockingStub",
        institutionBlockingStub);
  }

  @Test
  @DisplayName("내 기관 리뷰 목록 조회 성공")
  void myInstitutionReviews_Success() {
    // Given
    MyInstitutionReviewsRequest request = MyInstitutionReviewsRequest.newBuilder()
        .setUserId(TEST_USER_ID)
        .build();

    MyInstitutionReviewsResponse protoResponse = MyInstitutionReviewsResponse.newBuilder()
        // 필요한 응답 데이터 설정
        .build();

    when(institutionBlockingStub.myInstitutionReviews(eq(request)))
        .thenReturn(protoResponse);

    // When
    MyInstitutionReviewsResponseDto response =
        myInstitutionReviewsService.myInstitutionReviews(TEST_USER_ID);

    // Then
    assertNotNull(response);
    verify(institutionBlockingStub).myInstitutionReviews(eq(request));
  }

  @Test
  @DisplayName("gRPC 서버 내부 에러 발생 시 예외 처리")
  void myInstitutionReviews_WhenGrpcInternalError() {
    // Given
    when(institutionBlockingStub.myInstitutionReviews(any(MyInstitutionReviewsRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> myInstitutionReviewsService.myInstitutionReviews(TEST_USER_ID));
    assertTrue(exception.getMessage().contains("Error occurred in myInstitutionReviews"));
  }

  @Test
  @DisplayName("사용자를 찾을 수 없는 경우 예외 처리")
  void myInstitutionReviews_WhenUserNotFound() {
    // Given
    when(institutionBlockingStub.myInstitutionReviews(any(MyInstitutionReviewsRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> myInstitutionReviewsService.myInstitutionReviews(TEST_USER_ID));
  }

  @Test
  @DisplayName("잘못된 사용자 ID 형식으로 인한 실패")
  void myInstitutionReviews_WhenInvalidUserId() {
    // Given
    when(institutionBlockingStub.myInstitutionReviews(any(MyInstitutionReviewsRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> myInstitutionReviewsService.myInstitutionReviews("invalid-user-id"));
  }
}
