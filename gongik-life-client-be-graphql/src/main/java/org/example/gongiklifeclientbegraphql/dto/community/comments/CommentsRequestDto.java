package org.example.gongiklifeclientbegraphql.dto.community.comments;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentsRequestDto {

  private String postId;

  public CommentsRequest toProto() {
    return CommentsRequest.newBuilder()
        .setPostId(postId)
        .build();
  }
}
