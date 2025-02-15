package org.example.gongiklifeclientbegraphql.dto.posts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostsRequestDto {

  private String userId;
  private Integer postCategoryId;
  private String cursor;
  private Integer pageSize;

  public PostsRequest toProto() {
    PostsRequest.Builder builder = PostsRequest.newBuilder()
        .setPostCategoryId(postCategoryId)
        .setPageSize(pageSize);

    if (userId != null) {
      builder.setUserId(userId);
    }

    if (cursor != null) {
      builder.setCursor(cursor);
    }

    return builder.build();
  }
}
