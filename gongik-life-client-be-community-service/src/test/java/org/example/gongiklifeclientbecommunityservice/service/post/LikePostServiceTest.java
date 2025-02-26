package org.example.gongiklifeclientbecommunityservice.service.post;

import dto.community.LikePostRequestDto;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.entity.PostLike;
import org.example.gongiklifeclientbecommunityservice.respository.PostLikeRepository;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikePostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private LikePostService likePostService;

    // 헬퍼 메소드: 테스트용 LikePostRequestDto 생성 (생성자나 빌더 사용에 맞게 수정)
    private LikePostRequestDto createRequest(String postId, String userId) {
        return new LikePostRequestDto(postId, userId);
    }

    @Test
    @DisplayName("성공: 게시물 좋아요 - 정상 처리")
    void likePost_success() {
        // Given
        String validPostIdStr = "11111111-1111-1111-1111-111111111111";
        String validUserIdStr = "22222222-2222-2222-2222-222222222222";
        LikePostRequestDto request = createRequest(validPostIdStr, validUserIdStr);
        UUID validPostId = UUID.fromString(validPostIdStr);
        UUID validUserId = UUID.fromString(validUserIdStr);

        // 게시물 엔티티 생성 (초기 likeCount 0)
        Post post = new Post();
        post.setId(validPostId);
        post.setUserId(validUserId);
        post.setLikeCount(0);

        // Repository 및 존재여부 체크 mock 설정
        when(postRepository.findById(validPostId)).thenReturn(Optional.of(post));
        when(postLikeRepository.existsByIdPostIdAndIdUserId(validPostId, validUserId)).thenReturn(false);
        when(postLikeRepository.save(any(PostLike.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        likePostService.likePost(request);

        // Then: 게시물 좋아요 수가 1 증가되었음을 확인
        assertEquals(1, post.getLikeCount());
        verify(postRepository).findById(validPostId);
        verify(postLikeRepository).existsByIdPostIdAndIdUserId(validPostId, validUserId);
        verify(postLikeRepository).save(any(PostLike.class));
    }

    @Test
    @DisplayName("실패: 잘못된 게시물 ID 형식")
    void likePost_invalidPostId() {
        // Given
        String invalidPostIdStr = "invalid-uuid";
        String validUserIdStr = "22222222-2222-2222-2222-222222222222";
        LikePostRequestDto request = createRequest(invalidPostIdStr, validUserIdStr);

        // When & Then: 게시물 ID 파싱 실패로 예외 발생
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () -> {
            likePostService.likePost(request);
        });
        assertTrue(ex.getMessage().contains("잘못된 게시물 ID 형식"));
    }

    @Test
    @DisplayName("실패: 잘못된 사용자 ID 형식")
    void likePost_invalidUserId() {
        // Given
        String validPostIdStr = "11111111-1111-1111-1111-111111111111";
        String invalidUserIdStr = "invalid-uuid";
        LikePostRequestDto request = createRequest(validPostIdStr, invalidUserIdStr);

        // When & Then: 사용자 ID 파싱 실패로 예외 발생
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () -> {
            likePostService.likePost(request);
        });
        assertTrue(ex.getMessage().contains("잘못된 사용자 ID 형식"));
    }

    @Test
    @DisplayName("실패: 게시물 조회 실패 - 존재하지 않는 게시물")
    void likePost_postNotFound() {
        // Given
        String validPostIdStr = "11111111-1111-1111-1111-111111111111";
        String validUserIdStr = "22222222-2222-2222-2222-222222222222";
        LikePostRequestDto request = createRequest(validPostIdStr, validUserIdStr);
        UUID validPostId = UUID.fromString(validPostIdStr);

        when(postRepository.findById(validPostId)).thenReturn(Optional.empty());

        // When & Then: 게시물을 찾지 못해 예외 발생
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () -> {
            likePostService.likePost(request);
        });
        assertTrue(ex.getMessage().contains("게시물을 찾을 수 없습니다"));
        verify(postRepository).findById(validPostId);
    }

    @Test
    @DisplayName("실패: 이미 좋아요한 게시물")
    void likePost_alreadyLiked() {
        // Given
        String validPostIdStr = "11111111-1111-1111-1111-111111111111";
        String validUserIdStr = "22222222-2222-2222-2222-222222222222";
        LikePostRequestDto request = createRequest(validPostIdStr, validUserIdStr);
        UUID validPostId = UUID.fromString(validPostIdStr);
        UUID validUserId = UUID.fromString(validUserIdStr);

        // 게시물 엔티티 생성
        Post post = new Post();
        post.setId(validPostId);
        post.setUserId(validUserId);
        post.setLikeCount(0);

        when(postRepository.findById(validPostId)).thenReturn(Optional.of(post));
        // 이미 좋아요한 상태로 반환
        when(postLikeRepository.existsByIdPostIdAndIdUserId(validPostId, validUserId)).thenReturn(true);

        // When & Then: 이미 좋아요한 후 예외 발생
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () -> {
            likePostService.likePost(request);
        });
        assertTrue(ex.getMessage().contains("이미 좋아요한 게시물입니다"));
        verify(postRepository).findById(validPostId);
        verify(postLikeRepository).existsByIdPostIdAndIdUserId(validPostId, validUserId);
    }
}
