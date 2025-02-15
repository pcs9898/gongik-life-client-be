package org.example.gongiklifeclientbegraphql.dto.common;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.GetPostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class PostResponseDto {

  private String id;
  private PostUserDto user;
  private Integer categoryId;
  private String title;
  private String content;
  private Integer likeCount;
  private Integer commentCount;
  private String createdAt;
  private Boolean isLiked;

  public static PostResponseDto fromCreatePostResponseProto(CreatePostResponse post) {

    return PostResponseDto.builder()
        .id(post.getId())
        .user(PostUserDto.builder()
            .userId(post.getUser().getUserId())
            .userName(post.getUser().getUserName())
            .build())
        .categoryId(post.getCategoryId())
        .title(post.getTitle())
        .content(post.getContent())
        .likeCount(post.getLikeCount())
        .commentCount(post.getCommentCount())
        .createdAt(post.getCreatedAt())
        .build();
  }

  public static PostResponseDto fromUpdatePostResponseProto(UpdatePostResponse updatePostResponse) {

    return PostResponseDto.builder()
        .id(updatePostResponse.getId())
        .user(PostUserDto.builder()
            .userId(updatePostResponse.getUser().getUserId())
            .userName(updatePostResponse.getUser().getUserName())
            .build())
        .categoryId(updatePostResponse.getCategoryId())
        .title(updatePostResponse.getTitle())
        .content(updatePostResponse.getContent())
        .likeCount(updatePostResponse.getLikeCount())
        .commentCount(updatePostResponse.getCommentCount())
        .createdAt(updatePostResponse.getCreatedAt())
        .build();
  }

  public static PostResponseDto fromPostResponseProto(GetPostResponse post) {

    return PostResponseDto.builder()
        .id(post.getId())
        .user(PostUserDto.builder()
            .userId(post.getUser().getUserId())
            .userName(post.getUser().getUserName())
            .build())
        .categoryId(post.getCategoryId())
        .title(post.getTitle())
        .content(post.getContent())
        .likeCount(post.getLikeCount())
        .commentCount(post.getCommentCount())
        .createdAt(post.getCreatedAt())
        .build();
  }
}
