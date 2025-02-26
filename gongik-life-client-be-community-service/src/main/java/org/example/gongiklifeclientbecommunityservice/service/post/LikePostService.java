package org.example.gongiklifeclientbecommunityservice.service.post;

import dto.community.LikePostRequestDto;
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

import java.util.Date;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class LikePostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public void likePost(LikePostRequestDto requestDto) {
        log.info("게시물 좋아요 요청 - 게시물 ID: {}, 사용자 ID: {}", requestDto.getPostId(), requestDto.getUserId());

        UUID postId = parseUUID(requestDto.getPostId(), "게시물");
        UUID userId = parseUUID(requestDto.getUserId(), "사용자");

        // 1. 게시물 존재 확인
        Post post = findPostById(postId);

        // 2. 이미 좋아요 했는지 확인
        checkAlreadyLiked(postId, userId);

        // 3. 좋아요 저장
        savePostLike(postId, userId);

        // 4. 게시물 좋아요 수 증가
        incrementLikeCount(post);

        log.info("게시물 좋아요 완료 - 게시물 ID: {}, 사용자 ID: {}", postId, userId);
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

    private void checkAlreadyLiked(UUID postId, UUID userId) {
        if (postLikeRepository.existsByIdPostIdAndIdUserId(postId, userId)) {
            log.warn("이미 좋아요한 게시물 - 게시물 ID: {}, 사용자 ID: {}", postId, userId);
            throw Status.ALREADY_EXISTS
                    .withDescription("이미 좋아요한 게시물입니다")
                    .asRuntimeException();
        }
    }

    private void savePostLike(UUID postId, UUID userId) {
        PostLike newLike = new PostLike(
                new PostLikeId(postId, userId),
                new Date()
        );
        postLikeRepository.save(newLike);
    }

    private void incrementLikeCount(Post post) {
        post.setLikeCount(post.getLikeCount() + 1);
    }
}
