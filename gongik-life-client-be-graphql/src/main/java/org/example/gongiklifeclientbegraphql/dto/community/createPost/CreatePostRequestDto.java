package org.example.gongiklifeclientbegraphql.dto.community.createPost;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostRequest;
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
public class CreatePostRequestDto {

  private String userId;
  private int categoryId;
  private String title;
  private String content;

  public CreatePostRequest toProto() {
    return CreatePostRequest.newBuilder()
        .setUserId(userId)
        .setCategoryId(categoryId)
        .setTitle(title)
        .setContent(content)
        .build();
  }
}
