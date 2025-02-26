package org.example.gongiklifeclientbecommunityservice.service.comment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentForList;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentsResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PageInfo;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbecommunityservice.dto.MyCommentProjection;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyCommentsServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private MyCommentsService myCommentsService;

    // 헬퍼 메소드: 테스트용 MyCommentsRequest 생성 (cursor 미포함)
    private MyCommentsRequest createRequest(String userId, int pageSize) {
        return MyCommentsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize)
                .build();
    }

    // 헬퍼 메소드: 테스트용 MyCommentsRequest 생성 (cursor 포함)
    private MyCommentsRequest createRequestWithCursor(String userId, String cursor, int pageSize) {
        return MyCommentsRequest.newBuilder()
                .setUserId(userId)
                .setCursor(cursor)
                .setPageSize(pageSize)
                .build();
    }

    // 헬퍼 메소드: MyCommentProjection 목 객체 생성
    private MyCommentProjection createProjection(UUID id, UUID postId, String postTitle, String content, Date createdAt) {
        MyCommentProjection projection = org.mockito.Mockito.mock(MyCommentProjection.class);
        when(projection.getId()).thenReturn(id);
        when(projection.getPostId()).thenReturn(postId);
        when(projection.getPostTitle()).thenReturn(postTitle);
        when(projection.getContent()).thenReturn(content);
        when(projection.getCreatedAt()).thenReturn(createdAt);
        return projection;
    }

    @Test
    @DisplayName("정상: 댓글 목록 조회 (cursor 미포함) - 댓글이 존재하는 경우")
    void myComments_successWithoutCursor() {
        // Given
        String validUserId = "123e4567-e89b-12d3-a456-426614174000";
        int pageSize = 2;
        MyCommentsRequest request = createRequest(validUserId, pageSize);
        UUID userId = UUID.fromString(validUserId);

        UUID commentId1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID commentId2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID postId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        Date createdAt1 = new Date(1000L);
        Date createdAt2 = new Date(2000L);

        MyCommentProjection projection1 = createProjection(commentId1, postId, "Post Title A", "Content A", createdAt1);
        MyCommentProjection projection2 = createProjection(commentId2, postId, "Post Title A", "Content B", createdAt2);

        List<MyCommentProjection> projections = Arrays.asList(projection1, projection2);
        when(commentRepository.findMyCommentsWithCursor(eq(userId), eq(null), eq(pageSize)))
                .thenReturn(projections);

        // When
        MyCommentsResponse response = myCommentsService.myComments(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getListCommentCount());

        // 첫 번째 댓글 검증
        MyCommentForList commentResponse1 = response.getListComment(0);
        assertEquals(commentId1.toString(), commentResponse1.getId());
        assertEquals("Content A", commentResponse1.getContent());
        assertEquals(createdAt1.toString(), commentResponse1.getCreatedAt());
        assertEquals(postId.toString(), commentResponse1.getPost().getPostId());
        assertEquals("Post Title A", commentResponse1.getPost().getPostTitle());

        // 두 번째 댓글 검증
        MyCommentForList commentResponse2 = response.getListComment(1);
        assertEquals(commentId2.toString(), commentResponse2.getId());
        assertEquals("Content B", commentResponse2.getContent());
        assertEquals(createdAt2.toString(), commentResponse2.getCreatedAt());
        assertEquals(postId.toString(), commentResponse2.getPost().getPostId());
        assertEquals("Post Title A", commentResponse2.getPost().getPostTitle());

        // PageInfo 검증: 리스트 사이즈가 pageSize와 같으므로 hasNextPage true, endCursor는 마지막 댓글 id
        PageInfo pageInfo = response.getPageInfo();
        assertTrue(pageInfo.getHasNextPage());
        assertEquals(commentId2.toString(), pageInfo.getEndCursor());
    }

    @Test
    @DisplayName("정상: 댓글 목록 조회 (cursor 포함) - cursor를 사용하여 조회")
    void myComments_successWithCursor() {
        // Given
        String validUserId = "123e4567-e89b-12d3-a456-426614174000";
        int pageSize = 3;
        // 제공된 cursor 값
        String cursorStr = "dddddddd-dddd-dddd-dddd-dddddddddddd";
        MyCommentsRequest request = createRequestWithCursor(validUserId, cursorStr, pageSize);
        UUID userId = UUID.fromString(validUserId);
        UUID cursor = UUID.fromString(cursorStr);

        UUID commentId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
        UUID postId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        Date createdAt = new Date(3000L);

        MyCommentProjection projection = createProjection(commentId, postId, "Post Title B", "Content Only", createdAt);
        List<MyCommentProjection> projections = Collections.singletonList(projection);
        when(commentRepository.findMyCommentsWithCursor(eq(userId), eq(cursor), eq(pageSize)))
                .thenReturn(projections);

        // When
        MyCommentsResponse response = myCommentsService.myComments(request);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getListCommentCount());
        MyCommentForList commentResponse = response.getListComment(0);
        assertEquals(commentId.toString(), commentResponse.getId());
        assertEquals("Content Only", commentResponse.getContent());
        assertEquals(createdAt.toString(), commentResponse.getCreatedAt());
        assertEquals(postId.toString(), commentResponse.getPost().getPostId());
        assertEquals("Post Title B", commentResponse.getPost().getPostTitle());

        // PageInfo 검증: 댓글 수가 pageSize보다 작으므로 hasNextPage false
        PageInfo pageInfo = response.getPageInfo();
        assertFalse(pageInfo.getHasNextPage());
        assertEquals(commentId.toString(), pageInfo.getEndCursor());
    }

    @Test
    @DisplayName("정상: 댓글이 없는 경우 빈 응답 반환")
    void myComments_empty() {
        // Given
        String validUserId = "123e4567-e89b-12d3-a456-426614174001";
        int pageSize = 5;
        MyCommentsRequest request = createRequest(validUserId, pageSize);
        UUID userId = UUID.fromString(validUserId);

        when(commentRepository.findMyCommentsWithCursor(eq(userId), eq(null), eq(pageSize)))
                .thenReturn(Collections.emptyList());

        // When
        MyCommentsResponse response = myCommentsService.myComments(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getListCommentCount());
        PageInfo pageInfo = response.getPageInfo();
        assertFalse(pageInfo.getHasNextPage());
    }

    @Test
    @DisplayName("실패: 잘못된 사용자 ID 형식 시 예외 발생")
    void myComments_invalidUserId() {
        // Given
        MyCommentsRequest request = createRequest("invalid-uuid", 3);

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                myCommentsService.myComments(request)
        );
        assertTrue(exception.getMessage().contains("잘못된 사용자 ID 형식"));
    }

    @Test
    @DisplayName("실패: 댓글 조회 중 오류 발생 시 INTERNAL 오류 전환")
    void myComments_fetchError() {
        // Given
        String validUserId = "123e4567-e89b-12d3-a456-426614174002";
        int pageSize = 4;
        MyCommentsRequest request = createRequest(validUserId, pageSize);
        UUID userId = UUID.fromString(validUserId);

        when(commentRepository.findMyCommentsWithCursor(eq(userId), eq(null), eq(pageSize)))
                .thenThrow(new RuntimeException("DB error"));

        // When & Then
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () ->
                myCommentsService.myComments(request)
        );
        assertTrue(exception.getMessage().contains("댓글 조회 중 오류가 발생했습니다"));
    }
}
