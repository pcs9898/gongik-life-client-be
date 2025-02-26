package org.example.gongiklifeclientbecommunityservice.service.comment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateCommentService {


    private final CommentRepository commentRepository;

    @Transactional
    public CommunityServiceOuterClass.UpdateCommentResponse updateComment(CommunityServiceOuterClass.UpdateCommentRequest request) {
        log.info("댓글 수정 요청 - 댓글 ID: {}, 사용자 ID: {}", request.getCommentId(), request.getUserId());

        // 1. 댓글 ID 유효성 검증
        UUID commentId = parseUUID(request.getCommentId(), "댓글");
        UUID userId = parseUUID(request.getUserId(), "사용자");

        // 2. 댓글 존재 확인
        Comment comment = findComment(commentId);

        // 3. 권한 확인
        validateCommentOwnership(comment, userId);

        // 4. 댓글 내용 업데이트
        updateCommentContent(comment, request.getContent());

        // 5. 응답 생성
        CommunityServiceOuterClass.UpdateCommentResponse response = buildSuccessResponse(request.getCommentId());

        log.info("댓글 수정 완료 - 댓글 ID: {}", commentId);
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
            log.error("댓글 수정 권한 없음 - 댓글 ID: {}, 요청 사용자 ID: {}, 댓글 작성자 ID: {}",
                    comment.getId(), userId, comment.getUserId());
            throw Status.PERMISSION_DENIED
                    .withDescription("권한이 없습니다. 자신의 댓글만 수정할 수 있습니다.")
                    .asRuntimeException();
        }
    }

    private void updateCommentContent(Comment comment, String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            log.error("댓글 내용이 비어있음 - 댓글 ID: {}", comment.getId());
            throw Status.INVALID_ARGUMENT
                    .withDescription("댓글 내용은 비어있을 수 없습니다")
                    .asRuntimeException();
        }

        comment.setContent(newContent);
        commentRepository.save(comment);
    }

    private CommunityServiceOuterClass.UpdateCommentResponse buildSuccessResponse(String commentId) {
        return CommunityServiceOuterClass.UpdateCommentResponse.newBuilder()
                .setId(commentId)
                .setSuccess(true)
                .build();
    }
}