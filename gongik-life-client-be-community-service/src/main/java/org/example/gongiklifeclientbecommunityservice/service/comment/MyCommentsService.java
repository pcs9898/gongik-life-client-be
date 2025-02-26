package org.example.gongiklifeclientbecommunityservice.service.comment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.dto.MyCommentProjection;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCommentsService {
    
    private final CommentRepository commentRepository;

    public CommunityServiceOuterClass.MyCommentsResponse myComments(CommunityServiceOuterClass.MyCommentsRequest request) {
        log.info("내 댓글 목록 조회 요청 - 사용자 ID: {}, 페이지 크기: {}",
                request.getUserId(), request.getPageSize());

        // 1. 사용자 ID 유효성 검증
        UUID userId = parseUUID(request.getUserId(), "사용자");

        // 2. 커서 파싱 (있는 경우)
        UUID cursor = request.hasCursor() ? parseUUID(request.getCursor(), "커서") : null;

        // 3. 내 댓글 조회
        List<MyCommentProjection> comments = fetchMyComments(userId, cursor, request.getPageSize());
        if (comments.isEmpty()) {
            log.info("조회된 댓글이 없습니다 - 사용자 ID: {}", userId);
            return buildEmptyResponse();
        }

        // 4. 응답 구성
        List<CommunityServiceOuterClass.MyCommentForList> myCommentForLists = convertToMyCommentList(comments);

        // 5. 페이지 정보 구성
        CommunityServiceOuterClass.PageInfo pageInfo = buildPageInfo(comments, request.getPageSize());

        // 6. 최종 응답 생성
        CommunityServiceOuterClass.MyCommentsResponse response = buildResponse(myCommentForLists, pageInfo);

        log.info("내 댓글 목록 조회 완료 - 사용자 ID: {}, 조회된 댓글 수: {}", userId, comments.size());
        return response;
    }

    private UUID parseUUID(String id, String entityName) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 {} ID 형식: {}", entityName, id);
            throw Status.INVALID_ARGUMENT
                    .withDescription(String.format("잘못된 %s ID 형식", entityName))
                    .asRuntimeException();
        }
    }

    private List<MyCommentProjection> fetchMyComments(UUID userId, UUID cursor, int pageSize) {
        try {
            return commentRepository.findMyCommentsWithCursor(userId, cursor, pageSize);
        } catch (Exception e) {
            log.error("내 댓글 조회 중 오류 발생 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            throw Status.INTERNAL
                    .withDescription("댓글 조회 중 오류가 발생했습니다")
                    .asRuntimeException();
        }
    }

    private List<CommunityServiceOuterClass.MyCommentForList> convertToMyCommentList(List<MyCommentProjection> comments) {
        return comments.stream()
                .map(this::convertToMyCommentForList)
                .toList();
    }

    private CommunityServiceOuterClass.MyCommentForList convertToMyCommentForList(MyCommentProjection projection) {
        return CommunityServiceOuterClass.MyCommentForList.newBuilder()
                .setId(projection.getId().toString())
                .setPost(buildPostShortInfo(projection))
                .setContent(projection.getContent())
                .setCreatedAt(projection.getCreatedAt().toString())
                .build();
    }

    private CommunityServiceOuterClass.PostShortInfo buildPostShortInfo(MyCommentProjection projection) {
        return CommunityServiceOuterClass.PostShortInfo.newBuilder()
                .setPostId(projection.getPostId().toString())
                .setPostTitle(projection.getPostTitle())
                .build();
    }

    private CommunityServiceOuterClass.PageInfo buildPageInfo(List<MyCommentProjection> comments, int pageSize) {
        CommunityServiceOuterClass.PageInfo.Builder pageInfoBuilder = CommunityServiceOuterClass.PageInfo.newBuilder()
                .setHasNextPage(comments.size() == pageSize);

        if (!comments.isEmpty()) {
            pageInfoBuilder.setEndCursor(comments.get(comments.size() - 1).getId().toString());
        }

        return pageInfoBuilder.build();
    }

    private CommunityServiceOuterClass.MyCommentsResponse buildResponse(
            List<CommunityServiceOuterClass.MyCommentForList> myCommentForLists,
            CommunityServiceOuterClass.PageInfo pageInfo) {

        return CommunityServiceOuterClass.MyCommentsResponse.newBuilder()
                .addAllListComment(myCommentForLists)
                .setPageInfo(pageInfo)
                .build();
    }

    private CommunityServiceOuterClass.MyCommentsResponse buildEmptyResponse() {
        return CommunityServiceOuterClass.MyCommentsResponse.newBuilder()
                .setPageInfo(CommunityServiceOuterClass.PageInfo.newBuilder()
                        .setHasNextPage(false)
                        .build())
                .build();
    }
}