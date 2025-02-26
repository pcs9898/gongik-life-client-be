package org.example.gongiklifeclientbegraphql.service.institution;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GetInstitutionReviewsServiceTest {

  private static final String TEST_INSTITUTION_ID = "test-institution-id";

  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  @InjectMocks
  private GetInstitutionReviewsService getInstitutionReviewsService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(getInstitutionReviewsService, "institutionBlockingStub",
        institutionBlockingStub);
  }

  @Test
  @DisplayName("기관 리뷰 목록 조회 성공")
  void institutionReviews_Success() {
    // Given
    InstitutionReviewsRequestDto requestDto = createTestRequestDto();
    InstitutionReviewsRequest protoRequest = requestDto.toInstitutionReviewsRequestProto();
    InstitutionReviewsResponse protoResponse = createTestProtoResponse();

    when(institutionBlockingStub.institutionReviews(eq(protoRequest)))
        .thenReturn(protoResponse);

    // When
    InstitutionReviewsResponseDto response =
        getInstitutionReviewsService.institutionReviews(requestDto);

    // Then
    assertNotNull(response);
    verify(institutionBlockingStub).institutionReviews(eq(protoRequest));
  }

  @Test
  @DisplayName("gRPC 서버 내부 에러 발생 시 예외 처리")
  void institutionReviews_WhenGrpcInternalError() {
    // Given
    InstitutionReviewsRequestDto requestDto = createTestRequestDto();
    when(institutionBlockingStub.institutionReviews(any(InstitutionReviewsRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> getInstitutionReviewsService.institutionReviews(requestDto));
    assertTrue(exception.getMessage().contains("Error occurred in institutionReviews"));
  }

  @Test
  @DisplayName("잘못된 기관 ID로 조회 시 예외 처리")
  void institutionReviews_WhenInvalidInstitutionId() {
    // Given
    InstitutionReviewsRequestDto requestDto = createTestRequestDto();
    when(institutionBlockingStub.institutionReviews(any(InstitutionReviewsRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> getInstitutionReviewsService.institutionReviews(requestDto));
  }

  @Test
  @DisplayName("잘못된 요청 데이터로 인한 실패")
  void institutionReviews_WhenInvalidRequest() {
    // Given
    InstitutionReviewsRequestDto requestDto = createTestRequestDto();
    when(institutionBlockingStub.institutionReviews(any(InstitutionReviewsRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> getInstitutionReviewsService.institutionReviews(requestDto));
  }

  private InstitutionReviewsRequestDto createTestRequestDto() {
    return InstitutionReviewsRequestDto.builder()
        .institutionCategoryId(1)
        .pageSize(5)
        .userId("test-user-id")
        // 필요한 다른 필드들 설정
        .build();
  }

  private InstitutionReviewsResponse createTestProtoResponse() {
    return InstitutionReviewsResponse.newBuilder()
        
        .setPageInfo(
            PageInfo.newBuilder()
                .setEndCursor("1231rf")
                .setHasNextPage(true)
                .build()
        )

        .build();
  }
}
