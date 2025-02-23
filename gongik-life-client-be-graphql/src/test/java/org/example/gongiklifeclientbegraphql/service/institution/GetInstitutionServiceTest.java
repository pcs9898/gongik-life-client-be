package org.example.gongiklifeclientbegraphql.service.institution;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionReviewCountRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionReviewCountResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.service.InstitutionCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GetInstitutionServiceTest {

  private static final String TEST_INSTITUTION_ID = "test-institution-id";

  @Mock
  private InstitutionCacheService institutionCacheService;

  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  @InjectMocks
  private GetInstitutionService getInstitutionService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(getInstitutionService, "institutionBlockingStub",
        institutionBlockingStub);
  }

  @Test
  @DisplayName("기관 정보 조회 성공")
  void institution_Success() {
    // Given
    InstitutionRequestDto requestDto = new InstitutionRequestDto(TEST_INSTITUTION_ID);
    InstitutionResponseDto cachedResponse = createTestInstitutionResponseDto();
    GetInstitutionReviewCountResponse reviewCountResponse = GetInstitutionReviewCountResponse.newBuilder()
        .setReviewCount(5)
        .build();

    when(institutionCacheService.getInstitution(TEST_INSTITUTION_ID))
        .thenReturn(cachedResponse);
    when(institutionBlockingStub.getInstitutionReviewCount(
        any(GetInstitutionReviewCountRequest.class)))
        .thenReturn(reviewCountResponse);

    // When
    InstitutionResponseDto response = getInstitutionService.institution(requestDto);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(TEST_INSTITUTION_ID, response.getId()),
        () -> assertEquals("Test Institution", response.getName()),
        () -> assertEquals(5, response.getReviewCount())
    );
    verify(institutionCacheService).getInstitution(TEST_INSTITUTION_ID);
    verify(institutionBlockingStub).getInstitutionReviewCount(
        any(GetInstitutionReviewCountRequest.class));
  }

  @Test
  @DisplayName("캐시 서비스 에러 발생 시 예외 처리")
  void institution_WhenCacheServiceFails() {
    // Given
    InstitutionRequestDto requestDto = new InstitutionRequestDto(TEST_INSTITUTION_ID);
    when(institutionCacheService.getInstitution(TEST_INSTITUTION_ID))
        .thenThrow(new RuntimeException("Cache service error"));

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> getInstitutionService.institution(requestDto));
    assertTrue(exception.getMessage().contains("Error occurred in GetInstitutionService"));
  }

  @Test
  @DisplayName("리뷰 카운트 조회 실패 시 예외 처리")
  void institution_WhenReviewCountFails() {
    // Given
    InstitutionRequestDto requestDto = new InstitutionRequestDto(TEST_INSTITUTION_ID);
    InstitutionResponseDto cachedResponse = createTestInstitutionResponseDto();

    when(institutionCacheService.getInstitution(TEST_INSTITUTION_ID))
        .thenReturn(cachedResponse);
    when(institutionBlockingStub.getInstitutionReviewCount(
        any(GetInstitutionReviewCountRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> getInstitutionService.institution(requestDto));
  }

  private InstitutionResponseDto createTestInstitutionResponseDto() {
    InstitutionResponseDto responseDto = new InstitutionResponseDto();
    responseDto.setId(TEST_INSTITUTION_ID);
    responseDto.setName("Test Institution");
    // 필요한 다른 필드들 설정
    return responseDto;
  }
}
