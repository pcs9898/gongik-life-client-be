package org.example.gongiklifeclientbegraphql.controller;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountResponse;
import dto.community.LikePostRequestDto;
import dto.community.UnLikePostRequestDto;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CreateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CreateCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.createPost.CreatePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.deletePost.DeletePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deletePost.DeletePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.likePost.LikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.post.PostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.unLikePost.UnLikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.updatepost.UpdatePostRequestDto;
import org.example.gongiklifeclientbegraphql.service.CommunityCacheService;
import org.example.gongiklifeclientbegraphql.service.CommunityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommunityController {

  private final CommunityService communityService;
  private final CommunityCacheService communityCacheService;

  @MutationMapping
  public PostResponseDto createPost(
      @Argument("createPostInput") CreatePostRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment

  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return communityService.createPost(requestDto);
    } catch (Exception e) {
      log.error("Failed to get userId from dataFetchingEnvironment", e);
      throw e;
    }
  }

  @MutationMapping
  public PostResponseDto updatePost(
      @Argument("updatePostInput") UpdatePostRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      PostResponseDto updatedPostResponse = communityCacheService.updatePost(requestDto);

      Boolean isLiked = communityService.isLikedPost(requestDto.getPostId(), userId);

      updatedPostResponse.setIsLiked(isLiked);

      return updatedPostResponse;

    } catch (Exception e) {
      log.error("Failed to update post", e);
      throw e;
    }
  }

  @MutationMapping
  public DeletePostResponseDto deletePost(
      @Argument("deletePostInput") DeletePostRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return communityCacheService.deletePost(requestDto);
    } catch (Exception e) {
      log.error("Failed to delete post", e);
      throw e;
    }
  }

  @MutationMapping()
  public LikePostResponseDto likePost(
      @Argument("likePostInput") LikePostRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return communityService.likePost(requestDto);
    } catch (Exception e) {
      log.error("Failed to like post", e);
      throw e;
    }
  }

  @MutationMapping
  public UnLikePostResponseDto unLikePost(
      @Argument("unLikePostInput") UnLikePostRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return communityService.unLikePost(requestDto);
    } catch (Exception e) {
      log.error("Failed to unLike post", e);
      throw e;
    }
  }

  @QueryMapping
  public PostResponseDto post(
      @Argument("postInput") PostRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      if (!"-1".equals(userId)) {
        requestDto.setUserId(userId);
      }

      PostResponseDto postResponse = communityCacheService.getPost(requestDto.getPostId());

      IsLikedPostAndCommentCountResponse isLikedPostAndCommentCountResponse = communityService.isLikedPostAndCommentCount(
          requestDto);

      postResponse.setIsLiked(isLikedPostAndCommentCountResponse.getIsLiked());

      postResponse.setCommentCount(isLikedPostAndCommentCountResponse.getCommentCount());

      return postResponse;
    } catch (Exception e) {
      log.error("Failed to get post", e);
      throw e;
    }
  }

  @QueryMapping
  public PostsResponseDto posts(
      @Argument("postsFilter") PostsRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      if (!"-1".equals(userId)) {
        requestDto.setUserId(userId);
      }

      return communityService.posts(requestDto);
    } catch (Exception e) {
      log.error("Failed to get posts", e);
      throw e;
    }
  }

  @MutationMapping
  public CreateCommentResponseDto createComment(
      @Argument("createCommentInput") CreateCommentRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return communityService.createComment(requestDto);
    } catch (Exception e) {
      log.error("Failed to create comment", e);
      throw e;
    }
  }


  @MutationMapping
  public UpdateCommentResponseDto updateComment(
      @Argument("updateCommentInput") UpdateCommentRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return communityService.updateComment(requestDto);
    } catch (Exception e) {
      log.error("Failed to update comment", e);
      throw e;
    }
  }

  @MutationMapping
  public DeleteCommentResponseDto deleteComment(
      @Argument("deleteCommentInput") DeleteCommentRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return communityService.deleteComment(requestDto);
    } catch (Exception e) {
      log.error("Failed to delete comment", e);
      throw e;
    }
  }


  @QueryMapping
  public CommentsResponseDto comments(
      @Argument("commentsInput") CommentsRequestDto requestDto
  ) {
    try {

      return communityService.comments(requestDto);
    } catch (Exception e) {
      log.error("Failed to get comments", e);
      throw e;
    }
  }

  @QueryMapping
  public MyPostsResponseDto myPosts(
      @Argument("myPostsFilter") MyPostsRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return communityService.myPosts(requestDto);
    } catch (Exception e) {
      log.error("Failed to get my posts", e);
      throw e;
    }
  }

}
