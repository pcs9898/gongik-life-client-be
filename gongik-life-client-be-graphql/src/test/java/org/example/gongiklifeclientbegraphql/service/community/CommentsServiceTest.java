package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentForList;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostUser;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.common.PostUserDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CommentForListDto;
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
class CommentsServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private CommentsService commentsService;

    @BeforeEach
    void setUp() {
// @GrpcClient로 주입되는 필드가 있을 경우 ReflectionTestUtils로 주입합니다.
        ReflectionTestUtils.setField(commentsService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("댓글 조회 성공")
    void comments_Success() {
// Given
        CommentsRequestDto requestDto = createTestCommentsRequestDto();
        CommentsRequest protoRequest = requestDto.toCommentsRequestProto();
        CommentsResponse protoResponse = createTestGrpcCommentsResponse();


        when(communityServiceBlockingStub.comments(eq(protoRequest)))
                .thenReturn(protoResponse);

// When
        CommentsResponseDto responseDto = commentsService.comments(requestDto);

// Then
        assertNotNull(responseDto);
        assertNotNull(responseDto.getListComment());
        assertTrue(responseDto.getListComment().size() > 0);

// 변환된 첫 번째 댓글의 필드 검증
        CommentForListDto commentDto = responseDto.getListComment().get(0);
        assertEquals("comment-1", commentDto.getId());
        assertEquals("post-1", commentDto.getPostId());
        assertEquals("This is a test comment", commentDto.getContent());
        assertEquals("2025-02-26T12:00:00", commentDto.getCreatedAt());
        assertNotNull(commentDto.getUser());
        PostUserDto userDto = commentDto.getUser();
        assertEquals("test-user-id", userDto.getUserId());
        assertEquals("Test User", userDto.getUserName());

        verify(communityServiceBlockingStub).comments(eq(protoRequest));
    }

    @Test
    @DisplayName("gRPC 서버 내부 에러 발생 시 댓글 조회 예외 처리")
    void comments_WhenGrpcInternalError() {
// Given
        CommentsRequestDto requestDto = createTestCommentsRequestDto();


        when(communityServiceBlockingStub.comments(any(CommentsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

// When & Then
        Exception exception = assertThrows(RuntimeException.class,
                () -> commentsService.comments(requestDto));
        assertTrue(exception.getMessage().contains("CommentsService"));
    }

    @Test
    @DisplayName("잘못된 요청 데이터로 인한 실패")
    void comments_WhenInvalidRequest() {
// Given
        CommentsRequestDto requestDto = createTestCommentsRequestDto();


        when(communityServiceBlockingStub.comments(any(CommentsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

// When & Then
        assertThrows(RuntimeException.class,
                () -> commentsService.comments(requestDto));
    }

    // 테스트용 CommentsRequestDto 생성 메서드
    private CommentsRequestDto createTestCommentsRequestDto() {
        return CommentsRequestDto.builder()
// 필요한 필드를 설정합니다. (예시로 postId를 설정)
                .postId("post-1")
                .build();
    }

    // 테스트용 dummy gRPC CommentsResponse 생성 메서드
    private CommentsResponse createTestGrpcCommentsResponse() {
        PostUser grpcUser = PostUser.newBuilder()
                .setUserId("test-user-id")
                .setUserName("Test User")
                .build();


        CommentForList grpcComment = CommentForList.newBuilder()
                .setId("comment-1")
                .setPostId("post-1")
                .setContent("This is a test comment")
                .setCreatedAt("2025-02-26T12:00:00")
                .setParentCommentId("")  // 부모 댓글이 없는 경우
                .setUser(grpcUser)
                .build();

        return CommentsResponse.newBuilder()
                .addListComment(grpcComment)
                .build();
    }
}