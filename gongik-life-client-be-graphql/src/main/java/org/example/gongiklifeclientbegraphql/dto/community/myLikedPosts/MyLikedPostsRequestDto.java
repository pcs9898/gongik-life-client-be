package org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyLikedPostsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyLikedPostsRequestDto {


  private String userId;
  private String cursor;
  private Integer pageSize;

  public MyLikedPostsRequest toProto() {
    MyLikedPostsRequest.Builder builder = MyLikedPostsRequest.newBuilder()
        .setUserId(userId)
        .setPageSize(pageSize);

    if (cursor != null) {
      builder.setCursor(cursor);
    }

    return builder.build();
  }

}
