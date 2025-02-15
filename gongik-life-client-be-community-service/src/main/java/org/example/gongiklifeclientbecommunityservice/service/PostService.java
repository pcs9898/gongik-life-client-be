package org.example.gongiklifeclientbecommunityservice.service;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import dto.community.LikePostRequestDto;
import dto.community.UnLikePostRequestDto;
import io.grpc.Status;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.entity.PostLike;
import org.example.gongiklifeclientbecommunityservice.entity.PostLikeId;
import org.example.gongiklifeclientbecommunityservice.respository.PostLikeRepository;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

  private final PostRepository postRepository;
  private final PostLikeRepository postLikeRepository;
  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  @Transactional
  public CreatePostResponse createPost(CreatePostRequest request) {

    String userName = getUserNameById(request.getUserId());

    return postRepository.save(Post.fromCreatePostRequestProto(request))
        .toCreatePostResponseProto(userName);
  }

  public UpdatePostResponse updatePost(UpdatePostRequest request) {
    Post post = postRepository.findById(UUID.fromString(request.getPostId()))
        .orElseThrow(() -> Status.NOT_FOUND.withDescription("Post not found").asRuntimeException());

    post.fromUpdatePostRequestProto(request);

    String userName = getUserNameById(post.getUserId().toString());

    return postRepository.save(post).toUpdatePostResponseProto(userName);
  }

  private String getUserNameById(String userId) {
    return userServiceBlockingStub.getUserNameById(
        GetUserNameByIdRequest.newBuilder().setUserId(userId).build()
    ).getUserName();
  }

  public IsLikedPostResponse isLikedPost(IsLikedPostRequest request) {
    boolean isLiked = postLikeRepository.existsByIdPostIdAndIdUserId(
        UUID.fromString(request.getPostId()), UUID.fromString(request.getUserId()));

    return IsLikedPostResponse.newBuilder().setIsLiked(isLiked).build();

  }

  @Transactional
  public DeletePostResponse deletePost(DeletePostRequest request) {
    Post post = postRepository.findByIdAndUserId(
            UUID.fromString(request.getPostId()), UUID.fromString(request.getUserId()))
        .orElseThrow(
            () -> Status.NOT_FOUND.withDescription("Post not found, or maybe already deleted")
                .asRuntimeException());

    post.setDeletedAt(new Date());

    postRepository.save(post);

    return DeletePostResponse.newBuilder().setSuccess(true).build();
  }

  @Transactional
  public void likePost(LikePostRequestDto requestDto) {
    // 1. check post exist
    Post post = postRepository.findById(UUID.fromString(requestDto.getPostId()))
        .orElseThrow(() -> Status.NOT_FOUND.withDescription("Post not found").asRuntimeException());

    // 2. check already liked
    if (postLikeRepository.existsByIdPostIdAndIdUserId(
        UUID.fromString(requestDto.getPostId()), UUID.fromString(requestDto.getUserId()))) {
      throw Status.ALREADY_EXISTS.withDescription("Already liked").asRuntimeException();
    }

    // 3. like post
    PostLike newLike = new PostLike(
        new PostLikeId(UUID.fromString(requestDto.getPostId()),
            UUID.fromString(requestDto.getUserId())),
        new Date()
    );

    postLikeRepository.save(newLike);

    post.setLikeCount(post.getLikeCount() + 1);
  }

  @Transactional
  public void unLikePost(UnLikePostRequestDto requestDto) {
    // 1. check post exist
    Post post = postRepository.findById(UUID.fromString(requestDto.getPostId()))
        .orElseThrow(() -> Status.NOT_FOUND.withDescription("Post not found").asRuntimeException());

    // 2. check already liked
    PostLike like = postLikeRepository.findById(
        new PostLikeId(UUID.fromString(requestDto.getPostId()),
            UUID.fromString(requestDto.getUserId()))
    ).orElseThrow(() -> Status.NOT_FOUND.withDescription("Not liked yet").asRuntimeException());

    // 3. unlike post
    postLikeRepository.delete(like);

    post.setLikeCount(post.getLikeCount() - 1);
  }
}


