package org.example.gongiklifeclientbegraphql.controller;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountResponse;
import dto.community.LikePostRequestDto;
import dto.community.UnLikePostRequestDto;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.createPost.CreatePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.deletePost.DeletePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.deletePost.DeletePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.likePost.LikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.post.PostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.unLikePost.UnLikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.updatepost.UpdatePostRequestDto;
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


}
