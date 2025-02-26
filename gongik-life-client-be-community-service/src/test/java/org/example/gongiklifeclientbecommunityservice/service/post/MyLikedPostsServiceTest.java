package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyLikedPostsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyLikedPostsResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PageInfo;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostForList;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyLikedPostsServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MyLikedPostsService myLikedPostsService;

    // 헬퍼 메소드: MyLikedPostsRequest 생성 (cursor 미포함)
    private MyLikedPostsRequest createRequest(String userId, int pageSize) {
        return MyLikedPostsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize)
                .build();
    }

    // 헬퍼 메소드: MyLikedPostsRequest 생성 (cursor 포함)
    private MyLikedPostsRequest createRequestWithCursor(String userId, String cursor, int pageSize) {
        return MyLikedPostsRequest.newBuilder()
                .setUserId(userId)
                .setCursor(cursor)
                .setPageSize(pageSize)
                .build();
    }

    // 헬퍼 메소드: 테스트용 PostProjection 목 객체 생성
    private PostProjection createMockPostProjection(UUID id, UUID authorId, int categoryId,
                                                    String title, String content, int likeCount,
                                                    int commentCount, Date createdAt, boolean isLiked) {
        PostProjection projection = mock(PostProjection.class);
        when(projection.getId()).thenReturn(id);
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
    @DisplayName("정상: 좋아요한 게시물 목록 조회 - 게시물이 존재하는 경우")
    void myLikedPosts_success() {
        // Given
        String validUserIdStr = "11111111-1111-1111-1111-111111111111";
        int pageSize = 2;
        MyLikedPostsRequest request = createRequest(validUserIdStr, pageSize);
        UUID validUserId = UUID.fromString(validUserIdStr);

        // 두 개의 게시물 생성
        UUID postId1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID postId2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        // 게시물 작성자 ID (author)
        UUID author1 = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        UUID author2 = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        Date createdAt1 = new Date(1000L);
        Date createdAt2 = new Date(2000L);

        PostProjection proj1 = createMockPostProjection(
                postId1, author1, 1, "Title 1", "Content 1", 10, 5, createdAt1, true
        );
        PostProjection proj2 = createMockPostProjection(
                postId2, author2, 1, "Title 2", "Content 2", 20, 8, createdAt2, false
        );
        List<PostProjection> projections = List.of(proj1, proj2);

        // Repository에서 좋아요한 게시물 리스트 리턴
        when(postRepository.findMyLikedPostsWithCursor(validUserId, null, pageSize))
                .thenReturn(projections);

        // 사용자 서비스: 게시물 작성자 ID 목록에 대해 이름 반환
        Map<String, String> userNameMap = Map.of(
                author1.toString(), "Author1",
                author2.toString(), "Author2"
        );
        when(userService.getUserNamesByIds(anyList())).thenReturn(userNameMap);

        // When
        MyLikedPostsResponse response = myLikedPostsService.myLikedPosts(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getListPostCount());

        // 첫 번째 게시물 검사
        PostForList postForList1 = response.getListPost(0);
        assertEquals(postId1.toString(), postForList1.getId());
        assertEquals("Title 1", postForList1.getTitle());
        assertEquals("Content 1", postForList1.getContent());
        assertEquals(10, postForList1.getLikeCount());
        assertEquals(5, postForList1.getCommentCount());
        assertEquals(createdAt1.toString(), postForList1.getCreatedAt());
        // 작성자 정보 검사
        assertEquals(author1.toString(), postForList1.getUser().getUserId());
        assertEquals("Author1", postForList1.getUser().getUserName());

        // 두 번째 게시물 검사
        PostForList postForList2 = response.getListPost(1);
        assertEquals(postId2.toString(), postForList2.getId());
        assertEquals("Title 2", postForList2.getTitle());
        assertEquals("Content 2", postForList2.getContent());
        assertEquals(20, postForList2.getLikeCount());
        assertEquals(8, postForList2.getCommentCount());
        assertEquals(createdAt2.toString(), postForList2.getCreatedAt());
        assertEquals(author2.toString(), postForList2.getUser().getUserId());
        assertEquals("Author2", postForList2.getUser().getUserName());

        // 페이지 정보 검사: 게시물 수가 pageSize와 같으므로 hasNextPage true, endCursor는 마지막 게시물의 id
        PageInfo pageInfo = response.getPageInfo();
        assertTrue(pageInfo.getHasNextPage());
        assertEquals(postId2.toString(), pageInfo.getEndCursor());

        verify(postRepository).findMyLikedPostsWithCursor(validUserId, null, pageSize);
        verify(userService).getUserNamesByIds(anyList());
    }

    @Test
    @DisplayName("정상: 좋아요한 게시물 목록 조회 - 게시물이 없을 경우 빈 응답 반환")
    void myLikedPosts_empty() {
        // Given
        String validUserIdStr = "11111111-1111-1111-1111-111111111112";
        int pageSize = 5;
        MyLikedPostsRequest request = createRequest(validUserIdStr, pageSize);
        UUID validUserId = UUID.fromString(validUserIdStr);

        // Repository에서 빈 리스트 반환
        when(postRepository.findMyLikedPostsWithCursor(validUserId, null, pageSize))
                .thenReturn(Collections.emptyList());

        // When
        MyLikedPostsResponse response = myLikedPostsService.myLikedPosts(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getListPostCount());
        PageInfo pageInfo = response.getPageInfo();
        assertFalse(pageInfo.getHasNextPage());
    }

    @Test
    @DisplayName("실패: 잘못된 사용자 ID 형식인 경우 예외 발생")
    void myLikedPosts_invalidUserId() {
        // Given
        MyLikedPostsRequest request = createRequest("invalid-uuid", 3);

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            myLikedPostsService.myLikedPosts(request);
        });
        assertTrue(exception.getMessage().contains("잘못된 사용자 ID 형식"));
    }

    @Test
    @DisplayName("정상: 사용자 서비스가 null 반환 시 '알 수 없음' 기본값 적용")
    void myLikedPosts_userServiceReturnsNull() {
        // Given
        String validUserIdStr = "11111111-1111-1111-1111-111111111113";
        int pageSize = 1;
        MyLikedPostsRequest request = createRequest(validUserIdStr, pageSize);
        UUID validUserId = UUID.fromString(validUserIdStr);

        UUID postId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
        UUID authorId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        Date createdAt = new Date(3000L);
        PostProjection projection = createMockPostProjection(
                postId, authorId, 1, "Title Only", "Content Only", 15, 3, createdAt, true
        );
        List<PostProjection> projections = Collections.singletonList(projection);

        when(postRepository.findMyLikedPostsWithCursor(validUserId, null, pageSize))
                .thenReturn(projections);

        // 사용자 서비스가 null을 반환하는 경우
        when(userService.getUserNamesByIds(anyList())).thenReturn(null);

        // When
        MyLikedPostsResponse response = myLikedPostsService.myLikedPosts(request);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getListPostCount());
        PostForList postForList = response.getListPost(0);
        // 기본값 "알 수 없음"이 설정되어야 함
        assertEquals("알 수 없음", postForList.getUser().getUserName());
    }
}
