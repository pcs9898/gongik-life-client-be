package org.example.gongiklifeclientbecommunityservice.service;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentForList;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeleteCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeleteCommentResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.ExistsCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.ExistsCommentResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentForList;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentsResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PageInfo;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostShortInfo;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostUser;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdsRequest;
import dto.notification.CreateNotificationRequestDto;
import io.grpc.Status;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbecommunityservice.dto.MyCommentProjection;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.producer.CreateNotificationProducer;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

  private final CommentRepository commentRepository;
  private final CreateNotificationProducer createNotificationProducer;
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
    postService.plusCommentCountById(post.getId());

    // 4. get username by user id
    String username = userServiceBlockingStub.getUserNameById(
        GetUserNameByIdRequest.newBuilder().setUserId(request.getUserId()).build()
    ).getUserName();

    if (request.hasParentCommentId()) {
      // 5. create notification for parent comment
      createNotificationProducer.sendCreateNotificationRequest(
          CreateNotificationRequestDto.builder()
              .userId(savedComment.getParentComment().getUserId().toString())
              .notificationTypeId(2)
              .title(username + " replied to your comment")
              .content(savedComment.getContent())
              .postId(request.getPostId())
              .targetCommentId(request.getParentCommentId())
              .build()
      );
    } else {
      // 5. create notification for post
      createNotificationProducer.sendCreateNotificationRequest(
          CreateNotificationRequestDto.builder()
              .userId(post.getUserId().toString())
              .notificationTypeId(1)
              .title(username + " commented on your post")
              .content(savedComment.getContent())
              .postId(request.getPostId())
              .targetCommentId(savedComment.getId().toString())
              .build()
      );
    }

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
    Comment comment = commentRepository.findByIdAndDeletedAtIsNull(
            UUID.fromString(request.getCommentId()))
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

  @Transactional
  public DeleteCommentResponse deleteComment(DeleteCommentRequest request) {
    Comment comment = commentRepository.findByIdAndDeletedAtIsNull(
            UUID.fromString(request.getCommentId()))
        .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

    if (!comment.getUserId().equals(UUID.fromString(request.getUserId()))) {
      throw Status.PERMISSION_DENIED.withDescription(
          "Permission denied, You can delete only your comment.").asRuntimeException();
    }

    comment.setDeletedAt(new Date());

    commentRepository.save(comment);

    // update comment count
    postService.minusCommentCountById(comment.getPost().getId());

    return DeleteCommentResponse.newBuilder()
        .setCommentId(comment.getId().toString())
        .setSuccess(true)
        .build();

  }

  public CommentsResponse comments(CommentsRequest request) {
    UUID postId = UUID.fromString(request.getPostId());
    // native query를 통해 평탄한(flat) 리스트 조회
    List<Comment> allComments = commentRepository.findCommentTreeByPostId(postId);

    List<String> userIds = allComments.stream()
        .map(comment -> comment.getUserId().toString())
        .toList();

    Map<String, String> userNameMap = userServiceBlockingStub.getUserNameByIds(
        GetUserNameByIdsRequest.newBuilder().addAllUserIds(userIds).build()
    ).getUsersMap();

    // 부모가 있는 댓글들에 대해, 부모 ID별 그룹핑 (재귀 쿼리로 조회해도 부모/자식 관계가 미리 채워져 있지 않을 수 있으므로 직접 그룹핑)
    Map<UUID, List<Comment>> childrenMap = allComments.stream()
        .filter(comment -> comment.getParentComment() != null)
        .collect(Collectors.groupingBy(comment -> comment.getParentComment().getId()));

    // 루트 댓글 필터 (부모가 없는 경우)
    List<Comment> rootComments = allComments.stream()
        .filter(comment -> comment.getParentComment() == null)
        .collect(Collectors.toList());

    CommentsResponse.Builder responseBuilder = CommentsResponse.newBuilder();
    for (Comment root : rootComments) {
      responseBuilder.addListComment(buildCommentForList(root, childrenMap, userNameMap));
    }

    return responseBuilder.build();
  }

  // 재귀적으로 Comment 엔티티를 gRPC CommentForList 메시지로 변환하는 함수
  private CommentForList buildCommentForList(Comment comment,
      Map<UUID, List<Comment>> childrenMap, Map<String, String> userNameMap) {

// 기본적으로 필수값들 설정 (id, postId, createdAt 등)
    CommentForList.Builder builder = CommentForList.newBuilder()
        .setId(comment.getId().toString())
        .setPostId(comment.getPost().getId().toString())
        .setCreatedAt(comment.getCreatedAt().toString());

// 삭제되지 않은 댓글이라면 content와 user 값을 설정합니다.
    if (comment.getDeletedAt() == null) {
      // content가 null이 아닌 경우에만 설정
      if (comment.getContent() != null) {
        builder.setContent(comment.getContent());
      }
      // user 정보도 설정 (userNameMap에서 null이 반환되지 않도록 확인)
      String userIdStr = comment.getUserId().toString();
      String userName = userNameMap.get(userIdStr);
      if (userName != null) {
        builder.setUser(
            PostUser.newBuilder()
                .setUserId(userIdStr)
                .setUserName(userName)
                .build()
        );
      }
    }
// parentComment가 존재하면 parentCommentId 설정
    if (comment.getParentComment() != null) {
      builder.setParentCommentId(comment.getParentComment().getId().toString());
    }
// 자식 댓글(대댓글)이 있다면 재귀 호출하여 추가
    List<Comment> children = childrenMap.get(comment.getId());
    if (children != null && !children.isEmpty()) {
      for (Comment child : children) {
        builder.addChildComments(buildCommentForList(child, childrenMap, userNameMap));
      }
    }
    return builder.build();
  }


  public MyCommentsResponse myComments(MyCommentsRequest request) {

    List<MyCommentProjection> comments = commentRepository.findMyCommentsWithCursor(
        UUID.fromString(request.getUserId()),
        request.hasCursor() ? UUID.fromString(request.getCursor()) : null,
        request.getPageSize()
    );

    List<MyCommentForList> myCommentForLists = comments.stream()
        .map(projection -> {
          return MyCommentForList.newBuilder()
              .setId(projection.getId().toString())
              .setPost(PostShortInfo.newBuilder()
                  .setPostId(projection.getPostId().toString())
                  .setPostTitle(projection.getPostTitle())
                  .build())
              .setContent(projection.getContent())
              .setCreatedAt(projection.getCreatedAt().toString())
              .build();
        }).toList();

    PageInfo.Builder pageInfoBuilder = PageInfo.newBuilder()
        .setHasNextPage(comments.size() == request.getPageSize());

    if (!comments.isEmpty()) {
      pageInfoBuilder.setEndCursor(comments.get(comments.size() - 1).getId().toString());
    }

    return MyCommentsResponse.newBuilder()
        .addAllListComment(myCommentForLists)
        .setPageInfo(pageInfoBuilder.build())
        .build();


  }


  public ExistsCommentResponse existsComment(ExistsCommentRequest request) {
    boolean exists = commentRepository.existsByIdAndDeletedAtIsNull(
        UUID.fromString(request.getCommentId()));

    return ExistsCommentResponse.newBuilder()
        .setExists(exists)
        .build();
  }
}
