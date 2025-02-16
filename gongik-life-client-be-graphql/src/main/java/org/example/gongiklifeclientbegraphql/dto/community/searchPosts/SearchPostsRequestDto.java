package org.example.gongiklifeclientbegraphql.dto.community.searchPosts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.SearchPostsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPostsRequestDto {

  private String userId;
  private String searchKeyword;
  private Integer postCategoryId;
  private String cursor;
  private Integer pageSize;

  public SearchPostsRequest toProto() {
    SearchPostsRequest.Builder builder = SearchPostsRequest.newBuilder()
        .setSearchKeyword(searchKeyword)
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
