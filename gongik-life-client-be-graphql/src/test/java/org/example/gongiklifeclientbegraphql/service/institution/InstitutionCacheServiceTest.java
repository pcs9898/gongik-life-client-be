package org.example.gongiklifeclientbegraphql.service.institution;


import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstitutionCacheServiceTest {


    @Mock
    private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

    @InjectMocks
    private InstitutionCacheService institutionCacheService;

    @BeforeEach
    void setUp() {
        // @Cacheable 어노테이션 등으로 인한 주입 이슈가 있을 경우 ReflectionTestUtils를 사용하여 주입합니다.
        ReflectionTestUtils.setField(institutionCacheService, "institutionBlockingStub", institutionBlockingStub);
    }

    @Test
    @DisplayName("기관 리뷰 조회 성공")
    void getInstitutionReview_Success() {
        // Given
        String testReviewId = "test-review-id";
        InstitutionServiceOuterClass.InstitutionReviewRequest expectedRequest = InstitutionServiceOuterClass.InstitutionReviewRequest.newBuilder()
                .setInstitutionReviewId(testReviewId)
                .build();

        // 필요한 필드 값을 설정하여 dummy 프로토콜 응답 생성
        InstitutionServiceOuterClass.InstitutionReviewResponse protoResponse = InstitutionServiceOuterClass.InstitutionReviewResponse.newBuilder()
                // 예: .setSomeField(value)
                .build();

        when(institutionBlockingStub.institutionReview(eq(expectedRequest)))
                .thenReturn(protoResponse);

        // When
        InstitutionReviewResponseDto responseDto = institutionCacheService.getInstitutionReview(testReviewId);

        // Then
        assertNotNull(responseDto);
        // fromInstitutionReviewResponseProto() 메서드 내부의 변환 로직에 따라 dto 객체가 생성됩니다.
        verify(institutionBlockingStub).institutionReview(eq(expectedRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 기관 리뷰 조회 예외 처리")
    void getInstitutionReview_WhenGrpcError() {
        // Given
        String testReviewId = "test-review-id";
        when(institutionBlockingStub.institutionReview(any()))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                institutionCacheService.getInstitutionReview(testReviewId)
        );
        assertTrue(exception.getMessage().contains("InstitutionCacheService"));
    }

    @Test
    @DisplayName("기관 정보 조회 성공")
    void getInstitution_Success() {
        // Given
        String testInstitutionId = "test-institution-id";
        InstitutionServiceOuterClass.InstitutionRequest expectedRequest = InstitutionServiceOuterClass.InstitutionRequest.newBuilder()
                .setInstitutionId(testInstitutionId)
                .build();

        InstitutionServiceOuterClass.InstitutionResponse protoResponse = InstitutionServiceOuterClass.InstitutionResponse.newBuilder()
                // 예: .setSomeField(value)
                .build();

        when(institutionBlockingStub.institution(eq(expectedRequest)))
                .thenReturn(protoResponse);

        // When
        InstitutionResponseDto responseDto = institutionCacheService.getInstitution(testInstitutionId);

        // Then
        assertNotNull(responseDto);
        verify(institutionBlockingStub).institution(eq(expectedRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 기관 정보 조회 예외 처리")
    void getInstitution_WhenGrpcError() {
        // Given
        String testInstitutionId = "test-institution-id";
        when(institutionBlockingStub.institution(any()))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                institutionCacheService.getInstitution(testInstitutionId)
        );
        // 예외 메시지에 "institution" 키워드가 포함되어야 합니다.
        assertTrue(exception.getMessage().contains("institution"));
    }
}