package org.example.gongiklifeclientbecommunityservice.service.comment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import dto.notification.CreateNotificationRequestDto;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.producer.CreateNotificationProducer;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.example.gongiklifeclientbecommunityservice.service.post.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateCommentService {

    private final CommentRepository commentRepository;
    private final CreateNotificationProducer createNotificationProducer;
    private final PostService postService;
    private final UserService userService;

    @Transactional
    public CommunityServiceOuterClass.CreateCommentResponse createComment(CommunityServiceOuterClass.CreateCommentRequest request) {
        log.info("댓글 생성 요청 - 게시물 ID: {}, 사용자 ID: {}", request.getPostId(), request.getUserId());

        // 1. 게시물 존재 확인
        Post post = findPost(request.getPostId());

        // 2. 부모 댓글 확인 (있는 경우)
        Comment parentComment = null;
        if (request.hasParentCommentId()) {
            parentComment = findAndValidateParentComment(request.getParentCommentId(), post);
        }

        // 3. 댓글 생성 및 저장
        Comment savedComment = saveComment(request, post, parentComment);

        // 4. 게시물 댓글 수 증가
        postService.plusCommentCountById(post.getId());

        // 5. 사용자 이름 조회
        String username = fetchUsername(request.getUserId());

        // 6. 알림 생성 (필요한 경우)
        sendNotificationIfNeeded(post, savedComment, request, username);

        // 7. 응답 생성
        CommunityServiceOuterClass.CreateCommentResponse response = buildResponse(savedComment, username, request.getPostId());

        log.info("댓글 생성 완료 - 댓글 ID: {}", savedComment.getId());
        return response;
    }

    private Post findPost(String postId) {
        try {
            return postService.findPostById(postId);
        } catch (Exception e) {
            log.error("게시물을 찾을 수 없음 - ID: {}", postId);
            throw Status.NOT_FOUND
                    .withDescription("게시물을 찾을 수 없습니다")
                    .asRuntimeException();
        }
    }

    private Comment findAndValidateParentComment(String parentCommentId, Post post) {
        try {
            Comment parentComment = commentRepository.findById(UUID.fromString(parentCommentId))
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다"));

            if (!parentComment.getPost().getId().equals(post.getId())) {
                log.error("부모 댓글이 동일한 게시물에 속하지 않음 - 부모 댓글 ID: {}, 게시물 ID: {}",
                        parentCommentId, post.getId());
                throw Status.INVALID_ARGUMENT
                        .withDescription("부모 댓글이 동일한 게시물에 속하지 않습니다")
                        .asRuntimeException();
            }

            return parentComment;
        } catch (IllegalArgumentException e) {
            log.error("부모 댓글을 찾을 수 없음 - ID: {}", parentCommentId);
            throw Status.NOT_FOUND
                    .withDescription("부모 댓글을 찾을 수 없습니다")
                    .asRuntimeException();
        }
    }

    private Comment saveComment(CommunityServiceOuterClass.CreateCommentRequest request,
                                Post post, Comment parentComment) {
        Comment.CommentBuilder commentBuilder = Comment.builder()
                .userId(UUID.fromString(request.getUserId()))
                .post(post)
                .content(request.getContent());

        if (parentComment != null) {
            commentBuilder.parentComment(parentComment);
        }

        return commentRepository.save(commentBuilder.build());
    }

    private String fetchUsername(String userId) {
        try {
            String username = userService.getUserNameById(userId);
            if (username == null || username.isEmpty()) {
                log.warn("사용자 이름을 찾을 수 없음 - 사용자 ID: {}", userId);
                return "알 수 없음";
            }
            return username;
        } catch (Exception e) {
            log.error("사용자 이름 조회 중 오류 발생 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            return "알 수 없음";
        }
    }

    private void sendNotificationIfNeeded(Post post, Comment savedComment,
                                          CommunityServiceOuterClass.CreateCommentRequest request,
                                          String username) {
        // 자신의 게시물/댓글에는 알림을 보내지 않음
        if (post.getUserId().toString().equals(request.getUserId())) {
            return;
        }

        if (request.hasParentCommentId()) {
            sendReplyNotification(savedComment, username, request);
        } else {
            sendCommentNotification(post, savedComment, username, request);
        }
    }

    private void sendReplyNotification(Comment savedComment, String username,
                                       CommunityServiceOuterClass.CreateCommentRequest request) {
        createNotificationProducer.sendCreateNotificationRequest(
                CreateNotificationRequestDto.builder()
                        .userId(savedComment.getParentComment().getUserId().toString())
                        .notificationTypeId(2)
                        .title(username + " replied to your comment")
                        .content(savedComment.getContent())
                        .postId(request.getPostId())
                        .targetCommentId(request.getParentCommentId())
                        .build()
        );
    }

    private void sendCommentNotification(Post post, Comment savedComment, String username,
                                         CommunityServiceOuterClass.CreateCommentRequest request) {
        createNotificationProducer.sendCreateNotificationRequest(
                CreateNotificationRequestDto.builder()
                        .userId(post.getUserId().toString())
                        .notificationTypeId(1)
                        .title(username + " commented on your post")
                        .content(savedComment.getContent())
                        .postId(request.getPostId())
                        .targetCommentId(savedComment.getId().toString())
                        .build()
        );
    }

    private CommunityServiceOuterClass.CreateCommentResponse buildResponse(Comment savedComment,
                                                                           String username,
                                                                           String postId) {
        return CommunityServiceOuterClass.CreateCommentResponse.newBuilder()
                .setId(savedComment.getId().toString())
                .setUser(CommunityServiceOuterClass.PostUser.newBuilder()
                        .setUserId(savedComment.getUserId().toString())
                        .setUserName(username)
                        .build())
                .setPostId(postId)
                .setContent(savedComment.getContent())
                .setCreatedAt(savedComment.getCreatedAt().toString())
                .build();
    }
}