package org.example.gongiklifeclientbegraphql.service.institution;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CreateInstitutionReviewServiceTest {

  private static final String TEST_INSTITUTION_ID = "test-institution-id";
  private static final String TEST_USER_ID = "test-user-id";

  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  @InjectMocks
  private CreateInstitutionReviewService createInstitutionReviewService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(createInstitutionReviewService, "institutionBlockingStub",
        institutionBlockingStub);
  }

  @Test
  @DisplayName("기관 리뷰 생성 성공")
  void createInstitutionReview_Success() {
    // Given
    CreateInstitutionReviewRequestDto requestDto = createTestRequestDto();
    InstitutionReviewResponse protoResponse = InstitutionReviewResponse.newBuilder()
        // 필요한 응답 데이터 설정
        .build();

    when(institutionBlockingStub.createInstitutionReview(
        eq(requestDto.toCreateInstitutionReviewRequestProto())))
        .thenReturn(protoResponse);

    // When
    CreateInstitutionReviewResponseDto response =
        createInstitutionReviewService.createInstitutionReview(requestDto);

    // Then
    assertNotNull(response);
    verify(institutionBlockingStub).createInstitutionReview(
        eq(requestDto.toCreateInstitutionReviewRequestProto()));
  }

  @Test
  @DisplayName("gRPC 서버 내부 에러 발생 시 예외 처리")
  void createInstitutionReview_WhenGrpcInternalError() {
    // Given
    CreateInstitutionReviewRequestDto requestDto = createTestRequestDto();
    when(institutionBlockingStub.createInstitutionReview(any()))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> createInstitutionReviewService.createInstitutionReview(requestDto));
    assertTrue(exception.getMessage().contains("Error occurred in createInstitutionReview"));
  }

  @Test
  @DisplayName("잘못된 기관 ID로 리뷰 생성 시도 시 예외 처리")
  void createInstitutionReview_WhenInvalidInstitutionId() {
    // Given
    CreateInstitutionReviewRequestDto requestDto = createTestRequestDto();
    when(institutionBlockingStub.createInstitutionReview(any()))
        .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> createInstitutionReviewService.createInstitutionReview(requestDto));
  }

  private CreateInstitutionReviewRequestDto createTestRequestDto() {
    return CreateInstitutionReviewRequestDto.builder()
        .institutionId(TEST_INSTITUTION_ID)
        .userId(TEST_USER_ID)
        .facilityRating(5)
        .locationRating(5)
        .staffRating(5)
        .visitorRating(5)
        .vacationFreedomRating(5)
        .mainTasks("mainTasks")
        .prosCons("prosCons")
        .averageWorkhours(480)
        .workTypeRulesId(1)
        .uniformWearingRulesId(3)
        .socialServicePeopleCountId(2)

        .build();
  }
}
