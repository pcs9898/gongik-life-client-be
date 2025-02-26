package org.example.gongiklifeclientbecommunityservice.service.post;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PageInfo;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostForList;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsResponse;
import org.example.gongiklifeclientbecommunityservice.dto.PostProjection;
import org.example.gongiklifeclientbecommunityservice.respository.PostLikeRepository;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPostsServiceTest {


    @Mock
    private PostRepository postRepository;

    // PostLikeRepository는 현재 코드에서 사용되지 않으므로 별도의 동작 설정 없이 주입합니다.
    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GetPostsService getPostsService;

    // 헬퍼 메소드: userId, cursor 없이 PostsRequest 생성
    private PostsRequest createPostsRequest(int postCategoryId, int pageSize) {
        return PostsRequest.newBuilder()
                .setPostCategoryId(postCategoryId)
                .setPageSize(pageSize)
                .build();
    }

    // 헬퍼 메소드: userId 또는 cursor가 있는 PostsRequest 생성
    private PostsRequest createPostsRequestWithUser(int postCategoryId, int pageSize, String userId, String cursor) {
        PostsRequest.Builder builder = PostsRequest.newBuilder()
                .setPostCategoryId(postCategoryId)
                .setPageSize(pageSize);
        if (userId != null) {
            builder.setUserId(userId);
        }
        if (cursor != null) {
            builder.setCursor(cursor);
        }
        return builder.build();
    }

    // 헬퍼 메소드: 테스트용 PostProjection 목 객체 생성
    private PostProjection createMockPostProjection(UUID id, UUID userId, int categoryId,
                                                    String title, String content,
                                                    int likeCount, int commentCount,
                                                    Date createdAt, boolean isLiked) {
        PostProjection projection = mock(PostProjection.class);
        when(projection.getId()).thenReturn(id);
        when(projection.getUserId()).thenReturn(userId);
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
    @DisplayName("성공: 게시물 목록 조회 - 게시물이 존재하는 경우")
    void posts_success() {
        // Given
        int postCategoryId = 1;
        int pageSize = 2;
        String userIdStr = "123e4567-e89b-12d3-a456-426614174000";
        // cursor는 설정하지 않음
        PostsRequest request = createPostsRequestWithUser(postCategoryId, pageSize, userIdStr, null);

        // 테스트용 PostProjection 생성
        UUID postId1 = UUID.randomUUID();
        UUID postId2 = UUID.randomUUID();
        UUID userId = UUID.fromString(userIdStr);
        Date now = new Date();
        PostProjection projection1 = createMockPostProjection(
                postId1, userId, postCategoryId, "Post Title 1", "Content 1", 10, 5, now, true
        );
        PostProjection projection2 = createMockPostProjection(
                postId2, userId, postCategoryId, "Post Title 2", "Content 2", 20, 8, new Date(now.getTime() + 1000), false
        );
        List<PostProjection> projectionList = List.of(projection1, projection2);

        // Repository: userId와 cursor 파싱 결과에 따라 호출됨
        when(postRepository.findPostsWithCursor(
                eq(UUID.fromString(userIdStr)),
                eq(postCategoryId),
                isNull(),
                eq(pageSize)
        )).thenReturn(projectionList);

        // UserService: userId 리스트에 대해 이름 맵 반환
        Map<String, String> userNameMap = Map.of(userIdStr, "TestUser");
        when(userService.getUserNamesByIds(anyList())).thenReturn(userNameMap);

        // When
        PostsResponse response = getPostsService.posts(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getListPostCount());

        // 첫 번째 게시물 검증
        PostForList postForList1 = response.getListPost(0);
        assertEquals(postId1.toString(), postForList1.getId());
        assertEquals("Post Title 1", postForList1.getTitle());
        assertEquals("Content 1", postForList1.getContent());
        assertEquals(10, postForList1.getLikeCount());
        assertEquals(5, postForList1.getCommentCount());
        assertEquals("TestUser", postForList1.getUser().getUserName());

        // 페이지 정보: 게시물 수가 pageSize와 같으므로 hasNextPage는 true, endCursor는 마지막 게시물의 id
        PageInfo pageInfo = response.getPageInfo();
        assertTrue(pageInfo.getHasNextPage());
        assertEquals(postId2.toString(), pageInfo.getEndCursor());
    }

    @Test
    @DisplayName("정상: 게시물 목록 조회 - 게시물이 없을 경우 빈 응답 반환")
    void posts_empty() {
        // Given
        int postCategoryId = 1;
        int pageSize = 5;
        PostsRequest request = createPostsRequest(postCategoryId, pageSize);

        when(postRepository.findPostsWithCursor(isNull(), eq(postCategoryId), isNull(), eq(pageSize)))
                .thenReturn(List.of());

        // When
        PostsResponse response = getPostsService.posts(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getListPostCount());
        PageInfo pageInfo = response.getPageInfo();
        assertFalse(pageInfo.getHasNextPage());
    }

    @Test
    @DisplayName("실패: 잘못된 UUID 형식 - 사용자 ID에 대해 예외 발생")
    void posts_invalidUUID() {
        // Given: 잘못된 userId 형식이 포함된 요청
        int postCategoryId = 1;
        int pageSize = 3;
        PostsRequest request = PostsRequest.newBuilder()
                .setPostCategoryId(postCategoryId)
                .setPageSize(pageSize)
                .setUserId("invalid-uuid")
                .build();

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            getPostsService.posts(request);
        });
        assertTrue(exception.getMessage().contains("잘못된 ID 형식"));
    }
}