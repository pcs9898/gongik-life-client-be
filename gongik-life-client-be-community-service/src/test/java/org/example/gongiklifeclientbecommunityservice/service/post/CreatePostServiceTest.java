package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostResponse;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CreatePostService createPostService;

    @Test
    @DisplayName("성공: 게시글 생성")
    void createPost_success() {
        // Given
        // 테스트용 request 객체 생성 (필요한 필드만 설정)
        String dummyUserId = "123e4567-e89b-12d3-a456-426614174000";
        CreatePostRequest request = CreatePostRequest.newBuilder()
                .setUserId(dummyUserId)
                .setTitle("Test Post")
                .setContent("This is a test post")
                .build();

        // 사용자 서비스가 반환할 사용자 이름 설정
        String mockUserName = "MockUser";
        when(userService.getUserNameById(dummyUserId)).thenReturn(mockUserName);

        // PostRepository의 save()에 의해 반환될 Post 객체 mocking
        Post mockPost = mock(Post.class);
        CreatePostResponse expectedResponse = CreatePostResponse.newBuilder()
                .setId("mock-post-id")
                .setUser(CommunityServiceOuterClass.PostUser.newBuilder().setUserName("mockUserName").setUserId("dummyUserId").build())
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(mockPost);
        // 저장된 Post 객체에 대해 toCreatePostResponseProto() 메서드 호출 시 expectedResponse 반환하도록 설정
        when(mockPost.toCreatePostResponseProto(mockUserName)).thenReturn(expectedResponse);

        // When
        CreatePostResponse actualResponse = createPostService.createPost(request);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(userService).getUserNameById(dummyUserId);
        verify(postRepository).save(any(Post.class));
        verify(mockPost).toCreatePostResponseProto(mockUserName);
    }
}
