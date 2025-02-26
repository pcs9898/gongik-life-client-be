package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.deletePost.DeletePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deletePost.DeletePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.updatepost.UpdatePostRequestDto;
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
class CommunityCacheServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private CommunityCacheService communityCacheService;

    @BeforeEach
    void setUp() {
        // @GrpcClient 어노테이션으로 주입되는 필드를 ReflectionTestUtils로 설정합니다[1]
        ReflectionTestUtils.setField(communityCacheService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("updatePost 성공")
    void updatePost_Success() {
        // Given
        String testPostId = "test-post-id";
        String testUserId = "test-user-id";
        UpdatePostRequestDto requestDto = createTestUpdatePostRequestDto(testPostId, testUserId);
        UpdatePostRequest protoRequest = requestDto.toUpdatePostRequestProto();
        UpdatePostResponse grpcResponse = UpdatePostResponse.newBuilder()
                // 필요한 응답 데이터 설정 (예: dummy 값)
                .build();

        when(communityServiceBlockingStub.updatePost(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        PostResponseDto responseDto = communityCacheService.updatePost(requestDto);

        // Then
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).updatePost(eq(protoRequest));
    }

    @Test
    @DisplayName("updatePost gRPC 에러 시 예외 처리")
    void updatePost_WhenGrpcError() {
        // Given
        String testPostId = "test-post-id";
        String testUserId = "test-user-id";
        UpdatePostRequestDto requestDto = createTestUpdatePostRequestDto(testPostId, testUserId);

        when(communityServiceBlockingStub.updatePost(any()))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                communityCacheService.updatePost(requestDto));
        assertTrue(exception.getMessage().contains("UpdatePostService"));
    }

    @Test
    @DisplayName("deletePost 성공")
    void deletePost_Success() {
        // Given
        String testPostId = "test-post-id";
        String testUserId = "test-user-id";
        DeletePostRequestDto requestDto = createTestDeletePostRequestDto(testPostId, testUserId);
        DeletePostRequest protoRequest = requestDto.toDeletePostRequestProto();
        DeletePostResponse grpcResponse = DeletePostResponse.newBuilder()
                // 필요한 응답 데이터 설정
                .build();

        when(communityServiceBlockingStub.deletePost(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        DeletePostResponseDto responseDto = communityCacheService.deletePost(requestDto);

        // Then
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).deletePost(eq(protoRequest));
    }

    @Test
    @DisplayName("deletePost gRPC 에러 시 예외 처리")
    void deletePost_WhenGrpcError() {
        // Given
        String testPostId = "test-post-id";
        String testUserId = "test-user-id";
        DeletePostRequestDto requestDto = createTestDeletePostRequestDto(testPostId, testUserId);

        when(communityServiceBlockingStub.deletePost(any()))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                communityCacheService.deletePost(requestDto));
        assertTrue(exception.getMessage().contains("DeletePostService"));
    }

    @Test
    @DisplayName("getPost 성공")
    void getPost_Success() {
        // Given
        String testPostId = "test-post-id";

        GetPostRequest expectedRequest = GetPostRequest.newBuilder().setPostId(testPostId).build();
        GetPostResponse grpcResponse = GetPostResponse.newBuilder()
                // 예: 응답 변환을 위한 dummy 값 설정
                .build();

        when(communityServiceBlockingStub.getPost(eq(expectedRequest)))
                .thenReturn(grpcResponse);

        // When
        PostResponseDto responseDto = communityCacheService.getPost(testPostId);

        // Then
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).getPost(eq(expectedRequest));
    }

    @Test
    @DisplayName("getPost gRPC 에러 시 예외 처리")
    void getPost_WhenGrpcError() {
        // Given
        String testPostId = "test-post-id";


        when(communityServiceBlockingStub.getPost(any()))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                communityCacheService.getPost(testPostId));
        assertTrue(exception.getMessage().contains("GetPostService"));
    }

    // 테스트용 UpdatePostRequestDto 생성 메서드
    private UpdatePostRequestDto createTestUpdatePostRequestDto(String postId, String testUserId) {
        return UpdatePostRequestDto.builder()
                .postId(postId)
                .userId(testUserId)
                // 필요한 추가 필드들 설정
                .build();
    }

    // 테스트용 DeletePostRequestDto 생성 메서드
    private DeletePostRequestDto createTestDeletePostRequestDto(String postId, String testUserId) {
        return DeletePostRequestDto.builder()
                .postId(postId)
                .userId(testUserId)
                // 필요한 추가 필드들 설정
                .build();
    }
}
