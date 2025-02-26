package org.example.gongiklifeclientbecommunityservice.service.post;

import dto.community.UnLikePostRequestDto;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.entity.PostLike;
import org.example.gongiklifeclientbecommunityservice.entity.PostLikeId;
import org.example.gongiklifeclientbecommunityservice.respository.PostLikeRepository;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
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

@ExtendWith(MockitoExtension.class)
class UnLikePostServiceTest {


    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private UnLikePostService unLikePostService;

    // 헬퍼 메소드: 테스트용 UnLikePostRequestDto 생성
    private UnLikePostRequestDto createRequest(String postId, String userId) {
        // DTO의 생성자가 postId와 userId를 인자로 받는다고 가정합니다.
        return new UnLikePostRequestDto(postId, userId);
    }

    @Test
    @DisplayName("성공: 정상적인 좋아요 취소 (likeCount > 0인 경우)")
    void unLikePost_success_decrementsLikeCount() {
        // Given
        String postIdStr = "11111111-1111-1111-1111-111111111111";
        String userIdStr = "22222222-2222-2222-2222-222222222222";
        UnLikePostRequestDto request = createRequest(postIdStr, userIdStr);
        UUID postId = UUID.fromString(postIdStr);
        UUID userId = UUID.fromString(userIdStr);

        Post post = new Post();
        post.setId(postId);
        post.setLikeCount(5);

        PostLike like = new PostLike(new PostLikeId(postId, userId), new Date());

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.findById(new PostLikeId(postId, userId)))
                .thenReturn(Optional.of(like));

        // When
        unLikePostService.unLikePost(request);

        // Then
        // 게시글의 좋아요 수가 1 감소하여 4가 되어야 합니다.
        assertEquals(4, post.getLikeCount());
        verify(postRepository).findById(postId);
        verify(postLikeRepository).findById(new PostLikeId(postId, userId));
        verify(postLikeRepository).delete(like);
    }

    @Test
    @DisplayName("성공: 좋아요 수가 이미 0인 경우 (0 이하로 내려가지 않음)")
    void unLikePost_success_likeCountAtZero() {
        // Given
        String postIdStr = "33333333-3333-3333-3333-333333333333";
        String userIdStr = "44444444-4444-4444-4444-444444444444";
        UnLikePostRequestDto request = createRequest(postIdStr, userIdStr);
        UUID postId = UUID.fromString(postIdStr);
        UUID userId = UUID.fromString(userIdStr);

        Post post = new Post();
        post.setId(postId);
        post.setLikeCount(0);

        PostLike like = new PostLike(new PostLikeId(postId, userId), new Date());
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.findById(new PostLikeId(postId, userId)))
                .thenReturn(Optional.of(like));

        // When
        unLikePostService.unLikePost(request);

        // Then
        // 좋아요 수는 0으로 유지되어야 합니다.
        assertEquals(0, post.getLikeCount());
        verify(postLikeRepository).delete(like);
    }

    @Test
    @DisplayName("실패: 잘못된 게시물 ID 형식")
    void unLikePost_invalidPostId() {
        // Given
        String invalidPostId = "invalid-uuid";
        String userIdStr = "22222222-2222-2222-2222-222222222222";
        UnLikePostRequestDto request = createRequest(invalidPostId, userIdStr);

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                unLikePostService.unLikePost(request)
        );
        assertTrue(exception.getMessage().contains("잘못된 게시물 ID 형식"));
    }

    @Test
    @DisplayName("실패: 잘못된 사용자 ID 형식")
    void unLikePost_invalidUserId() {
        // Given
        String postIdStr = "11111111-1111-1111-1111-111111111111";
        String invalidUserId = "invalid-uuid";
        UnLikePostRequestDto request = createRequest(postIdStr, invalidUserId);

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                unLikePostService.unLikePost(request)
        );
        assertTrue(exception.getMessage().contains("잘못된 사용자 ID 형식"));
    }

    @Test
    @DisplayName("실패: 게시물이 존재하지 않는 경우")
    void unLikePost_postNotFound() {
        // Given
        String postIdStr = "55555555-5555-5555-5555-555555555555";
        String userIdStr = "66666666-6666-6666-6666-666666666666";
        UnLikePostRequestDto request = createRequest(postIdStr, userIdStr);
        UUID postId = UUID.fromString(postIdStr);
        UUID userId = UUID.fromString(userIdStr);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                unLikePostService.unLikePost(request)
        );
        assertTrue(exception.getMessage().contains("게시물을 찾을 수 없습니다"));
        verify(postRepository).findById(postId);
        verify(postLikeRepository, never()).findById(any());
    }

    @Test
    @DisplayName("실패: 좋아요 기록이 존재하지 않는 경우")
    void unLikePost_likeRecordNotFound() {
        // Given
        String postIdStr = "77777777-7777-7777-7777-777777777777";
        String userIdStr = "88888888-8888-8888-8888-888888888888";
        UnLikePostRequestDto request = createRequest(postIdStr, userIdStr);
        UUID postId = UUID.fromString(postIdStr);
        UUID userId = UUID.fromString(userIdStr);

        Post post = new Post();
        post.setId(postId);
        post.setLikeCount(3);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.findById(new PostLikeId(postId, userId))).thenReturn(Optional.empty());

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                unLikePostService.unLikePost(request)
        );
        assertTrue(exception.getMessage().contains("아직 좋아요하지 않은 게시물입니다"));
        verify(postRepository).findById(postId);
        verify(postLikeRepository).findById(new PostLikeId(postId, userId));
    }
}