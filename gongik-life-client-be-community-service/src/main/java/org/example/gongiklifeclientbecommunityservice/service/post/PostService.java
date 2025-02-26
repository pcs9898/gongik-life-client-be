package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.ExistsPostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.ExistsPostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountResponse;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.respository.PostLikeRepository;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public CommunityServiceOuterClass.IsLikedPostResponse isLikedPost(CommunityServiceOuterClass.IsLikedPostRequest request) {
        boolean isLiked = postLikeRepository.existsByIdPostIdAndIdUserId(
                UUID.fromString(request.getPostId()), UUID.fromString(request.getUserId()));

        return CommunityServiceOuterClass.IsLikedPostResponse.newBuilder().setIsLiked(isLiked).build();
    }

    public IsLikedPostAndCommentCountResponse isLikedPostAndCommentCount(
            IsLikedPostAndCommentCountRequest request) {

        Integer commentCount = postRepository.findCommentCountById(
                UUID.fromString(request.getPostId()));

        boolean isLiked = false;
        if (request.hasUserId()) {
            isLiked = postLikeRepository.existsByIdPostIdAndIdUserId(
                    UUID.fromString(request.getPostId()), UUID.fromString(request.getUserId()));
        }

        return IsLikedPostAndCommentCountResponse.newBuilder()
                .setIsLiked(isLiked)
                .setCommentCount(commentCount)
                .build();
    }


    public Post findPostById(String postId) {
        return postRepository.findById(UUID.fromString(postId))
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("Post not found").asRuntimeException());
    }

    public void plusCommentCountById(UUID postId) {
        postRepository.plusCommentCountById(postId);
    }

    public void minusCommentCountById(UUID postId) {
        postRepository.minusCommentCountById(postId);
    }


    public ExistsPostResponse existsPost(ExistsPostRequest request) {
        boolean exists = postRepository.existsById(UUID.fromString(request.getPostId()));

        return ExistsPostResponse.newBuilder().setExists(exists).build();
    }
}


