package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.createPost.CreatePostRequestDto;
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
class CreatePostServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private CreatePostService createPostService;

    @BeforeEach
    void setUp() {
        // @GrpcClient 어노테이션으로 주입되는 필드를 강제로 주입합니다.
        ReflectionTestUtils.setField(createPostService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_Success() {
        // Given: 테스트용 CreatePostRequestDto 객체 생성 및 Proto 변환
        CreatePostRequestDto requestDto = createTestRequestDto();
        CreatePostRequest protoRequest = requestDto.toCreatePostRequestProto();

        // Dummy gRPC 응답 객체 생성 (필요한 필드 값을 설정)
        CreatePostResponse grpcResponse = CreatePostResponse.newBuilder()
                .setId("test-post-id")
                .setTitle("Test Title")
                .setContent("Test content")
                .build();

        // gRPC 클라이언트 스텁 모킹: 예상 Proto 요청에 대해 grpcResponse를 반환하도록 설정
        when(communityServiceBlockingStub.createPost(eq(protoRequest))).thenReturn(grpcResponse);

        // When: Service 메서드 호출
        PostResponseDto responseDto = createPostService.createPost(requestDto);

        // Then: 응답 DTO가 null이 아니며, stub 호출이 올바르게 수행되었음을 검증
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).createPost(eq(protoRequest));
    }

    @Test
    @DisplayName("게시글 생성 gRPC 에러 발생 시 예외 처리")
    void createPost_WhenGrpcError() {
        // Given: 테스트용 CreatePostRequestDto 객체 생성
        CreatePostRequestDto requestDto = createTestRequestDto();

        // gRPC 호출 시 Status.INTERNAL 예외를 발생시키도록 모킹
        when(communityServiceBlockingStub.createPost(any(CreatePostRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then: ServiceExceptionHandlingUtil.handle()에 의해 예외가 발생하며, "CreatePostService" 키워드가 포함된 메시지를 검증
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                createPostService.createPost(requestDto)
        );
        assertTrue(exception.getMessage().contains("CreatePostService"));
    }

    // 테스트용 CreatePostRequestDto 객체를 생성하는 메서드
    private CreatePostRequestDto createTestRequestDto() {
        return CreatePostRequestDto.builder()
                .userId("test-user-id")
                .categoryId(1)
                .title("Test Title")
                .content("Test content")
                // 추가 필드가 있다면 이곳에 설정합니다.
                .build();
    }
}
