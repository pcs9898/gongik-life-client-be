package org.example.gongiklifeclientbegraphql.dto.common;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostResponse;
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
public class PostDto {

  private String id;
  private PostUserDto user;
  private Integer categoryId;
  private String title;
  private String content;
  private Integer likeCount;
  private Integer commentCount;
  private String createdAt;

  public static PostDto fromProto(CreatePostResponse post) {

    return PostDto.builder()
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
