package org.example.gongiklifeclientbecommunityservice.service.post;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass.*;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.dto.PostProjection;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyPostsServiceTest {


    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MyPostsService myPostsService;

    // 헬퍼 메소드: cursor 없이 MyPostsRequest 생성
    private MyPostsRequest createRequest(String userId, int pageSize) {
        return MyPostsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize)
                .build();
    }

    // 헬퍼 메소드: 테스트용 PostProjection 목 객체 생성
    private PostProjection createMockPostProjection(UUID postId, UUID authorId, int categoryId,
                                                    String title, String content, int likeCount,
                                                    int commentCount, Date createdAt, boolean isLiked) {
        PostProjection projection = mock(PostProjection.class);
        when(projection.getId()).thenReturn(postId);
        when(projection.getUserId()).thenReturn(authorId);
        when(projection.getCategoryId()).thenReturn(categoryId);
        when(projection.getTitle()).thenReturn(title);
        when(projection.getContent()).thenReturn(content);
        when(projection.getLikeCount()).thenReturn(likeCount);
        when(projection.getCommentCount()).thenReturn(commentCount);
        when(projection.getCreatedAt()).thenReturn(createdAt);
        when(projection.getIsLiked()).thenReturn(isLiked);
        return projection;
    }

    @Test
    @DisplayName("정상: 내 게시물 목록 조회 - 게시물이 존재하는 경우")
    void myPosts_success() {
        // Given
        String validUserIdStr = "11111111-1111-1111-1111-111111111111";
        int pageSize = 2;
        MyPostsRequest request = createRequest(validUserIdStr, pageSize);
        UUID userId = UUID.fromString(validUserIdStr);

        // 사용자 이름 조회: myPosts()에서는 fetchUserName()을 통해 사용자 이름을 가져옴
        String fetchedUsername = "MyUserName";
        when(userService.getUserNameById(validUserIdStr)).thenReturn(fetchedUsername);

        // 내가 작성한 게시물 목록 (PostProjection) 생성 (작성자 ID는 요청 사용자와 동일)
        UUID postId1 = UUID.randomUUID();
        UUID postId2 = UUID.randomUUID();
        Date createdAt1 = new Date(1000L);
        Date createdAt2 = new Date(2000L);

        PostProjection proj1 = createMockPostProjection(
                postId1, userId, 1, "Title 1", "Content 1", 10, 5, createdAt1, true
        );
        PostProjection proj2 = createMockPostProjection(
                postId2, userId, 1, "Title 2", "Content 2", 20, 8, createdAt2, false
        );
        List<PostProjection> projections = List.of(proj1, proj2);

        // Repository에서 내 게시물 조회 시, cursor가 없으므로 null 전달됨.
        when(postRepository.findMyPostsWithCursor(userId, null, pageSize)).thenReturn(projections);

        // When
        MyPostsResponse response = myPostsService.myPosts(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getListPostCount());

        // 각 게시물 정보 검증 (사용자 이름은 fetchUserName() 결과로 동일하게 적용)
        PostForList listPost1 = response.getListPost(0);
        assertEquals(postId1.toString(), listPost1.getId());
        assertEquals("Title 1", listPost1.getTitle());
        assertEquals("Content 1", listPost1.getContent());
        assertEquals(10, listPost1.getLikeCount());
        assertEquals(5, listPost1.getCommentCount());
        assertEquals(createdAt1.toString(), listPost1.getCreatedAt());
        PostUser user1 = listPost1.getUser();
        assertEquals(userId.toString(), user1.getUserId());
        assertEquals(fetchedUsername, user1.getUserName());

        PostForList listPost2 = response.getListPost(1);
        assertEquals(postId2.toString(), listPost2.getId());
        assertEquals("Title 2", listPost2.getTitle());
        assertEquals("Content 2", listPost2.getContent());
        assertEquals(20, listPost2.getLikeCount());
        assertEquals(8, listPost2.getCommentCount());
        assertEquals(createdAt2.toString(), listPost2.getCreatedAt());
        PostUser user2 = listPost2.getUser();
        assertEquals(userId.toString(), user2.getUserId());
        assertEquals(fetchedUsername, user2.getUserName());

        // PageInfo 검증: 게시물 수가 pageSize와 동일하므로 hasNextPage 는 true, endCursor는 마지막 게시물 id
        PageInfo pageInfo = response.getPageInfo();
        assertTrue(pageInfo.getHasNextPage());
        assertEquals(postId2.toString(), pageInfo.getEndCursor());

        verify(userService).getUserNameById(validUserIdStr);
        verify(postRepository).findMyPostsWithCursor(userId, null, pageSize);
    }

    @Test
    @DisplayName("정상: 내 게시물 목록 조회 - 게시물이 없을 경우 빈 응답 반환")
    void myPosts_empty() {
        // Given
        String validUserIdStr = "11111111-1111-1111-1111-111111111112";
        int pageSize = 5;
        MyPostsRequest request = createRequest(validUserIdStr, pageSize);
        UUID userId = UUID.fromString(validUserIdStr);

        when(postRepository.findMyPostsWithCursor(userId, null, pageSize)).thenReturn(Collections.emptyList());
        when(userService.getUserNameById(validUserIdStr)).thenReturn("MyUserName");

        // When
        MyPostsResponse response = myPostsService.myPosts(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getListPostCount());
        assertFalse(response.getPageInfo().getHasNextPage());
    }

    @Test
    @DisplayName("실패: 잘못된 사용자 ID 형식인 경우 예외 발생")
    void myPosts_invalidUserId() {
        // Given
        MyPostsRequest request = createRequest("invalid-uuid", 3);
        // When & Then
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () ->
                myPostsService.myPosts(request)
        );
        assertTrue(ex.getMessage().contains("잘못된 사용자 ID 형식"));
    }
}