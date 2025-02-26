package org.example.gongiklifeclientbegraphql.service.institution;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class InstitutionReviewsByInstitutionServiceTest {

  private static final String TEST_INSTITUTION_ID = "test-institution-id";

  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  @InjectMocks
  private InstitutionReviewsByInstitutionService institutionReviewsByInstitutionService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(institutionReviewsByInstitutionService, "institutionBlockingStub",
        institutionBlockingStub);
  }

  @Test
  @DisplayName("기관 리뷰 목록 조회 성공")
  void institutionReviewsByInstitution_Success() {
    // Given
    InstitutionReviewsByInstitutionRequestDto requestDto = createTestRequestDto();
    InstitutionReviewsByInstitutionRequest protoRequest = requestDto.toProto();
    InstitutionReviewsByInstitutionResponse protoResponse = createTestProtoResponse();

    when(institutionBlockingStub.institutionReviewsByInstitution(eq(protoRequest)))
        .thenReturn(protoResponse);

    // When
    InstitutionReviewsByInstitutionResponseDto response =
        institutionReviewsByInstitutionService.institutionReviewsByInstitution(requestDto);

    // Then
    assertNotNull(response);
    verify(institutionBlockingStub).institutionReviewsByInstitution(eq(protoRequest));
  }

  @Test
  @DisplayName("gRPC 서버 내부 에러 발생 시 예외 처리")
  void institutionReviewsByInstitution_WhenGrpcInternalError() {
    // Given
    InstitutionReviewsByInstitutionRequestDto requestDto = createTestRequestDto();
    when(institutionBlockingStub.institutionReviewsByInstitution(any()))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> institutionReviewsByInstitutionService.institutionReviewsByInstitution(requestDto));
    assertTrue(
        exception.getMessage().contains("Error occurred in institutionReviewsByInstitution"));
  }

  @Test
  @DisplayName("잘못된 기관 ID로 조회 시 예외 처리")
  void institutionReviewsByInstitution_WhenInvalidInstitutionId() {
    // Given
    InstitutionReviewsByInstitutionRequestDto requestDto = createTestRequestDto();
    when(institutionBlockingStub.institutionReviewsByInstitution(any()))
        .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> institutionReviewsByInstitutionService.institutionReviewsByInstitution(requestDto));
  }

  private InstitutionReviewsByInstitutionRequestDto createTestRequestDto() {
    return InstitutionReviewsByInstitutionRequestDto.builder()
        .institutionId(TEST_INSTITUTION_ID)
        .pageSize(10)
        .build();
  }

  private InstitutionReviewsByInstitutionResponse createTestProtoResponse() {
    return InstitutionReviewsByInstitutionResponse.newBuilder()
        // 필요한 응답 데이터 설정

        .setPageInfo(PageInfo.newBuilder()
            .setHasNextPage(true)
            .setEndCursor("test-end-cursor")
            .build())
        .build();
  }
}
