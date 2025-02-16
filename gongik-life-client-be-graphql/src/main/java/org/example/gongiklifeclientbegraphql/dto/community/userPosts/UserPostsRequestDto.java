package org.example.gongiklifeclientbegraphql.dto.community.userPosts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UserPostsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPostsRequestDto {

  private String myUserId;
  private String userId;
  private String cursor;
  private Integer pageSize;

  public UserPostsRequest toProto() {
    UserPostsRequest.Builder builder = UserPostsRequest.newBuilder()
        .setUserId(userId)
        .setPageSize(pageSize);

    if (myUserId != null) {
      builder.setMyUserId(myUserId);
    }

    if (cursor != null) {
      builder.setCursor(cursor);
    }

    return builder.build();
  }

}
