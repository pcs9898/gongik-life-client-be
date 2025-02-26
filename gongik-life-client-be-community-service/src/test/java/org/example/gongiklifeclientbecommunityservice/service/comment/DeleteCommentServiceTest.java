package org.example.gongiklifeclientbecommunityservice.service.comment;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.example.gongiklifeclientbecommunityservice.service.post.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCommentServiceTest {

    private static final String TEST_COMMENT_ID = "123e4567-e89b-12d3-a456-426614174010";
    private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174011";
    private static final String TEST_OTHER_USER_ID = "123e4567-e89b-12d3-a456-426614174012";
    private static final String TEST_POST_ID = "123e4567-e89b-12d3-a456-426614174013";

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private DeleteCommentService deleteCommentService;

    // 헬퍼 메소드: DeleteCommentRequest 객체 생성
    private CommunityServiceOuterClass.DeleteCommentRequest createTestRequest(String commentId, String userId) {
        return CommunityServiceOuterClass.DeleteCommentRequest.newBuilder()
                .setCommentId(commentId)
                .setUserId(userId)
                .build();
    }

    // 헬퍼 메소드: 테스트용 Comment 객체 생성
    private Comment createTestComment() {
        Comment comment = new Comment();
        comment.setId(UUID.fromString(TEST_COMMENT_ID));
        comment.setUserId(UUID.fromString(TEST_USER_ID));
        comment.setDeletedAt(null);
        Post post = new Post();
        post.setId(UUID.fromString(TEST_POST_ID));
        comment.setPost(post);
        return comment;
    }

    @Test
    @DisplayName("성공: 댓글 삭제")
    void deleteComment_success() {
        // Given
        CommunityServiceOuterClass.DeleteCommentRequest request =
                createTestRequest(TEST_COMMENT_ID, TEST_USER_ID);
        Comment comment = createTestComment();

        when(commentRepository.findByIdAndDeletedAtIsNull(UUID.fromString(TEST_COMMENT_ID)))
                .thenReturn(Optional.of(comment));
        // 댓글 소프트 삭제 시 save() 호출하여 deletedAt 필드가 채워진다고 가정
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(postService).minusCommentCountById(any(UUID.class));

        // When
        CommunityServiceOuterClass.DeleteCommentResponse response = deleteCommentService.deleteComment(request);

        // Then
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(TEST_COMMENT_ID, response.getCommentId());
        verify(commentRepository).findByIdAndDeletedAtIsNull(UUID.fromString(TEST_COMMENT_ID));
        verify(commentRepository).save(any(Comment.class));
        verify(postService).minusCommentCountById(UUID.fromString(TEST_POST_ID));
    }

    @Test
    @DisplayName("실패: 잘못된 댓글 ID 형식")
    void deleteComment_invalidCommentIdFormat() {
        // Given: 유효하지 않은 UUID 형식의 댓글 ID
        CommunityServiceOuterClass.DeleteCommentRequest request =
                createTestRequest("invalid-uuid", TEST_USER_ID);

        // When & Then: parseUUID에서 예외 발생
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                deleteCommentService.deleteComment(request)
        );
        assertTrue(exception.getMessage().contains("잘못된 댓글 ID 형식"));
    }

    @Test
    @DisplayName("실패: 댓글을 찾을 수 없음")
    void deleteComment_commentNotFound() {
        // Given
        CommunityServiceOuterClass.DeleteCommentRequest request =
                createTestRequest(TEST_COMMENT_ID, TEST_USER_ID);

        when(commentRepository.findByIdAndDeletedAtIsNull(UUID.fromString(TEST_COMMENT_ID)))
                .thenReturn(Optional.empty());

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                deleteCommentService.deleteComment(request)
        );
        assertTrue(exception.getMessage().contains("댓글을 찾을 수 없습니다"));
    }

    @Test
    @DisplayName("실패: 댓글 소유권 없음")
    void deleteComment_invalidOwnership() {
        // Given: 댓글 작성자와 다른 사용자 요청
        CommunityServiceOuterClass.DeleteCommentRequest request =
                createTestRequest(TEST_COMMENT_ID, TEST_OTHER_USER_ID);
        Comment comment = createTestComment(); // comment의 userId는 TEST_USER_ID

        when(commentRepository.findByIdAndDeletedAtIsNull(UUID.fromString(TEST_COMMENT_ID)))
                .thenReturn(Optional.of(comment));

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                deleteCommentService.deleteComment(request)
        );
        assertTrue(exception.getMessage().contains("권한이 없습니다. 자신의 댓글만 삭제할 수 있습니다."));
    }

    @Test
    @DisplayName("성공: 게시물 댓글 수 감소 실패해도 댓글 삭제 처리")
    void deleteComment_postCountDecrementFailure() {
        // Given
        CommunityServiceOuterClass.DeleteCommentRequest request =
                createTestRequest(TEST_COMMENT_ID, TEST_USER_ID);
        Comment comment = createTestComment();

        when(commentRepository.findByIdAndDeletedAtIsNull(UUID.fromString(TEST_COMMENT_ID)))
                .thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // 게시물 댓글 수 감소 시 예외 발생하더라도 전체 트랜잭션은 롤백되지 않음
        doThrow(new RuntimeException("post count error")).when(postService)
                .minusCommentCountById(any(UUID.class));

        // When
        CommunityServiceOuterClass.DeleteCommentResponse response = deleteCommentService.deleteComment(request);

        // Then
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(TEST_COMMENT_ID, response.getCommentId());
        verify(commentRepository).findByIdAndDeletedAtIsNull(UUID.fromString(TEST_COMMENT_ID));
        verify(commentRepository).save(any(Comment.class));
        verify(postService).minusCommentCountById(UUID.fromString(TEST_POST_ID));
    }
}
