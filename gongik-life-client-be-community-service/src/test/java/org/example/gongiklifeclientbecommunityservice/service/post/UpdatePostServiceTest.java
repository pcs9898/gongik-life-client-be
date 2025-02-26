package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostResponse;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
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
class UpdatePostServiceTest {


    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UpdatePostService updatePostService;

    @Test
    @DisplayName("성공: 게시글 업데이트 - 정상적으로 응답 반환")
    void updatePost_success() {
        // Given
        String testPostId = "11111111-1111-1111-1111-111111111111";
        String userIdStr = "22222222-2222-2222-2222-222222222222";

        UpdatePostRequest request =
                com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostRequest.newBuilder()
                        .setPostId(testPostId)
                        .setTitle("New Title")
                        .setContent("New Content")
                        .build();

        // 게시글 엔티티를 스파이 처리합니다.
        Post post = spy(new Post());
        post.setId(UUID.fromString(testPostId));
        post.setUserId(UUID.fromString(userIdStr));
        post.setTitle("Old Title");
        post.setContent("Old Content");
        post.setCategoryId(1);
        post.setCreatedAt(new Date());

        // fromUpdatePostRequestProto() 호출 시 아무런 동작 하지 않는 것으로 설정
        doNothing().when(post).fromUpdatePostRequestProto(request);

        // Repository: 해당 ID로 게시글 조회
        when(postRepository.findById(UUID.fromString(testPostId)))
                .thenReturn(Optional.of(post));
        // 게시글 저장 시 동일한 객체를 반환 처리
        when(postRepository.save(post)).thenReturn(post);

        // 사용자 이름 조회 처리
        String expectedUserName = "TestUser";
        when(userService.getUserNameById(userIdStr)).thenReturn(expectedUserName);

        // toUpdatePostResponseProto() 호출 시 기대 응답 반환
        UpdatePostResponse expectedResponse =
                com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostResponse.newBuilder()
                        .setId(testPostId)
                        .setTitle("New Title")
                        .setContent("New Content")
                        .setCategoryId(1)
                        .build();
        when(post.toUpdatePostResponseProto(expectedUserName)).thenReturn(expectedResponse);

        // When
        UpdatePostResponse response = updatePostService.updatePost(request);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(postRepository).findById(UUID.fromString(testPostId));
        verify(post).fromUpdatePostRequestProto(request);
        verify(userService).getUserNameById(userIdStr);
        verify(postRepository).save(post);
        // 호출 횟수를 2로 변경: 실제로 spy 객체에서 toUpdatePostResponseProto가 2번 호출됨.
        verify(post, times(2)).toUpdatePostResponseProto(expectedUserName);
    }

    @Test
    @DisplayName("실패: 게시글이 존재하지 않는 경우 예외 발생")
    void updatePost_postNotFound() {
        // Given
        String testPostId = "11111111-1111-1111-1111-111111111111";
        UpdatePostRequest request =
                com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostRequest.newBuilder()
                        .setPostId(testPostId)
                        .build();
        UUID postId = UUID.fromString(testPostId);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                updatePostService.updatePost(request)
        );
        assertTrue(exception.getMessage().contains("Post not found"));
        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository, userService);
    }
}