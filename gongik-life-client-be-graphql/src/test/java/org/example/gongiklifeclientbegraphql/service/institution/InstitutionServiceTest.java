package org.example.gongiklifeclientbegraphql.service.institution;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetMyAverageWorkhoursRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetMyAverageWorkhoursResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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
class InstitutionServiceTest {

    @Mock
    private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

    @Mock
    private LikeInstitutionReviewService institutionCacheService;

    @InjectMocks
    private InstitutionService institutionService;

    @BeforeEach
    void setUp() {
        // @InjectMocks를 사용하더라도 @GrpcClient 어노테이션 등으로 인한 주입 이슈가 있을 경우 ReflectionTestUtils를 활용할 수 있습니다.
        ReflectionTestUtils.setField(institutionService, "institutionBlockingStub", institutionBlockingStub);
    }

    @Test
    @DisplayName("기관 좋아요 여부 조회 성공")
    void isLikedInstitutionReview_Success() {
        // Given
        String testReviewId = "test-review-id";
        String testUserId = "test-user-id";
        IsLikedInstitutionReviewRequest expectedRequest =
                IsLikedInstitutionReviewRequest.newBuilder()
                        .setInstitutionReviewId(testReviewId)
                        .setUserId(testUserId)
                        .build();
        IsLikedInstitutionReviewResponse grpcResponse =
                IsLikedInstitutionReviewResponse.newBuilder()
                        .setIsLiked(true)
                        .build();

        when(institutionBlockingStub.isLikedInstitutionReview(eq(expectedRequest)))
                .thenReturn(grpcResponse);

        // When
        Boolean isLiked = institutionService.isLikedInstitutionReview(testReviewId, testUserId);

        // Then
        assertNotNull(isLiked);
        assertTrue(isLiked);
        verify(institutionBlockingStub).isLikedInstitutionReview(eq(expectedRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 기관 좋아요 여부 조회 예외 처리")
    void isLikedInstitutionReview_WhenGrpcError() {
        // Given
        String testReviewId = "test-review-id";
        String testUserId = "test-user-id";

        when(institutionBlockingStub.isLikedInstitutionReview(any()))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                institutionService.isLikedInstitutionReview(testReviewId, testUserId)
        );
        assertTrue(exception.getMessage().contains("isLikedInstitutionReviewInstitutionService"));
    }

    @Test
    @DisplayName("내 평균 근무시간 조회 성공")
    void getMyAverageWorkhours_Success() {
        // Given
        String testUserId = "test-user-id";
        String testInstitutionId = "test-institution-id";
        GetMyAverageWorkhoursRequest expectedRequest =
                GetMyAverageWorkhoursRequest.newBuilder()
                        .setUserId(testUserId)
                        .setInstitutionId(testInstitutionId)
                        .build();
        GetMyAverageWorkhoursResponse grpcResponse =
                GetMyAverageWorkhoursResponse.newBuilder()
                        .setMyAverageWorkhours(480)
                        .build();

        when(institutionBlockingStub.getMyAverageWorkhours(eq(expectedRequest)))
                .thenReturn(grpcResponse);

        // When
        Integer averageWorkhours = institutionService.getMyAverageWorkhours(testUserId, testInstitutionId);

        // Then
        assertNotNull(averageWorkhours);
        assertEquals(480, averageWorkhours);
        verify(institutionBlockingStub).getMyAverageWorkhours(eq(expectedRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 내 평균 근무시간 조회 예외 처리")
    void getMyAverageWorkhours_WhenGrpcError() {
        // Given
        String testUserId = "test-user-id";
        String testInstitutionId = "test-institution-id";

        when(institutionBlockingStub.getMyAverageWorkhours(any()))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                institutionService.getMyAverageWorkhours(testUserId, testInstitutionId)
        );
        assertTrue(exception.getMessage().contains("getMyAverageWorkhoursInstitutionService"));
    }
}
