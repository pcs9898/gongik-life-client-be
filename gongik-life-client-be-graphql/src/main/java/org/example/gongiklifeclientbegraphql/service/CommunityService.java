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
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.post.PostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.unLikePost.UnLikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsResponseDto;
import org.example.gongiklifeclientbegraphql.producer.community.LikePostProducer;
import org.example.gongiklifeclientbegraphql.producer.community.UnLikePostProducer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityService {

  private final LikePostProducer likePostProducer;
  private final UnLikePostProducer unLikePostProducer;

  @GrpcClient("gongik-life-client-be-community-service")
  private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

  public PostResponseDto createPost(CreatePostRequestDto requestDto) {
    try {
      return PostResponseDto.fromCreatePostResponseProto(communityServiceBlockingStub.createPost(
          requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }


  public Boolean isLikedPost(String postId, String userId) {
    try {
      return communityServiceBlockingStub.isLikedPost(
          IsLikedPostRequest.newBuilder()
              .setPostId(postId)
              .setUserId(userId)
              .build()
      ).getIsLiked();
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public LikePostResponseDto likePost(LikePostRequestDto requestDto) {
    try {

      likePostProducer.sendLikePostRequest(requestDto);

      return LikePostResponseDto.builder().success(true).build();

    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public UnLikePostResponseDto unLikePost(UnLikePostRequestDto requestDto) {
    try {

      unLikePostProducer.sendUnLikePostRequest(requestDto);

      return UnLikePostResponseDto.builder().success(true).build();

    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public IsLikedPostAndCommentCountResponse isLikedPostAndCommentCount(PostRequestDto requestDto) {

    return communityServiceBlockingStub.isLikedPostAndCommentCount(requestDto.toProto());
  }

  public PostsResponseDto posts(PostsRequestDto requestDto) {
    try {
      return PostsResponseDto.fromProto(communityServiceBlockingStub.posts(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public CreateCommentResponseDto createComment(CreateCommentRequestDto requestDto) {
    try {
      return CreateCommentResponseDto.fromProto(
          communityServiceBlockingStub.createComment(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }


  public UpdateCommentResponseDto updateComment(UpdateCommentRequestDto requestDto) {
    try {
      return UpdateCommentResponseDto.fromProto(
          communityServiceBlockingStub.updateComment(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public DeleteCommentResponseDto deleteComment(DeleteCommentRequestDto requestDto) {
    try {
      return DeleteCommentResponseDto.fromProto(
          communityServiceBlockingStub.deleteComment(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public CommentsResponseDto comments(CommentsRequestDto requestDto) {
    try {

      CommentsResponse grpcResponse = communityServiceBlockingStub.comments(requestDto.toProto());

      return convertCommentsGrpcResponse(grpcResponse);
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
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

    // parentCommentId 필드는 optional이므로 값이 존재할 때만 설정
    if (grpcComment.getParentCommentId() != null && !grpcComment.getParentCommentId().isEmpty()) {
      dto.setParentCommentId(grpcComment.getParentCommentId());
    }

    // 작성자 정보 매핑
    PostUser grpcUser = grpcComment.getUser();
    PostUserDto userDto = new PostUserDto();
    userDto.setUserId(grpcUser.getUserId());
    userDto.setUserName(grpcUser.getUserName());
    dto.setUser(userDto);

    // 재귀적으로 childComments 변환
    List<CommentForListDto> childComments = grpcComment.getChildCommentsList()
        .stream()
        .map(this::convertCommentForList)
        .collect(Collectors.toList());
    dto.setChildComments(childComments);

    return dto;
  }

  public MyPostsResponseDto myPosts(MyPostsRequestDto requestDto) {
    try {
      return MyPostsResponseDto.fromProto(
          communityServiceBlockingStub.myPosts(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;

    }
  }

  public UserPostsResponseDto userPosts(UserPostsRequestDto requestDto) {
    try {
      return UserPostsResponseDto.fromProto(
          communityServiceBlockingStub.userPosts(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

}
