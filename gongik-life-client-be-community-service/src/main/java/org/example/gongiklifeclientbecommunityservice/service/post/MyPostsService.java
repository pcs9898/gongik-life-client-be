package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.dto.PostProjection;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPostsService {


    private final PostRepository postRepository;
    private final UserService userService;

    public CommunityServiceOuterClass.MyPostsResponse myPosts(CommunityServiceOuterClass.MyPostsRequest request) {
        log.info("내 게시물 목록 조회 요청 - 사용자 ID: {}, 페이지 크기: {}",
                request.getUserId(), request.getPageSize());

        // 1. 사용자 ID 유효성 검증
        UUID userId = parseUUID(request.getUserId(), "사용자");

        // 2. 사용자 이름 조회
        String username = fetchUserName(request.getUserId());

        // 3. 게시물 조회
        List<PostProjection> posts = fetchMyPosts(userId, request);
        if (posts.isEmpty()) {
            log.info("조회된 게시물이 없습니다 - 사용자 ID: {}", userId);
            return buildEmptyResponse();
        }

        // 4. 응답 구성
        List<CommunityServiceOuterClass.PostForList> listPosts = convertToPostList(posts, username);

        // 5. 페이지 정보 구성
        CommunityServiceOuterClass.PageInfo pageInfo = buildPageInfo(posts, request.getPageSize());

        // 6. 최종 응답 생성
        CommunityServiceOuterClass.MyPostsResponse response = buildResponse(listPosts, pageInfo);

        log.info("내 게시물 목록 조회 완료 - 사용자 ID: {}, 조회된 게시물 수: {}", userId, listPosts.size());
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

    private String fetchUserName(String userId) {
        try {
            String username = userService.getUserNameById(userId);
            if (username == null || username.isEmpty()) {
                log.warn("사용자 이름을 찾을 수 없음 - 사용자 ID: {}", userId);
                return "알 수 없음";
            }
            return username;
        } catch (Exception e) {
            log.error("사용자 이름 조회 중 오류 발생 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            return "알 수 없음";
        }
    }

    private List<PostProjection> fetchMyPosts(UUID userId, CommunityServiceOuterClass.MyPostsRequest request) {
        UUID cursor = request.hasCursor() ? parseUUID(request.getCursor(), "커서") : null;

        return postRepository.findMyPostsWithCursor(
                userId,
                cursor,
                request.getPageSize()
        );
    }

    private List<CommunityServiceOuterClass.PostForList> convertToPostList(
            List<PostProjection> posts, String username) {

        return posts.stream()
                .map(post -> convertToPostForList(post, username))
                .toList();
    }

    private CommunityServiceOuterClass.PostForList convertToPostForList(
            PostProjection post, String username) {

        CommunityServiceOuterClass.PostUser user = buildPostUser(post.getUserId().toString(), username);

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

    private CommunityServiceOuterClass.MyPostsResponse buildResponse(
            List<CommunityServiceOuterClass.PostForList> listPosts,
            CommunityServiceOuterClass.PageInfo pageInfo) {

        return CommunityServiceOuterClass.MyPostsResponse.newBuilder()
                .addAllListPost(listPosts)
                .setPageInfo(pageInfo)
                .build();
    }

    private CommunityServiceOuterClass.MyPostsResponse buildEmptyResponse() {
        return CommunityServiceOuterClass.MyPostsResponse.newBuilder()
                .setPageInfo(CommunityServiceOuterClass.PageInfo.newBuilder()
                        .setHasNextPage(false)
                        .build())
                .build();
    }
}