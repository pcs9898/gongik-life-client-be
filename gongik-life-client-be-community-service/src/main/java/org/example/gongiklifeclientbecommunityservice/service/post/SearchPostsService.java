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
public class SearchPostsService {

    private final PostRepository postRepository;
    private final UserService userService;

    public CommunityServiceOuterClass.SearchPostsResponse searchPosts(CommunityServiceOuterClass.SearchPostsRequest request) {
        log.info("게시물 검색 요청 - 검색어: {}, 카테고리: {}, 페이지 크기: {}",
                request.getSearchKeyword(), request.getPostCategoryId(), request.getPageSize());

        // 1. 검색 파라미터 준비
        UUID userId = request.hasUserId() ? parseUUID(request.getUserId(), "사용자") : null;
        UUID cursor = request.hasCursor() ? parseUUID(request.getCursor(), "커서") : null;

        // 2. 게시물 검색
        List<PostProjection> posts = fetchSearchPosts(request, userId, cursor);
        if (posts.isEmpty()) {
            log.info("검색 결과가 없습니다 - 검색어: {}", request.getSearchKeyword());
            return buildEmptyResponse();
        }

        // 3. 사용자 정보 조회
        Map<String, String> userNameMap = fetchUserNames(posts);

        // 4. 응답 구성
        List<CommunityServiceOuterClass.PostForList> listPosts = convertToPostList(posts, userNameMap);

        // 5. 페이지 정보 구성
        CommunityServiceOuterClass.PageInfo pageInfo = buildPageInfo(posts, request.getPageSize());

        // 6. 최종 응답 생성
        CommunityServiceOuterClass.SearchPostsResponse response = buildResponse(listPosts, pageInfo);

        log.info("게시물 검색 완료 - 검색어: {}, 검색 결과 수: {}", request.getSearchKeyword(), listPosts.size());
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

    private List<PostProjection> fetchSearchPosts(
            CommunityServiceOuterClass.SearchPostsRequest request, UUID userId, UUID cursor) {

        return postRepository.searchPosts(
                request.getSearchKeyword(),
                request.getPostCategoryId(),
                cursor,
                request.getPageSize(),
                userId
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

    private CommunityServiceOuterClass.SearchPostsResponse buildResponse(
            List<CommunityServiceOuterClass.PostForList> listPosts,
            CommunityServiceOuterClass.PageInfo pageInfo) {

        return CommunityServiceOuterClass.SearchPostsResponse.newBuilder()
                .addAllListPost(listPosts)
                .setPageInfo(pageInfo)
                .build();
    }

    private CommunityServiceOuterClass.SearchPostsResponse buildEmptyResponse() {
        return CommunityServiceOuterClass.SearchPostsResponse.newBuilder()
                .setPageInfo(CommunityServiceOuterClass.PageInfo.newBuilder()
                        .setHasNextPage(false)
                        .build())
                .build();
    }
}