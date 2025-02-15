package org.example.gongiklifeclientbegraphql.dto.deletePost;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeletePostRequestDto {

  private String userId;
  private String postId;

  public DeletePostRequest toProto() {
    return DeletePostRequest.newBuilder()
        .setUserId(userId)
        .setPostId(postId)
        .build();
  }
}
