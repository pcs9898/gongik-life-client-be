package org.example.gongiklifeclientbegraphql.service;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentForList;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostUser;
import dto.community.LikePostRequestDto;
import dto.community.UnLikePostRequestDto;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.common.PostUserDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CommentForListDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CreateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CreateCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.createPost.CreatePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.likePost.LikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.myComments.MyCommentsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myComments.MyCommentsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts.MyLikedPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts.MyLikedPostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.post.PostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.searchPosts.SearchPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.searchPosts.SearchPostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.unLikePost.UnLikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsResponseDto;
import org.example.gongiklifeclientbegraphql.exception.CommunityServiceException;
import org.example.gongiklifeclientbegraphql.producer.community.LikePostProducer;
import org.example.gongiklifeclientbegraphql.producer.community.UnLikePostProducer;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityService {

  private final LikePostProducer likePostProducer;
  private final UnLikePostProducer unLikePostProducer;

  @GrpcClient("gongik-life-client-be-community-service")
  private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

  /**
   * 공통 gRPC 호출 헬퍼
   */
  private <T> T executeGrpcCall(Supplier<T> grpcCall) {
    try {
      return grpcCall.get();
    } catch (Exception ex) {
      log.error("gRPC 호출 중 오류 발생: ", ex);
      throw new CommunityServiceException("gRPC 호출 오류", ex);
    }
  }

  public PostResponseDto createPost(CreatePostRequestDto requestDto) {
    Assert.notNull(requestDto, "CreatePostRequestDto는 null일 수 없습니다.");
    return PostResponseDto.fromCreatePostResponseProto(
        executeGrpcCall(() -> communityServiceBlockingStub.createPost(requestDto.toProto()))
    );
  }

  public Boolean isLikedPost(String postId, String userId) {
    Assert.hasText(postId, "postId는 비어있을 수 없습니다.");
    Assert.hasText(userId, "userId는 비어있을 수 없습니다.");
    IsLikedPostRequest request = IsLikedPostRequest.newBuilder()
        .setPostId(postId)
        .setUserId(userId)
        .build();
    return executeGrpcCall(() -> communityServiceBlockingStub.isLikedPost(request))
        .getIsLiked();
  }

  public LikePostResponseDto likePost(LikePostRequestDto requestDto) {
    Assert.notNull(requestDto, "LikePostRequestDto는 null일 수 없습니다.");
    try {
      likePostProducer.sendLikePostRequest(requestDto);
      return LikePostResponseDto.builder().success(true).build();
    } catch (Exception ex) {
      log.error("Kafka 호출 중 오류 발생: ", ex);
      throw new CommunityServiceException("Kafka 호출 오류", ex);
    }
  }

  public UnLikePostResponseDto unLikePost(UnLikePostRequestDto requestDto) {
    Assert.notNull(requestDto, "UnLikePostRequestDto는 null일 수 없습니다.");
    try {
      unLikePostProducer.sendUnLikePostRequest(requestDto);
      return UnLikePostResponseDto.builder().success(true).build();
    } catch (Exception ex) {
      log.error("Kafka 호출 중 오류 발생: ", ex);
      throw new CommunityServiceException("Kafka 호출 오류", ex);
    }
  }

  public IsLikedPostAndCommentCountResponse isLikedPostAndCommentCount(PostRequestDto requestDto) {
    Assert.notNull(requestDto, "PostRequestDto는 null일 수 없습니다.");
    return executeGrpcCall(() -> communityServiceBlockingStub
        .isLikedPostAndCommentCount(requestDto.toProto()));
  }

  public PostsResponseDto posts(PostsRequestDto requestDto) {
    Assert.notNull(requestDto, "PostsRequestDto는 null일 수 없습니다.");
    return PostsResponseDto.fromProto(
        executeGrpcCall(() -> communityServiceBlockingStub.posts(requestDto.toProto()))
    );
  }

  public CreateCommentResponseDto createComment(CreateCommentRequestDto requestDto) {
    Assert.notNull(requestDto, "CreateCommentRequestDto는 null일 수 없습니다.");
    return CreateCommentResponseDto.fromProto(
        executeGrpcCall(() -> communityServiceBlockingStub.createComment(requestDto.toProto()))
    );
  }

  public UpdateCommentResponseDto updateComment(UpdateCommentRequestDto requestDto) {
    Assert.notNull(requestDto, "UpdateCommentRequestDto는 null일 수 없습니다.");
    return UpdateCommentResponseDto.fromProto(
        executeGrpcCall(() -> communityServiceBlockingStub.updateComment(requestDto.toProto()))
    );
  }

  public DeleteCommentResponseDto deleteComment(DeleteCommentRequestDto requestDto) {
    Assert.notNull(requestDto, "DeleteCommentRequestDto는 null일 수 없습니다.");
    return DeleteCommentResponseDto.fromProto(
        executeGrpcCall(() -> communityServiceBlockingStub.deleteComment(requestDto.toProto()))
    );
  }

  public CommentsResponseDto comments(CommentsRequestDto requestDto) {
    Assert.notNull(requestDto, "CommentsRequestDto는 null일 수 없습니다.");
    CommentsResponse grpcResponse = executeGrpcCall(
        () -> communityServiceBlockingStub.comments(requestDto.toProto())
    );
    return convertCommentsGrpcResponse(grpcResponse);
  }

  private CommentsResponseDto convertCommentsGrpcResponse(CommentsResponse grpcResponse) {
    CommentsResponseDto responseDto = new CommentsResponseDto();
    List<CommentForListDto> listComment = grpcResponse.getListCommentList()
        .stream()
        .map(this::convertCommentForList)
        .collect(Collectors.toList());
    responseDto.setListComment(listComment);
    return responseDto;
  }

  private CommentForListDto convertCommentForList(CommentForList grpcComment) {
    CommentForListDto dto = new CommentForListDto();
    dto.setId(grpcComment.getId());
    dto.setPostId(grpcComment.getPostId());
    dto.setContent(grpcComment.getContent());
    dto.setCreatedAt(grpcComment.getCreatedAt());

    if (grpcComment.hasParentCommentId() && !grpcComment.getParentCommentId().isEmpty()) {
      dto.setParentCommentId(grpcComment.getParentCommentId());
    }

// 작성자 정보 매핑
    PostUser grpcUser = grpcComment.getUser();
    PostUserDto userDto = new PostUserDto();
    userDto.setUserId(grpcUser.getUserId());
    userDto.setUserName(grpcUser.getUserName());
    dto.setUser(userDto);

    List<CommentForListDto> childComments = grpcComment.getChildCommentsList()
        .stream()
        .map(this::convertCommentForList)
        .collect(Collectors.toList());
    dto.setChildComments(childComments);

    return dto;
  }

  public MyPostsResponseDto myPosts(MyPostsRequestDto requestDto) {
    Assert.notNull(requestDto, "MyPostsRequestDto는 null일 수 없습니다.");
    return MyPostsResponseDto.fromProto(
        executeGrpcCall(() -> communityServiceBlockingStub.myPosts(requestDto.toProto()))
    );
  }

  public UserPostsResponseDto userPosts(UserPostsRequestDto requestDto) {
    Assert.notNull(requestDto, "UserPostsRequestDto는 null일 수 없습니다.");
    return UserPostsResponseDto.fromProto(
        executeGrpcCall(() -> communityServiceBlockingStub.userPosts(requestDto.toProto()))
    );
  }

  public MyLikedPostsResponseDto myLikedPosts(MyLikedPostsRequestDto requestDto) {
    Assert.notNull(requestDto, "MyLikedPostsRequestDto는 null일 수 없습니다.");
    return MyLikedPostsResponseDto.fromProto(
        executeGrpcCall(() -> communityServiceBlockingStub.myLikedPosts(requestDto.toProto()))
    );
  }

  public MyCommentsResponseDto myComments(MyCommentsRequestDto requestDto) {
    Assert.notNull(requestDto, "MyCommentsRequestDto는 null일 수 없습니다.");
    return MyCommentsResponseDto.fromProto(
        executeGrpcCall(() -> communityServiceBlockingStub.myComments(requestDto.toProto()))
    );
  }

  public SearchPostsResponseDto searchPosts(SearchPostsRequestDto requestDto) {
    Assert.notNull(requestDto, "SearchPostsRequestDto는 null일 수 없습니다.");
    return SearchPostsResponseDto.fromProto(
        executeGrpcCall(() -> communityServiceBlockingStub.searchPosts(requestDto.toProto()))
    );
  }
}