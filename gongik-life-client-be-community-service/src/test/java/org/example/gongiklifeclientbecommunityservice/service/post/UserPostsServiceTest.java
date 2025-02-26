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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPostsServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserPostsService userPostsService;

    // 헬퍼 메소드: UserPostsRequest 생성 (myUserId 및 cursor가 선택적으로 포함됨)
    private UserPostsRequest createRequest(String userId, String myUserId, String cursor, int pageSize) {
        UserPostsRequest.Builder builder = UserPostsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize);
        if (myUserId != null) {
            builder.setMyUserId(myUserId);
        }
        if (cursor != null) {
            builder.setCursor(cursor);
        }
        return builder.build();
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
    @DisplayName("정상: 특정 사용자의 게시물 목록 조회 - 게시물이 존재하는 경우")
    void userPosts_success() {
        // Given
        String validUserId = "11111111-1111-1111-1111-111111111111";
        int pageSize = 2;
        // myUserId 및 cursor가 선택사항이므로 여기서는 null로 처리
        UserPostsRequest request = createRequest(validUserId, null, null, pageSize);
        UUID userId = UUID.fromString(validUserId);

        // 사용자 이름 조회: userService에서 이름 반환
        String fetchedUserName = "TestUser";
        when(userService.getUserNameById(validUserId)).thenReturn(fetchedUserName);

        // Repository: 특정 사용자의 게시물 목록 반환
        // 테스트를 위해 두 개의 PostProjection 생성 (작성자 ID는 동일)
        UUID postId1 = UUID.randomUUID();
        UUID postId2 = UUID.randomUUID();
        Date createdAt1 = new Date(1000L);
        Date createdAt2 = new Date(2000L);
        PostProjection proj1 = createMockPostProjection(postId1, userId, 1, "Title 1", "Content 1", 10, 5, createdAt1, true);
        PostProjection proj2 = createMockPostProjection(postId2, userId, 1, "Title 2", "Content 2", 20, 8, createdAt2, false);
        List<PostProjection> projections = List.of(proj1, proj2);

        // fetchUserPosts() 내부에서 Repository 호출 시 myUserId와 cursor는 파싱되어 null이 됨.
        when(postRepository.findPostsByUserWithCursor(eq(userId), isNull(), isNull(), eq(pageSize)))
                .thenReturn(projections);

        // When
        UserPostsResponse response = userPostsService.userPosts(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getListPostCount());

        // 첫 번째 PostForList 검증
        PostForList postForList1 = response.getListPost(0);
        assertEquals(postId1.toString(), postForList1.getId());
        assertEquals("Title 1", postForList1.getTitle());
        assertEquals("Content 1", postForList1.getContent());
        assertEquals(10, postForList1.getLikeCount());
        assertEquals(5, postForList1.getCommentCount());
        assertEquals(createdAt1.toString(), postForList1.getCreatedAt());
        PostUser user1 = postForList1.getUser();
        assertEquals(userId.toString(), user1.getUserId());
        assertEquals(fetchedUserName, user1.getUserName());

        // 두 번째 PostForList 검증
        PostForList postForList2 = response.getListPost(1);
        assertEquals(postId2.toString(), postForList2.getId());
        assertEquals("Title 2", postForList2.getTitle());
        assertEquals("Content 2", postForList2.getContent());
        assertEquals(20, postForList2.getLikeCount());
        assertEquals(8, postForList2.getCommentCount());
        assertEquals(createdAt2.toString(), postForList2.getCreatedAt());
        PostUser user2 = postForList2.getUser();
        assertEquals(userId.toString(), user2.getUserId());
        assertEquals(fetchedUserName, user2.getUserName());

        // PageInfo 검증: 게시물 수가 pageSize와 동일하므로 hasNextPage는 true, endCursor는 마지막 게시물 ID
        PageInfo pageInfo = response.getPageInfo();
        assertTrue(pageInfo.getHasNextPage());
        assertEquals(postId2.toString(), pageInfo.getEndCursor());

        verify(userService).getUserNameById(validUserId);
        verify(postRepository).findPostsByUserWithCursor(userId, null, null, pageSize);
    }

    @Test
    @DisplayName("정상: 특정 사용자의 게시물 목록 조회 - 게시물이 없을 경우 빈 응답 반환")
    void userPosts_empty() {
        // Given
        String validUserId = "11111111-1111-1111-1111-111111111112";
        int pageSize = 5;
        UserPostsRequest request = createRequest(validUserId, null, null, pageSize);
        UUID userId = UUID.fromString(validUserId);

        when(userService.getUserNameById(validUserId)).thenReturn("TestUser");
        when(postRepository.findPostsByUserWithCursor(userId, null, null, pageSize))
                .thenReturn(Collections.emptyList());

        // When
        UserPostsResponse response = userPostsService.userPosts(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getListPostCount());
        assertFalse(response.getPageInfo().getHasNextPage());
    }

    @Test
    @DisplayName("실패: 잘못된 사용자 ID 형식인 경우 예외 발생")
    void userPosts_invalidUserId() {
        // Given 잘못된 형식의 사용자 ID
        UserPostsRequest request = createRequest("invalid-uuid", null, null, 3);

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                userPostsService.userPosts(request)
        );
        assertTrue(exception.getMessage().contains("잘못된 사용자 ID 형식"));
    }
}
