package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.dto.PostProjection;
import org.example.gongiklifeclientbecommunityservice.respository.PostLikeRepository;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPostsService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserService userService;

    public CommunityServiceOuterClass.PostsResponse posts(CommunityServiceOuterClass.PostsRequest request) {
        log.info("게시물 목록 조회 요청 - 카테고리: {}, 페이지 크기: {}",
                request.getPostCategoryId(), request.getPageSize());

        // 1. 게시물 조회
        List<PostProjection> posts = fetchPosts(request);
        if (posts.isEmpty()) {
            log.info("조회된 게시물이 없습니다");
            return buildEmptyResponse();
        }

        // 2. 사용자 정보 조회
        Map<String, String> userNameMap = fetchUserNames(posts);

        // 3. 응답 구성
        List<CommunityServiceOuterClass.PostForList> listPosts = convertToPostList(posts, userNameMap);

        // 4. 페이지 정보 구성
        CommunityServiceOuterClass.PageInfo pageInfo = buildPageInfo(posts, request.getPageSize());

        // 5. 최종 응답 생성
        CommunityServiceOuterClass.PostsResponse response = buildResponse(listPosts, pageInfo);

        log.info("게시물 목록 조회 완료 - 조회된 게시물 수: {}", listPosts.size());
        return response;
    }

    private List<PostProjection> fetchPosts(CommunityServiceOuterClass.PostsRequest request) {
        UUID userId = request.hasUserId() ? parseUUID(request.getUserId()) : null;
        UUID cursor = request.hasCursor() ? parseUUID(request.getCursor()) : null;

        return postRepository.findPostsWithCursor(
                userId,
                request.getPostCategoryId(),
                cursor,
                request.getPageSize()
        );
    }

    private UUID parseUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 UUID 형식: {}", id);
            throw new IllegalArgumentException("잘못된 ID 형식: " + id);
        }
    }

    private Map<String, String> fetchUserNames(List<PostProjection> posts) {
        List<String> userIds = posts.stream()
                .map(PostProjection::getUserId)
                .map(UUID::toString)
                .toList();

        return userService.getUserNamesByIds(userIds);
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

    private CommunityServiceOuterClass.PostsResponse buildResponse(
            List<CommunityServiceOuterClass.PostForList> listPosts,
            CommunityServiceOuterClass.PageInfo pageInfo) {

        return CommunityServiceOuterClass.PostsResponse.newBuilder()
                .addAllListPost(listPosts)
                .setPageInfo(pageInfo)
                .build();
    }

    private CommunityServiceOuterClass.PostsResponse buildEmptyResponse() {
        return CommunityServiceOuterClass.PostsResponse.newBuilder()
                .setPageInfo(CommunityServiceOuterClass.PageInfo.newBuilder()
                        .setHasNextPage(false)
                        .build())
                .build();
    }
}
