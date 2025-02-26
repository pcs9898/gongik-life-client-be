package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CreateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CreateCommentResponseDto;
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
class CreateCommentServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private CreateCommentService createCommentService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 통해 수동으로 주입합니다.
        ReflectionTestUtils.setField(createCommentService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_Success() {
        // Given
        CreateCommentRequestDto requestDto = createTestRequestDto();
        CreateCommentRequest protoRequest = requestDto.toCreateCommentRequestProto();
        // dummy gRPC 응답 객체 생성. 필요한 필드들(예: commentId, content 등)을 설정합니다.
        CreateCommentResponse grpcResponse = CreateCommentResponse.newBuilder()
                .setId("dummy-id")
                .setContent("테스트 댓글 내용")
                .build();

        when(communityServiceBlockingStub.createComment(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        CreateCommentResponseDto responseDto = createCommentService.createComment(requestDto);

        // Then
        assertNotNull(responseDto);
        // 필요에 따라 responseDto의 필드 값을 추가 검증할 수 있습니다.
        verify(communityServiceBlockingStub).createComment(eq(protoRequest));
    }

    @Test
    @DisplayName("gRPC 서버 에러 발생 시 댓글 생성 예외 처리")
    void createComment_WhenGrpcError() {
        // Given
        CreateCommentRequestDto requestDto = createTestRequestDto();

        when(communityServiceBlockingStub.createComment(any(CreateCommentRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                createCommentService.createComment(requestDto));
        assertTrue(exception.getMessage().contains("CreateCommentService"));
    }

    // 테스트용 CreateCommentRequestDto 객체 생성 메서드
    private CreateCommentRequestDto createTestRequestDto() {
        // 필요한 값들을 채워서 객체를 생성합니다.
        return CreateCommentRequestDto.builder()
                .postId("test-post-id")
                .userId("test-user-id")
                .content("테스트 댓글 내용")
                // 추가 필드가 있다면 설정
                .build();
    }
}
