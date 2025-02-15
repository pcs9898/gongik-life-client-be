package org.example.gongiklifeclientbegraphql.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.PostDto;
import org.example.gongiklifeclientbegraphql.dto.createPost.CreatePostRequestDto;
import org.example.gongiklifeclientbegraphql.service.CommunityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommunityController {

  private final CommunityService communityService;

  @MutationMapping
  public PostDto createPost(
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

}
