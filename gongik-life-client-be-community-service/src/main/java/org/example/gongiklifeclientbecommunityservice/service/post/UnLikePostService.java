package org.example.gongiklifeclientbecommunityservice.service.post;

import dto.community.UnLikePostRequestDto;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.entity.PostLike;
import org.example.gongiklifeclientbecommunityservice.entity.PostLikeId;
import org.example.gongiklifeclientbecommunityservice.respository.PostLikeRepository;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnLikePostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public void unLikePost(UnLikePostRequestDto requestDto) {
        log.info("게시물 좋아요 취소 요청 - 게시물 ID: {}, 사용자 ID: {}", requestDto.getPostId(), requestDto.getUserId());

        UUID postId = parseUUID(requestDto.getPostId(), "게시물");
        UUID userId = parseUUID(requestDto.getUserId(), "사용자");

        // 1. 게시물 존재 확인
        Post post = findPostById(postId);

        // 2. 좋아요 기록 확인
        PostLike like = findPostLike(postId, userId);

        // 3. 좋아요 삭제
        deletePostLike(like);

        // 4. 게시물 좋아요 수 감소
        decrementLikeCount(post);

        log.info("게시물 좋아요 취소 완료 - 게시물 ID: {}, 사용자 ID: {}", postId, userId);
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

    private Post findPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("게시물을 찾을 수 없음 - ID: {}", postId);
                    return Status.NOT_FOUND
                            .withDescription("게시물을 찾을 수 없습니다")
                            .asRuntimeException();
                });
    }

    private PostLike findPostLike(UUID postId, UUID userId) {
        PostLikeId postLikeId = new PostLikeId(postId, userId);
        return postLikeRepository.findById(postLikeId)
                .orElseThrow(() -> {
                    log.error("좋아요 기록을 찾을 수 없음 - 게시물 ID: {}, 사용자 ID: {}", postId, userId);
                    return Status.NOT_FOUND
                            .withDescription("아직 좋아요하지 않은 게시물입니다")
                            .asRuntimeException();
                });
    }

    private void deletePostLike(PostLike like) {
        postLikeRepository.delete(like);
    }

    private void decrementLikeCount(Post post) {
        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            log.warn("좋아요 수가 이미 0인 게시물 - ID: {}", post.getId());
            post.setLikeCount(0);
        }
    }
}