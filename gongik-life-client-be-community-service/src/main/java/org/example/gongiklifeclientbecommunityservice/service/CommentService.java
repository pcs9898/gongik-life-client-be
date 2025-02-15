package org.example.gongiklifeclientbecommunityservice.service;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostUser;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import io.grpc.Status;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostService postService;

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;


  public void deleteAllCommentsByPost(String postId) {
    try {
      commentRepository.softDeleteAllByPostId(UUID.fromString(postId));
    } catch (Exception e) {
      log.error("Error deleting comments by postId: {}", postId, e);
      throw e;
    }
  }

  @Transactional
  public CreateCommentResponse createComment(CreateCommentRequest request) {
    // 1. check if the post exists
    Post post = postService.findPostById(request.getPostId());

    // 2. create comment
    Comment.CommentBuilder comment = Comment.builder()
        .userId(UUID.fromString(request.getUserId()))
        .post(post)
        .content(request.getContent());

    if (request.hasParentCommentId()) {
      Comment parentComment = commentRepository.findById(
              UUID.fromString(request.getParentCommentId()))
          .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

      if (parentComment.getPost().getId() != post.getId()) {
        throw Status.INVALID_ARGUMENT.withDescription(
            "Parent comment is not in the same post"
        ).asRuntimeException();
      }

      comment.parentComment(parentComment);
    }

    Comment savedComment = commentRepository.save(comment.build());

    // 3. update comment count
    postService.updateCommentCount(post.getId());

    // 4. get username by user id
    String username = userServiceBlockingStub.getUserNameById(
        GetUserNameByIdRequest.newBuilder().setUserId(request.getUserId()).build()
    ).getUserName();

    return CreateCommentResponse.newBuilder()
        .setId(savedComment.getId().toString())
        .setUser(PostUser.newBuilder()
            .setUserId(savedComment.getUserId().toString())
            .setUserName(username)
            .build())
        .setPostId(request.getPostId())
        .setContent(savedComment.getContent())
        .setCreatedAt(savedComment.getCreatedAt().toString())
        .build();

  }


  public UpdateCommentResponse updateComment(UpdateCommentRequest request) {
    Comment comment = commentRepository.findById(UUID.fromString(request.getCommentId()))
        .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

    if (!comment.getUserId().equals(UUID.fromString(request.getUserId()))) {
      throw Status.PERMISSION_DENIED.withDescription(
          "Permission denied, You can update only your comment.").asRuntimeException();
    }

    comment.setContent(request.getContent());
    commentRepository.save(comment);

    return UpdateCommentResponse.newBuilder()
        .setId(request.getCommentId())
        .setSuccess(true)
        .build();
  }

}
