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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchPostsServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SearchPostsService searchPostsService;

    // 헬퍼 메소드: 테스트용 PostProjection 목 객체 생성
    private PostProjection createMockPostProjection(UUID postId, UUID userId, int categoryId,
                                                    String title, String content, int likeCount,
                                                    int commentCount, Date createdAt, boolean isLiked) {
        PostProjection projection = mock(PostProjection.class);
        when(projection.getId()).thenReturn(postId);
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
    @DisplayName("정상: 검색 결과가 있을 경우")
    void searchPosts_success() {
        // Given
        String searchKeyword = "테스트";
        int postCategoryId = 1;
        int pageSize = 3;
        String userIdStr = "11111111-1111-1111-1111-111111111111";
        String cursorStr = "22222222-2222-2222-2222-222222222222";

        SearchPostsRequest request = SearchPostsRequest.newBuilder()
                .setSearchKeyword(searchKeyword)
                .setPostCategoryId(postCategoryId)
                .setPageSize(pageSize)
                .setUserId(userIdStr)
                .setCursor(cursorStr)
                .build();

        UUID userId = UUID.fromString(userIdStr);
        UUID cursor = UUID.fromString(cursorStr);

        // 두 개의 PostProjection 생성
        UUID postId1 = UUID.randomUUID();
        UUID postId2 = UUID.randomUUID();
        Date now = new Date();
        PostProjection proj1 = createMockPostProjection(postId1, userId, postCategoryId, "제목1", "내용1", 5, 2, now, true);
        PostProjection proj2 = createMockPostProjection(postId2, userId, postCategoryId, "제목2", "내용2", 10, 4, new Date(now.getTime() + 1000), false);
        List<PostProjection> projections = List.of(proj1, proj2);

        when(postRepository.searchPosts(eq(searchKeyword), eq(postCategoryId), eq(cursor), eq(pageSize), eq(userId)))
                .thenReturn(projections);

        // UserService: 게시물 작성자 ID에 해당하는 사용자 이름 맵 반환
        Map<String, String> userNameMap = Map.of(userIdStr, "테스트사용자");
        when(userService.getUserNamesByIds(anyList())).thenReturn(userNameMap);

        // When
        SearchPostsResponse response = searchPostsService.searchPosts(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getListPostCount());

        // 첫 번째 PostForList 검증
        PostForList postForList1 = response.getListPost(0);
        assertEquals(postId1.toString(), postForList1.getId());
        assertEquals("제목1", postForList1.getTitle());
        assertEquals("내용1", postForList1.getContent());
        assertEquals(5, postForList1.getLikeCount());
        assertEquals(2, postForList1.getCommentCount());
        assertEquals(now.toString(), postForList1.getCreatedAt());
        PostUser user = postForList1.getUser();
        assertEquals(userIdStr, user.getUserId());
        assertEquals("테스트사용자", user.getUserName());

        // 페이지 정보: 게시물 수가 pageSize와 다르므로 hasNextPage는 false, endCursor는 마지막 게시물의 ID
        PageInfo pageInfo = response.getPageInfo();
        assertFalse(pageInfo.getHasNextPage());
        assertEquals(postId2.toString(), pageInfo.getEndCursor());
    }

    @Test
    @DisplayName("정상: 검색 결과가 없을 경우 빈 응답 반환")
    void searchPosts_empty() {
        // Given
        String searchKeyword = "없음";
        int postCategoryId = 1;
        int pageSize = 5;
        SearchPostsRequest request = SearchPostsRequest.newBuilder()
                .setSearchKeyword(searchKeyword)
                .setPostCategoryId(postCategoryId)
                .setPageSize(pageSize)
                .build();

        when(postRepository.searchPosts(eq(searchKeyword), eq(postCategoryId), isNull(), eq(pageSize), isNull()))
                .thenReturn(Collections.emptyList());

        // When
        SearchPostsResponse response = searchPostsService.searchPosts(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getListPostCount());
        assertFalse(response.getPageInfo().getHasNextPage());
    }

    @Test
    @DisplayName("실패: 잘못된 사용자 ID 형식인 경우 예외 발생")
    void searchPosts_invalidUserId() {
        // Given : 잘못된 사용자 ID 형식을 포함한 요청
        SearchPostsRequest request = SearchPostsRequest.newBuilder()
                .setSearchKeyword("테스트")
                .setPostCategoryId(1)
                .setPageSize(3)
                .setUserId("invalid-uuid")
                .build();

        // When & Then : 파싱 실패로 예외 발생
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                searchPostsService.searchPosts(request)
        );
        assertTrue(exception.getMessage().contains("잘못된 사용자 ID 형식"));
    }

    @Test
    @DisplayName("실패: 잘못된 커서 형식인 경우 예외 발생")
    void searchPosts_invalidCursor() {
        // Given : 잘못된 커서 형식을 포함한 요청
        SearchPostsRequest request = SearchPostsRequest.newBuilder()
                .setSearchKeyword("테스트")
                .setPostCategoryId(1)
                .setPageSize(3)
                .setCursor("invalid-uuid")
                .build();

        // When & Then : 파싱 실패로 예외 발생
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                searchPostsService.searchPosts(request)
        );
        assertTrue(exception.getMessage().contains("잘못된 커서 ID 형식"));
    }
}
