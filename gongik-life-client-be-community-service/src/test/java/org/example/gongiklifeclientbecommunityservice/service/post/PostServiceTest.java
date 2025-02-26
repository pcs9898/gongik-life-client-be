package org.example.gongiklifeclientbecommunityservice.service.post;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass.*;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {


    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("isLikedPost: 좋아요 여부 true인 경우")
    void isLikedPost_true() {
        // Given
        String postIdStr = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
        String userIdStr = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";

        IsLikedPostRequest request = IsLikedPostRequest.newBuilder()
                .setPostId(postIdStr)
                .setUserId(userIdStr)
                .build();

        UUID postId = UUID.fromString(postIdStr);
        UUID userId = UUID.fromString(userIdStr);

        when(postLikeRepository.existsByIdPostIdAndIdUserId(eq(postId), eq(userId))).thenReturn(true);

        // When
        IsLikedPostResponse response = postService.isLikedPost(request);

        // Then
        assertTrue(response.getIsLiked());
        verify(postLikeRepository).existsByIdPostIdAndIdUserId(postId, userId);
    }

    @Test
    @DisplayName("isLikedPost: 좋아요 여부 false인 경우")
    void isLikedPost_false() {
        // Given
        String postIdStr = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
        String userIdStr = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";

        IsLikedPostRequest request = IsLikedPostRequest.newBuilder()
                .setPostId(postIdStr)
                .setUserId(userIdStr)
                .build();

        UUID postId = UUID.fromString(postIdStr);
        UUID userId = UUID.fromString(userIdStr);

        when(postLikeRepository.existsByIdPostIdAndIdUserId(eq(postId), eq(userId))).thenReturn(false);

        // When
        IsLikedPostResponse response = postService.isLikedPost(request);

        // Then
        assertFalse(response.getIsLiked());
        verify(postLikeRepository).existsByIdPostIdAndIdUserId(postId, userId);
    }

    @Test
    @DisplayName("isLikedPostAndCommentCount: 사용자가 존재하는 경우")
    void isLikedPostAndCommentCount_withUser() {
        // Given
        String postIdStr = "cccccccc-cccc-cccc-cccc-cccccccccccc";
        String userIdStr = "dddddddd-dddd-dddd-dddd-dddddddddddd";
        int commentCount = 7;

        IsLikedPostAndCommentCountRequest request = IsLikedPostAndCommentCountRequest.newBuilder()
                .setPostId(postIdStr)
                .setUserId(userIdStr)
                .build();

        UUID postId = UUID.fromString(postIdStr);
        UUID userId = UUID.fromString(userIdStr);

        when(postRepository.findCommentCountById(eq(postId))).thenReturn(commentCount);
        when(postLikeRepository.existsByIdPostIdAndIdUserId(eq(postId), eq(userId))).thenReturn(true);

        // When
        IsLikedPostAndCommentCountResponse response = postService.isLikedPostAndCommentCount(request);

        // Then
        assertTrue(response.getIsLiked());
        assertEquals(commentCount, response.getCommentCount());
        verify(postRepository).findCommentCountById(postId);
        verify(postLikeRepository).existsByIdPostIdAndIdUserId(postId, userId);
    }

    @Test
    @DisplayName("isLikedPostAndCommentCount: 사용자 ID 미포함인 경우(좋아요 false)")
    void isLikedPostAndCommentCount_withoutUser() {
        // Given
        String postIdStr = "cccccccc-cccc-cccc-cccc-cccccccccccc";
        int commentCount = 3;

        IsLikedPostAndCommentCountRequest request = IsLikedPostAndCommentCountRequest.newBuilder()
                .setPostId(postIdStr)
                .build();

        UUID postId = UUID.fromString(postIdStr);

        when(postRepository.findCommentCountById(eq(postId))).thenReturn(commentCount);
        // 사용자 ID 미포함이므로 isLiked 기본값 remains false

        // When
        IsLikedPostAndCommentCountResponse response = postService.isLikedPostAndCommentCount(request);

        // Then
        assertFalse(response.getIsLiked());
        assertEquals(commentCount, response.getCommentCount());
        verify(postRepository).findCommentCountById(postId);
        // postLikeRepository.existsById... 호출되지 because request.hasUserId() is false.
    }

    @Test
    @DisplayName("findPostById: 게시글이 존재하는 경우")
    void findPostById_success() {
        // Given
        String postIdStr = "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee";
        UUID postId = UUID.fromString(postIdStr);
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(eq(postId))).thenReturn(Optional.of(post));

        // When
        Post result = postService.findPostById(postIdStr);

        // Then
        assertNotNull(result);
        assertEquals(postId, result.getId());
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("findPostById: 게시글 미존재 시 예외 발생")
    void findPostById_notFound() {
        // Given
        String postIdStr = "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee";
        UUID postId = UUID.fromString(postIdStr);

        when(postRepository.findById(eq(postId))).thenReturn(Optional.empty());

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                postService.findPostById(postIdStr)
        );
        assertTrue(exception.getMessage().contains("Post not found"));
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("plusCommentCountById: 게시글의 댓글 수 증가")
    void plusCommentCountById() {
        // Given
        UUID postId = UUID.randomUUID();

        // When
        postService.plusCommentCountById(postId);

        // Then
        verify(postRepository).plusCommentCountById(postId);
    }

    @Test
    @DisplayName("minusCommentCountById: 게시글의 댓글 수 감소")
    void minusCommentCountById() {
        // Given
        UUID postId = UUID.randomUUID();

        // When
        postService.minusCommentCountById(postId);

        // Then
        verify(postRepository).minusCommentCountById(postId);
    }

    @Test
    @DisplayName("existsPost: 게시글이 존재하는 경우")
    void existsPost_exists() {
        // Given
        String postIdStr = "ffffffff-ffff-ffff-ffff-ffffffffffff";
        ExistsPostRequest request = ExistsPostRequest.newBuilder()
                .setPostId(postIdStr)
                .build();
        UUID postId = UUID.fromString(postIdStr);

        when(postRepository.existsById(eq(postId))).thenReturn(true);

        // When
        ExistsPostResponse response = postService.existsPost(request);

        // Then
        assertTrue(response.getExists());
        verify(postRepository).existsById(postId);
    }

    @Test
    @DisplayName("existsPost: 게시글이 존재하지 않는 경우")
    void existsPost_notExists() {
        // Given
        String postIdStr = "ffffffff-ffff-ffff-ffff-ffffffffffff";
        ExistsPostRequest request = ExistsPostRequest.newBuilder()
                .setPostId(postIdStr)
                .build();
        UUID postId = UUID.fromString(postIdStr);

        when(postRepository.existsById(eq(postId))).thenReturn(false);

        // When
        ExistsPostResponse response = postService.existsPost(request);

        // Then
        assertFalse(response.getExists());
        verify(postRepository).existsById(postId);
    }
}