package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeleteCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeleteCommentResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentResponseDto;
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
class DeleteCommentServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private DeleteCommentService deleteCommentService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 사용하여 수동 주입합니다.
        ReflectionTestUtils.setField(deleteCommentService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() {
        // Given
        DeleteCommentRequestDto requestDto = createTestDeleteCommentRequestDto();
        DeleteCommentRequest protoRequest = requestDto.toDeleteCommentRequestProto();
        DeleteCommentResponse grpcResponse = DeleteCommentResponse.newBuilder()
                .setCommentId("test-comment-id")
                // 필요한 다른 응답 필드들을 설정 (필요 시 추가)
                .build();

        when(communityServiceBlockingStub.deleteComment(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        DeleteCommentResponseDto responseDto = deleteCommentService.deleteComment(requestDto);

        // Then
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).deleteComment(eq(protoRequest));
    }

    @Test
    @DisplayName("댓글 삭제 gRPC 에러 발생 시 예외 처리")
    void deleteComment_WhenGrpcError() {
        // Given
        DeleteCommentRequestDto requestDto = createTestDeleteCommentRequestDto();

        when(communityServiceBlockingStub.deleteComment(any(DeleteCommentRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                deleteCommentService.deleteComment(requestDto)
        );
        assertTrue(exception.getMessage().contains("DeleteCommentService"));
    }

    // 테스트용 DeleteCommentRequestDto 객체 생성 메서드
    private DeleteCommentRequestDto createTestDeleteCommentRequestDto() {
        return DeleteCommentRequestDto.builder()
                .commentId("test-comment-id")
                .userId("test-user-id")
                // 필요한 다른 필드가 있다면 추가 설정
                .build();
    }
}
