package org.example.gongiklifeclientbecommunityservice.service.comment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentResponse;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCommentServiceTest {

    private static final String VALID_COMMENT_ID = "11111111-1111-1111-1111-111111111111";
    private static final String VALID_USER_ID = "22222222-2222-2222-2222-222222222222";
    private static final String OTHER_USER_ID = "33333333-3333-3333-3333-333333333333";

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private UpdateCommentService updateCommentService;

    // 헬퍼 메소드: 유효한 UpdateCommentRequest 생성
    private UpdateCommentRequest createValidRequest(String commentId, String userId, String content) {
        return UpdateCommentRequest.newBuilder()
                .setCommentId(commentId)
                .setUserId(userId)
                .setContent(content)
                .build();
    }

    // 헬퍼 메소드: 테스트용 Comment 객체 생성
    private Comment createTestComment(UUID commentId, UUID userId, String content) {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setDeletedAt(null);
        return comment;
    }

    @Test
    @DisplayName("성공: 댓글 수정 성공")
    void updateComment_success() {
        // Given
        UpdateCommentRequest request = createValidRequest(VALID_COMMENT_ID, VALID_USER_ID, "Updated comment content");
        UUID commentUUID = UUID.fromString(VALID_COMMENT_ID);
        UUID userUUID = UUID.fromString(VALID_USER_ID);
        Comment testComment = createTestComment(commentUUID, userUUID, "Old content");

        when(commentRepository.findByIdAndDeletedAtIsNull(commentUUID))
                .thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UpdateCommentResponse response = updateCommentService.updateComment(request);

        // Then
        assertEquals("Updated comment content", testComment.getContent());
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(VALID_COMMENT_ID, response.getId());

        verify(commentRepository, times(1)).findByIdAndDeletedAtIsNull(commentUUID);
        verify(commentRepository, times(1)).save(testComment);
    }

    @Test
    @DisplayName("실패: 잘못된 댓글 ID 형식")
    void updateComment_invalidCommentId() {
        // Given
        UpdateCommentRequest request = createValidRequest("invalid-uuid", VALID_USER_ID, "Content");

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                updateCommentService.updateComment(request)
        );
        assertTrue(exception.getMessage().contains("잘못된 댓글 ID 형식"));
    }

    @Test
    @DisplayName("실패: 잘못된 사용자 ID 형식")
    void updateComment_invalidUserId() {
        // Given
        UpdateCommentRequest request = createValidRequest(VALID_COMMENT_ID, "invalid-uuid", "Content");

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                updateCommentService.updateComment(request)
        );
        assertTrue(exception.getMessage().contains("잘못된 사용자 ID 형식"));
    }

    @Test
    @DisplayName("실패: 댓글을 찾을 수 없음")
    void updateComment_commentNotFound() {
        // Given
        UpdateCommentRequest request = createValidRequest(VALID_COMMENT_ID, VALID_USER_ID, "Content");
        UUID commentUUID = UUID.fromString(VALID_COMMENT_ID);

        when(commentRepository.findByIdAndDeletedAtIsNull(commentUUID))
                .thenReturn(Optional.empty());

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                updateCommentService.updateComment(request)
        );
        assertTrue(exception.getMessage().contains("댓글을 찾을 수 없습니다"));
    }

    @Test
    @DisplayName("실패: 댓글 수정 권한 없음")
    void updateComment_permissionDenied() {
        // Given
        UpdateCommentRequest request = createValidRequest(VALID_COMMENT_ID, VALID_USER_ID, "Updated content");
        UUID commentUUID = UUID.fromString(VALID_COMMENT_ID);
        // 댓글 작성자의 userId가 다르게 설정됨
        Comment testComment = createTestComment(commentUUID, UUID.fromString(OTHER_USER_ID), "Old content");

        when(commentRepository.findByIdAndDeletedAtIsNull(commentUUID))
                .thenReturn(Optional.of(testComment));

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                updateCommentService.updateComment(request)
        );
        assertTrue(exception.getMessage().contains("권한이 없습니다. 자신의 댓글만 수정할 수 있습니다."));
    }

    @Test
    @DisplayName("실패: 댓글 내용이 비어있음")
    void updateComment_emptyContent() {
        // Given
        UpdateCommentRequest request = createValidRequest(VALID_COMMENT_ID, VALID_USER_ID, "   ");
        UUID commentUUID = UUID.fromString(VALID_COMMENT_ID);
        UUID userUUID = UUID.fromString(VALID_USER_ID);
        Comment testComment = createTestComment(commentUUID, userUUID, "Old content");

        when(commentRepository.findByIdAndDeletedAtIsNull(commentUUID))
                .thenReturn(Optional.of(testComment));

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                updateCommentService.updateComment(request)
        );
        assertTrue(exception.getMessage().contains("댓글 내용은 비어있을 수 없습니다"));
    }
}
