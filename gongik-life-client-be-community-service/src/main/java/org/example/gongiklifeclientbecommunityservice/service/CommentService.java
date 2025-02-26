package org.example.gongiklifeclientbecommunityservice.service;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.ExistsCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.ExistsCommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;


    public void deleteAllCommentsByPost(String postId) {
        try {
            commentRepository.softDeleteAllByPostId(UUID.fromString(postId));
        } catch (Exception e) {
            log.error("Error deleting comments by postId: {}", postId, e);
            throw e;
        }
    }

    public ExistsCommentResponse existsComment(ExistsCommentRequest request) {
        boolean exists = commentRepository.existsByIdAndDeletedAtIsNull(
                UUID.fromString(request.getCommentId()));

        return ExistsCommentResponse.newBuilder()
                .setExists(exists)
                .build();
    }
}
