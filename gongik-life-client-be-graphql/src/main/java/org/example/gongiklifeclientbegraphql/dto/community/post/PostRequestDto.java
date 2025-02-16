package org.example.gongiklifeclientbegraphql.dto.community.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

  private String userId;
  private String postId;

  public IsLikedPostAndCommentCountRequest toProto() {
    IsLikedPostAndCommentCountRequest.Builder response = IsLikedPostAndCommentCountRequest.newBuilder()
        .setPostId(postId);

    if (userId != null) {
      response.setUserId(userId);
    }

    return response.build();
  }
}
