package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.dto.PostProjection;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyLikedPostsService {

    private final PostRepository postRepository;
    private final UserService userService;

    public CommunityServiceOuterClass.MyLikedPostsResponse myLikedPosts(CommunityServiceOuterClass.MyLikedPostsRequest request) {
        log.info("내가 좋아요한 게시물 목록 조회 요청 - 사용자 ID: {}, 페이지 크기: {}",
                request.getUserId(), request.getPageSize());

        // 1. 사용자 ID 유효성 검증
        UUID userId = parseUUID(request.getUserId(), "사용자");

        // 2. 좋아요한 게시물 조회
        List<PostProjection> posts = fetchMyLikedPosts(userId, request);
        if (posts.isEmpty()) {
            log.info("좋아요한 게시물이 없습니다 - 사용자 ID: {}", userId);
            return buildEmptyResponse();
        }

        // 3. 게시물 작성자 정보 조회
        Map<String, String> userNameMap = fetchUserNames(posts);

        // 4. 응답 구성
        List<CommunityServiceOuterClass.PostForList> listPosts = convertToPostList(posts, userNameMap);

        // 5. 페이지 정보 구성
        CommunityServiceOuterClass.PageInfo pageInfo = buildPageInfo(posts, request.getPageSize());

        // 6. 최종 응답 생성
        CommunityServiceOuterClass.MyLikedPostsResponse response = buildResponse(listPosts, pageInfo);

        log.info("내가 좋아요한 게시물 목록 조회 완료 - 사용자 ID: {}, 조회된 게시물 수: {}", userId, listPosts.size());
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

    private List<PostProjection> fetchMyLikedPosts(UUID userId, CommunityServiceOuterClass.MyLikedPostsRequest request) {
        UUID cursor = request.hasCursor() ? parseUUID(request.getCursor(), "커서") : null;

        return postRepository.findMyLikedPostsWithCursor(
                userId,
                cursor,
                request.getPageSize()
        );
    }

    private Map<String, String> fetchUserNames(List<PostProjection> posts) {
        List<String> userIds = posts.stream()
                .map(PostProjection::getUserId)
                .map(UUID::toString)
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

    private List<CommunityServiceOuterClass.PostForList> convertToPostList(
            List<PostProjection> posts, Map<String, String> userNameMap) {

        return posts.stream()
                .map(post -> convertToPostForList(post, userNameMap))
                .toList();
    }

    private CommunityServiceOuterClass.PostForList convertToPostForList(
            PostProjection post, Map<String, String> userNameMap) {

        String userId = post.getUserId().toString();
        String userName = userNameMap.getOrDefault(userId, "알 수 없음");

        CommunityServiceOuterClass.PostUser user = buildPostUser(userId, userName);

        return CommunityServiceOuterClass.PostForList.newBuilder()
                .setId(post.getId().toString())
                .setUser(user)
                .setCategoryId(post.getCategoryId())
                .setTitle(post.getTitle())
                .setContent(post.getContent())
                .setLikeCount(post.getLikeCount())
                .setCommentCount(post.getCommentCount())
                .setCreatedAt(post.getCreatedAt().toString())
                .setIsLiked(post.getIsLiked())
                .build();
    }

    private CommunityServiceOuterClass.PostUser buildPostUser(String userId, String userName) {
        return CommunityServiceOuterClass.PostUser.newBuilder()
                .setUserId(userId)
                .setUserName(userName)
                .build();
    }

    private CommunityServiceOuterClass.PageInfo buildPageInfo(List<PostProjection> posts, int pageSize) {
        CommunityServiceOuterClass.PageInfo.Builder pageInfoBuilder = CommunityServiceOuterClass.PageInfo.newBuilder()
                .setHasNextPage(posts.size() == pageSize);

        if (!posts.isEmpty()) {
            pageInfoBuilder.setEndCursor(posts.get(posts.size() - 1).getId().toString());
        }

        return pageInfoBuilder.build();
    }

    private CommunityServiceOuterClass.MyLikedPostsResponse buildResponse(
            List<CommunityServiceOuterClass.PostForList> listPosts,
            CommunityServiceOuterClass.PageInfo pageInfo) {

        return CommunityServiceOuterClass.MyLikedPostsResponse.newBuilder()
                .addAllListPost(listPosts)
                .setPageInfo(pageInfo)
                .build();
    }

    private CommunityServiceOuterClass.MyLikedPostsResponse buildEmptyResponse() {
        return CommunityServiceOuterClass.MyLikedPostsResponse.newBuilder()
                .setPageInfo(CommunityServiceOuterClass.PageInfo.newBuilder()
                        .setHasNextPage(false)
                        .build())
                .build();
    }
}