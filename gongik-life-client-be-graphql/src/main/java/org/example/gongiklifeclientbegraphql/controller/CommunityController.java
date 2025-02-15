package org.example.gongiklifeclientbegraphql.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.createPost.CreatePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.deletePost.DeletePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.deletePost.DeletePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.updatepost.UpdatePostRequestDto;
import org.example.gongiklifeclientbegraphql.service.CommunityCacheService;
import org.example.gongiklifeclientbegraphql.service.CommunityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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
  

}
