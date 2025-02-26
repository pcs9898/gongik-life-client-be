package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostResponse;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePostServiceTest {

    private static final String TEST_POST_ID = "11111111-1111-1111-1111-111111111111";
    private static final String TEST_USER_ID = "22222222-2222-2222-2222-222222222222";

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private DeletePostService deletePostService;

    @Test
    @DisplayName("성공: 게시글 삭제")
    void deletePost_success() {
        // 테스트용 요청 객체 생성
        DeletePostRequest request = DeletePostRequest.newBuilder()
                .setPostId(TEST_POST_ID)
                .setUserId(TEST_USER_ID)
                .build();
        UUID postUUID = UUID.fromString(TEST_POST_ID);
        UUID userUUID = UUID.fromString(TEST_USER_ID);

        // 삭제 전 게시글 엔티티 준비 (deletedAt은 null)
        Post post = new Post();
        post.setId(postUUID);
        post.setUserId(userUUID);
        post.setDeletedAt(null);

        when(postRepository.findByIdAndUserId(postUUID, userUUID))
                .thenReturn(Optional.of(post));
        // save() 호출 시 동일한 엔티티 반환 처리
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 서비스 호출
        DeletePostResponse response = deletePostService.deletePost(request);

        // 응답과 게시글의 deletedAt 값이 정상적으로 설정되었는지 확인
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(post.getDeletedAt());

        verify(postRepository).findByIdAndUserId(postUUID, userUUID);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("실패: 게시물이 존재하지 않거나 이미 삭제된 경우")
    void deletePost_notFound() {
        // 테스트용 요청 객체 생성
        DeletePostRequest request = DeletePostRequest.newBuilder()
                .setPostId(TEST_POST_ID)
                .setUserId(TEST_USER_ID)
                .build();
        UUID postUUID = UUID.fromString(TEST_POST_ID);
        UUID userUUID = UUID.fromString(TEST_USER_ID);

        // Repository에서 게시글을 찾지 못하도록 처리
        when(postRepository.findByIdAndUserId(postUUID, userUUID))
                .thenReturn(Optional.empty());

        // 서비스 호출 시 StatusRuntimeException 발생 검증
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                deletePostService.deletePost(request)
        );
        assertTrue(exception.getMessage().contains("Post not found, or maybe already deleted"));

        verify(postRepository).findByIdAndUserId(postUUID, userUUID);
        verify(postRepository, never()).save(any(Post.class));
    }
}
