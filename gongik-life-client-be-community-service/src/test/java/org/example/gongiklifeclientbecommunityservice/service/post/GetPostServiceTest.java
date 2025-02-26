package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.GetPostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.GetPostResponse;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GetPostService getPostService;

    @Test
    @DisplayName("성공: 게시글 조회 - 존재하는 게시글")
    void getPost_success() {
        // Given: 테스트용 게시글 ID와 사용자 ID, 사용자 이름 설정
        String testPostId = "11111111-1111-1111-1111-111111111111";
        String testUserId = "22222222-2222-2222-2222-222222222222";
        String testUserName = "TestUser";

        // 요청 객체 생성
        GetPostRequest request = GetPostRequest.newBuilder()
                .setPostId(testPostId)
                .build();

        // 게시글 엔티티 모의 객체 생성
        Post mockPost = mock(Post.class);
        when(mockPost.getUserId()).thenReturn(UUID.fromString(testUserId));

        // 기대하는 응답 객체 생성
        GetPostResponse expectedResponse = GetPostResponse.newBuilder()
                .setId(testPostId)
                .setUser(CommunityServiceOuterClass.PostUser.newBuilder().setUserId(testUserId).setUserName(testUserName).build())
                .build();

        // 모의 동작 정의: repository에서 게시글 조회, 사용자 이름 반환, 그리고 응답 프로토 변환
        when(postRepository.findById(UUID.fromString(testPostId)))
                .thenReturn(Optional.of(mockPost));
        when(userService.getUserNameById(testUserId))
                .thenReturn(testUserName);
        when(mockPost.toGetPostResponseProto(testUserName))
                .thenReturn(expectedResponse);

        // When: 게시글 조회 호출
        GetPostResponse response = getPostService.getPost(request);

        // Then: 응답 검증 및 모의 메서드 호출 검증
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(postRepository).findById(UUID.fromString(testPostId));
        verify(userService).getUserNameById(testUserId);
        verify(mockPost).toGetPostResponseProto(testUserName);
    }

    @Test
    @DisplayName("실패: 게시글 조회 - 존재하지 않는 게시글")
    void getPost_notFound() {
        // Given: 존재하지 않는 게시글 ID 설정
        String testPostId = "33333333-3333-3333-3333-333333333333";
        GetPostRequest request = GetPostRequest.newBuilder()
                .setPostId(testPostId)
                .build();

        // 게시글 조회 시 Optional.empty() 반환하도록 설정
        when(postRepository.findById(UUID.fromString(testPostId)))
                .thenReturn(Optional.empty());

        // When & Then: 예외 발생 여부와 메시지 검증
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                getPostService.getPost(request)
        );
        assertTrue(exception.getMessage().contains("Post not found"));
        verify(postRepository).findById(UUID.fromString(testPostId));
    }
}
