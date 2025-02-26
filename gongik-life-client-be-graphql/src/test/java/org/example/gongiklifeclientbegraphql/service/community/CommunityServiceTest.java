package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.post.PostRequestDto;
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
class CommunityServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private CommunityService communityService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드가 올바르게 반영되도록 ReflectionTestUtils를 사용합니다.
        ReflectionTestUtils.setField(communityService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("게시글 좋아요 여부 확인 성공")
    void isLikedPost_Success() {
        // Given
        String testPostId = "test-post-id";
        String testUserId = "test-user-id";
        IsLikedPostRequest expectedRequest = IsLikedPostRequest.newBuilder()
                .setPostId(testPostId)
                .setUserId(testUserId)
                .build();
        IsLikedPostResponse grpcResponse = IsLikedPostResponse.newBuilder()
                .setIsLiked(true)
                .build();

        when(communityServiceBlockingStub.isLikedPost(eq(expectedRequest)))
                .thenReturn(grpcResponse);

        // When
        Boolean isLiked = communityService.isLikedPost(testPostId, testUserId);

        // Then
        assertNotNull(isLiked);
        assertTrue(isLiked);
        verify(communityServiceBlockingStub).isLikedPost(eq(expectedRequest));
    }

    @Test
    @DisplayName("게시글 좋아요 여부 확인 gRPC 에러 발생 시 예외 처리")
    void isLikedPost_WhenGrpcError() {
        // Given
        String testPostId = "test-post-id";
        String testUserId = "test-user-id";

        when(communityServiceBlockingStub.isLikedPost(any(IsLikedPostRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                communityService.isLikedPost(testPostId, testUserId)
        );
        // 코드 내 ServiceExceptionHandlingUtil.handle()에 의해 "isLiedPostService" 키워드가 포함된 예외가 던져져야 합니다.
        assertTrue(exception.getMessage().contains("isLikedPostService"));
    }

    @Test
    @DisplayName("게시글 좋아요 및 댓글 개수 확인 성공")
    void isLikedPostAndCommentCount_Success() {
        // Given
        PostRequestDto requestDto = createTestPostRequestDto();
        // 요청 DTO가 Proto 변환을 수행하는 메서드를 통해 생성된 객체로 stub 호출됩니다.
        IsLikedPostAndCommentCountResponse grpcResponse = IsLikedPostAndCommentCountResponse.newBuilder()
                // 필요한 응답 필드를 설정
                .build();

        when(communityServiceBlockingStub.isLikedPostAndCommentCount(eq(requestDto.toIsLikedPostAndCommentCountRequestProto())))
                .thenReturn(grpcResponse);

        // When
        IsLikedPostAndCommentCountResponse response = communityService.isLikedPostAndCommentCount(requestDto);

        // Then
        assertNotNull(response);
        verify(communityServiceBlockingStub)
                .isLikedPostAndCommentCount(eq(requestDto.toIsLikedPostAndCommentCountRequestProto()));
    }

    @Test
    @DisplayName("게시글 좋아요 및 댓글 개수 확인 gRPC 에러 발생 시 예외 처리")
    void isLikedPostAndCommentCount_WhenGrpcError() {
        // Given
        PostRequestDto requestDto = createTestPostRequestDto();

        when(communityServiceBlockingStub.isLikedPostAndCommentCount(any()))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                communityService.isLikedPostAndCommentCount(requestDto)
        );
        assertTrue(exception.getMessage().contains("isLikedPostAndCommentCountService"));
    }

    // 테스트용 PostRequestDto 생성 메서드 (내부 필드들에 대해 필요한 값을 설정)
    private PostRequestDto createTestPostRequestDto() {
        // PostRequestDto가 builder 패턴을 제공한다고 가정합니다.
        return PostRequestDto.builder()
                .postId("test-post-id")
                // 필요시 다른 필드도 설정
                .build();
    }
}
