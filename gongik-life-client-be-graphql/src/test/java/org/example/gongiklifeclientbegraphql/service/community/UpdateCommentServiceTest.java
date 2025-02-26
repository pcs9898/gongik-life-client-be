package org.example.gongiklifeclientbegraphql.service.community;


import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentResponseDto;
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
public class UpdateCommentServiceTest {


    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private UpdateCommentService updateCommentService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드가 제대로 주입되도록 ReflectionTestUtils를 사용
        ReflectionTestUtils.setField(updateCommentService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() {
        // Given
        UpdateCommentRequestDto requestDto = createTestUpdateCommentRequestDto();
        UpdateCommentRequest protoRequest = requestDto.toUpdateCommentRequestProto();

        // Dummy gRPC 응답 객체 생성 (필요한 필드들을 설정)
        UpdateCommentResponse grpcResponse = UpdateCommentResponse.newBuilder()
                .setId("test-comment-id")
                .setSuccess(true)
                .build();

        when(communityServiceBlockingStub.updateComment(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        UpdateCommentResponseDto responseDto = updateCommentService.updateComment(requestDto);

        // Then
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).updateComment(eq(protoRequest));
    }

    @Test
    @DisplayName("댓글 수정 gRPC 에러 발생 시 예외 처리")
    void updateComment_WhenGrpcError() {
        // Given
        UpdateCommentRequestDto requestDto = createTestUpdateCommentRequestDto();

        when(communityServiceBlockingStub.updateComment(any(UpdateCommentRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> updateCommentService.updateComment(requestDto));
        assertTrue(exception.getMessage().contains("UpdateCommentService"));
    }

    // 테스트용 UpdateCommentRequestDto 객체 생성 메서드
    private UpdateCommentRequestDto createTestUpdateCommentRequestDto() {
        return UpdateCommentRequestDto.builder()
                .commentId("test-comment-id")
                .userId("test-user-id")
                .content("수정된 댓글 내용")
                .build();
    }
}