package org.example.gongiklifeclientbecommunityservice.service.comment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.example.gongiklifeclientbecommunityservice.service.post.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteCommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public CommunityServiceOuterClass.DeleteCommentResponse deleteComment(CommunityServiceOuterClass.DeleteCommentRequest request) {
        log.info("댓글 삭제 요청 - 댓글 ID: {}, 사용자 ID: {}", request.getCommentId(), request.getUserId());

        // 1. 댓글 ID 유효성 검증
        UUID commentId = parseUUID(request.getCommentId(), "댓글");
        UUID userId = parseUUID(request.getUserId(), "사용자");

        // 2. 댓글 존재 확인
        Comment comment = findComment(commentId);

        // 3. 권한 확인
        validateCommentOwnership(comment, userId);

        // 4. 댓글 소프트 삭제
        markCommentAsDeleted(comment);

        // 5. 게시물 댓글 수 감소
        decrementPostCommentCount(comment.getPost().getId());

        // 6. 응답 생성
        CommunityServiceOuterClass.DeleteCommentResponse response = buildSuccessResponse(comment.getId().toString());

        log.info("댓글 삭제 완료 - 댓글 ID: {}", commentId);
        return response;
    }

    private UUID parseUUID(String id, String entityName) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 {} ID 형식: {}", entityName, id);
            throw Status.INVALID_ARGUMENT
                    .withDescription(String.format("잘못된 %s ID 형식", entityName))
                    .asRuntimeException();
        }
    }

    private Comment findComment(UUID commentId) {
        return commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> {
                    log.error("댓글을 찾을 수 없음 - ID: {}", commentId);
                    return Status.NOT_FOUND
                            .withDescription("댓글을 찾을 수 없습니다")
                            .asRuntimeException();
                });
    }

    private void validateCommentOwnership(Comment comment, UUID userId) {
        if (!comment.getUserId().equals(userId)) {
            log.error("댓글 삭제 권한 없음 - 댓글 ID: {}, 요청 사용자 ID: {}, 댓글 작성자 ID: {}",
                    comment.getId(), userId, comment.getUserId());
            throw Status.PERMISSION_DENIED
                    .withDescription("권한이 없습니다. 자신의 댓글만 삭제할 수 있습니다.")
                    .asRuntimeException();
        }
    }

    private void markCommentAsDeleted(Comment comment) {
        comment.setDeletedAt(new Date());
        commentRepository.save(comment);
        log.debug("댓글 소프트 삭제 처리됨 - 댓글 ID: {}", comment.getId());
    }

    private void decrementPostCommentCount(UUID postId) {
        try {
            postService.minusCommentCountById(postId);
            log.debug("게시물 댓글 수 감소 처리됨 - 게시물 ID: {}", postId);
        } catch (Exception e) {
            log.error("게시물 댓글 수 감소 중 오류 발생 - 게시물 ID: {}, 오류: {}", postId, e.getMessage());
            // 댓글 삭제는 성공했으므로 이 오류로 인해 전체 트랜잭션을 롤백하지 않음
        }
    }

    private CommunityServiceOuterClass.DeleteCommentResponse buildSuccessResponse(String commentId) {
        return CommunityServiceOuterClass.DeleteCommentResponse.newBuilder()
                .setCommentId(commentId)
                .setSuccess(true)
                .build();
    }
}