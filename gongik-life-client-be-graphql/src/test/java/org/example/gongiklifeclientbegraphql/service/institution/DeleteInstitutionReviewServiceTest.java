package org.example.gongiklifeclientbegraphql.service.institution;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DeleteInstitutionReviewServiceTest {

  private static final String TEST_REVIEW_ID = "test-review-id";
  private static final String TEST_USER_ID = "test-user-id";

  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  @InjectMocks
  private DeleteInstitutionReviewService deleteInstitutionReviewService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(deleteInstitutionReviewService, "institutionBlockingStub",
        institutionBlockingStub);
  }

  @Test
  @DisplayName("기관 리뷰 삭제 성공")
  void deleteInstitutionReview_Success() {
    // Given
    DeleteInstitutionReviewRequestDto requestDto = createTestRequestDto();
    DeleteInstitutionReviewResponse protoResponse = DeleteInstitutionReviewResponse.newBuilder()
        .setSuccess(true)
        .build();

    when(institutionBlockingStub.deleteInstitutionReview(eq(requestDto.toProto())))
        .thenReturn(protoResponse);

    // When
    DeleteInstitutionReviewResponseDto response =
        deleteInstitutionReviewService.deleteInstitutionReview(requestDto);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(TEST_REVIEW_ID, response.getInstitutionReviewId()),
        () -> assertTrue(response.getSuccess())
    );
    verify(institutionBlockingStub).deleteInstitutionReview(eq(requestDto.toProto()));
  }

  @Test
  @DisplayName("기관 리뷰 삭제 실패")
  void deleteInstitutionReview_WhenDeleteFails() {
    // Given
    DeleteInstitutionReviewRequestDto requestDto = createTestRequestDto();
    DeleteInstitutionReviewResponse protoResponse = DeleteInstitutionReviewResponse.newBuilder()
        .setSuccess(false)
        .build();

    when(institutionBlockingStub.deleteInstitutionReview(eq(requestDto.toProto())))
        .thenReturn(protoResponse);

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> deleteInstitutionReviewService.deleteInstitutionReview(requestDto));
    assertEquals("Error occurred in deleteInstitutionReview : Failed to delete institution review",
        exception.getMessage());
  }

  @Test
  @DisplayName("gRPC 서버 내부 에러 발생 시 예외 처리")
  void deleteInstitutionReview_WhenGrpcInternalError() {
    // Given
    DeleteInstitutionReviewRequestDto requestDto = createTestRequestDto();
    when(institutionBlockingStub.deleteInstitutionReview(any()))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> deleteInstitutionReviewService.deleteInstitutionReview(requestDto));
    assertTrue(exception.getMessage().contains("Error occurred in deleteInstitutionReview"));
  }

  @Test
  @DisplayName("권한이 없는 사용자의 삭제 시도")
  void deleteInstitutionReview_WhenUnauthorized() {
    // Given
    DeleteInstitutionReviewRequestDto requestDto = createTestRequestDto();
    when(institutionBlockingStub.deleteInstitutionReview(any()))
        .thenThrow(new StatusRuntimeException(Status.PERMISSION_DENIED));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> deleteInstitutionReviewService.deleteInstitutionReview(requestDto));
  }

  private DeleteInstitutionReviewRequestDto createTestRequestDto() {
    return DeleteInstitutionReviewRequestDto.builder()
        .institutionReviewId(TEST_REVIEW_ID)
        .userId(TEST_USER_ID)
        .build();
  }
}
