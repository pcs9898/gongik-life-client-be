package org.example.gongiklifeclientbecommunityservice.service.comment;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentForList;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsResponse;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCommentsServiceTest {


    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GetCommentsService getCommentsService;

    // 헬퍼 메소드: 테스트용 CommentsRequest 객체 생성
    private CommentsRequest createCommentsRequest(String postId) {
        return CommentsRequest.newBuilder()
                .setPostId(postId)
                .build();
    }

    // 헬퍼 메소드: 테스트용 Post 객체 생성 (최소한의 필드만 설정)
    private Post createTestPost(UUID postId) {
        Post post = new Post();
        post.setId(postId);
        return post;
    }

    // 헬퍼 메소드: 루트 댓글 생성 (parentComment가 null)
    private Comment createRootComment(UUID rootCommentId, UUID postId, UUID rootUserId) {
        Comment comment = new Comment();
        comment.setId(rootCommentId);
        comment.setUserId(rootUserId);
        comment.setContent("Root comment");
        comment.setCreatedAt(new Date());
        comment.setDeletedAt(null);
        comment.setPost(createTestPost(postId));
        comment.setParentComment(null);
        return comment;
    }

    // 헬퍼 메소드: 자식(답글) 댓글 생성 (parentComment 설정)
    private Comment createChildComment(UUID childCommentId, UUID postId, UUID childUserId, Comment parent) {
        Comment comment = new Comment();
        comment.setId(childCommentId);
        comment.setUserId(childUserId);
        comment.setContent("Child comment");
        comment.setCreatedAt(new Date());
        comment.setDeletedAt(null);
        comment.setPost(createTestPost(postId));
        comment.setParentComment(parent);
        return comment;
    }

    @Test
    @DisplayName("정상: 댓글 목록 조회 - 루트 댓글과 자식 댓글이 계층구조로 반환됨")
    void comments_success() {
        // Given
        UUID postId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID rootCommentId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID childCommentId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID rootUserId = UUID.fromString("21111111-2111-2111-2111-211111111111");
        UUID childUserId = UUID.fromString("32222222-3222-3222-3222-222222222222");

        Comment rootComment = createRootComment(rootCommentId, postId, rootUserId);
        Comment childComment = createChildComment(childCommentId, postId, childUserId, rootComment);

        // Repository는 루트 댓글과 자식 댓글 모두 반환
        List<Comment> comments = Arrays.asList(rootComment, childComment);
        when(commentRepository.findCommentTreeByPostId(postId))
                .thenReturn(comments);

        // UserService: 댓글 작성자 ID 목록에 대해 이름 반환
        Map<String, String> userNameMap = new HashMap<>();
        userNameMap.put(rootUserId.toString(), "RootUser");
        userNameMap.put(childUserId.toString(), "ChildUser");
        when(userService.getUserNamesByIds(anyList())).thenReturn(userNameMap);

        CommentsRequest request = createCommentsRequest(postId.toString());

        // When
        CommentsResponse response = getCommentsService.comments(request);

        // Then
        assertNotNull(response);
        // 루트 댓글만 응답 목록에 포함됨
        assertEquals(1, response.getListCommentCount());
        CommentForList rootResponse = response.getListComment(0);
        assertEquals(rootCommentId.toString(), rootResponse.getId());
        assertEquals("Root comment", rootResponse.getContent());
        assertEquals(postId.toString(), rootResponse.getPostId());
        assertEquals("RootUser", rootResponse.getUser().getUserName());
        assertEquals(rootUserId.toString(), rootResponse.getUser().getUserId());

        // 자식 댓글이 계층구조에 추가되었는지 확인
        assertEquals(1, rootResponse.getChildCommentsCount());
        CommentForList childResponse = rootResponse.getChildComments(0);
        assertEquals(childCommentId.toString(), childResponse.getId());
        assertEquals("Child comment", childResponse.getContent());
        assertEquals("ChildUser", childResponse.getUser().getUserName());
        assertEquals(childUserId.toString(), childResponse.getUser().getUserId());
        // 자식 댓글은 부모 댓글 ID가 설정되어야 함
        assertEquals(rootCommentId.toString(), childResponse.getParentCommentId());
    }

    @Test
    @DisplayName("정상: 댓글이 없는 경우 빈 응답 반환")
    void comments_empty() {
        // Given
        UUID postId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(commentRepository.findCommentTreeByPostId(postId))
                .thenReturn(Collections.emptyList());

        CommentsRequest request = createCommentsRequest(postId.toString());

        // When
        CommentsResponse response = getCommentsService.comments(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getListCommentCount());
    }

    @Test
    @DisplayName("실패: 잘못된 게시물 ID 형식이면 예외 발생")
    void comments_invalidPostId() {
        // Given
        CommentsRequest request = createCommentsRequest("invalid-uuid");

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            getCommentsService.comments(request);
        });
        assertTrue(exception.getMessage().contains("잘못된 게시물 ID 형식"));
    }

    @Test
    @DisplayName("실패: 댓글 조회 중 Repository에서 예외 발생 시 INTERNAL 오류 전환")
    void comments_fetchCommentsError() {
        // Given
        UUID postId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        when(commentRepository.findCommentTreeByPostId(postId))
                .thenThrow(new RuntimeException("DB error"));

        CommentsRequest request = createCommentsRequest(postId.toString());

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            getCommentsService.comments(request);
        });
        assertTrue(exception.getMessage().contains("댓글 조회 중 오류가 발생했습니다"));
    }

    @Test
    @DisplayName("정상: 사용자 서비스에서 null 반환 시 '알 수 없음' 처리")
    void comments_userServiceReturnsNull() {
        // Given
        UUID postId = UUID.fromString("00000000-0000-0000-0000-000000000004");
        UUID rootCommentId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID rootUserId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        Comment rootComment = createRootComment(rootCommentId, postId, rootUserId);

        when(commentRepository.findCommentTreeByPostId(postId))
                .thenReturn(Collections.singletonList(rootComment));
        // userService가 null을 반환하는 경우
        when(userService.getUserNamesByIds(anyList())).thenReturn(null);

        CommentsRequest request = createCommentsRequest(postId.toString());

        // When
        CommentsResponse response = getCommentsService.comments(request);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getListCommentCount());
        CommentForList rootResponse = response.getListComment(0);
        // userService에서 null이므로 기본값 "알 수 없음"이 설정되어야 함
        assertEquals("알 수 없음", rootResponse.getUser().getUserName());
    }
}