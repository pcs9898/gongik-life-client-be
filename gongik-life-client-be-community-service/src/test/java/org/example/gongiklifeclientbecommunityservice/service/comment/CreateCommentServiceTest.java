package org.example.gongiklifeclientbecommunityservice.service.comment;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.producer.CreateNotificationProducer;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.example.gongiklifeclientbecommunityservice.service.post.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 테스트 시, Post, Comment 클래스는 필요한 최소 필드(예: id, userId, content, createdAt, post, parentComment 등)만 가지고 있다고 가정합니다.

@ExtendWith(MockitoExtension.class)
class CreateCommentServiceTest {


    private static final String TEST_POST_ID = "123e4567-e89b-12d3-a456-426614174002";
    private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174003";
    private static final String TEST_PARENT_COMMENT_ID = "123e4567-e89b-12d3-a456-426614174004";
    private static final String TEST_POST_OWNER_ID = "123e4567-e89b-12d3-a456-426614174005";

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CreateNotificationProducer createNotificationProducer;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CreateCommentService createCommentService;

    // 헬퍼 메소드 – 테스트용 CreateCommentRequest 객체 생성 (부모 댓글 여부 선택)
    private CommunityServiceOuterClass.CreateCommentRequest createTestRequest(boolean withParent) {
        CommunityServiceOuterClass.CreateCommentRequest.Builder builder =
                CommunityServiceOuterClass.CreateCommentRequest.newBuilder()
                        .setPostId(TEST_POST_ID)
                        .setUserId(TEST_USER_ID)
                        .setContent("Test comment content");

        if (withParent) {
            builder.setParentCommentId(TEST_PARENT_COMMENT_ID);
        }
        return builder.build();
    }

    // 헬퍼 메소드 – 테스트용 Post 객체 생성
    private Post createTestPost() {
        Post post = new Post();
        post.setId(UUID.fromString(TEST_POST_ID));
        // 게시물 생성 시, 작성자와 댓글 작성자가 다르도록 설정 (알림 전송 확인)
        post.setUserId(UUID.fromString(TEST_POST_OWNER_ID));
        return post;
    }

    // 헬퍼 메소드 – 테스트용 부모 Comment 생성
    private Comment createTestParentComment(Post post) {
        Comment comment = new Comment();
        comment.setId(UUID.fromString(TEST_PARENT_COMMENT_ID));
        comment.setUserId(UUID.randomUUID());
        comment.setContent("Parent comment");
        comment.setCreatedAt(new Date());
        comment.setPost(post);
        return comment;
    }

    // 댓글 저장 시, id와 createdAt을 부여하도록 모의 동작 설정 (공통적으로 사용)
    private Comment simulateSave(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(UUID.randomUUID());
        }
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(new Date());
        }
        return comment;
    }

    @Test
    @DisplayName("성공: 부모 댓글 없이 댓글 생성")
    void createComment_success_withoutParent() {
        // Given
        CommunityServiceOuterClass.CreateCommentRequest request = createTestRequest(false);
        Post post = createTestPost();

        when(postService.findPostById(TEST_POST_ID)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment commentArg = invocation.getArgument(0);
            return simulateSave(commentArg);
        });
        when(userService.getUserNameById(TEST_USER_ID)).thenReturn("TestUser");

        // When
        CommunityServiceOuterClass.CreateCommentResponse response =
                createCommentService.createComment(request);

        // Then
        assertNotNull(response);
        assertEquals("Test comment content", response.getContent());
        assertEquals(TEST_POST_ID, response.getPostId());
        // 응답에 담긴 사용자 정보 확인
        assertEquals("TestUser", response.getUser().getUserName());
        assertEquals(TEST_USER_ID, response.getUser().getUserId());

        verify(postService).findPostById(TEST_POST_ID);
        verify(commentRepository).save(any(Comment.class));
        verify(postService).plusCommentCountById(UUID.fromString(TEST_POST_ID));
        verify(userService).getUserNameById(TEST_USER_ID);
        // 댓글 작성자와 게시물 작성자가 다르면 댓글 알림(일반 댓글)에 대한 알림 전송이 호출됨
        verify(createNotificationProducer).sendCreateNotificationRequest(argThat(dto ->
                dto.getNotificationTypeId() == 1 && dto.getPostId().equals(TEST_POST_ID)
        ));
    }

    @Test
    @DisplayName("성공: 부모 댓글이 있는 댓글 생성 (답글)")
    void createComment_success_withParent() {
        // Given
        CommunityServiceOuterClass.CreateCommentRequest request = createTestRequest(true);
        Post post = createTestPost();
        Comment parentComment = createTestParentComment(post);

        when(postService.findPostById(TEST_POST_ID)).thenReturn(post);
        when(commentRepository.findById(UUID.fromString(TEST_PARENT_COMMENT_ID)))
                .thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment commentArg = invocation.getArgument(0);
            return simulateSave(commentArg);
        });
        when(userService.getUserNameById(TEST_USER_ID)).thenReturn("TestUser");

        // When
        CommunityServiceOuterClass.CreateCommentResponse response =
                createCommentService.createComment(request);

        // Then
        assertNotNull(response);
        assertEquals("Test comment content", response.getContent());
        assertEquals(TEST_POST_ID, response.getPostId());
        verify(postService).findPostById(TEST_POST_ID);
        verify(commentRepository).findById(UUID.fromString(TEST_PARENT_COMMENT_ID));
        verify(commentRepository).save(any(Comment.class));
        verify(postService).plusCommentCountById(UUID.fromString(TEST_POST_ID));
        verify(userService).getUserNameById(TEST_USER_ID);
        // 자식(답글) 알림 전송 확인 (notificationTypeId == 2)
        verify(createNotificationProducer).sendCreateNotificationRequest(argThat(dto ->
                dto.getNotificationTypeId() == 2 &&
                        dto.getPostId().equals(TEST_POST_ID) &&
                        dto.getTargetCommentId().equals(TEST_PARENT_COMMENT_ID)
        ));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 게시물")
    void createComment_postNotFound() {
        // Given
        CommunityServiceOuterClass.CreateCommentRequest request = createTestRequest(false);
        when(postService.findPostById(TEST_POST_ID)).thenThrow(new RuntimeException("Not found"));

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                createCommentService.createComment(request)
        );
        assertTrue(exception.getMessage().contains("게시물을 찾을 수 없습니다"));
        verify(postService).findPostById(TEST_POST_ID);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 부모 댓글")
    void createComment_parentCommentNotFound() {
        // Given
        CommunityServiceOuterClass.CreateCommentRequest request = createTestRequest(true);
        Post post = createTestPost();
        when(postService.findPostById(TEST_POST_ID)).thenReturn(post);
        when(commentRepository.findById(UUID.fromString(TEST_PARENT_COMMENT_ID)))
                .thenReturn(Optional.empty());

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                createCommentService.createComment(request)
        );
        assertTrue(exception.getMessage().contains("부모 댓글을 찾을 수 없습니다"));
        verify(postService).findPostById(TEST_POST_ID);
        verify(commentRepository).findById(UUID.fromString(TEST_PARENT_COMMENT_ID));
    }

    @Test
    @DisplayName("실패: 부모 댓글이 게시물과 매칭되지 않음")
    void createComment_parentCommentMismatch() {
        // Given
        CommunityServiceOuterClass.CreateCommentRequest request = createTestRequest(true);
        Post post = createTestPost();

        // 부모 댓글이 다른 게시물에 속하도록 생성
        Post anotherPost = new Post();
        anotherPost.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"));
        anotherPost.setUserId(UUID.randomUUID());
        Comment parentComment = createTestParentComment(anotherPost);

        when(postService.findPostById(TEST_POST_ID)).thenReturn(post);
        when(commentRepository.findById(UUID.fromString(TEST_PARENT_COMMENT_ID)))
                .thenReturn(Optional.of(parentComment));

        // When & Then
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                createCommentService.createComment(request)
        );
        verify(postService).findPostById(TEST_POST_ID);
        verify(commentRepository).findById(UUID.fromString(TEST_PARENT_COMMENT_ID));
    }
}