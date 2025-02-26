package org.example.gongiklifeclientbecommunityservice.service.comment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class GetCommentsService {


    private final CommentRepository commentRepository;
    private final UserService userService;

    public CommunityServiceOuterClass.CommentsResponse comments(CommunityServiceOuterClass.CommentsRequest request) {
        log.info("게시물 댓글 목록 조회 요청 - 게시물 ID: {}", request.getPostId());

        // 1. 게시물 ID 유효성 검증
        UUID postId = parseUUID(request.getPostId(), "게시물");

        // 2. 댓글 조회
        List<Comment> allComments = fetchComments(postId);
        if (allComments.isEmpty()) {
            log.info("조회된 댓글이 없습니다 - 게시물 ID: {}", postId);
            return buildEmptyResponse();
        }

        // 3. 사용자 정보 조회
        Map<String, String> userNameMap = fetchUserNames(allComments);

        // 4. 댓글 계층 구조 구성
        Map<UUID, List<Comment>> childrenMap = buildCommentHierarchy(allComments);

        // 5. 루트 댓글 필터링
        List<Comment> rootComments = filterRootComments(allComments);

        // 6. 응답 구성
        CommunityServiceOuterClass.CommentsResponse response = buildResponse(rootComments, childrenMap, userNameMap);

        log.info("게시물 댓글 목록 조회 완료 - 게시물 ID: {}, 루트 댓글 수: {}, 전체 댓글 수: {}",
                postId, rootComments.size(), allComments.size());
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

    private List<Comment> fetchComments(UUID postId) {
        try {
            return commentRepository.findCommentTreeByPostId(postId);
        } catch (Exception e) {
            log.error("댓글 조회 중 오류 발생 - 게시물 ID: {}, 오류: {}", postId, e.getMessage());
            throw Status.INTERNAL
                    .withDescription("댓글 조회 중 오류가 발생했습니다")
                    .asRuntimeException();
        }
    }

    private Map<String, String> fetchUserNames(List<Comment> comments) {
        List<String> userIds = comments.stream()
                .map(comment -> comment.getUserId().toString())
                .toList();

        try {
            Map<String, String> userNameMap = userService.getUserNamesByIds(userIds);
            if (userNameMap == null) {
                log.warn("사용자 이름 맵이 null로 반환됨");
                return new HashMap<>();
            }
            return userNameMap;
        } catch (Exception e) {
            log.error("사용자 이름 조회 중 오류 발생: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<UUID, List<Comment>> buildCommentHierarchy(List<Comment> allComments) {
        return allComments.stream()
                .filter(comment -> comment.getParentComment() != null)
                .collect(Collectors.groupingBy(comment -> comment.getParentComment().getId()));
    }

    private List<Comment> filterRootComments(List<Comment> allComments) {
        return allComments.stream()
                .filter(comment -> comment.getParentComment() == null)
                .collect(Collectors.toList());
    }

    private CommunityServiceOuterClass.CommentsResponse buildResponse(
            List<Comment> rootComments,
            Map<UUID, List<Comment>> childrenMap,
            Map<String, String> userNameMap) {

        CommunityServiceOuterClass.CommentsResponse.Builder responseBuilder =
                CommunityServiceOuterClass.CommentsResponse.newBuilder();

        for (Comment root : rootComments) {
            responseBuilder.addListComment(buildCommentForList(root, childrenMap, userNameMap));
        }

        return responseBuilder.build();
    }

    private CommunityServiceOuterClass.CommentForList buildCommentForList(
            Comment comment,
            Map<UUID, List<Comment>> childrenMap,
            Map<String, String> userNameMap) {

        // 기본 빌더 생성 및 필수 필드 설정
        CommunityServiceOuterClass.CommentForList.Builder builder = CommunityServiceOuterClass.CommentForList.newBuilder()
                .setId(comment.getId().toString())
                .setPostId(comment.getPost().getId().toString())
                .setCreatedAt(comment.getCreatedAt().toString());

        // 삭제되지 않은 댓글인 경우 내용과 사용자 정보 설정
        if (comment.getDeletedAt() == null) {
            addContentIfNotNull(builder, comment);
            addUserInfoIfAvailable(builder, comment, userNameMap);
        }

        // 부모 댓글 ID 설정 (있는 경우)
        addParentCommentIdIfExists(builder, comment);

        // 자식 댓글 추가 (있는 경우)
        addChildCommentsIfExists(builder, comment, childrenMap, userNameMap);

        return builder.build();
    }

    private void addContentIfNotNull(CommunityServiceOuterClass.CommentForList.Builder builder, Comment comment) {
        if (comment.getContent() != null) {
            builder.setContent(comment.getContent());
        }
    }

    private void addUserInfoIfAvailable(
            CommunityServiceOuterClass.CommentForList.Builder builder,
            Comment comment,
            Map<String, String> userNameMap) {

        String userIdStr = comment.getUserId().toString();
        String userName = userNameMap.getOrDefault(userIdStr, "알 수 없음");

        builder.setUser(
                CommunityServiceOuterClass.PostUser.newBuilder()
                        .setUserId(userIdStr)
                        .setUserName(userName)
                        .build()
        );
    }

    private void addParentCommentIdIfExists(CommunityServiceOuterClass.CommentForList.Builder builder, Comment comment) {
        if (comment.getParentComment() != null) {
            builder.setParentCommentId(comment.getParentComment().getId().toString());
        }
    }

    private void addChildCommentsIfExists(
            CommunityServiceOuterClass.CommentForList.Builder builder,
            Comment comment,
            Map<UUID, List<Comment>> childrenMap,
            Map<String, String> userNameMap) {

        List<Comment> children = childrenMap.get(comment.getId());
        if (children != null && !children.isEmpty()) {
            for (Comment child : children) {
                builder.addChildComments(buildCommentForList(child, childrenMap, userNameMap));
            }
        }
    }

    private CommunityServiceOuterClass.CommentsResponse buildEmptyResponse() {
        return CommunityServiceOuterClass.CommentsResponse.newBuilder().build();
    }
}